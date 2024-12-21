/**
 * PSR 寄存器实现，完成数值和条件码/模式的相互转换。
 */
class PSR {
    /**
     * 程序所处模式。
     */
    var mode: Mode = Mode.SUPERVISOR

    /**
     * 程序条件码。
     */
    var cond: Condition = Condition.ZERO

    /**
     * PSR 数值属性。
     *
     * 该属性通过重新定义 Getter 和 Setter 使得对其赋值时能自动获取 / 计算 mode 和 cond 的值。
     */
    var value: UShort
        get() = ((mode.value shl 15) or (cond.value)).toUShort()
        set(value) {
            //cond = Condition.fromBits(value.toInt())
            cond = value.toCondition()
            mode = if ((value.toInt() and (1 shl 15)) == 0) Mode.SUPERVISOR else Mode.USER
        }
}