function [ out ] = EntropyDensity(densityMat)
%ENTROPYDENSITY Summary of this function goes here
%   Detailed explanation goes here
[row,col] = size(densityMat);
numOfElem = row*col;
sum = 0;
for i=1:row
    for j=1:col
        p = densityMat(i,j);
        if p>0
            sum = sum +  log2(p);
        end
    end
end
sum = sum * (-1)/numOfElem;
out = sum;
end
