package com.lihao.math.checked;

/**
 * Created by sbwdlihao on 09/12/2016.
 */
public class Checked {

    // a + b
    public static long addInt64(long a, long b) {
        return Math.addExact(a, b);
    }

    // a - b
    public static long subInt64(long a, long b) {
        return Math.subtractExact(a, b);
    }

    // a * b
    public static long mulInt64(long a, long b) {
        return Math.multiplyExact(a, b);
    }

    // a / b
    public static long divInt64(long a, long b) {
        if (b == 0 || a == Long.MIN_VALUE && b == -1) {
            throw  new ArithmeticException("long overflow");
        }
        return a / b;
    }

    // a % b
    public static long modInt64(long a, long b) {
        if (b == 0 || a == Long.MIN_VALUE && b == -1) {
            throw  new ArithmeticException("long overflow");
        }
        return a % b;
    }

    // -a
    public static long negateInt64(long a) {
        return Math.negateExact(a);
    }

    // a << b
    public static long lShiftInt64(long a, long b) {
        if (b < 0 || b >= 64) {
            throw new ArithmeticException("long overflow");
        }
        if ((a >= 0 && a > Long.MAX_VALUE >> b) || (a < 0 && a < Long.MIN_VALUE >> b)) {
            throw new ArithmeticException("long overflow");
        }
        return a << b;
    }
}
