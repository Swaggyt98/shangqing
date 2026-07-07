package com.zosoftware.solid.utils;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import uk.me.berndporr.iirj.Butterworth;

public class MathUtils {

    public static List<Integer> findPeaks(double[] data, double threshold, int distance) {
        List<Integer> peaksIndices = new ArrayList<>();

        boolean isPeak = false;
        int peakStartIndex = 0;

        for (int i = 1; i < data.length; i++) {
            if (data[i] > data[i - 1]) { // 上升趋势
                if (!isPeak) { // 如果不是峰值，标记开始
                    peakStartIndex = i - 1;
                    isPeak = true;
                }
            } else if (data[i] < data[i - 1]) { // 下降趋势
                if (isPeak) { // 如果是峰值，计算峰值位置
                    int peakIndex = findPeakIndex(data, peakStartIndex, i - 1);
                    if (data[peakIndex] >= threshold) { // 判断是否满足阈值条件
                        peaksIndices.add(peakIndex);
                    }
                    isPeak = false;
                    i += distance; // 跳过 distance 距离以防止重叠峰
                }
            }
        }

        return peaksIndices;
    }

    private static int findPeakIndex(double[] data, int startIndex, int endIndex) {
        int peakIndex = startIndex;
        for (int i = startIndex + 1; i <= endIndex; i++) {
            if (data[i] > data[peakIndex]) {
                peakIndex = i;
            }
        }
        return peakIndex;
    }

    public static double[] butter_bandpass_filter(double[] data, double lowcut, double highcut, double fs, int order){
        Butterworth butterworth = new Butterworth();
        double widthFrequency = highcut - lowcut;
        double centerFrequency = (highcut + lowcut) / 2;
        butterworth.bandPass(order, fs, centerFrequency, widthFrequency);
        double[] list = new double[data.length];
        int in = 0;
        for(double v : data){
            double f = butterworth.filter(v);
            list[in] = f;
            in++;
        }
        return list;
    }

    public static double[] butter_low_filter(double[] data, double lowpass, double fs, int order){
        Butterworth butterworth = new Butterworth();
        butterworth.lowPass(order, fs, lowpass);
        double[] list = new double[data.length];
        int in = 0;
        for(double v : data){
            double f = butterworth.filter(v);
            list[in] = f;
            in++;
        }
        return list;
    }

    public static double[] savgolFilter(double[] data, int windowLength, int polynomialOrder) {
        if (windowLength % 2 == 0 || windowLength < 1) {
            throw new IllegalArgumentException("Window length must be an odd positive number.");
        }
        if (polynomialOrder < 1 || polynomialOrder > windowLength - 1) {
            throw new IllegalArgumentException("Polynomial order must be a positive integer less than window length.");
        }

        double[] smoothedData = new double[data.length];

        int halfWin = windowLength / 2;
        for (int i = halfWin; i < data.length - halfWin; i++) {
            double[] coeffs = calculateSGCoefficients(windowLength, polynomialOrder, i);
            double sum = 0.0;
            for (int j = 0; j < windowLength; j++) {
                sum += coeffs[j] * data[i - halfWin + j];
            }
            smoothedData[i] = sum;
        }

        // 处理边界效应，直接复制边界点数据
        for (int i = 0; i < halfWin; i++) {
            smoothedData[i] = data[i];
            smoothedData[data.length - 1 - i] = data[data.length - 1 - i];
        }

        return smoothedData;
    }

    private static double[] calculateSGCoefficients(int windowLength, int polynomialOrder, int center) {
        double[] coeffs = new double[windowLength];
        int halfWin = windowLength / 2;

        // 计算最小二乘拟合系数
        for (int i = -halfWin; i <= halfWin; i++) {
            double[] y = new double[windowLength];
            for (int j = 0; j < windowLength; j++) {
                y[j] = Math.pow(j - halfWin, i);
            }

            double sum = 0.0;
            for (int j = 0; j < windowLength; j++) {
                sum += y[j] * y[j];
            }

            // 计算拟合系数
            for (int j = 0; j < windowLength; j++) {
                coeffs[j] += y[j] / sum;
            }
        }

        return coeffs;
    }

    public static double gaussian(double x, double a, double x0, double sigma) {
        return a * Math.exp(-(Math.pow(x - x0, 2) / (2 * Math.pow(sigma, 2))));
    }

