/**
 * PC 寄存器类。
 */
class ProgramCounter {
    private var pc: UShort = 0u

    /**
     * 移动 PC 到指定位置。
     */
    fun goto(p: UShort) {
        pc = p
    }

    /**
     * 为 PC 附加指定的偏移量。
     */
    fun offset(p: Short) {
        pc = (pc.toInt() + p).toUShort()
    }

    /**
     * 获取 PC 值，并将 PC 自增。
     */
    fun getAndInc(): UShort = pc++

    //获取 PC 的值, PC 自身不自增
    fun get(): UShort = pc
}