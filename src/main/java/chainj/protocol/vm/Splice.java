package chainj.protocol.vm;

import chainj.math.checked.Checked;
import com.google.common.primitives.Bytes;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by sbwdlihao on 07/01/2017.
 */
class Splice {

    static final Function<VirtualMachine, Void> opCat = vm -> {
        vm.applyCost(4);
        byte[] b = vm.pop(true);
        byte[] a = vm.pop(true);
        int lens = a.length + b.length;
        vm.applyCost(lens);
        vm.deferCost(-lens);
        vm.push(Bytes.concat(a, b), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opSubStr = vm -> {
        vm.applyCost(4);
        long size = vm.popInt64(true);
        if (size < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        vm.applyCost(size);
        vm.deferCost(-size);
        long offset = vm.popInt64(true);
        if (offset < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        byte[] str = vm.pop(true);
        long end = Checked.addInt64(offset, size);
        if (end > str.length) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        vm.push(Arrays.copyOfRange(str, (int)offset, (int)end), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opLeft = vm -> {
        vm.applyCost(4);
        long size = vm.popInt64(true);
        if (size < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        vm.applyCost(size);
        vm.deferCost(-size);
        byte[] str = vm.pop(true);
        if (size > str.length) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        vm.push(Arrays.copyOfRange(str, 0, (int)size), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opRight = vm -> {
        vm.applyCost(4);
        long size = vm.popInt64(true);
        if (size < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        vm.applyCost(size);
        vm.deferCost(-size);
        byte[] str = vm.pop(true);
        if (size > str.length) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        vm.push(Arrays.copyOfRange(str, str.length - (int)size, str.length), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opSize = vm -> {
        vm.applyCost(1);
        byte[] str = vm.top();
        vm.pushInt64(str.length, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opCatPushdata = vm -> {
        vm.applyCost(4);
        byte[] b = vm.pop(true);
        byte[] a = vm.pop(true);
        int lens = a.length + b.length;
        vm.applyCost(lens);
        vm.deferCost(-lens);
        vm.push(Bytes.concat(a, PushData.pushDataBytes(b)), true);
        return null;
    };
}
