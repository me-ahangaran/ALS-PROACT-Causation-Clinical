clc
clear all
close all

depMatIdx = csvread('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Intermediate\CFD\CFDIdx.txt');
timePosMat = csvread('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Intermediate\CFD\TimePosition.txt');
depMat = csvread('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Intermediate\CFD\CFD.txt');
[row, col] = size(depMatIdx);
bw = 100;

numOfFeatures = 87; %all features contains virtual feature

X_Mat = zeros(row,bw);
entropyProb = zeros(row,1);
F_Mat = zeros(row,bw);
XP_Mat = zeros(row,bw-1);   %x pivot for prob values
P_Mat = zeros(row,bw-1);    %probability values
maxProb_Mat = zeros(row,3); %max point probability. (nextChange, probValue)

% initial density and prob entropy list
for i=1:row
    entropyProb(i,1) = -1;
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
    data = timePosMat(startIdx:endIdx,1);
    if mod(idx,100) == 0
        disp(idx);
    end
    try
        [f,xi,bandwidth] = ksdensity(data,'npoints',bw,'function','pdf');
%         entProb = EntropyDist(f);
        X_Mat(idx,:) = xi(1,:);
        F_Mat(idx,:) = f(1,:);
        
        p = zeros(1, bw-1);
        xp = zeros(1, bw-1);
        for i=1:(bw-2)
            xp(1,i) = (xi(1,i) + xi(1,i+1)) / 2;
            p(1,i) = ((f(1,i) + f(1,i+1)) / 2) * (xi(1,i+1) - xi(1,i));
        end
        
        %max point distribution setting based on probability
%         [row, col, prob] = maxProbPoint(p);
%         maxProb_Mat(idx,1) = xp(1,col);
%         maxProb_Mat(idx,2) = prob;
        [maxVal, probVal, densVal] = MaxProbPointTarget(f,xi,p);
        maxProb_Mat(idx,1) = maxVal;
        maxProb_Mat(idx,2) = probVal;
        maxProb_Mat(idx,3) = densVal;
        
                
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
        P_Mat(i,j) = (F_Mat(i,j) + F_Mat(i,j+1))/2 * (X_Mat(i,j+1) - X_Mat(i,j));
        XP_Mat(i,j) = (X_Mat(i,j+1) + X_Mat(i,j)) / 2;
    end
end

%calculate the entropy values
for i=1:row
    ent = EntropyDist(P_Mat(i,:));
    if (ent ~= 0)
        entropyProb(i,1) = ent;
    end
end


        
        fnameDensity = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Intermediate\TimePosition\TimePosDensity.txt');
        fid_density = fopen(fnameDensity,'w');
        dlmwrite(fnameDensity,F_Mat);
        fclose(fid_density);

        fnameX = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Intermediate\TimePosition\TimePosX.txt');
        fid_X = fopen(fnameX,'w');
        dlmwrite(fnameX,X_Mat);
        fclose(fid_X);
        
        fnameEntropy = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Intermediate\TimePosition\TimePosEntropy.txt');
        fid_Ent = fopen(fnameEntropy,'w');
        dlmwrite(fnameEntropy,entropyProb);
        fclose(fid_Ent);
        
        fnameProb = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Intermediate\TimePosition\TimePosProb.txt');
        fid_Prob = fopen(fnameProb,'w');
        dlmwrite(fnameProb,P_Mat);
        fclose(fid_Prob);
        
        fnameXP = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Intermediate\TimePosition\TimePosXP.txt');
        fid_XP = fopen(fnameXP,'w');
        dlmwrite(fnameXP,XP_Mat);
        fclose(fid_XP);
        
        fnameMaxProb = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Intermediate\TimePosition\TimePosMaxPointsProb.txt');
        fid_MaxProb = fopen(fnameMaxProb,'w');
        dlmwrite(fnameMaxProb,maxProb_Mat);
        fclose(fid_MaxProb);
 
