package utils;

import java.util.Arrays;

public class Stats {
    // Function to give
// index of the median
    static int median(double a[],
                      int l, int r)
    {
        int n = r - l + 1;
        n = (n + 1) / 2 - 1;
        return n + l;
    }

    // Function to
// calculate IQR
    public static double IQR(double [] a, int n)
    {
        Arrays.sort(a);

        // Index of median
        // of entire data
        int mid_index = median(a, 0, n);

        // Median of first half
        double Q1 = a[median(a, 0,
                mid_index)];

        // Median of second half
        double Q3 = a[median(a,
                mid_index + 1, n)];

        // IQR calculation
        return (Q3 - Q1);
    }

}
