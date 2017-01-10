package chainj.protocol.vm;

import chainj.protocol.bc.*;
import chainj.protocol.bc.txinput.EmptyTxInput;
import org.bouncycastle.util.encoders.Hex;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by sbwdlihao on 01/01/2017.
 */
public class VirtualMachine {

    static PrintStream traceOut;

    static final long initialRunLimit = 10000;

    private byte[] program = new byte[0]; // the program currently executing
    private byte[] mainProgram = new byte[0]; // the outermost program, returned by OP_PROGRAM
    private int programIndex;
    private int nextProgramIndex;
    private long runLimit;
    private long deferredCost;
    private boolean expansionReserved;

    // Stores the data parsed out of an opcode. Used as input to
    // data-pushing opcodes.
    private byte[] data = new byte[0];

    // CHECKPREDICATE spawns a child vm with depth+1
    private int depth;

    // In each of these stacks, stack[len(stack)-1] is the top element.
    private List<byte[]> dataStack = new ArrayList<>();
    private List<byte[]> altStack = new ArrayList<>();

    private Transaction tx;
    private int inputIndex;
    private SigHasher sigHasher;
    private Block block;

    void setProgram(byte[] program) {
        Objects.requireNonNull(program);
        this.program = program;
    }

    byte[] getMainProgram() {
        return mainProgram;
    }

    void setMainProgram(byte[] mainProgram) {
        Objects.requireNonNull(mainProgram);
        this.mainProgram = mainProgram;
    }

