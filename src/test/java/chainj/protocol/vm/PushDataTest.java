package chainj.protocol.vm;

import chainj.Case;
import com.google.common.primitives.Bytes;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sbwdlihao on 02/01/2017.
 */
public class PushDataTest {

    @Test
    public void testPushdataOps() {
        List<VMCase> cases = new ArrayList<>(Arrays.asList(
                new VMCase(OP.OP_FALSE, new VirtualMachine(50000), new VirtualMachine(49991, new ArrayList<>(Arrays.asList(new byte[]{})))),
                new VMCase(OP.OP_FALSE, new VirtualMachine(1), Errors.ErrRunLimitExceeded),
                new VMCase(OP.OP_1NEGATE, new VirtualMachine(50000), new VirtualMachine(49983, new ArrayList<>(Arrays.asList(Types.int64Bytes(-1L))))),
                new VMCase(OP.OP_1NEGATE, new VirtualMachine(1), Errors.ErrRunLimitExceeded)
                ));
        List<Byte> pushdataOPs = new ArrayList<>(Arrays.asList(OP.OP_PUSHDATA1, OP.OP_PUSHDATA2, OP.OP_PUSHDATA4));
        for (int i = 1; i <= 75; i++) {
            pushdataOPs.add((byte) i);
        }
        pushdataOPs.forEach(op -> {
            cases.add(new VMCase(op,
                    new VirtualMachine(50000, new ArrayList<>(), "data".getBytes()),
                    new VirtualMachine(49987, new ArrayList<>(Arrays.asList("data".getBytes())), "data".getBytes())
                    ));
            cases.add(new VMCase(op, new VirtualMachine(1, new ArrayList<>(), "data".getBytes()), Errors.ErrRunLimitExceeded));
        });
        pushdataOPs.addAll(Arrays.asList(OP.OP_FALSE, OP.OP_1NEGATE, OP.OP_NOP));
        pushdataOPs.forEach(op -> cases.add(new VMCase(op, new VirtualMachine(0), Errors.ErrRunLimitExceeded)));

        VMCase.runCase(cases);
    }

    @Test
    public void testPushDataBytes() {
        List<Case<byte[], byte[]>> cases = new ArrayList<>();
        cases.add(new Case<>(new byte[255], Bytes.concat(new byte[]{OP.OP_PUSHDATA1, (byte)0xff}, new byte[255])));
        cases.add(new Case<>(new byte[1 << 8], Bytes.concat(new byte[]{OP.OP_PUSHDATA2, 0, 1}, new byte[1 << 8])));
        cases.add(new Case<>(new byte[1 << 16], Bytes.concat(new byte[]{OP.OP_PUSHDATA4, 0, 0, 1, 0}, new byte[1 << 16])));
        for (int i = 1; i <= 75; i++) {
            cases.add(new Case<>(new byte[i], Bytes.concat(new byte[]{(byte) (OP.OP_DATA_1 - 1 + i)}, new byte[i])));
        }

        for (Case<byte[], byte[]> aVMCase : cases) {
            byte[] got = PushData.pushDataBytes(aVMCase.data);
            Assert.assertArrayEquals(aVMCase.want, got);
        }
    }
    
    @Test
    public void testPushDataInt64() {
        List<Case<Long, byte[]>> cases = new ArrayList<>();
        cases.add(new Case<>(0L, new byte[]{OP.OP_0}));
        cases.add(new Case<>(17L, new byte[]{OP.OP_DATA_1, (byte)0x11}));
        cases.add(new Case<>(255L, new byte[]{OP.OP_DATA_1, (byte)0xff}));
        cases.add(new Case<>(256L, new byte[]{OP.OP_DATA_2, (byte)0x00, (byte)0x01}));
        cases.add(new Case<>(-1L, new byte[]{OP.OP_DATA_8, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}));
        cases.add(new Case<>(-2L, new byte[]{OP.OP_DATA_8, (byte)0xfe, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}));
        for (long i = 1L; i <= 16L; i++) {
            cases.add(new Case<>(i, new byte[]{(byte)(OP.OP_1 - 1 + i)}));
        }
        cases.forEach(c -> {
            byte[] got = PushData.pushDataInt64(c.data);
            Assert.assertArrayEquals(c.want, got);
        });
    }
}
