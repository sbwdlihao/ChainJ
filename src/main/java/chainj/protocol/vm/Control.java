package chainj.protocol.vm;

import chainj.util.ByteBufferUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by sbwdlihao on 04/01/2017.
 */
class Control {

    static final Function<VirtualMachine, Void> opVerify = vm -> {
        vm.applyCost(1);
        byte[] p = vm.pop(true);
        if (!Types.asBool(p)) {
            throw new VMRunTimeException(Errors.ErrVerifyFailed);
        }
        return null;
    };

    static final Function<VirtualMachine, Void> opFail = vm -> {
        vm.applyCost(1);
        throw new VMRunTimeException(Errors.ErrReturn);
    };

    static final Function<VirtualMachine, Void> opCheckPredicate = vm -> {
        vm.applyCost(256);
        vm.deferCost(-256 + 64); // get most of that cost back at the end
        long limit = vm.popInt64(true); // 子虚拟机的runLimit
        byte[] predicate = vm.pop(true); // 子虚拟机的program
        long n = vm.popInt64(true); // 子虚拟机的数据栈长度
        if (limit < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        int l = vm.getDataStack().size();
        if (n > l) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        if (limit == 0) {
            limit = vm.getRunLimit();
        }
        vm.applyCost(limit);

        int i = (int)(l - n);
        VirtualMachine childVM = new VirtualMachine(predicate, vm.getMainProgram(), limit, vm.getDepth() + 1,
                new ArrayList<>(vm.getDataStack().subList(i, l)), vm.getTx(), vm.getInputIndex(), vm.getSigHasher());
        vm.setDataStack(new ArrayList<>(vm.getDataStack().subList(0, i)));
        boolean ok = false;
        Exception childErr = null;
        try {
            ok = childVM.run();
        } catch (Exception e) {
            childErr = e;
        }

        vm.deferCost(-childVM.getRunLimit());
        vm.deferCost(-stackCost(childVM.getDataStack()));
        vm.deferCost(-stackCost(childVM.getAltStack()));

        vm.pushBool(childErr == null && ok, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opJump = vm -> {
        vm.applyCost(1);
        int address = ByteBufferUtil.b2IntLE(vm.getData());
        vm.setNextProgramIndex(address);
        return null;
    };

    static final Function<VirtualMachine, Void> opJumpIf = vm -> {
        vm.applyCost(1);
        byte[] b = vm.pop(true);
        if (Types.asBool(b)) {
            int address = ByteBufferUtil.b2IntLE(vm.getData());
            vm.setNextProgramIndex(address);
        }
        return null;
    };

    private static long stackCost(List<byte[]> stack) {
        long result = stack.size() * 8;
        for (byte[] bytes : stack) {
            result += bytes.length;
        }
        return result;
    }
}
