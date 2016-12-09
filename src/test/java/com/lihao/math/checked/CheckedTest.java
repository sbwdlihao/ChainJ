package com.lihao.math.checked;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by sbwdlihao on 09/12/2016.
 */
public class CheckedTest {

    @Test
    public void testInt64() {
        assertThat(Checked.addInt64(2, 3)).isEqualTo(5);
        assertThat(Checked.addInt64(2, -3)).isEqualTo(-1);
        assertThat(Checked.addInt64(-2, -3)).isEqualTo(-5);
        assertThatThrownBy(() -> Checked.addInt64(Long.MAX_VALUE, 1)).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Checked.addInt64(Long.MIN_VALUE, Long.MIN_VALUE)).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Checked.addInt64(Long.MIN_VALUE, -1)).isInstanceOf(ArithmeticException.class);

        assertThat(Checked.subInt64(3, 2)).isEqualTo(1);
        assertThat(Checked.subInt64(2, 3)).isEqualTo(-1);
        assertThat(Checked.subInt64(-2, -3)).isEqualTo(1);
        assertThatThrownBy(() -> Checked.subInt64(Long.MIN_VALUE, 1)).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Checked.subInt64(-2, Long.MAX_VALUE)).isInstanceOf(ArithmeticException.class);

        assertThat(Checked.mulInt64(2, 3)).isEqualTo(6);
        assertThat(Checked.mulInt64(-2, -3)).isEqualTo(6);
        assertThat(Checked.mulInt64(-2, 3)).isEqualTo(-6);
        assertThat(Checked.mulInt64(Long.MAX_VALUE, -1)).isEqualTo(Long.MIN_VALUE + 1);
        assertThatThrownBy(() -> Checked.mulInt64(Long.MIN_VALUE, 2)).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Checked.mulInt64(Long.MAX_VALUE, 2)).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Checked.mulInt64(2, Long.MIN_VALUE)).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Checked.mulInt64(-2, Long.MIN_VALUE)).isInstanceOf(ArithmeticException.class);

        assertThat(Checked.divInt64(2, 2)).isEqualTo(1);
        assertThat(Checked.divInt64(-2, -2)).isEqualTo(1);
        assertThat(Checked.divInt64(-2, 2)).isEqualTo(-1);
        assertThatThrownBy(() -> Checked.divInt64(1, 0)).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Checked.divInt64(Long.MIN_VALUE, -1)).isInstanceOf(ArithmeticException.class);

        assertThat(Checked.modInt64(3, 2)).isEqualTo(1);
        assertThat(Checked.modInt64(-3, -2)).isEqualTo(-1);
        assertThat(Checked.modInt64(-3, 2)).isEqualTo(-1);
        assertThatThrownBy(() -> Checked.modInt64(1, 0)).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Checked.modInt64(Long.MIN_VALUE, -1)).isInstanceOf(ArithmeticException.class);

        assertThat(Checked.negateInt64(1)).isEqualTo(-1);
        assertThat(Checked.negateInt64(-1)).isEqualTo(1);
        assertThat(Checked.negateInt64(0)).isEqualTo(0);
        assertThatThrownBy(() -> Checked.negateInt64(Long.MIN_VALUE)).isInstanceOf(ArithmeticException.class);

        assertThat(Checked.lShiftInt64(1, 2)).isEqualTo(4);
        assertThat(Checked.lShiftInt64(-1, 2)).isEqualTo(-4);
        assertThatThrownBy(() -> Checked.lShiftInt64(1, 64)).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Checked.lShiftInt64(2, 63)).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Checked.lShiftInt64(-2, 63)).isInstanceOf(ArithmeticException.class);
    }
}
