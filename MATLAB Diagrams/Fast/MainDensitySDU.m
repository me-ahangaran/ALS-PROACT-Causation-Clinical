clc
clear all
close all

depMatIdx = csvread('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Fast\CFD\CFDIdx.txt');
depMat = csvread('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Fast\CFD\CFD.txt');

[row, col] = size(depMatIdx);
bw = 64;

X_Mat = zeros(row,bw);
Y_Mat = zeros(row,bw);
Xp_Mat = zeros(row,bw-1);
Yp_Mat = zeros(row,bw-1);
% entropyDensity = zeros(row,1);
entropyProb = zeros(row,1);
maxProb_Mat = zeros(row,5); %max point probability. (prevChange, nextChange, probValue, densValue, numberOfData)

%initial density and prob entropy list
for i=1:row
%     entropyDensity(i,1) = -1;
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
    data = depMat(startIdx:endIdx,:);
    numOfData = endIdx - startIdx;  %number of data for each causal link (matrix index)
    if mod(idx,100) == 0
        disp(idx);
    end
    try
        [bandwidth,density,probability,X,Y,X_p,Y_p]=kde2d(data,bw);
%         entDens = EntropyDist(density);
        entProb = EntropyDist(probability);
%         entropyDensity(idx,1) = entDens;
        entropyProb(idx,1) = entProb;
        X_Mat(idx,:) = X(1,:);
        Y_Mat(idx,:) = Y(:,1)';
        Xp_Mat(idx,:) = X_p;
        Yp_Mat(idx,:) = Y_p';
        
        %max point distribution setting based on probability
        [rowIdx, colIdx, prob, dens] = MaxProbPoint(probability, density);
%         maxProb_Mat(idx,1) = X_p(col);
%         maxProb_Mat(idx,2) = Y_p(row);
        maxProb_Mat(idx,1) = X(rowIdx, colIdx);   %this is true do not modify it. this is tested with real data
        maxProb_Mat(idx,2) = Y(rowIdx, colIdx);   %this is true do not modify it. this is tested with real data
        maxProb_Mat(idx,3) = prob;
        maxProb_Mat(idx,4) = dens;
        maxProb_Mat(idx,5) = numOfData;
        
        %max point distribution setting based on density
%         [row, col, prob] = maxProbPoint(density);
%         maxProb_Mat(idx,1) = X(1,row);
%         maxProb_Mat(idx,2) = Y(col,1);
%         maxProb_Mat(idx,3) = prob;
        
        
        fnameDensity = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Fast\Density\density(',mat2str(prevIdx),'-',mat2str(nextIdx),').txt');
        fid_density = fopen(fnameDensity,'w');
        dlmwrite(fnameDensity,density);
        fclose(fid_density);

        fnameProb = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Fast\Probability\prob(',mat2str(prevIdx),'-',mat2str(nextIdx),').txt');
        fid_prob = fopen(fnameProb,'w');
        dlmwrite(fnameProb,probability);
        fclose(fid_prob);
    catch exception
        if strcmp(exception.identifier,'MATLAB:fzero:ValuesAtEndPtsComplexOrNotFinite')...
                || strcmp(exception.identifier,'MATLAB:fzero:ValuesAtEndPtsSameSign')
            continue;
        end
    end
end

 fnameMaxProb = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Fast\MaxPointsProb.txt');
 fid_MaxProb = fopen(fnameMaxProb,'w');
 dlmwrite(fnameMaxProb,maxProb_Mat);
 fclose(fid_MaxProb);
 
 fnameX = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Fast\X.txt');
 fid_X = fopen(fnameX,'w');
 dlmwrite(fnameX,X_Mat);
 fclose(fid_X);
 
 fnameY = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Fast\Y.txt');
 fid_Y = fopen(fnameY,'w');
 dlmwrite(fnameY,Y_Mat);
 fclose(fid_Y);
 
 fnameXp = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Fast\Xp.txt');
 fid_Xp = fopen(fnameXp,'w');
 dlmwrite(fnameXp,Xp_Mat);
 fclose(fid_Xp);
 
 fnameYp = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Fast\Yp.txt');
 fid_Yp = fopen(fnameYp,'w');
 dlmwrite(fnameYp,Yp_Mat);
 fclose(fid_Yp);
 
 fnameEntProb = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\SDU\Progression\Fast\EntropyDensity.txt');
 fid_EntProb = fopen(fnameEntProb,'w');
 dlmwrite(fnameEntProb,entropyProb);
 fclose(fid_EntProb);
 
%  fnameEntDensity = strcat('E:\PHD\Thesis\Implementation\ALS-Matlab\Output\EntropyDensity.txt');
%  fid_EntDens = fopen(fnameEntDensity,'w');
%  dlmwrite(fnameEntDensity,entropyDensity);
%  fclose(fid_EntDens);
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% idx = 2;    %main index(1-16)
% prevIdx = depMatIdx(idx,1);
% nextIdx = depMatIdx(idx,2);
% startIdx = depMatIdx(idx,3);
% endIdx = depMatIdx(idx,4);
% data = depMat(startIdx:endIdx,:);
% [bandwidth,density,probability,X,Y,X_p,Y_p]=kde2d(data,64);

% fnameDensity = strcat('E:\PHD\Thesis\Implementation\ALS-Matlab\ALSOutput\density(',mat2str(prevIdx),',',mat2str(nextIdx),').txt');
% fid_density = fopen(fnameDensity,'w');
% dlmwrite(fnameDensity,density);
% fclose(fid_density);
% 
% fnameProb = strcat('E:\PHD\Thesis\Implementation\ALS-Matlab\ALSOutput\prob(',mat2str(prevIdx),',',mat2str(nextIdx),').txt');
% fid_prob = fopen(fnameProb,'w');
% dlmwrite(fnameProb,probability);
% fclose(fid_prob);
%         
% %plot density
% figure
% contour3(X,Y,density,100), hold on
% plot(data(:,1),data(:,2),'r.','MarkerSize',5)

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  figure
%  surf(X,Y,density)
%  hold on
%  colormap(parula(1))
%  plot(data(:,1),data(:,2),'w.','MarkerSize',5)