    void setProgramIndex(int programIndex) {
        if (programIndex < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        this.programIndex = programIndex;
    }

    void setNextProgramIndex(int nextProgramIndex) {
        if (nextProgramIndex < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        this.nextProgramIndex = nextProgramIndex;
    }

    long getRunLimit() {
        return runLimit;
    }

    void setRunLimit(long runLimit) {
        this.runLimit = runLimit;
    }

    public byte[] getData() {
        return data;
    }

    private void setData(byte[] data) {
        Objects.requireNonNull(data);
        this.data = data;
    }

    int getDepth() {
        return depth;
    }

    List<byte[]> getDataStack() {
        return dataStack;
    }

    void setDataStack(List<byte[]> dataStack) {
        Objects.requireNonNull(dataStack);
        this.dataStack = dataStack;
    }

    List<byte[]> getAltStack() {
        return altStack;
    }

    private void setAltStack(List<byte[]> altStack) {
        Objects.requireNonNull(altStack);
        this.altStack = altStack;
    }

    Transaction getTx() {
        return tx;
    }

    int getInputIndex() {
        return inputIndex;
    }

    SigHasher getSigHasher() {
        return sigHasher;
    }

    void setSigHasher(SigHasher sigHasher) {
        this.sigHasher = sigHasher;
    }

    public Block getBlock() {
        return block;
    }

    VirtualMachine(long runLimit) {
        this.runLimit = runLimit;
    }

    VirtualMachine(long runLimit, byte[] program) {
        this.runLimit = runLimit;
        setProgram(program);
    }

    VirtualMachine(long runLimit, List<byte[]> dataStack) {
        this.runLimit = runLimit;
        setDataStack(dataStack);
    }

    VirtualMachine(long runLimit, byte[] program, List<byte[]> dataStack) {
        this.runLimit = runLimit;
        setProgram(program);
        setDataStack(dataStack);
    }

    VirtualMachine(long runLimit, byte[] program, Transaction tx) {
        this.runLimit = runLimit;
        setProgram(program);
        this.tx = tx;
    }

    VirtualMachine(long runLimit, byte[] program, boolean expansionReserved) {
        this.runLimit = runLimit;
        setProgram(program);
        this.expansionReserved = expansionReserved;
    }

    VirtualMachine(long runLimit, List<byte[]> dataStack, List<byte[]> altStack) {
        this.runLimit = runLimit;
        setDataStack(dataStack);
        setAltStack(altStack);
    }

    VirtualMachine(long runLimit, List<byte[]> dataStack, byte[] data) {
        this.runLimit = runLimit;
        setDataStack(dataStack);
        setData(data);
    }

    VirtualMachine(long runLimit, byte[] program, List<byte[]> dataStack, int programIndex, int nextProgramIndex, byte[] data) {
        this.runLimit = runLimit;
        setProgram(program);
        setDataStack(dataStack);
        setProgramIndex(programIndex);
        setNextProgramIndex(nextProgramIndex);
        setData(data);
    }

    VirtualMachine(long runLimit, byte[] program, List<byte[]> dataStack, int programIndex, int nextProgramIndex, byte[] data, long deferredCost) {
        this.runLimit = runLimit;
        setProgram(program);
        setDataStack(dataStack);
        setProgramIndex(programIndex);
        setNextProgramIndex(nextProgramIndex);
        setData(data);
        this.deferredCost = deferredCost;
    }

    VirtualMachine(long runLimit, int programIndex, int nextProgramIndex, byte[] data) {
        this.runLimit = runLimit;
        setProgramIndex(programIndex);
        setNextProgramIndex(nextProgramIndex);
        setData(data);
    }

    VirtualMachine(long runLimit, long deferredCost, List<byte[]> dataStack) {
        this.runLimit = runLimit;
        this.deferredCost = deferredCost;
        setDataStack(dataStack);
    }

    VirtualMachine(long runLimit, Block block) {
        this.runLimit = runLimit;
        this.block = block;
    }

    VirtualMachine(long runLimit, Block block, byte[] program) {
        this.runLimit = runLimit;
        this.block = block;
        setProgram(program);
    }

    VirtualMachine(long runLimit, Block block, byte[] program, byte[] mainProgram, boolean expansionReserved) {
        this.runLimit = runLimit;
        this.block = block;
        setProgram(program);
        setMainProgram(mainProgram);
        this.expansionReserved = expansionReserved;
    }

    VirtualMachine(long runLimit, Block block, List<byte[]> dataStack) {
        this.runLimit = runLimit;
        this.block = block;
        setDataStack(dataStack);
    }

    VirtualMachine(long runLimit, Transaction tx, SigHasher sigHasher) {
        this.runLimit = runLimit;
        this.tx = tx;
        setSigHasher(sigHasher);
    }

    VirtualMachine(long runLimit, Transaction tx, SigHasher sigHasher, int inputIndex, byte[] program, byte[] mainProgram, boolean expansionReserved) {
        this.runLimit = runLimit;
        this.tx = tx;
        setSigHasher(sigHasher);
        this.inputIndex = inputIndex;
        setProgram(program);
        setMainProgram(mainProgram);
        this.expansionReserved = expansionReserved;
    }

    VirtualMachine(long runLimit, Transaction tx) {
        this.runLimit = runLimit;
        this.tx = tx;
    }

    VirtualMachine(long runLimit, Transaction tx, List<byte[]> dataStack) {
        this.runLimit = runLimit;
        this.tx = tx;
        setDataStack(dataStack);
    }

    VirtualMachine(long runLimit, Transaction tx, byte[] mainProgram) {
        this.runLimit = runLimit;
        this.tx = tx;
        setMainProgram(mainProgram);
    }

    VirtualMachine(long runLimit, Transaction tx, byte[] mainProgram, int inputIndex) {
        this.runLimit = runLimit;
        this.tx = tx;
        setMainProgram(mainProgram);
        this.inputIndex = inputIndex;
    }

    VirtualMachine(long runLimit, long deferredCost, Transaction tx, List<byte[]> dataStack) {
        this.runLimit = runLimit;
        this.deferredCost = deferredCost;
        this.tx = tx;
        setDataStack(dataStack);
    }

    VirtualMachine(long runLimit, long deferredCost, Transaction tx, List<byte[]> dataStack, int inputIndex) {
        this.runLimit = runLimit;
        this.deferredCost = deferredCost;
        this.tx = tx;
        setDataStack(dataStack);
        this.inputIndex = inputIndex;
    }

    VirtualMachine(long runLimit, Transaction tx, int inputIndex, byte[] program) {
        this.runLimit = runLimit;
        this.tx = tx;
        this.inputIndex = inputIndex;
        setProgram(program);
    }

    VirtualMachine(long runLimit, int programIndex, int nextProgramIndex, long deferredCost, List<byte[]> dataStack, byte[] data) {
        this.runLimit = runLimit;
        setProgramIndex(programIndex);
        setNextProgramIndex(nextProgramIndex);
        this.deferredCost = deferredCost;
        setDataStack(dataStack);
        setData(data);
    }

    VirtualMachine(byte[] program, byte[] mainProgram, long runLimit, int depth, List<byte[]> dataStack, Transaction tx, int inputIndex, SigHasher sigHasher) {
        setProgram(program);
        setMainProgram(mainProgram);
        this.runLimit = runLimit;
        this.depth = depth;
        setDataStack(dataStack);
        this.tx = tx;
        this.inputIndex = inputIndex;
        setSigHasher(sigHasher);
    }

    boolean run() {
        for (programIndex = 0; programIndex < program.length;) { // handle programIndex updates in step
            step();
        }
        return dataStack.size() > 0 && Types.asBool(dataStack.get(dataStack.size() - 1));
    }

    void step() {
        Instruction inst = OPS.parseOp(program, programIndex);
        nextProgramIndex = programIndex + inst.getLen();

        OPInfo opInfo = OPInfo.getOPInfo(inst.getOp());

        if (traceOut != null) {
            String opName = opInfo.getName();
            traceOut.append(String.format("vm depth %d programIndex %d limit %d %s", depth, programIndex, runLimit, opName));
            if (inst.getData().length > 0) {
                traceOut.append(Hex.toHexString(inst.getData()));
            }
            traceOut.append("\n");
        }

        if (OPInfo.isExpansion(inst.getOp())) {
            if (expansionReserved) {
                throw new VMRunTimeException(Errors.ErrDisallowedOpcode);
            }
            programIndex = nextProgramIndex;
            applyCost(1);
            return;
        }

        deferredCost = 0;
        setData(inst.getData());
        opInfo.getFn().apply(this);
        applyCost(deferredCost);
        programIndex = nextProgramIndex;

        if (traceOut != null) {
            for (int i = dataStack.size() - 1; i >= 0; i--) {
                byte[] data = dataStack.get(i);
                traceOut.append(String.format("  stack %d: %s\n", dataStack.size() - 1 - i, Hex.toHexString(data)));
            }
        }
    }

    // positive cost decreases runLimit, negative cost increases it
    void applyCost(long n) {
        if (n > runLimit) {
            throw new VMRunTimeException(Errors.ErrRunLimitExceeded);
        }
        runLimit -= n;
    }

    void pushBool(boolean b, boolean deferred) {
        push(Types.boolBytes(b), deferred);
    }

    void pushInt64(long n, boolean deferred) {
        push(Types.int64Bytes(n), deferred);
    }

    void push(byte[] data, boolean deferred) {
        long cost = 8L + data.length;
        if (deferred) {
            deferCost(cost);
        } else {
            applyCost(cost);
        }
        dataStack.add(data);
    }

    byte[] pop(boolean deferred) {
        if (dataStack.size() == 0) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        byte[] res = dataStack.get(dataStack.size() - 1);
        dataStack.remove(dataStack.size() - 1);
        long cost = 8L + res.length;
        if (deferred) {
            deferCost(-cost);
        } else {
            runLimit += cost;
        }
        return res;
    }

    byte[] top() {
        if (dataStack.size() == 0) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        return dataStack.get(dataStack.size() - 1);
    }

    long popInt64(boolean deferred) {
        byte[] bytes = pop(deferred);
        return Types.asInt64(bytes);
    }

    void popXYInt64(long[] x, long[] y) {
        popXYInt64(x, y, 2);
    }

    void popXYInt64(long[] x, long[] y, long cost) {
        applyCost(cost);
        y[0] = popInt64(true);
        x[0] = popInt64(true);
    }

    void popXYBool(boolean[] x, boolean[] y) {
        applyCost(2);
        y[0] = popBool();
        x[0] = popBool();
    }

    private boolean popBool() {
        return Types.asBool(pop(true));
    }

    void moveFromDataToAlt() {
        if (dataStack.size() == 0) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        altStack.add(dataStack.get(dataStack.size() - 1));
        dataStack.remove(dataStack.size() - 1);
    }

    void moveFromAltToData() {
        if (altStack.size() == 0) {
            throw new VMRunTimeException(Errors.ErrDataStackUnderflow);
        }
        dataStack.add(altStack.get(altStack.size() - 1));
        altStack.remove(altStack.size() - 1);
    }

    void deferCost(long n) {
        deferredCost += n;
    }

    static boolean verifyTxInput(Transaction tx, int inputIndex) {
        if (inputIndex < 0 || inputIndex > tx.getInputs().length) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        TxInput input = tx.getInputs()[inputIndex];
        if (input instanceof EmptyTxInput) {
            throw new VMRunTimeException(Errors.ErrUnsupportedTx);
        }

        boolean expansionReserved = tx.getVersion() == 1;
        SigHasher sigHasher = new SigHasher(tx.getTxData());

        if (input.vmVersion() != 1) {
            throw new VMRunTimeException(Errors.ErrUnsupportedVM);
        }

        VirtualMachine vm = new VirtualMachine(initialRunLimit, tx, sigHasher, inputIndex,
                input.vmProgram(), input.vmProgram(), expansionReserved);
        for (byte[] arg : input.arguments()) {
            vm.push(arg, false);
        }
        return vm.run();
    }

    static boolean verifyBlockHeader(BlockHeader prev, Block block) {
        VirtualMachine vm = new VirtualMachine(initialRunLimit, block,
                prev.getConsensusProgram(), prev.getConsensusProgram(), true);
        for (byte[] arg : block.getWitness()) {
            vm.push(arg, false);
        }
        return vm.run();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualMachine that = (VirtualMachine) o;

        if (programIndex != that.programIndex) return false;
        if (nextProgramIndex != that.nextProgramIndex) return false;
        if (runLimit != that.runLimit) return false;
        if (deferredCost != that.deferredCost) return false;
        if (expansionReserved != that.expansionReserved) return false;
        if (depth != that.depth) return false;
        if (!Arrays.equals(program, that.program)) return false;
        if (!Arrays.equals(mainProgram, that.mainProgram)) return false;
        if (!Arrays.equals(data, that.data)) return false;
        // 对于List<byte[]>这样的类型使用equals进行比较的话会得到false，因为byte[]的比较是引用而不是里面的值
        if ((dataStack == null && that.dataStack != null) || (dataStack != null && that.dataStack == null) ||
                (dataStack != null && !Arrays.deepEquals(dataStack.toArray(), that.dataStack.toArray()))) {
            return false;
        }
        if ((altStack == null && that.altStack != null) || (altStack != null && that.altStack == null) ||
                (altStack != null && !Arrays.deepEquals(altStack.toArray(), that.altStack.toArray()))) {
            return false;
        }
        if (tx != null ? !tx.equals(that.tx) : that.tx != null) return false;
        if (sigHasher != null ? !sigHasher.equals(that.sigHasher) : that.sigHasher != null) return false;
        return block != null ? block.equals(that.block) : that.block == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(program);
        result = 31 * result + Arrays.hashCode(mainProgram);
        result = 31 * result + programIndex;
        result = 31 * result + nextProgramIndex;
        result = 31 * result + (int) (runLimit ^ (runLimit >>> 32));
        result = 31 * result + (int) (deferredCost ^ (deferredCost >>> 32));
        result = 31 * result + (expansionReserved ? 1 : 0);
        result = 31 * result + Arrays.hashCode(data);
        result = 31 * result + depth;
        result = 31 * result + (dataStack != null ? dataStack.hashCode() : 0);
        result = 31 * result + (altStack != null ? altStack.hashCode() : 0);
        result = 31 * result + (tx != null ? tx.hashCode() : 0);
        result = 31 * result + (sigHasher != null ? sigHasher.hashCode() : 0);
        result = 31 * result + (block != null ? block.hashCode() : 0);
        return result;
    }
}
