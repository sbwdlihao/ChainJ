package chainj.protocol.vm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sbwdlihao on 04/01/2017.
 */
public class ControlTest {

    @Test
    public void testControlOps() {
        List<VMCase> cases = new ArrayList<>(Arrays.asList(
                new VMCase(OP.OP_JUMP, new VirtualMachine(50000, 0, 1, new byte[]{0x05, 0x00, 0x00, 0x00}),
                        new VirtualMachine(49999, 0, 5, new byte[]{0x05, 0x00, 0x00, 0x00})),
                new VMCase(OP.OP_JUMP, new VirtualMachine(50000, 0, 1, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0x7f}),
                        new VirtualMachine(49999, 0, 2147483647, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0x7f})),
                new VMCase(OP.OP_JUMPIF, new VirtualMachine(50000, 0, 1, 0, new ArrayList<>(Collections.singleton(new byte[]{1})), new byte[]{0x05, 0x00, 0x00, 0x00}),
                        new VirtualMachine(49999, 0, 5, -9, new ArrayList<>(), new byte[]{0x05, 0x00, 0x00, 0x00})),
                new VMCase(OP.OP_JUMPIF, new VirtualMachine(50000, 0, 1, 0, new ArrayList<>(Collections.singleton(new byte[]{})), new byte[]{0x05, 0x00, 0x00, 0x00}),
                        new VirtualMachine(49999, 0, 1, -8, new ArrayList<>(), new byte[]{0x05, 0x00, 0x00, 0x00})),
                new VMCase(OP.OP_VERIFY, new VirtualMachine(50000, 0, new ArrayList<>(Collections.singleton(new byte[]{1}))),
                        new VirtualMachine(49999, -9, new ArrayList<>())),
                new VMCase(OP.OP_VERIFY, new VirtualMachine(50000, 0, new ArrayList<>(Collections.singleton(new byte[]{1, 1}))),
                        new VirtualMachine(49999, -10, new ArrayList<>())),
                new VMCase(OP.OP_VERIFY, new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{}))), Errors.ErrVerifyFailed),
                new VMCase(OP.OP_VERIFY, new VirtualMachine(50000, new ArrayList<>()), Errors.ErrDataStackUnderflow),
                new VMCase(OP.OP_FAIL, new VirtualMachine(50000), Errors.ErrReturn),
                new VMCase(OP.OP_CHECKPREDICATE, new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{}, new byte[]{OP.OP_TRUE}, new byte[]{}))),
                        new VirtualMachine(0, -49951, new ArrayList<>(Collections.singleton(new byte[]{1})))),
                new VMCase(OP.OP_CHECKPREDICATE, new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{}, new byte[]{}, new byte[]{}))),
                        new VirtualMachine(0, -49952, new ArrayList<>(Collections.singleton(new byte[]{})))),
                new VMCase(OP.OP_CHECKPREDICATE, new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{}, new byte[]{OP.OP_FAIL}, new byte[]{}))),
                        new VirtualMachine(0, -49952, new ArrayList<>(Collections.singleton(new byte[]{})))),
                new VMCase(OP.OP_CHECKPREDICATE, new VirtualMachine(50000), Errors.ErrDataStackUnderflow),
                new VMCase(OP.OP_CHECKPREDICATE, new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{}))), Errors.ErrDataStackUnderflow),
                new VMCase(OP.OP_CHECKPREDICATE, new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{}, new byte[]{}))), Errors.ErrDataStackUnderflow),
                new VMCase(OP.OP_CHECKPREDICATE, new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{}, new byte[]{}, Types.int64Bytes(-1)))), Errors.ErrBadValue),
                new VMCase(OP.OP_CHECKPREDICATE, new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{}, new byte[]{}, Types.int64Bytes(50000)))), Errors.ErrRunLimitExceeded),
                new VMCase(OP.OP_CHECKPREDICATE,
                        new VirtualMachine(50000,
                                new ArrayList<>(
                                        Arrays.asList(
                                                new byte[]{0x05},
                                                new byte[]{0x07},
                                                new byte[]{0x02},
                                                new byte[]{OP.OP_ADD, OP.OP_12, OP.OP_NUMEQUAL},
                                                new byte[]{}
                                        ))),
                        new VirtualMachine(0, -49968, new ArrayList<>(Collections.singleton(new byte[]{0x01})))),
                new VMCase(OP.OP_CHECKPREDICATE,
                        new VirtualMachine(50000,
                                new ArrayList<>(
                                        Arrays.asList(
                                                new byte[]{0x05},
                                                new byte[]{0x07},
                                                new byte[]{0x01},
                                                new byte[]{OP.OP_ADD, OP.OP_12, OP.OP_NUMEQUAL},
                                                new byte[]{}
                                        ))),
                        new VirtualMachine(0, -49954, new ArrayList<>(Arrays.asList(
                                new byte[]{0x05},
                                new byte[]{}
                                ))))
        ));

        byte[] limitChecks = new byte[]{OP.OP_CHECKPREDICATE, OP.OP_VERIFY, OP.OP_FAIL};
        for (byte limitCheck : limitChecks) {
            cases.add(new VMCase(limitCheck, new VirtualMachine(0), Errors.ErrRunLimitExceeded));
        }

        VMCase.runCase(cases);
    }
}
