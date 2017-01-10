package chainj.protocol.vm;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sbwdlihao on 03/01/2017.
 */
public class OPSTest {

    class ParseOPCase {
        byte[] program;
        int programCount;
        Instruction want;
        String wantErr;

        ParseOPCase(byte[] program, Instruction want) {
            this.program = program;
            this.want = want;
        }

        ParseOPCase(byte[] program, String wantErr) {
            this.program = program;
            this.wantErr = wantErr;
        }

        ParseOPCase(byte[] program, int programCount, String wantErr) {
            this.program = program;
            this.programCount = programCount;
            this.wantErr = wantErr;
        }
    }

    @Test
    public void testParseOp() {
        ParseOPCase[] cases = new ParseOPCase[] {
          new ParseOPCase(new byte[]{OP.OP_ADD}, new Instruction(OP.OP_ADD, 1)),
          new ParseOPCase(new byte[]{OP.OP_16}, new Instruction(OP.OP_16, 1, new byte[]{16})),
          new ParseOPCase(new byte[]{OP.OP_DATA_5, 1, 1, 1, 1, 1}, new Instruction(OP.OP_DATA_5, 6, new byte[]{1, 1, 1, 1, 1})),
          new ParseOPCase(new byte[]{OP.OP_DATA_5, 1, 1, 1, 1, 1, (byte)255}, new Instruction(OP.OP_DATA_5, 6, new byte[]{1, 1, 1, 1, 1})),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA1, 1, 1}, new Instruction(OP.OP_PUSHDATA1, 3, new byte[]{1})),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA1, 1, 1, (byte)255}, new Instruction(OP.OP_PUSHDATA1, 3, new byte[]{1})),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA2, 1, 0, 1}, new Instruction(OP.OP_PUSHDATA2, 4, new byte[]{1})),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA2, 1, 0, 1, (byte)255}, new Instruction(OP.OP_PUSHDATA2, 4, new byte[]{1})),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA4, 1, 0, 0, 0, 1}, new Instruction(OP.OP_PUSHDATA4, 6, new byte[]{1})),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA4, 1, 0, 0, 0, 1, (byte)255}, new Instruction(OP.OP_PUSHDATA4, 6, new byte[]{1})),
          new ParseOPCase(new byte[]{}, Errors.ErrShortProgram),
          new ParseOPCase(new byte[]{OP.OP_0}, 1, Errors.ErrShortProgram),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA1}, Errors.ErrShortProgram),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA1, 1}, Errors.ErrShortProgram),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA2}, Errors.ErrShortProgram),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA2, 1, 0}, Errors.ErrShortProgram),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA4}, Errors.ErrShortProgram),
          new ParseOPCase(new byte[]{OP.OP_PUSHDATA4, 1, 0, 0, 0}, Errors.ErrShortProgram),
        };
        for (ParseOPCase aCase : cases) {
            try {
                Instruction got = OPS.parseOp(aCase.program, aCase.programCount);
                Assert.assertEquals(aCase.want, got);
            } catch (IllegalArgumentException e) {
                Assert.assertEquals(aCase.wantErr, e.getMessage());
            }
        }
    }

    class ParseProgramCase {
        byte[] program;
        List<Instruction> want;
        String wantErr;

        ParseProgramCase(byte[] program, List<Instruction> want) {
            this.program = program;
            this.want = want;
        }
    }

    @Test
    public void testParseProgram() {
        ParseProgramCase[] cases = new ParseProgramCase[] {
                new ParseProgramCase(new byte[]{OP.OP_2, OP.OP_3, OP.OP_ADD, OP.OP_5, OP.OP_NUMEQUAL}, Arrays.asList(
                        new Instruction(OP.OP_2, 1, new byte[]{0x02}),
                        new Instruction(OP.OP_3, 1, new byte[]{0x03}),
                        new Instruction(OP.OP_ADD, 1),
                        new Instruction(OP.OP_5, 1, new byte[]{0x05}),
                        new Instruction(OP.OP_NUMEQUAL, 1)
                )),
                new ParseProgramCase(new byte[]{(byte)255}, Collections.singletonList(
                        new Instruction((byte) 255, 1)
                ))
        };
        for (ParseProgramCase aCase : cases) {
            try {
                List<Instruction> got = OPS.parseProgram(aCase.program);
                Assert.assertEquals(aCase.want, got);
            } catch (IllegalArgumentException e) {
                Assert.assertEquals(aCase.wantErr, e.getMessage());
            }
        }
    }
}
