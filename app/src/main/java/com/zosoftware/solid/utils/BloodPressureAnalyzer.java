package com.zosoftware.solid.utils;
import biz.source_code.dsp.filter.FilterCharacteristicsType;
import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilter;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignFisher;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BloodPressureAnalyzer {

    private static final int TRIM_START = 1000;

    public static double[] main(List<Double> pressureData) throws IOException {
        // 过滤只保留 0 到 200 之间的值，并更新 pressureData 列表
        pressureData = pressureData.stream()
                .filter(value -> value >= 0 && value <= 200)
                .collect(Collectors.toList());
        double fs = 200.0; // 采样频率
        double lowpassCutoff = 1; // 低通滤波截止频率(用于相减获取交流分量)
        double lowpassCutoff_1 = 0.5; // 低通滤波截止频率（用于作为压力基准值）
        double initialBandpassLowcut = 0.5; // 初始带通滤波低截止频率
        double initialBandpassHighcut = 6; // 初始带通滤波高截止频率

        // 对数据进行低通滤波
        double[] pressureDataArray = pressureData.stream().mapToDouble(Double::doubleValue).toArray();
        double[] dcComponent = butterworthLowpass(pressureDataArray, lowpassCutoff, fs);
        double[] acComponent = subtractArrays(pressureDataArray, dcComponent);
        double[] dcComponent_1 = butterworthLowpass(pressureDataArray, lowpassCutoff_1, fs);
        // 对交流分量进行平滑处理
        double[] smoothedAcComponent = smoothData(acComponent);

        // 应用带通滤波
        double[] initialBandpassComponent = butterworthBandpass(smoothedAcComponent, initialBandpassLowcut,
                initialBandpassHighcut, fs);

        // 去除前300个点
        double[] trimmedPressureData = Arrays.copyOfRange(pressureDataArray, TRIM_START, pressureDataArray.length);
        double[] trimmedDcComponent = Arrays.copyOfRange(dcComponent_1, TRIM_START, dcComponent.length);
        double[] trimmedBandpassComponent = Arrays.copyOfRange(initialBandpassComponent, TRIM_START,
                initialBandpassComponent.length);

        // 调试打印
        System.out.printf("Trimmed data length: %d%n", trimmedBandpassComponent.length);

        // 估算心率（基于初次带通滤波后的交流成分）
        int[] peaks = findPeaks(trimmedBandpassComponent, 0.2, 50);
        if (peaks.length < 3) {
            System.out.printf("Detected peaks: %s%n", Arrays.toString(peaks));
            throw new IllegalArgumentException("Not enough peaks detected to estimate heart rate.");
        }
        int maxPeakIndex = findMaxPeakIndex(trimmedBandpassComponent, peaks);
        int[] selectedPeaks = selectPeaksAroundMax(peaks, maxPeakIndex);
        double[] peakIntervals = calculatePeakIntervals(selectedPeaks, fs);
        double heartRate = 60 / calculateMean(peakIntervals); // 每分钟心跳次数

        // 动态调整带通滤波器频率范围
        double dynamicLowcut = Math.max(0.5, (heartRate / 60) - 1);
        double dynamicHighcut = Math.min(6.0, (heartRate / 60) + 1);

        System.out.printf("Estimated Heart Rate: %.2f bpm%n", heartRate);
        System.out.printf("Dynamic Bandpass Filter Range: %.2f Hz - %.2f Hz%n", dynamicLowcut, dynamicHighcut);

        // 对原始数据进行动态调整后的带通滤波，然后再去除前300个点
        double[] dynamicBandpassComponent = butterworthBandpass(smoothedAcComponent, dynamicLowcut, dynamicHighcut, fs);
        double[] trimmedDynamicBandpassComponent = Arrays.copyOfRange(dynamicBandpassComponent, TRIM_START,
                dynamicBandpassComponent.length);

        // 筛选高于最大峰值50%的峰值
        double maxPeakValue = Arrays.stream(trimmedDynamicBandpassComponent).max().orElse(Double.MIN_VALUE);
        double threshold = 0.4 * maxPeakValue;
        int[] dynamicPeaks = findPeaks(trimmedDynamicBandpassComponent, threshold, 50);

        if (dynamicPeaks.length < 3) {
            System.out.printf("Detected dynamic peaks: %s%n", Arrays.toString(dynamicPeaks));
            throw new IllegalArgumentException(
                    "Not enough peaks detected above 50% of the maximum peak for Gaussian fitting.");
        }

        // 高斯拟合
        double[] peakX = new double[dynamicPeaks.length];
        double[] peakY = new double[dynamicPeaks.length];
        for (int i = 0; i < dynamicPeaks.length; i++) {
            peakX[i] = dynamicPeaks[i];
            peakY[i] = trimmedDynamicBandpassComponent[dynamicPeaks[i]];
        }

        double[] params = fitGaussian(peakX, peakY);
        double a = params[0];
        double x0 = params[1];
        double sigma = params[2];

        System.out.printf("Gaussian Fit Parameters: a=%.2f, x0=%.2f, sigma=%.2f%n", a, x0, sigma);

        // 生成拟合曲线
        double[] fitX = new double[trimmedDynamicBandpassComponent.length];
        double[] fitY = new double[trimmedDynamicBandpassComponent.length];
        for (int i = 0; i < fitX.length; i++) {
            fitX[i] = i;
            fitY[i] = GaussianFunction.value(i, a, x0, sigma);
        }

        // 找到高斯曲线上的峰值对应坐标
        int mapIndex = 0;
        double mapY = fitY[0];
        for (int i = 1; i < fitY.length; i++) {
            if (fitY[i] > mapY) {
                mapY = fitY[i];
                mapIndex = i;
            }
        }

        double mapX = fitX[mapIndex];

        // 根据MAP值选择K_SP和K_DP
        double mp = trimmedDcComponent[mapIndex];
        double[] kValues = getKspKdp(mp);
        double k_sp = kValues[0];
        double k_dp = kValues[1];

        // 计算DBP和SBP索引
        Integer dbpIndex = null;
        for (int i = 0; i < mapIndex; i++) {
            if (fitY[i] >= k_dp * mapY) {
                dbpIndex = i;
                break;
            }
        }
        Integer sbpIndex = null;
        for (int i = mapIndex; i < fitY.length; i++) {
            if (fitY[i] <= k_sp * mapY) {
                sbpIndex = i;
                break;
            }
        }

        // 得到结果
        if (dbpIndex != null && sbpIndex != null) {
            double dbpY = trimmedDcComponent[dbpIndex];
            double sbpY = trimmedDcComponent[sbpIndex];

            System.out.printf("DBP: %.2f mmHg at index %d%n", dbpY, dbpIndex);
            System.out.printf("SBP: %.2f mmHg at index %d%n", sbpY, sbpIndex);

            // 返回结果
            return new double[]{sbpY,dbpY};
        } else {
            System.out.println("Unable to determine DBP or SBP.");
            return new double[]{};
        }
    }

    // 低通滤波器
    private static double[] butterworthLowpass(double[] data, double cutoff, double fs) {
        IirFilterCoefficients coeffs = IirFilterDesignFisher.design(FilterPassType.lowpass,
                FilterCharacteristicsType.butterworth, 6, 0, cutoff / (0.5 * fs), 0);
        IirFilter filter = new IirFilter(coeffs);
        return filter(data, filter);
    }

    // 带通滤波器
    private static double[] butterworthBandpass(double[] data, double lowcut, double highcut, double fs) {
        IirFilterCoefficients coeffs = IirFilterDesignFisher.design(FilterPassType.bandpass,
                FilterCharacteristicsType.butterworth, 4, 0, lowcut / (0.5 * fs), highcut / (0.5 * fs));
        IirFilter filter = new IirFilter(coeffs);
        return filter(data, filter);
    }

    // 数据平滑处理
    private static double[] smoothData(double[] data) {
        double[] smoothedData = new double[data.length];
        int windowSize = 20;
        int halfWindow = windowSize / 2;
        for (int i = 0; i < data.length; i++) {
            int start = Math.max(0, i - halfWindow);
            int end = Math.min(data.length - 1, i + halfWindow);
            double sum = 0;
            int count = 0;
            for (int j = start; j <= end; j++) {
                sum += data[j];
                count++;
            }
            smoothedData[i] = sum / count;
        }
        return smoothedData;
    }

    // 过滤数据数组的方法
    private static double[] filter(double[] data, IirFilter filter) {
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = filter.step(data[i]);
        }
        return result;
    }

    // 寻找峰值的方法，带高度和距离条件
    private static int[] findPeaks(double[] data, double height, int distance) {
        List<Integer> peaks = new ArrayList<>();
        for (int i = distance; i < data.length - distance; i++) {
            boolean isPeak = true;
            for (int j = 1; j <= distance; j++) {
                if (data[i] < data[i - j] || data[i] < data[i + j]) {
                    isPeak = false;
                    break;
                }
            }
            if (isPeak && data[i] > height) {
                peaks.add(i);
            }
        }
        return peaks.stream().mapToInt(Integer::intValue).toArray();
    }

    // 找到最高峰值索引
    private static int findMaxPeakIndex(double[] data, int[] peaks) {
        int maxPeakIndex = 0;
        double maxPeakValue = data[peaks[0]];
        for (int i = 1; i < peaks.length; i++) {
            if (data[peaks[i]] > maxPeakValue) {
                maxPeakValue = data[peaks[i]];
                maxPeakIndex = i;
            }
        }
        return maxPeakIndex;
    }

    // 选择最高峰及其前后两个峰
    private static int[] selectPeaksAroundMax(int[] peaks, int maxPeakIndex) {
        int startIndex = Math.max(0, maxPeakIndex - 2);
        int endIndex = Math.min(peaks.length - 1, maxPeakIndex + 2);
        return Arrays.copyOfRange(peaks, startIndex, endIndex + 1);
    }

    // 计算峰值之间的时间间隔
    private static double[] calculatePeakIntervals(int[] peaks, double fs) {
        double[] intervals = new double[peaks.length - 1];
        for (int i = 0; i < peaks.length - 1; i++) {
            intervals[i] = (peaks[i + 1] - peaks[i]) / fs;
        }
        return intervals;
    }

    // 计算平均值
    private static double calculateMean(double[] values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }

    // 高斯函数
    private static class GaussianFunction {
        public static double value(double x, double a, double x0, double sigma) {
            return a * Math.exp(-(x - x0) * (x - x0) / (2 * sigma * sigma));
        }
    }

    // 使用Apache Commons Math进行高斯拟合
    private static double[] fitGaussian(double[] x, double[] y) {
        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < x.length; i++) {
            obs.add(x[i], y[i]);
        }

        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        double[] parameters = fitter.fit(obs.toList());

        return parameters;
    }

    private static double[] getKspKdp(double mp) {
        if (mp > 200) {
            return new double[] { 0.75, 0.50 };
        } else if (200 >= mp && mp > 150) {
            return new double[] { 0.82, 0.29 };
        } else if (150 >= mp && mp > 135) {
            return new double[] { 0.85, 0.45 };
        } else if (135 >= mp && mp > 120) {
            return new double[] { 0.78, 0.52 };
        } else if (120 >= mp && mp > 110) {
            return new double[] { 0.80, 0.57 };
        } else if (110 >= mp && mp > 70) {
            return new double[] { 0.64, 0.64 };
        } else {
            return new double[] { 0.50, 0.50 };
        }
    }

    // 从一个数组中减去另一个数组的每个对应元素
    private static double[] subtractArrays(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must have the same length.");
        }
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }
}
