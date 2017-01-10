package chainj.protocol.vm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sbwdlihao on 07/01/2017.
 */
public class StackTest {

    @Test
    public void testStackOps() {
        List<VMCase> cases = new ArrayList<>(Arrays.asList(
                new VMCase(OP.OP_TOALTSTACK,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{1}))),
                        new VirtualMachine(49998, new ArrayList<>(), new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_FROMALTSTACK,
                        new VirtualMachine(50000, new ArrayList<>(), new ArrayList<>(Collections.singleton(new byte[]{1}))),
                        new VirtualMachine(49998, new ArrayList<>(Collections.singleton(new byte[]{1})), new ArrayList<>())
                ),
                new VMCase(OP.OP_FROMALTSTACK,
                        new VirtualMachine(50000, new ArrayList<>(), new ArrayList<>()),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_2DROP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{1}, new byte[]{1}))),
                        new VirtualMachine(50016, new ArrayList<>())
                ),
                new VMCase(OP.OP_2DUP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49980, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}, new byte[]{2}, new byte[]{1})))
                ),
                new VMCase(OP.OP_3DUP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{3}, new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49970, new ArrayList<>(Arrays.asList(
                                new byte[]{3}, new byte[]{2}, new byte[]{1}, new byte[]{3}, new byte[]{2}, new byte[]{1})))
                ),
                new VMCase(OP.OP_2OVER,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{4}, new byte[]{3}, new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49980, new ArrayList<>(Arrays.asList(
                                new byte[]{4}, new byte[]{3}, new byte[]{2}, new byte[]{1}, new byte[]{4}, new byte[]{3})))
                ),
                new VMCase(OP.OP_2OVER,
                        new VirtualMachine(2, new ArrayList<>(Arrays.asList(
                                new byte[]{4}, new byte[]{3}, new byte[]{2}, new byte[]{1}))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_2ROT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{6}, new byte[]{5}, new byte[]{4}, new byte[]{3}, new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, new ArrayList<>(Arrays.asList(
                                new byte[]{4}, new byte[]{3}, new byte[]{2}, new byte[]{1}, new byte[]{6}, new byte[]{5})))
                ),
                new VMCase(OP.OP_2SWAP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{4}, new byte[]{3}, new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}, new byte[]{4}, new byte[]{3})))
                ),
                new VMCase(OP.OP_IFDUP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{1}))),
                        new VirtualMachine(49990, new ArrayList<>(Arrays.asList(
                                new byte[]{1}, new byte[]{1})))
                ),
                new VMCase(OP.OP_IFDUP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{}))),
                        new VirtualMachine(49999, new ArrayList<>(Arrays.asList(
                                new byte[]{})))
                ),
                new VMCase(OP.OP_IFDUP,
                        new VirtualMachine(1, new ArrayList<>(Arrays.asList(
                                new byte[]{1}))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_DEPTH,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{1}))),
                        new VirtualMachine(49990, new ArrayList<>(Arrays.asList(
                                new byte[]{1}, new byte[]{1})))
                ),
                new VMCase(OP.OP_DEPTH,
                        new VirtualMachine(1, new ArrayList<>(Arrays.asList(
                                new byte[]{1}))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_DROP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{1}))),
                        new VirtualMachine(50008, new ArrayList<>())
                ),
                new VMCase(OP.OP_DUP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{1}))),
                        new VirtualMachine(49990, new ArrayList<>(Arrays.asList(
                                new byte[]{1}, new byte[]{1})))
                ),
                new VMCase(OP.OP_DUP,
                        new VirtualMachine(1, new ArrayList<>(Arrays.asList(
                                new byte[]{1}))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_NIP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(50008, new ArrayList<>(Arrays.asList(
                                new byte[]{1})))
                ),
                new VMCase(OP.OP_NIP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{1}))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_OVER,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49990, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}, new byte[]{2})))
                ),
                new VMCase(OP.OP_OVER,
                        new VirtualMachine(1, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_PICK,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{3}, new byte[]{2}, new byte[]{1}, new byte[]{2}))),
                        new VirtualMachine(49998, new ArrayList<>(Arrays.asList(
                                new byte[]{3}, new byte[]{2}, new byte[]{1}, new byte[]{3})))
                ),
                new VMCase(OP.OP_PICK,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{3}, new byte[]{2}, new byte[]{1}, new byte[]{3}))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_PICK,
                        new VirtualMachine(2, new ArrayList<>(Arrays.asList(
                                new byte[]{(byte)0xff, (byte)0xff}, new byte[]{2}, new byte[]{1}, new byte[]{2}))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_ROLL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{3}, new byte[]{2}, new byte[]{1}, new byte[]{2}))),
                        new VirtualMachine(50007, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}, new byte[]{3})))
                ),
                new VMCase(OP.OP_ROLL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{3}, new byte[]{2}, new byte[]{1}, new byte[]{3}))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_ROT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{3}, new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}, new byte[]{3})))
                ),
                new VMCase(OP.OP_SWAP,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49999, new ArrayList<>(Arrays.asList(
                                new byte[]{1}, new byte[]{2})))
                ),
                new VMCase(OP.OP_TUCK,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49990, new ArrayList<>(Arrays.asList(
                                new byte[]{1}, new byte[]{2}, new byte[]{1})))
                ),
                new VMCase(OP.OP_TUCK,
                        new VirtualMachine(1, new ArrayList<>(Arrays.asList(
                                new byte[]{2}, new byte[]{1}))),
                        Errors.ErrRunLimitExceeded
                )
        ));

        byte[] stackOps = new byte[]{
                OP.OP_DEPTH, OP.OP_FROMALTSTACK, OP.OP_TOALTSTACK, OP.OP_2DROP, OP.OP_2DUP, OP.OP_3DUP,
                OP.OP_2OVER, OP.OP_2ROT, OP.OP_2SWAP, OP.OP_IFDUP, OP.OP_DROP, OP.OP_DUP, OP.OP_NIP,
                OP.OP_OVER, OP.OP_PICK, OP.OP_ROLL, OP.OP_ROT, OP.OP_SWAP, OP.OP_TUCK
        };

        for (byte stackOp : stackOps) {
            cases.add(new VMCase(stackOp, new VirtualMachine(0), Errors.ErrRunLimitExceeded));
        }
        for (int i = 2; i < stackOps.length; i++) {
            cases.add(new VMCase(stackOps[i], new VirtualMachine(50000, new ArrayList<>()), Errors.ErrDataStackUnderflow));
        }

        VMCase.runCase(cases);
    }
}