    public static double max(List<Double> array){
        double max = array.get(0);
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i) > max) {
                max = array.get(i);
            }
        }
        return max;
    }

    public static double mean(List<Integer> data) {
        return data.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    public static double std(List<Integer> data) {
        double mean = mean(data);
        double variance = data.stream().mapToDouble(val -> Math.pow(val - mean, 2)).average().orElse(0.0);
        return Math.sqrt(variance);
    }

    public static double[] getDBP_SBPresult(List<Double> pressure_data) {

        double fs = 100.0;  //# 采样频率   改为了100hz
        double lowpass_cutoff = 0.8; //# 低通滤波截止频率
        double bandpass_lowcut = 0.8;  //# 带通滤波低截止频率
        double bandpass_highcut = 6.0 ; //# 带通滤波高截止频率

        double[] dc_component = butter_low_filter(
                pressure_data.stream().mapToDouble(Double::doubleValue).toArray(),
                lowpass_cutoff,
                fs,
                6);
        double[] ac_component = new double[pressure_data.size()];
        for (int i = 0; i < pressure_data.size(); i++) {
            ac_component[i] = pressure_data.get(i) - dc_component[i];
        }

        double[] ac_component_smoothed = savgolFilter(ac_component, 101, 3);
        double[] cleaned_ac_component = butter_bandpass_filter(ac_component_smoothed, bandpass_lowcut, bandpass_highcut, fs, 4);

        int trim_start = 200; // 根据新的采样率调整

        List<Double> trimmed_pressure_data = pressure_data.subList(trim_start, pressure_data.size());
        double[] trimmed_dc_component = Arrays.copyOfRange(dc_component, trim_start, dc_component.length);
        double[] trimmed_ac_component = Arrays.copyOfRange(ac_component, trim_start, ac_component.length);
        double[] trimmed_cleaned_ac_component = Arrays.copyOfRange(cleaned_ac_component, trim_start, cleaned_ac_component.length);

        List<Integer> peaks = findPeaks(trimmed_cleaned_ac_component, 0, 40); // 根据新的采样率调整

        List<Integer> trimmed_indices = new ArrayList<>();
        for (int i = trim_start; i < pressure_data.size(); i++) {
            trimmed_indices.add(i);
        }

        List<Integer> peak_x = peaks.stream().map(trimmed_indices::get).collect(Collectors.toList());
//        List<Double> peak_y = peaks.stream().map( trimmed_cleaned_ac_component::get).collect(Collectors.toList());
        List<Double> peak_y = new ArrayList<>();
        for (Integer i:peaks) {
            peak_y.add(trimmed_cleaned_ac_component[i]);
        }

        double max_peak_value = max(peak_y);
        double threshold = 0.3 * max_peak_value;
        List<Integer> filtered_peak_indices = new ArrayList<>();

        for (int i = 0; i < peak_y.size(); i++) {
            if (peak_y.get(i) > threshold) {
                filtered_peak_indices.add(i);
            }
        }

        List<Integer> filtered_peak_x = filtered_peak_indices.stream().map(peak_x::get).collect(Collectors.toList());
        List<Double> filtered_peak_y = filtered_peak_indices.stream().map(peak_y::get).collect(Collectors.toList());

        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < filtered_peak_x.size(); i++) {
            obs.add(filtered_peak_x.get(i), filtered_peak_y.get(i));
        }

        ParametricUnivariateFunction gaussian = new Gaussian.Parametric();
        AbstractCurveFitter fitter = SimpleCurveFitter.create(gaussian, new double[]{1, mean(filtered_peak_x), std(filtered_peak_x)});
        double[] popt = fitter.fit(obs.toList());

        double map_y = Arrays.stream(trimmed_cleaned_ac_component).max().orElse(0);
        int map_index = Arrays.stream(trimmed_cleaned_ac_component).boxed().collect(Collectors.toList()).indexOf(map_y);
        double map_x = map_index + trim_start;

        double mp = trimmed_dc_component[map_index];

        double[] k_values = get_ksp_kdp(mp);
        double k_sp = k_values[0];
        double k_dp = k_values[1];

        int dbp_index = 0;
        for (int i = 0; i < map_index; i++) {
            if (trimmed_dc_component[i] >= k_dp * map_y) {
                dbp_index = i;
                break;
            }
        }

        int sbp_index = map_index;
        for (int i = map_index; i < trimmed_cleaned_ac_component.length; i++) {
            if (trimmed_dc_component[i] <= k_sp * map_y) {
                sbp_index = i;
                break;
            }
        }
        double dbp_x = dbp_index + trim_start;
        double sbp_x = sbp_index + trim_start;
        double[] result = new double[] {dbp_x,sbp_x};

        System.out.println("DBP: " + dbp_x);
        System.out.println("SBP: " + sbp_x);

        return result;
    }

    private static double[] get_ksp_kdp(double mp) {
        if (mp > 200) {
            return new double[]{0.50, 0.75};
        } else if (200 >= mp && mp > 150) {
            return new double[]{0.29, 0.82};
        } else if (150 >= mp && mp > 135) {
            return new double[]{0.45, 0.85};
        } else if (135 >= mp && mp > 120) {
            return new double[]{0.52, 0.78};
        } else if (120 >= mp && mp > 110) {
            return new double[]{0.57, 0.80};
        } else if (110 >= mp && mp > 70) {
            return new double[]{0.64, 0.64};
        } else {
            return new double[]{0.50, 0.50};
        }
    }
}
