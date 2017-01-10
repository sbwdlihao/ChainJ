package chainj.protocol.vm;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by sbwdlihao on 07/01/2017.
 */
class Bitwise {

    static final Function<VirtualMachine, Void> opInvert = vm -> {
        vm.applyCost(1);
        byte[] top = vm.top();
        vm.applyCost(top.length);
        // Could rewrite top in place but maybe it's a shared data
        // structure?
        byte[] newTop = new byte[top.length];
        for (int i = 0; i < top.length; i++) {
            newTop[i] = (byte)~top[i];
        }
        vm.getDataStack().set(vm.getDataStack().size() - 1, newTop);
        return null;
    };

    static final Function<VirtualMachine, Void> opAnd = vm -> {
        vm.applyCost(1);
        byte[] b = vm.pop(true);
        byte[] a = vm.pop(true);
        int min = b.length;
        int max = a.length;
        if (min > max) {
            min = a.length;
        }
        vm.applyCost(min);
        byte[] res = new byte[min];
        for (int i = 0; i < min; i++) {
            res[i] = (byte) (a[i] & b[i]);
        }
        vm.push(res, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opOr = vm -> {
        doOr(vm, false);
        return null;
    };

    static final Function<VirtualMachine, Void> opXor = vm -> {
        doOr(vm, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opEqual = vm -> {
        vm.pushBool(doEqual(vm), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opEqualVerify = vm -> {
        boolean res = doEqual(vm);
        if (!res) {
            throw new VMRunTimeException(Errors.ErrVerifyFailed);
        }
        return null;
    };

    private static void doOr(VirtualMachine vm, boolean xor) {
        vm.applyCost(1);
        byte[] b = vm.pop(true);
        byte[] a = vm.pop(true);
        int min = b.length;
        int max = a.length;
        if (min > max) {
            max = b.length;
        }
        vm.applyCost(max);
        byte[] res = new byte[max];
        for (int i = 0; i < res.length; i++) {
            byte aByte, bByte, resByte;
            if (i >= a.length) {
                aByte = 0;
            } else {
                aByte = a[i];
            }
            if (i >= b.length) {
                bByte = 0;
            } else {
                bByte = b[i];
            }
            if (xor) {
                resByte = (byte) (aByte ^ bByte);
            } else {
                resByte = (byte) (aByte | bByte);
            }
            res[i] = resByte;
        }
        vm.push(res, true);
    }

    private static boolean doEqual(VirtualMachine vm) {
        vm.applyCost(1);
        byte[] b = vm.pop(true);
        byte[] a = vm.pop(true);
        int min = b.length;
        int max = a.length;
        if (min > max) {
            min = a.length;
        }
        vm.applyCost(min);
        return Arrays.equals(a, b);
    }
}
