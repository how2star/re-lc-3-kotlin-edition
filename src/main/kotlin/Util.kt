/**
 * 将数字转换为形如 x1234 的十六进制表示。
 */
fun UShort.toHex(): String = "x" + toString(16).padStart(4, '0')