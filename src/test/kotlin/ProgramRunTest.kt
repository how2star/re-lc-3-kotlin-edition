import kotlin.test.Test
import kotlin.test.assertEquals

class ProgramRunTest {

    @Test
    fun `Run Program`() {
        // 可以修改为其它测试程序，但要相应更新测试条件
        val binSource = """
            0010110000010100
            0101000000100000
            0001001000100001
            1001010001111111
            0000011000000011
            0111000110000000
            0111001110000001
            0111010110000010
            1110011000001100
            0111011110000011
            0100100000000111
            0111011110000100
            1110000000000010
            1100000000000000
            1101000000000000
            1010000000000101
            0111000110000101
            1111000000100101
            0101011011100000
            0001011011101010
            1100000111000000
            0100000000000000
        """.trimIndent()

        val src = binSource.lines().map { it.toInt(2).toUShort() }

        val interpreter = Interpreter()

        interpreter.loadProgram(0x3000u, src)
        interpreter.init()
        interpreter.run()

        // 测试条件，可视情况修改
        interpreter.memory.run {
            val getAt = { addr: Int -> get(addr.toUShort(), Mode.SUPERVISOR).toInt() }

            assertEquals(getAt(0x4000), 0x0)
            assertEquals(getAt(0x4001), 0x1)
            assertEquals(getAt(0x4002), 0xfffe)
            assertEquals(getAt(0x4003), 0x3015)
            assertEquals(getAt(0x4004), 0xa)
            assertEquals(getAt(0x4005), 0x0)
        }
    }
}