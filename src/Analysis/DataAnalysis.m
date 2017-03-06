clc;
clear;

%data = importdata('../../data/testdata.txt');
data = csvread('../../data/data_03:04_15:10:14.csv');
time = data(:,1);
force = data(:,2);

figure(1); clf(1);
figure(1); hold on;
plot(time,force);
title('Force Recorded');
xlabel('Time (s)');
ylabel('Force (lbs)');
