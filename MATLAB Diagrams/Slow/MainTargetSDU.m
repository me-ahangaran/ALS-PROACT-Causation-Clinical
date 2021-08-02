clc
clear all
close all

depMatIdx = csvread('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\CFD\CFDIdx.txt');
targetMat = csvread('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\CFD\Target.txt');
targetMatAbs = csvread('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\CFD\TargetAbs.txt');
depMat = csvread('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\CFD\CFD.txt');
[row, col] = size(depMatIdx);
bw = 100;

numOfFeatures = 87; %all features contains virtual feature

X_Mat = zeros(row,bw);
X_MatAbs = zeros(row,bw);
entropyProb = zeros(row,1);
entropyDensity = zeros(row,1);
entropyProbAbs = zeros(row,1);
entropyDensityAbs = zeros(row,1);
F_Mat = zeros(row,bw);
F_MatAbs = zeros(row,bw);
XP_Mat = zeros(row,bw-1);   %x pivot for prob values
XP_MatAbs = zeros(row,bw-1);   %x pivot for prob values. absolute target
P_Mat = zeros(row,bw-1);    %probability values
P_MatAbs = zeros(row,bw-1);    %probability values for absolute target
maxProb_Mat = zeros(row,3); %max point probability. (targetChange, probValue, densValue)
maxProb_MatAbs = zeros(row,3); %max point probability for absolute target. (targetChange, probValue, densValue). absolute target

%data for calculate first feaures
X_MatFirstRow = zeros(numOfFeatures,bw);
entropyProbFirstRow = zeros(numOfFeatures,1);
F_MatFirstRow = zeros(numOfFeatures,bw);
XP_MatFirstRow = zeros(numOfFeatures,bw-1);
P_MatFirstRow = zeros(numOfFeatures,bw-1);
maxProbFirstRow_Mat = zeros(numOfFeatures,3);   %max point probability. (next change, prob value, dens value)

% initial density and prob entropy list
for i=1:row
    entropyProb(i,1) = -1;
    entropyDensity(i,1) = -1;
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
for idx=1:row
    prevIdx = depMatIdx(idx,1);
    nextIdx = depMatIdx(idx,2);
    startIdx = depMatIdx(idx,3);
    endIdx = depMatIdx(idx,4);    
    if (startIdx == -1) || (endIdx == -1)
        continue;
    end
    data = targetMat(startIdx:endIdx,1);
    dataAbs = targetMatAbs(startIdx:endIdx,1);
    dataFirstRowDepMat = depMat(startIdx:endIdx,2);
    if mod(idx,100) == 0
        disp(idx);
    end
    try
        [f,xi,bandwidth] = ksdensity(data,'npoints',bw,'function','pdf');
        [fAbs,xiAbs,bandwidthAbs] = ksdensity(dataAbs,'npoints',bw,'function','pdf');
        
        %first row
        if(prevIdx == 0)
            [fFirstRow,xiFirstRow,bandwidthFirstRow] = ksdensity(dataFirstRowDepMat,'npoints',bw,'function','pdf');
            
            X_MatFirstRow(idx,:) = xiFirstRow(1,:);
            F_MatFirstRow(idx,:) = fFirstRow(1,:);
            
            p_firstRow = zeros(1, bw-1);
            xp_firstRow = zeros(1, bw-1);
            for i2=1:(bw-2)
                xp_firstRow(1,i2) = (xiFirstRow(1,i2) + xiFirstRow(1,i2+1)) / 2;
                p_firstRow(1,i2) = ((fFirstRow(1,i2) + fFirstRow(1,i2+1)) / 2) * (xiFirstRow(1,i2+1) - xiFirstRow(1,i2));
            end
            
            %max point distribution setting based on probability on first row density
            entProbFirstRow = EntropyDist(p_firstRow);
            entropyProbFirstRow(idx) = entProbFirstRow;
            [r2, c2, prob2, d2] = MaxProbPoint(p_firstRow, fFirstRow);
            maxProbFirstRow_Mat(idx,1) = xiFirstRow(1,c2);
            maxProbFirstRow_Mat(idx,2) = prob2;
            maxProbFirstRow_Mat(idx,3) = d2;
        end
