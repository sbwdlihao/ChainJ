package chainj.protocol.vm;

import chainj.util.ByteBufferUtil;
import com.google.common.primitives.Bytes;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by sbwdlihao on 31/12/2016.
 */
public class PushData {

    static final Function<VirtualMachine, Void> opFalse = vm -> {
        vm.applyCost(1);
        vm.pushBool(false, false);
        return null;
    };

    static final Function<VirtualMachine, Void> opPushdata = vm -> {
        vm.applyCost(1);
        byte[] d = Arrays.copyOf(vm.getData(), vm.getData().length);
        vm.push(d, false);
        return null;
    };

    static final Function<VirtualMachine, Void> op1Negate = vm -> {
        vm.applyCost(1);
        vm.pushInt64(-1, false);
        return null;
    };

    static final Function<VirtualMachine, Void> opNop = vm -> {
        vm.applyCost(1);
        return null;
    };

    // chain的pushData一次最多可以写入4GB的字节数组，而这里理论上一次最多可以写入2GB，实际上受限于JVM，能写入的最大值会更小
    // 不过就区块链而言，不可能写入这么大的数据量
    public static byte[] pushDataBytes(byte[] in) {
        int l = in.length;
        if (l == 0) {
            return new byte[]{OP.OP_0};
        }
        if (l <= 75) {
            return Bytes.concat(new byte[]{(byte)(OP.OP_DATA_1 + l - 1)}, in);
        }
        if (l < 1 << 8) {
            return Bytes.concat(new byte[]{OP.OP_PUSHDATA1, (byte)l}, in);
        }
        if (l < 1 << 16) {
            return Bytes.concat(new byte[]{OP.OP_PUSHDATA2}, ByteBufferUtil.short2BLE((short)l), in);
        }
        return Bytes.concat(new byte[]{OP.OP_PUSHDATA4}, ByteBufferUtil.int2BLE(l), in);
    }

    public static byte[] pushDataInt64(long n) {
        if (n == 0) {
            return new byte[]{OP.OP_0};
        }
        if (n >= 1 && n <= 16) {
            return new byte[]{(byte)(OP.OP_1 + n - 1)};
        }
        return pushDataBytes(Types.int64Bytes(n));
    }
}
