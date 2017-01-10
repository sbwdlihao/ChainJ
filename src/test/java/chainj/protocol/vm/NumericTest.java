package chainj.protocol.vm;

import chainj.Case;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sbwdlihao on 05/01/2017.
 */
public class NumericTest {

    @Test
    public void testNumericOps() {
        List<VMCase> cases = new ArrayList<>(Arrays.asList(
                new VMCase(OP.OP_1ADD,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{2}))),
                        new VirtualMachine(49998, new ArrayList<>(Collections.singleton(new byte[]{3})))
                ),
                new VMCase(OP.OP_1SUB,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{2}))),
                        new VirtualMachine(49998, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_2MUL,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{2}))),
                        new VirtualMachine(49998, new ArrayList<>(Collections.singleton(new byte[]{4})))
                ),
                new VMCase(OP.OP_2DIV,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{2}))),
                        new VirtualMachine(49998, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_2DIV,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(Types.int64Bytes(-2)))),
                        new VirtualMachine(49998, new ArrayList<>(Collections.singleton(Types.int64Bytes(-1))))
                ),
                new VMCase(OP.OP_2DIV,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(Types.int64Bytes(-1)))),
                        new VirtualMachine(49998, new ArrayList<>(Collections.singleton(Types.int64Bytes(-1))))
                ),
                new VMCase(OP.OP_NEGATE,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{2}))),
                        new VirtualMachine(49998, 7, new ArrayList<>(Collections.singleton(Types.int64Bytes(-2))))
                ),
                new VMCase(OP.OP_ABS,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(Types.int64Bytes(-2)))),
                        new VirtualMachine(49998, -7, new ArrayList<>(Collections.singleton(new byte[]{2})))
                ),
                new VMCase(OP.OP_NOT,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{2}))),
                        new VirtualMachine(49998, -1, new ArrayList<>(Collections.singleton(new byte[]{})))
                ),
                new VMCase(OP.OP_0NOTEQUAL,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{2}))),
                        new VirtualMachine(49998, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_ADD,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Collections.singleton(new byte[]{3})))
                ),
                new VMCase(OP.OP_SUB,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_MUL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49992, -9, new ArrayList<>(Collections.singleton(new byte[]{2})))
                ),
                new VMCase(OP.OP_DIV,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49992, -9, new ArrayList<>(Collections.singleton(new byte[]{2})))
                ),
                new VMCase(OP.OP_DIV,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(Types.int64Bytes(-2), new byte[]{1}))),
                        new VirtualMachine(49992, -9, new ArrayList<>(Collections.singleton(Types.int64Bytes(-2))))
                ),
                new VMCase(OP.OP_DIV,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(Types.int64Bytes(-2), Types.int64Bytes(-1)))),
                        new VirtualMachine(49992, -23, new ArrayList<>(Collections.singleton(new byte[]{2})))
                ),
                new VMCase(OP.OP_DIV,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(Types.int64Bytes(-2), Types.int64Bytes(2)))),
                        new VirtualMachine(49992, -9, new ArrayList<>(Collections.singleton(Types.int64Bytes(-1))))
                ),
                new VMCase(OP.OP_DIV,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{}))),
                        Errors.ErrDivZero
                ),
                new VMCase(OP.OP_MOD,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49992, -10, new ArrayList<>(Collections.singleton(new byte[]{})))
                ),
                new VMCase(OP.OP_MOD,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(Types.int64Bytes(-12), new byte[]{10}))),
                        new VirtualMachine(49992, -16, new ArrayList<>(Collections.singleton(new byte[]{8})))
                ),
                new VMCase(OP.OP_MOD,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{}))),
                        Errors.ErrDivZero
                ),
                new VMCase(OP.OP_LSHIFT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49992, -9, new ArrayList<>(Collections.singleton(new byte[]{4})))
                ),
                new VMCase(OP.OP_LSHIFT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(Types.int64Bytes(-2), new byte[]{1}))),
                        new VirtualMachine(49992, -9, new ArrayList<>(Collections.singleton(Types.int64Bytes(-4))))
                ),
                new VMCase(OP.OP_RSHIFT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49992, -9, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_RSHIFT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(Types.int64Bytes(-2), new byte[]{1}))),
                        new VirtualMachine(49992, -9, new ArrayList<>(Collections.singleton(Types.int64Bytes(-1))))
                ),
                new VMCase(OP.OP_BOOLAND,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_BOOLOR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_NUMEQUAL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -10, new ArrayList<>(Collections.singleton(new byte[]{})))
                ),
                new VMCase(OP.OP_NUMEQUALVERIFY,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{2}))),
                        new VirtualMachine(49998, -18, new ArrayList<>())
                ),
                new VMCase(OP.OP_NUMEQUALVERIFY,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{1}, new byte[]{2}))),
                        Errors.ErrVerifyFailed
                ),
                new VMCase(OP.OP_NUMNOTEQUAL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_LESSTHAN,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -10, new ArrayList<>(Collections.singleton(new byte[]{})))
                ),
                new VMCase(OP.OP_LESSTHANOREQUAL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -10, new ArrayList<>(Collections.singleton(new byte[]{})))
                ),
                new VMCase(OP.OP_GREATERTHAN,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_GREATERTHANOREQUAL,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_MIN,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_MAX,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{1}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Collections.singleton(new byte[]{2})))
                ),
                new VMCase(OP.OP_MAX,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{1}, new byte[]{2}))),
                        new VirtualMachine(49998, -9, new ArrayList<>(Collections.singleton(new byte[]{2})))
                ),
                new VMCase(OP.OP_WITHIN,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{1}, new byte[]{1}, new byte[]{2}))),
                        new VirtualMachine(49996, -18, new ArrayList<>(Collections.singleton(new byte[]{1})))
                ),
                new VMCase(OP.OP_WITHIN,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{1}, new byte[]{2}))),
                        Errors.ErrDataStackUnderflow
                )
        ));

        byte[] numOps = new byte[]{
                OP.OP_1ADD, OP.OP_1SUB, OP.OP_2MUL, OP.OP_2DIV, OP.OP_NEGATE, OP.OP_ABS, OP.OP_NOT, OP.OP_0NOTEQUAL,
                OP.OP_ADD, OP.OP_SUB, OP.OP_MUL, OP.OP_DIV, OP.OP_MOD, OP.OP_LSHIFT, OP.OP_RSHIFT, OP.OP_BOOLAND,
                OP.OP_BOOLOR, OP.OP_NUMEQUAL, OP.OP_NUMEQUALVERIFY, OP.OP_NUMNOTEQUAL, OP.OP_LESSTHAN,
                OP.OP_LESSTHANOREQUAL, OP.OP_GREATERTHAN, OP.OP_GREATERTHANOREQUAL, OP.OP_MIN, OP.OP_MAX, OP.OP_WITHIN,
        };
        byte[] twoOps = Arrays.copyOfRange(numOps, 8, numOps.length);
        for (byte numOp : numOps) {
            cases.add(new VMCase(numOp, new VirtualMachine(50000, new ArrayList<>()), Errors.ErrDataStackUnderflow));
            cases.add(new VMCase(numOp,
                    new VirtualMachine(0, new ArrayList<>(Arrays.asList(new byte[]{2}, new byte[]{2}, new byte[]{2}))),
                    Errors.ErrRunLimitExceeded
            ));
        }
        for (byte twoOp : twoOps) {
            cases.add(new VMCase(twoOp, new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{1}))),
                    Errors.ErrDataStackUnderflow));
        }

        VMCase.runCase(cases);
    }

    @Test
    public void testRangeErrs() {
        List<Case<String, Boolean>> cases = new ArrayList<>(Arrays.asList(
                new Case<>("0 1ADD", false),
                new Case<>(String.format("%d 1ADD", Long.MIN_VALUE), false),
                new Case<>(String.format("%d 1ADD", Long.MAX_VALUE - 1), false),
                new Case<>(String.format("%d 1ADD", Long.MAX_VALUE), true),
                new Case<>("0 1SUB", false),
                new Case<>(String.format("%d 1SUB", Long.MAX_VALUE), false),
                new Case<>(String.format("%d 1SUB", Long.MIN_VALUE + 1), false),
                new Case<>(String.format("%d 1SUB", Long.MIN_VALUE), true),
                new Case<>("1 2MUL", false),
                new Case<>(String.format("%d 2MUL", Long.MAX_VALUE/2 - 1), false),
                new Case<>(String.format("%d 2MUL", Long.MAX_VALUE/2 + 1), true),
                new Case<>(String.format("%d 2MUL", Long.MIN_VALUE/2 + 1), false),
                new Case<>(String.format("%d 2MUL", Long.MIN_VALUE/2 - 1), true),
                new Case<>("1 NEGATE", false),
                new Case<>("-1 NEGATE", false),
                new Case<>(String.format("%d NEGATE", Long.MAX_VALUE), false),
                new Case<>(String.format("%d NEGATE", Long.MIN_VALUE), true),
                new Case<>("1 ABS", false),
                new Case<>("-1 ABS", false),
                new Case<>(String.format("%d ABS", Long.MAX_VALUE), false),
                new Case<>(String.format("%d ABS", Long.MIN_VALUE), true),
                new Case<>("2 3 ADD", false),
                new Case<>(String.format("%d %d ADD", Long.MIN_VALUE, Long.MAX_VALUE), false),
                new Case<>(String.format("%d %d ADD", Long.MAX_VALUE/2 - 1, Long.MAX_VALUE/2 - 2), false),
                new Case<>(String.format("%d %d ADD", Long.MAX_VALUE/2 + 1, Long.MAX_VALUE/2 + 2), true),
                new Case<>(String.format("%d %d ADD", Long.MIN_VALUE/2 + 1, Long.MIN_VALUE/2 + 2), false),
                new Case<>(String.format("%d %d ADD", Long.MIN_VALUE/2 - 1, Long.MIN_VALUE/2 - 2), true),
                new Case<>("2 3 SUB", false),
                new Case<>(String.format("1 %d SUB", Long.MAX_VALUE), false),
                new Case<>(String.format("-1 %d SUB", Long.MIN_VALUE), false),
                new Case<>(String.format("1 %d SUB", Long.MIN_VALUE), true),
                new Case<>(String.format("-1 %d SUB", Long.MAX_VALUE), false),
                new Case<>(String.format("-2 %d SUB", Long.MAX_VALUE), true),
                new Case<>("1 2 LSHIFT", false),
                new Case<>("-1 2 LSHIFT", false),
                new Case<>("-1 63 LSHIFT", false),
                new Case<>("-1 64 LSHIFT", true),
                new Case<>("0 64 LSHIFT", false),
                new Case<>("1 62 LSHIFT", false),
                new Case<>("1 63 LSHIFT", true),
                new Case<>(String.format("%d 0 LSHIFT", Long.MAX_VALUE), false),
                new Case<>(String.format("%d 1 LSHIFT", Long.MAX_VALUE), true),
                new Case<>(String.format("%d 1 LSHIFT", Long.MAX_VALUE/2), false),
                new Case<>(String.format("%d 2 LSHIFT", Long.MAX_VALUE/2), true),
                new Case<>(String.format("%d 0 LSHIFT", Long.MIN_VALUE), false),
                new Case<>(String.format("%d 1 LSHIFT", Long.MIN_VALUE), true),
                new Case<>(String.format("%d 1 LSHIFT", Long.MIN_VALUE/2), false),
                new Case<>(String.format("%d 2 LSHIFT", Long.MIN_VALUE/2), true)
        ));

        cases.forEach(c->{
            byte[] program = Assemble.assemble(c.data);
            VirtualMachine vm = new VirtualMachine(50000, program);
            try {
                vm.run();
                Assert.assertFalse(c.want);
            } catch (VMRunTimeException e) {
                Assert.assertTrue(c.want);
                Assert.assertEquals(Errors.ErrRange, e.getMessage());
            }
        });
    }
}