%       entProb = EntropyDist(f);
        X_Mat(idx,:) = xi(1,:);
        F_Mat(idx,:) = f(1,:);
        
        %absolute target
        X_MatAbs(idx,:) = xiAbs(1,:);
        F_MatAbs(idx,:) = fAbs(1,:);
        
        p = zeros(1, bw-1);
        xp = zeros(1, bw-1);
        
        %absolute target
        pAbs = zeros(1, bw-1);
        xpAbs = zeros(1, bw-1);
       
        
        for i1=1:(bw-2)
            xp(1,i1) = (xi(1,i1) + xi(1,i1+1)) / 2;
            p(1,i1) = ((f(1,i1) + f(1,i1+1)) / 2) * (xi(1,i1+1) - xi(1,i1));
        end
        
        %absolute target
        for i1=1:(bw-2)
            xpAbs(1,i1) = (xiAbs(1,i1) + xiAbs(1,i1+1)) / 2;
            pAbs(1,i1) = ((fAbs(1,i1) + fAbs(1,i1+1)) / 2) * (xiAbs(1,i1+1) - xiAbs(1,i1));
        end
        
        %max point distribution setting based on probability
        [maxVal, probVal, densVal] = MaxProbPointTarget(f,xi,p);
        maxProb_Mat(idx,1) = maxVal;
        maxProb_Mat(idx,2) = probVal;
        maxProb_Mat(idx,3) = densVal;
        
        %absolute target value
        [maxValAbs, probValAbs, densValAbs] = MaxProbPointTarget(fAbs,xiAbs,pAbs);
        maxProb_MatAbs(idx,1) = maxValAbs;
        maxProb_MatAbs(idx,2) = probValAbs;
        maxProb_MatAbs(idx,3) = densValAbs;
                
        
    catch exception
        if strcmp(exception.identifier,'MATLAB:fzero:ValuesAtEndPtsComplexOrNotFinite')...
                || strcmp(exception.identifier,'MATLAB:fzero:ValuesAtEndPtsSameSign')
            continue;
        end
    end
end

%initialize the probability values
for i=1:row
    for j=1:(bw-2)
        P_Mat(i,j) = ((F_Mat(i,j) + F_Mat(i,j+1))/2) .* (X_Mat(i,j+1) - X_Mat(i,j));
        XP_Mat(i,j) = (X_Mat(i,j+1) + X_Mat(i,j)) / 2;
    end
end

%absolute target
for i=1:row
    for j=1:(bw-2)
        P_MatAbs(i,j) = ((F_MatAbs(i,j) + F_MatAbs(i,j+1))/2) .* (X_MatAbs(i,j+1) - X_MatAbs(i,j));
        XP_MatAbs(i,j) = (X_MatAbs(i,j+1) + X_MatAbs(i,j)) / 2;
    end
end

%first row
for i=1:numOfFeatures
    for j=1:(bw-2)
        P_MatFirstRow(i,j) = ((F_MatFirstRow(i,j) + F_MatFirstRow(i,j+1))/2) * (X_MatFirstRow(i,j+1) - X_MatFirstRow(i,j));
        XP_MatFirstRow(i,j) = (X_MatFirstRow(i,j+1) + X_MatFirstRow(i,j)) / 2;
    end
end

%calculate the entropy values
for i=1:row
    ent = EntropyDist(P_Mat(i,:));
    entDens = EntropyDensity(F_Mat(i,:));
    if (ent ~= 0)
        entropyProb(i,1) = ent;
    end
    
    if (entDens ~= 0)
         entropyDensity(i,1) = entDens;
    end
    
end

%absolute target
for i=1:row
    entAbs = EntropyDist(P_MatAbs(i,:));
    entDensAbs = EntropyDensity(F_MatAbs(i,:));
    if (entAbs ~= 0)
        entropyProbAbs(i,1) = entAbs;
    end
    
    if (entDensAbs ~= 0)
         entropyDensityAbs(i,1) = entDensAbs;
    end
    
end

%entropy for first row
for i=1:numOfFeatures
    entFirstRow = EntropyDist(P_MatFirstRow(i,:));
    if (entFirstRow ~= 0)
        entropyProbFirstRow(i,1) = entFirstRow;
    end
