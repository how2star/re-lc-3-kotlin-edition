/**
 * 条件码数据定义。
 */
enum class Condition(val value: Int) {
    NEGATIVE(0b100),
    ZERO(0b010),
    POSITIVE(0b001);

    companion object {
        /**
         * 转换三位二进制码为条件码。
         */
        fun fromBits(b: Int): Condition =
            when (b) {
                0b100 -> NEGATIVE
                0b010 -> ZERO
                0b001 -> POSITIVE
                else -> throw IllegalArgumentException("条件码 ${b.toString(2)} 无效")
            }
    }
}

/**
 * 根据数值（视作补码）的正负性转换为条件码。
 */
fun UShort.toCondition(): Condition =
    when {
        this and (1 shl 15).toUShort() != (0u).toUShort() -> Condition.NEGATIVE
        this == (0u).toUShort() -> Condition.ZERO
        else -> Condition.POSITIVE
    }