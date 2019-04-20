package jumpaku.othello.selectors

import org.junit.Assert.assertTrue
import org.junit.Test

class PatternEvaluatorTest {

    @Test
    fun testCorner() {
        val s0 = "ooooooaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.Corner.evaluate(s0, true) > 0)
    }

    @Test
    fun testMountain() {
        val s0 = "_oooooo_\na_oxoxxa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.Mountain.evaluate(s0, true) > 0)
        val s1 = "_oooooo_\naxoxox_a\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.Mountain.evaluate(s1, true) > 0)
    }

    @Test
    fun testHalfSquare() {
        val s0 = "__oaa___\na_oaaa_a\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.HalfSquare.evaluate(s0, true) > 0)
        val s1 = "__oaax__\na_oaaa_a\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.HalfSquare.evaluate(s1, true) > 0)
    }

    @Test
    fun testSquare() {
        val s0 = "__oaao__\na_oaao_a\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.Square.evaluate(s0, true) > 0)
    }

    @Test
    fun testGoodA() {
        val s0 = "__oaaa__\na__aaa_a\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.GoodA.evaluate(s0, true) > 0)
        val s1 = "__oaaa__\na_xaaa_a\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.GoodA.evaluate(s1, true) > 0)
    }

    @Test
    fun testCheckCornerC() {
        val s0 = "_xoxaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.CheckCornerC.evaluate(s0, true) > 0)
        val s1 = "_xo_xoaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaxa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.CheckCornerC.evaluate(s1, true) > 0)
    }

    @Test
    fun testCheckCornerX() {
        val s0 = "_aaaaaaa\naxaaaaaa\naaxaaaaa\naaaxaaaa\naaaaxaaa\naaaaaxaa\naaaaaaxa\naaaaaaao"
        assertTrue(PatternEvaluator.BoardPattern.CheckCornerX.evaluate(s0, true) > 0)
        val s1 = "_aaaaaaa\naxaaaaaa\naaxaaaaa\naaaxaaaa\naaaaxaaa\naaaaaxaa\naaaaaaoa\naaaaaaao"
        assertTrue(PatternEvaluator.BoardPattern.CheckCornerX.evaluate(s1, true) > 0)
        val s2 = "_aaaaaaa\naxaaaaaa\naaxaaaaa\naaaxaaaa\naaaaxaaa\naaaaaxaa\naaaaaaoa\naaaaaaax"
        assertTrue(PatternEvaluator.BoardPattern.CheckCornerX.evaluate(s2, true) > 0)
        val s3 = "_aaaaaaa\naxaaaaaa\naaxaaaaa\naaaxaaaa\naaaaxaaa\naaaaaoaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.CheckCornerX.evaluate(s3, true) > 0)
    }

    @Test
    fun testExpectCorner1() {
        val s0 = "_x_x_aaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.ExpectCorner1.evaluate(s0, true) > 0)
        val s1 = "_x_xoxaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.ExpectCorner1.evaluate(s1, true) > 0)
        val s2 = "_x_xoaao\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.ExpectCorner1.evaluate(s2, true) > 0)
    }
    @Test
    fun testExpectCorner2() {
        val s0 = "_x__o_aa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.ExpectCorner2.evaluate(s0, true) > 0)
        val s1 = "_x__oxoa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.ExpectCorner2.evaluate(s1, true) > 0)
        val s2 = "_x__oaao\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.ExpectCorner2.evaluate(s2, true) > 0)
    }
    @Test
    fun testExpectCorner3() {
        val s0 = "_x_oxoaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.ExpectCorner3.evaluate(s0, true) > 0)
        val s1 = "_x_oaaao\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.ExpectCorner3.evaluate(s1, true) > 0)
        val s2 = "_x_oxo__\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.ExpectCorner3.evaluate(s2, true) > 0)
    }

    @Test
    fun testWing() {
        val s0 = "__xxxxx_\na_xaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.Wing.evaluate(s0, true) > 0)
    }

    @Test
    fun testAttackWing() {
        val s0 = "__xxxxx_\naooaaaaa\naooaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.AttackWing.evaluate(s0, true) > 0)
    }

    @Test
    fun testStandoff() {
        val s0 = "_oooo_x_\na_aaaa_a\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.Standoff.evaluate(s0, true) > 0)
    }

    @Test
    fun testAttackStandoff() {
        val s0 = "_o_xxxx_\naoaxaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.AttackStandoff.evaluate(s0, true) > 0)
        val s1 = "_oo_xxx_\naoaaxaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.AttackStandoff.evaluate(s1, true) > 0)
        val s2 = "_ooo_xx_\naoaaaxaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.AttackStandoff.evaluate(s2, true) > 0)
        val s3 = "_oooo_x_\naoaaaxaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa"
        assertTrue(PatternEvaluator.BoardPattern.AttackStandoff.evaluate(s3, true) > 0)
    }

    @Test
    fun testSafeX() {
        val s0 = "_aaaaaaa\naoaaaaaa\naaoaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaaoa\naaaaaaao"
        assertTrue(PatternEvaluator.BoardPattern.SafeX.evaluate(s0, true) > 0)
        val s1 = "_aaaaaaa\naoaaaaaa\naaoaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaaoa\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.SafeX.evaluate(s1, true) > 0)
        val s2 = "_aaaaaaa\naoaaaaaa\naaoaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.SafeX.evaluate(s2, true) > 0)
    }
    @Test
    fun testCrossFour() {
        val s0 = "_aaaaaaa\na_aaaaaa\naaoaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.CrossFour.evaluate(s0, true) > 0)
    }
    @Test
    fun testOneBlanks1() {
        val s0 = "_xaaaaaa\nxxaaaaaa\naaoaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.OneBlanks1.evaluate(s0, true) > 0)
    }
    @Test
    fun testOneBlanks2() {
        val s0 = "o_xaaaaa\nxxxaaaaa\naaoaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.OneBlanks2.evaluate(s0, true) > 0)
    }
    @Test
    fun testOneBlanks3() {
        val s0 = "oxxaaaaa\nx_xaaaaa\nxxxaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.OneBlanks3.evaluate(s0, true) > 0)
    }
    @Test
    fun testTwoBlanks1() {
        val s0 = "__xaaaaa\nxxxaaaaa\naaoaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.TwoBlanks1.evaluate(s0, true) < 0)
    }
    @Test
    fun testTwoBlanks2() {
        val s0 = "x_xaaaaa\nx_xaaaaa\nxxxaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.TwoBlanks2.evaluate(s0, true) < 0)
    }
    @Test
    fun testTwoBlanks3() {
        val s0 = "_xaaaaaa\nx_xaaaaa\nxxxaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.TwoBlanks3.evaluate(s0, true) < 0)
    }
    @Test
    fun testThreeBlanks1() {
        val s0 = "___xaaaa\nxxxxaaaa\naaoaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.ThreeBlanks1.evaluate(s0, true) > 0)
    }
    @Test
    fun testThreeBlanks2() {
        val s0 = "o_xaaaaa\n__xaaaaa\nxxxaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.ThreeBlanks2.evaluate(s0, true) > 0)
    }
    @Test
    fun testThreeBlanks3() {
        val s0 = "__xaaaaa\nx_xaaaaa\nxxxaaaaa\naaaoaaaa\naaaaoaaa\naaaaaoaa\naaaaaa_a\naaaaaaa_"
        assertTrue(PatternEvaluator.BoardPattern.ThreeBlanks3.evaluate(s0, true) > 0)
    }
}