end

        
        fnameDensity = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\TargetDensity.txt');
        fid_density = fopen(fnameDensity,'w');
        dlmwrite(fnameDensity,F_Mat);
        fclose(fid_density);
        
        %absolute target
        fnameDensityAbs = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\TargetDensityAbs.txt');
        fid_densityAbs = fopen(fnameDensityAbs,'w');
        dlmwrite(fnameDensityAbs,F_MatAbs);
        fclose(fid_densityAbs);

        fnameX = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\TargetX.txt');
        fid_X = fopen(fnameX,'w');
        dlmwrite(fnameX,X_Mat);
        fclose(fid_X);
        
        %absolute target
        fnameXAbs = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\TargetXAbs.txt');
        fid_XAbs = fopen(fnameXAbs,'w');
        dlmwrite(fnameXAbs,X_MatAbs);
        fclose(fid_XAbs);
        
        fnameEntropy = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\EntropyTarget.txt');
        fid_Ent = fopen(fnameEntropy,'w');
        dlmwrite(fnameEntropy,entropyProb);
        fclose(fid_Ent);
        
        %absolute target
        fnameEntropyAbs = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\EntropyTargetAbs.txt');
        fid_EntAbs = fopen(fnameEntropyAbs,'w');
        dlmwrite(fnameEntropyAbs,entropyProbAbs);
        fclose(fid_EntAbs);
        
%         fnameEntropy = strcat('E:\PHD\Thesis\Implementation\ALS-Matlab\Output\EntropyDensTarget.txt');
%         fid_Ent = fopen(fnameEntropy,'w');
%         dlmwrite(fnameEntropy,entropyDensity);
%         fclose(fid_Ent);
        
        fnameProb = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\TargetProb.txt');
        fid_Prob = fopen(fnameProb,'w');
        dlmwrite(fnameProb,P_Mat);
        fclose(fid_Prob);
        
        %absolute target
        fnameProbAbs = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\TargetProbAbs.txt');
        fid_ProbAbs = fopen(fnameProbAbs,'w');
        dlmwrite(fnameProbAbs,P_MatAbs);
        fclose(fid_ProbAbs);
        
        fnameXP = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\TargetXP.txt');
        fid_XP = fopen(fnameXP,'w');
        dlmwrite(fnameXP,XP_Mat);
        fclose(fid_XP);
        
        %absolute target
        fnameXPAbs = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\TargetXPAbs.txt');
        fid_XPAbs = fopen(fnameXPAbs,'w');
        dlmwrite(fnameXPAbs,XP_MatAbs);
        fclose(fid_XPAbs);
        
        fnameMaxProb = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\TargetMaxPointsProb.txt');
        fid_MaxProb = fopen(fnameMaxProb,'w');
        dlmwrite(fnameMaxProb,maxProb_Mat);
        fclose(fid_MaxProb);
        
        %absolute target
        fnameMaxProbAbs = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\Target\TargetMaxPointsProbAbs.txt');
        fid_MaxProbAbs = fopen(fnameMaxProbAbs,'w');
        dlmwrite(fnameMaxProbAbs,maxProb_MatAbs);
        fclose(fid_MaxProbAbs);
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%creating first row files
        fnameDensityFirstRow = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\FirstRow\FirstRowDensity.txt');
        fid_densityFirstRow = fopen(fnameDensityFirstRow,'w');
        dlmwrite(fnameDensityFirstRow,F_MatFirstRow);
        fclose(fid_densityFirstRow);

        fnameXFirstRow = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\FirstRow\FirstRowX.txt');
        fid_XFirstRow = fopen(fnameXFirstRow,'w');
        dlmwrite(fnameXFirstRow,X_MatFirstRow);
        fclose(fid_XFirstRow);
        
        fnameEntropyFirstRow = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\FirstRow\FirstRowEntropy.txt');
        fid_EntFirstRow = fopen(fnameEntropyFirstRow,'w');
        dlmwrite(fnameEntropyFirstRow,entropyProbFirstRow);
        fclose(fid_EntFirstRow);
        
        fnameProbFirstRow = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\FirstRow\FirstRowProb.txt');
        fid_ProbFirstRow = fopen(fnameProbFirstRow,'w');
        dlmwrite(fnameProbFirstRow,P_MatFirstRow);
        fclose(fid_ProbFirstRow);
        
        fnameXPFirstRow = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\FirstRow\FirstRowXP.txt');
        fid_XPFirstRow = fopen(fnameXPFirstRow,'w');
        dlmwrite(fnameXPFirstRow,XP_MatFirstRow);
        fclose(fid_XPFirstRow);
        
        fnameFirstRowMaxProb = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Slow\FirstRow\FirstRowDensityMaxPoints.txt');
        fid_FirstRowMaxProb = fopen(fnameFirstRowMaxProb,'w');
        dlmwrite(fnameFirstRowMaxProb,maxProbFirstRow_Mat);
        fclose(fid_FirstRowMaxProb);
        
                
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%