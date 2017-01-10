package chainj.protocol.vm;

import chainj.crypto.Sha3;
import chainj.protocol.bc.AssetID;
import chainj.protocol.bc.Outpoint;
import chainj.protocol.bc.TxInput;
import chainj.protocol.bc.TxOutput;
import chainj.protocol.bc.txinput.IssuanceInput;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by sbwdlihao on 08/01/2017.
 */
class Introspection {

    static final Function<VirtualMachine, Void> opCheckOutput = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(16);
        byte[] program = vm.pop(true);
        long vmVersion = vm.popInt64(true);
        if (vmVersion < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        byte[] assertID = vm.pop(true);
        long amount = vm.popInt64(true);
        if (amount < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        byte[] refDataHash = vm.pop(true);
        long index = vm.popInt64(true);
        if (index < 0 || vm.getTx().getOutputs().length <= index) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        TxOutput o = vm.getTx().getOutputs()[(int)index];
        if (o.getAssetVersion() != 1 ||
                o.getAmount() != amount ||
                o.getVmVersion() != vmVersion ||
                !Arrays.equals(o.getControlProgram(), program) ||
                !Arrays.equals(o.getAssertID().getValue(), assertID)) {
            vm.pushBool(false, true);
            return null;
        }
        if (refDataHash.length > 0) {
            byte[] hash = Sha3.sum256(o.getReferenceData());
            if (!Arrays.equals(hash, refDataHash)) {
                vm.pushBool(false, true);
                return null;
            }
        }
        vm.pushBool(true, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opAsset = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        AssetID assetID = vm.getTx().getInputs()[vm.getInputIndex()].assertID();
        vm.push(assetID.getValue(), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opAmount = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        long amount = vm.getTx().getInputs()[vm.getInputIndex()].amount();
        vm.pushInt64(amount, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opProgram = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        vm.push(vm.getMainProgram(), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opMinTime = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        vm.pushInt64(vm.getTx().getMinTime(), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opMaxTime = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        long maxTime = vm.getTx().getMaxTime();
        if (maxTime <= 0) {
            maxTime = Long.MAX_VALUE;
        }
        vm.pushInt64(maxTime, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opRefDataHash = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        byte[] hash = Sha3.sum256(vm.getTx().getInputs()[vm.getInputIndex()].getReferenceData());
        vm.push(hash, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opTxRefDataHash = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        byte[] hash = Sha3.sum256(vm.getTx().getReferenceData());
        vm.push(hash, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opIndex = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        vm.pushInt64(vm.getInputIndex(), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opOutpoint = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        TxInput txInput = vm.getTx().getInputs()[vm.getInputIndex()];
        if (txInput.isIssuance()) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        Outpoint outpoint = txInput.outpoint();
        vm.push(outpoint.getHash().getValue(), true);
        vm.pushInt64(outpoint.getIndex(), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opNonce = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        TxInput txInput = vm.getTx().getInputs()[vm.getInputIndex()];
        if (!txInput.isIssuance()) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        IssuanceInput issuanceInput = (IssuanceInput)txInput;
        vm.applyCost(1);
        vm.push(issuanceInput.getNonce(), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opNextProgram = vm -> {
        if (vm.getBlock() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        vm.push(vm.getBlock().getConsensusProgram(), true);
        return null;
    };

    static final Function<VirtualMachine, Void> opBlockTime = vm -> {
        if (vm.getBlock() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(1);
        vm.pushInt64(vm.getBlock().getTimestampMS(), true);
        return null;
    };
}
