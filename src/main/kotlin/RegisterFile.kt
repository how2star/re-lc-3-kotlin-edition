/**
 * 寄存器堆类，用于存取寄存器值。
 */
class RegisterFile {
    private val reg = Array<UShort>(8) { 0u } // Array 构造函数的第二个参数用于初始化数组

    /**
     * 获取寄存器值。
     */
    fun get(id: Number): UShort = reg[checkId(id)]

    /**
     * 写入寄存器值。
     */
    fun set(id: Number, value: UShort) {
        //TODO("请实现寄存器的写入")
        reg[checkId(id)] = value
    }

    /**
     * 将寄存器堆清零。
     */
    fun clear() {
        reg.fill(0u)
    }

    /**
     * 检查提供的寄存器编号是否正确，当正确时返回转换为 Int 类型的编号，否则抛出错误。
     */
    private fun checkId(id: Number): Int =
        // 注意 also 的使用，避免多次调用 toInt 或引入中间变量
        id.toInt().also {
            if (it < 0 || it > 7) throw IllegalArgumentException("寄存器编号无效：$id")
        }
}