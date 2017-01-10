package chainj.protocol.vm;

import chainj.math.checked.Checked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by sbwdlihao on 07/01/2017.
 */
class Stack {

    static final Function<VirtualMachine, Void> opToAltStack = vm -> {
        vm.applyCost(2);
        // no standard memory cost accounting here
        vm.moveFromDataToAlt();
        return null;
    };

    static final Function<VirtualMachine, Void> opFromAltStack = vm -> {
        vm.applyCost(2);
        // no standard memory cost accounting here
        vm.moveFromAltToData();
        return null;
    };

    static final Function<VirtualMachine, Void> opDrop = vm -> {
        nDrop(vm, 1);
        return null;
    };

    static final Function<VirtualMachine, Void> op2Drop = vm -> {
        nDrop(vm, 2);
        return null;
    };

    static final Function<VirtualMachine, Void> opDup = vm -> {
        nDup(vm, 1);
        return null;
    };

    static final Function<VirtualMachine, Void> op2Dup = vm -> {
        nDup(vm, 2);
        return null;
    };

    static final Function<VirtualMachine, Void> op3Dup = vm -> {
        nDup(vm, 3);
        return null;
    };

    static final Function<VirtualMachine, Void> opIfDup = vm -> {
        vm.applyCost(1);
        byte[] item = vm.top();
        if (Types.asBool(item)) {
            vm.push(item, false);
        }
        return null;
    };

    static final Function<VirtualMachine, Void> opOver = vm -> {
        nOver(vm, 1);
        return null;
    };

    static final Function<VirtualMachine, Void> op2Over = vm -> {
        nOver(vm, 2);
        return null;
    };

    static final Function<VirtualMachine, Void> opRot = vm -> {
        vm.applyCost(2);
        rot(vm, 3);
        return null;
    };

    static final Function<VirtualMachine, Void> op2Rot = vm -> {
        vm.applyCost(2);
        int dSize = vm.getDataStack().size();
        if (dSize < 6) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        List<byte[]> newStack = new ArrayList<>();
        newStack.addAll(vm.getDataStack().subList(0, dSize - 6));
        newStack.addAll(vm.getDataStack().subList(dSize - 4, dSize));
        newStack.add(vm.getDataStack().get(dSize - 6));
        newStack.add(vm.getDataStack().get(dSize - 5));
        vm.setDataStack(newStack);
        return null;
    };

    static final Function<VirtualMachine, Void> opSwap = vm -> {
        nSwap(vm, 1);
        return null;
    };

    static final Function<VirtualMachine, Void> op2Swap = vm -> {
        nSwap(vm, 2);
        return null;
    };

    static final Function<VirtualMachine, Void> opDepth = vm -> {
        vm.applyCost(1);
        vm.pushInt64(vm.getDataStack().size(), false);
        return null;
    };

    static final Function<VirtualMachine, Void> opNip = vm -> {
        vm.applyCost(1);
        byte[] top = vm.top();
        // temporarily pop off the top value with no standard memory accounting
        vm.setDataStack(new ArrayList<>(vm.getDataStack().subList(0, vm.getDataStack().size() - 1)));
        vm.pop(false);
        // now put the top item back
        vm.getDataStack().add(top);
        return null;
    };

    static final Function<VirtualMachine, Void> opPick = vm -> {
        vm.applyCost(2);
        long n = vm.popInt64(false);
        if (n < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        long off;
        try {
            off = Checked.addInt64(n, 1);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        if (vm.getDataStack().size() < off) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        vm.push(vm.getDataStack().get(vm.getDataStack().size() - (int)off), false);
        return null;
    };

    static final Function<VirtualMachine, Void> opRoll = vm -> {
        vm.applyCost(2);
        long n = vm.popInt64(false);
        if (n < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        long off;
        try {
            off = Checked.addInt64(n, 1);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        rot(vm, off);
        return null;
    };

    static final Function<VirtualMachine, Void> opTuck = vm -> {
        vm.applyCost(1);
        int dSize = vm.getDataStack().size();
        if (dSize < 2) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        List<byte[]> top2 = vm.getDataStack().subList(dSize - 2, dSize);
        // temporarily remove the top two items without standard memory accounting
        vm.setDataStack(new ArrayList<>(vm.getDataStack().subList(0, dSize - 2)));
        vm.push(top2.get(1), false);
        vm.getDataStack().addAll(top2);
        return null;
    };

    private static void nDrop(VirtualMachine vm, int n) {
        vm.applyCost(n);
        for (int i = 0; i < n; i++) {
            vm.pop(false);
        }
    }

    private static void nDup(VirtualMachine vm, int n) {
        vm.applyCost(n);
        if (vm.getDataStack().size() < n) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        for (int i = 0; i < n; i++) {
            vm.push(vm.getDataStack().get(vm.getDataStack().size() - n), false);
        }
    }

    private static void nOver(VirtualMachine vm, int n) {
        vm.applyCost(n);
        if (vm.getDataStack().size() < n * 2) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        for (int i = 0; i < n; i++) {
            vm.push(vm.getDataStack().get(vm.getDataStack().size() - n * 2), false);
        }
    }

    private static void nSwap(VirtualMachine vm, int n) {
        vm.applyCost(n);
        int dSize = vm.getDataStack().size();
        if (dSize < n * 2) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        byte[][] items = new byte[n * 2][];
        for (int i = 0; i < items.length; i++) {
            items[i] = vm.getDataStack().get(dSize - items.length + i);
        }
        for (int i = 0; i < items.length - n; i++) {
            vm.getDataStack().set(dSize - items.length + i, items[n + i]);
        }
        for (int i = 0; i < n; i++) {
            vm.getDataStack().set(dSize - n + i, items[i]);
        }
    }

    private static void rot(VirtualMachine vm, long n) {
        if (n < 1) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        if (vm.getDataStack().size() < n) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        int index = vm.getDataStack().size() - (int)n;
        List<byte[]> newStack = new ArrayList<>();
        newStack.addAll(vm.getDataStack().subList(0, index));
        newStack.addAll(vm.getDataStack().subList(index + 1, vm.getDataStack().size()));
        newStack.add(vm.getDataStack().get(index));
        vm.setDataStack(newStack);
    }
}
