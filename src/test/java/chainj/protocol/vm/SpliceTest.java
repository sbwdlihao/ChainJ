package chainj.protocol.vm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sbwdlihao on 07/01/2017.
 */
public class SpliceTest {

    @Test
    public void testSpliceOps() {
        List<VMCase> cases = new ArrayList<>(Arrays.asList(
                new VMCase(OP.OP_CAT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "hello".getBytes(), "world".getBytes()
                        ))),
                        new VirtualMachine(49986, -18, new ArrayList<>(Collections.singleton("helloworld".getBytes())))
                ),
                new VMCase(OP.OP_CAT,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton("world".getBytes()))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CAT,
                        new VirtualMachine(4, new ArrayList<>(Arrays.asList(
                                "hello".getBytes(), "world".getBytes()
                        ))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_SUBSTR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{3}, new byte[]{5}
                        ))),
                        new VirtualMachine(49991, -28, new ArrayList<>(Collections.singleton("lowor".getBytes())))
                ),
                new VMCase(OP.OP_SUBSTR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{3}, Types.int64Bytes(-1)
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_SUBSTR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), Types.int64Bytes(-5), new byte[]{5}
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_SUBSTR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{6}, new byte[]{5}
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_SUBSTR,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{5}))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_SUBSTR,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{3}, new byte[]{5}))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_SUBSTR,
                        new VirtualMachine(4, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{3}, new byte[]{5}
                        ))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_LEFT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{5}
                        ))),
                        new VirtualMachine(49991, -19, new ArrayList<>(Collections.singleton("hello".getBytes())))
                ),
                new VMCase(OP.OP_LEFT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), Types.int64Bytes(-1)
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_LEFT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{11}
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_LEFT,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{5}))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_LEFT,
                        new VirtualMachine(4, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{5}
                        ))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_RIGHT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{5}
                        ))),
                        new VirtualMachine(49991, -19, new ArrayList<>(Collections.singleton("world".getBytes())))
                ),
                new VMCase(OP.OP_RIGHT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), Types.int64Bytes(-1)
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_RIGHT,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{11}
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_RIGHT,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(new byte[]{5}))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_RIGHT,
                        new VirtualMachine(4, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{5}
                        ))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_SIZE,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(
                                "helloworld".getBytes()
                        ))),
                        new VirtualMachine(49999, 9, new ArrayList<>(Arrays.asList(
                                "helloworld".getBytes(), new byte[]{10}
                        )))
                ),
                new VMCase(OP.OP_CATPUSHDATA,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{(byte)0xff},
                                new byte[]{(byte)0xab, (byte)0xcd}
                        ))),
                        new VirtualMachine(49993, -10, new ArrayList<>(Collections.singleton(
                                new byte[]{(byte)0xff, OP.OP_DATA_2, (byte)0xab, (byte)0xcd}
                        )))
                ),
                new VMCase(OP.OP_CATPUSHDATA,
                        new VirtualMachine(50000, new ArrayList<>(Collections.singleton(
                                new byte[]{(byte)0xab, (byte)0xcd}
                        ))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CATPUSHDATA,
                        new VirtualMachine(4, new ArrayList<>(Arrays.asList(
                                new byte[]{(byte)0xff},
                                new byte[]{(byte)0xab, (byte)0xcd}
                        ))),
                        Errors.ErrRunLimitExceeded
                )
        ));

        for (byte b : new byte[]{OP.OP_CAT, OP.OP_SUBSTR, OP.OP_LEFT, OP.OP_RIGHT, OP.OP_SIZE, OP.OP_CATPUSHDATA}) {
            cases.add(new VMCase(b,
                    new VirtualMachine(0),
                    Errors.ErrRunLimitExceeded
            ));
            cases.add(new VMCase(b,
                    new VirtualMachine(50000, new ArrayList<>()),
                    Errors.ErrDataStackUnderflow
            ));
        }
        VMCase.runCase(cases);
    }
}
