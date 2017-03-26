clc;
clear;

data = csvread('/Users/blakeolsen/Documents/coding/design2/src/Client/../../data/data_03:06_16:10:38.csv');
time = data(:,1);
force = data(:,2);

figure(1); clf(1);
figure(1); hold on;
plot(time,force);
title('Force Recorded');
xlabel('Time (s)');
ylabel('Force (lbs)');
