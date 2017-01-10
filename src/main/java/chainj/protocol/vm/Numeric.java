package chainj.protocol.vm;

import chainj.math.checked.Checked;

import java.util.function.Function;

/**
 * Created by sbwdlihao on 04/01/2017.
 */
class Numeric {

    static final Function<VirtualMachine, Void> op1Add = vm -> {
        vm.applyCost(2);
        long n = vm.popInt64(true);
        try {
            n = Checked.addInt64(n, 1);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        vm.pushInt64(n, true);
        return null;
    };

    static final Function<VirtualMachine, Void> op1Sub = vm -> {
        vm.applyCost(2);
        long n = vm.popInt64(true);
        try {
            n = Checked.subInt64(n, 1);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        vm.pushInt64(n, true);
        return null;
    };

    static final Function<VirtualMachine, Void> op2Mul = vm -> {
        vm.applyCost(2);
        long n = vm.popInt64(true);
        try {
            n = Checked.mulInt64(n, 2);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        vm.pushInt64(n, true);
        return null;
    };

    static final Function<VirtualMachine, Void> op2Div = vm -> {
        vm.applyCost(2);
        long n = vm.popInt64(true);
        vm.pushInt64(n>>1, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opNegate = vm -> {
        vm.applyCost(2);
        long n = vm.popInt64(true);
        try {
            n = Checked.negateInt64(n);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        vm.pushInt64(n, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opAbs = vm -> {
        vm.applyCost(2);
        long n = vm.popInt64(true);
        try {
            n = Checked.negateInt64(n);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        if (n < 0) {
            n = -n;
        }
        vm.pushInt64(n, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opNot = vm -> {
        vm.applyCost(2);
        long n = vm.popInt64(true);
        vm.pushBool(n == 0, true);
        return null;
    };

    static final Function<VirtualMachine, Void> op0NotEqual = vm -> {
        vm.applyCost(2);
        long n = vm.popInt64(true);
        vm.pushBool(n != 0, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opAdd = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        long res;
        try {
            res = Checked.addInt64(x[0], y[0]);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        vm.pushInt64(res, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opSub = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        long res;
        try {
            res = Checked.subInt64(x[0], y[0]);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        vm.pushInt64(res, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opMul = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y, 8);
        long res;
        try {
            res = Checked.mulInt64(x[0], y[0]);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        vm.pushInt64(res, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opDiv = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y, 8);
        if (y[0] == 0) {
            throw new VMRunTimeException(Errors.ErrDivZero);
        }
        long res;
        try {
            res = Checked.divInt64(x[0], y[0]);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        vm.pushInt64(res, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opMod = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y, 8);
        if (y[0] == 0) {
            throw new VMRunTimeException(Errors.ErrDivZero);
        }
        long res;
        try {
            res = Checked.floorModInt64(x[0], y[0]);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        vm.pushInt64(res, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opLShift = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y, 8);
        if (x[0] == 0 || y[0] == 0) {
            vm.pushInt64(x[0], true);
            return null;
        }
        long res;
        try {
            res = Checked.lShiftInt64(x[0], y[0]);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrRange);
        }
        vm.pushInt64(res, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opRShift = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y, 8);
        if (y[0] < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        vm.pushInt64(x[0]>>y[0], true);
        return null;
    };

    static final Function<VirtualMachine, Void> opBoolAnd = vm -> {
        boolean[] y = new boolean[1];
        boolean[] x = new boolean[1];
        vm.popXYBool(x, y);
        vm.pushBool(x[0] && y[0], true);
        return null;
    };

    static final Function<VirtualMachine, Void> opBoolOr = vm -> {
        boolean[] y = new boolean[1];
        boolean[] x = new boolean[1];
        vm.popXYBool(x, y);
        vm.pushBool(x[0] || y[0], true);
        return null;
    };

    static final Function<VirtualMachine, Void> opNumEqual = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        vm.pushBool(x[0] == y[0], true);
        return null;
    };

    static final Function<VirtualMachine, Void> opNumEqualVerify = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        if (x[0] != y[0]) {
            throw new VMRunTimeException(Errors.ErrVerifyFailed);
        }
        return null;
    };

    static final Function<VirtualMachine, Void> opNumNotEqual = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        vm.pushBool(x[0] != y[0], true);
        return null;
    };

    static final Function<VirtualMachine, Void> opLessThan = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        vm.pushBool(x[0] < y[0], true);
        return null;
    };

    static final Function<VirtualMachine, Void> opGreaterThan = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        vm.pushBool(x[0] > y[0], true);
        return null;
    };

    static final Function<VirtualMachine, Void> opLessThanOrEqual = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        vm.pushBool(x[0] <= y[0], true);
        return null;
    };

    static final Function<VirtualMachine, Void> opGreaterThanOrEqual = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        vm.pushBool(x[0] >= y[0], true);
        return null;
    };

    static final Function<VirtualMachine, Void> opMin = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        vm.pushInt64(Math.min(x[0], y[0]), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opMax = vm -> {
        long[] y = new long[1];
        long[] x = new long[1];
        vm.popXYInt64(x, y);
        vm.pushInt64(Math.max(x[0], y[0]), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opWithin = vm -> {
        vm.applyCost(4);
        long max = vm.popInt64(true);
        long min = vm.popInt64(true);
        long x = vm.popInt64(true);
        vm.pushBool(x >= min && x < max, true);
        return null;
    };
}
