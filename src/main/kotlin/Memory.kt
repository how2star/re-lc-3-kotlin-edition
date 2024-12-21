/**
 * 内存类，用于存取内存数据。
 *
 * 内存的实现采取压缩存储方式，即值为 0 的数据将不会记录在内存中。
 *
 * 由于 Re LC-3 没有设备驱动寄存器，因此 0xFE00 至 0xFFFF 的空间将视作没有定义。
 */
class Memory {
    private val mem = HashMap<UShort, UShort>()

    /**
     * 获取内存地址上的值。
     */
    fun get(addr: UShort, mode: Mode): UShort =
        mem.getOrDefault(checkAddr(addr, mode), 0u)

    /**
     * 设置内存地址上的值。
     */
    fun set(addr: UShort, value: UShort, mode: Mode) {
        checkAddr(addr, mode)

        if (value == (0u).toUShort()) {
            mem.remove(addr)
        } else {
            mem[addr] = value
        }
    }

    /**
     * 检查地址是否有效。
     */
    private fun checkAddr(addr: UShort, mode: Mode): UShort =
        addr.also {
            if (it >= 0xfe00u) throw IllegalArgumentException("地址 ${it.toHex()} 无效")

            if (mode.isUser() && it < 0x3000u) {
                throw IllegalArgumentException("不能在用户模式下访问 ${it.toHex()} 处的内存")
            }
        }
}