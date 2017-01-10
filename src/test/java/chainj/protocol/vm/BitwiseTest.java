package chainj.protocol.vm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sbwdlihao on 07/01/2017.
 */
public class BitwiseTest {

    @Test
    public void testBitwiseOps() {
        List<VMCase> cases = new ArrayList<>(Arrays.asList(
                new VMCase(OP.OP_INVERT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)255}))),
                        new VirtualMachine(49998, new ArrayList<>(Arrays.asList(new byte[]{0})))
                ),
                new VMCase(OP.OP_INVERT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)255, 0}))),
                        new VirtualMachine(49997, new ArrayList<>(Arrays.asList(new byte[]{0, (byte)255})))
                ),
                new VMCase(OP.OP_AND,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0x80}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Arrays.asList(new byte[]{(byte)0x80})))
                ),
                new VMCase(OP.OP_AND,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0x80, (byte)0xff}))),
                        new VirtualMachine(49998, -10, new ArrayList<>(Arrays.asList(new byte[]{(byte)0x80})))
                ),
                new VMCase(OP.OP_AND,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0x80, (byte)0xff}, new byte[]{(byte)0xff}))),
                        new VirtualMachine(49998, -10, new ArrayList<>(Arrays.asList(new byte[]{(byte)0x80})))
                ),
                new VMCase(OP.OP_OR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0x80}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff})))
                ),
                new VMCase(OP.OP_OR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0x80, 0x10}))),
                        new VirtualMachine(49997, -9, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff, 0x10})))
                ),
                new VMCase(OP.OP_OR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff, 0x10}, new byte[]{(byte)0x80}))),
                        new VirtualMachine(49997, -9, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff, 0x10})))
                ),
                new VMCase(OP.OP_XOR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0x80}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Arrays.asList(new byte[]{(byte)0x7f})))
                ),
                new VMCase(OP.OP_XOR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0x80, 0x10}))),
                        new VirtualMachine(49997, -9, new ArrayList<>(Arrays.asList(new byte[]{(byte)0x7f, 0x10})))
                ),
                new VMCase(OP.OP_XOR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff, 0x10}, new byte[]{(byte)0x80}))),
                        new VirtualMachine(49997, -9, new ArrayList<>(Arrays.asList(new byte[]{(byte)0x7f, 0x10})))
                ),
                new VMCase(OP.OP_EQUAL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0xff}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Arrays.asList(new byte[]{1})))
                ),
                new VMCase(OP.OP_EQUAL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff, 0x10}, new byte[]{(byte)0xff, 0x10}))),
                        new VirtualMachine(49997, -11, new ArrayList<>(Arrays.asList(new byte[]{1})))
                ),
                new VMCase(OP.OP_EQUAL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0x80}))),
                        new VirtualMachine(49998, -10, new ArrayList<>(Arrays.asList(new byte[]{})))
                ),
                new VMCase(OP.OP_EQUAL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0xff, (byte)0x80}))),
                        new VirtualMachine(49998, -11, new ArrayList<>(Arrays.asList(new byte[]{})))
                ),
                new VMCase(OP.OP_EQUAL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff, (byte)0x80}, new byte[]{(byte)0xff}))),
                        new VirtualMachine(49998, -11, new ArrayList<>(Arrays.asList(new byte[]{})))
                ),
                new VMCase(OP.OP_EQUALVERIFY,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0xff}))),
                        new VirtualMachine(49998, -18, new ArrayList<>())
                ),
                new VMCase(OP.OP_EQUALVERIFY,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff, (byte)0x10}, new byte[]{(byte)0xff, (byte)0x10}))),
                        new VirtualMachine(49997, -20, new ArrayList<>())
                ),
                new VMCase(OP.OP_EQUALVERIFY,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0x80}))),
                        Errors.ErrVerifyFailed
                ),
                new VMCase(OP.OP_EQUALVERIFY,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0xff, (byte)0x80}))),
                        Errors.ErrVerifyFailed
                ),
                new VMCase(OP.OP_EQUALVERIFY,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff, (byte)0x80}, new byte[]{(byte)0xff}))),
                        Errors.ErrVerifyFailed
                )
        ));

        for (byte b : new byte[]{OP.OP_INVERT, OP.OP_AND, OP.OP_OR, OP.OP_XOR, OP.OP_EQUAL, OP.OP_EQUALVERIFY}) {
            cases.add(new VMCase(b, new VirtualMachine(50000, new ArrayList<>()),
                    Errors.ErrDataStackUnderflow));
            cases.add(new VMCase(b,
                    new VirtualMachine(0, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0xff}))),
                    Errors.ErrRunLimitExceeded));
            cases.add(new VMCase(b,
                    new VirtualMachine(1, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}, new byte[]{(byte)0xff}))),
                    Errors.ErrRunLimitExceeded));
        }

        for (byte b : new byte[]{OP.OP_AND, OP.OP_OR, OP.OP_XOR, OP.OP_EQUAL, OP.OP_EQUALVERIFY}) {
            cases.add(new VMCase(b, new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{(byte)0xff}))),
                    Errors.ErrDataStackUnderflow));
        }

        VMCase.runCase(cases);
    }
}
