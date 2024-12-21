/**
 * 解释器核心，处理指令，执行操作。
 */
class Interpreter {
    val psr = PSR()
    val pc = ProgramCounter()
    val reg = RegisterFile()
    val memory = Memory()
    
    private var running = false

    /**
     * 解释并执行读取的指令（已转换为 Int 以供移位操作）。
     */
    private fun interpret(inst: Int) {
        // when 类似于 C 语言中的 switch，根据操作码进行不同操作
        when (val opCode = inst shr 12) {
            0b0000 -> {
                // BR
                val cc = inst shr 9 and 0b111
                if (cc and psr.cond.value != 0) {
                    // 发生跳转，注意如何解释偏移量并加到 PC 上
                    pc.offset((inst and 0x1ff).toSigned(9))
                    //val pcIncreased = pc.getAndInc()
                    //pc.goto((pcIncreased + (inst and 0x1ff).toSigned(9).toUShort()).toUShort())
                }
            }

            0b1111 -> {
                // TRAP
                // 由于 Re LC-3 是用户模式虚拟机，这里我们并不执行完整的中断流程，仅对内置操作进行代码层面的实现
                when (val trapVec = inst and 0xff) {
                    // HALT
                    0x25 -> running = false

                    // OUT
                    0x21 -> print(reg.get(0).toInt().toChar())

                    // PUTS
                    0x22 -> {
                        //TODO("请实现 PUTS 调用")
                        var addr = reg.get(0)
                        while(true){
                            val char = memory.get(addr, psr.mode).toInt()
                            if(char == 0) break     // 遇到结束符，退出
                            print(char.toChar())
                            addr++
                        }
                    }

                    else -> throw IllegalArgumentException("不支持的中断号 $trapVec")
                }
            }

            0b1101 -> {
                // 保留操作码
                print("代码中有保留操作码")
                print(psr.mode)
                print(psr.value)
                print(psr.cond)
                print("请实现指令 ${opCode.toString(2)} 的解释")
                print(inst)
                //throw IllegalArgumentException("无法执行保留操作码 1101")
            }

            //else -> TODO("请实现指令 ${opCode.toString(2)} 的解释")

            0b0001 -> {
                // ADD
                val dr = (inst shr 9) and 0b111
                val sr1 = (inst shr 6) and 0b111
                if ((inst shr 5) and 1 == 1) {
                    // 立即数模式
                    val imm5 = (inst and 0b11111).toSigned(5)
                    reg.set(dr, (reg.get(sr1).toInt() + imm5).toUShort())

                } else {
                    // 寄存器模式
                    val sr2 = inst and 0b111
                    reg.set(dr, (reg.get(sr1).toInt() + reg.get(sr2).toInt()).toUShort())

                }
                psr.value = reg.get(dr)
            }

            0b0101 -> {
                // AND
                val dr = (inst shr 9) and 0b111
                val sr1 = (inst shr 6) and 0b111
                if ((inst shr 5) and 1 == 1) {
                    // 立即数模式
                    val imm5 = (inst and 0b11111).toSigned(5)
                    reg.set(dr, (reg.get(sr1).toInt() and imm5.toInt()).toUShort())
                } else {
                    // 寄存器模式
                    val sr2 = inst and 0b111
                    reg.set(dr, (reg.get(sr1).toInt() and reg.get(sr2).toInt()).toUShort())
                }
                psr.value = reg.get(dr)
            }

            0b1001 -> {
                // NOT
                val dr = (inst shr 9) and 0b111
                val sr = (inst shr 6) and 0b111
                reg.set(dr, reg.get(sr).inv())
                psr.value = reg.get(dr)
            }

            0b0010 -> {
                // LD
                //pc.offset((inst and 0x1ff).toSigned(9))
                val dr = (inst shr 9) and 0b111
                val offset = (inst and 0x1ff).toSigned(9)
                reg.set(dr, memory.get((pc.get() + offset.toUShort()).toUShort(), psr.mode))
                psr.value = reg.get(dr)
            }

            0b1010 -> {
                // LDI
                val dr = (inst shr 9) and 0b111
                val offset = (inst and 0x1FF).toSigned(9)
                val addr = memory.get((pc.get() + offset.toUShort()).toUShort(), psr.mode)
                reg.set(dr, memory.get(addr, psr.mode))
                psr.value = reg.get(dr)
            }

            0b0110 -> {
                // LDR
                val dr = (inst shr 9) and 0b111
                val baseR = (inst shr 6) and 0b111
                val offset = (inst and 0x3F).toSigned(6)
                reg.set(dr, memory.get((reg.get(baseR) + offset.toUShort()).toUShort(), psr.mode))
                psr.value = reg.get(dr)
            }

            0b0011 -> {
                // ST
                val sr = (inst shr 9) and 0b111
                val offset = (inst and 0x1ff).toSigned(9)
                memory.set((pc.get() + offset.toUShort()).toUShort(), reg.get(sr), psr.mode)
            }

            0b1011 -> {
                // STI
                val sr = (inst shr 9) and 0b111
                val offset = (inst and 0x1FF).toSigned(9)
                val addr = memory.get((pc.get() + offset.toUShort()).toUShort(), psr.mode)
                memory.set(addr, reg.get(sr), psr.mode)
            }

            0b0111 -> {
                // STR
                val sr = (inst shr 9) and 0b111
                val baseR = (inst shr 6) and 0b111
                val offset = (inst and 0x3F).toSigned(6)
                memory.set((reg.get(baseR) + offset.toUShort()).toUShort(), reg.get(sr), psr.mode)
            }

            0b1100 -> {
                // JMP / RET
                val baseR = (inst shr 6) and 0b111
                pc.goto(reg.get(baseR))
            }

            0b0100 -> {
                // JSR / JSRR
                reg.set(7, pc.get())
                if ((inst shr 11) and 1 == 1) {
                    val offset = (inst and 0x7FF).toSigned(11)
                    pc.goto((pc.get() + offset.toUShort()).toUShort())
                } else {
                    val baseR = (inst shr 6) and 0b111
                    pc.goto(reg.get(baseR))
                }
            }

            0b1000 -> {
                // RTI (Return from Interrupt)
                if (psr.mode == Mode.SUPERVISOR) {
                    val pcValue = (pc.get() - 1u).toUShort()
                    val savedPC = memory.get(pcValue, Mode.SUPERVISOR)
                    val savedPSR = memory.get((pcValue + 1u).toUShort(), Mode.SUPERVISOR)
                    pc.goto(savedPC)
                    psr.value = savedPSR
                } else {
                    throw IllegalStateException("RTI 只能在管理模式下执行")
                }
            }

            0b1110 -> {
                // LEA
                val dr = (inst shr 9) and 0b111
                val offset = (inst and 0x1FF).toSigned(9)
                reg.set(dr, (pc.get() + offset.toUShort()).toUShort())
            }

            else -> throw IllegalArgumentException("未知指令操作码 ${opCode.toString(2)}")
        }
    }

    /**
     * 执行下一条指令。
     */
    private fun runNext() {
        interpret(memory.get(pc.getAndInc(), psr.mode).toInt())
    }

    /**
     * 初始化虚拟机。
     */
    fun init() {
        reg.clear()
        pc.goto(0x3000u)
    }

    /**
     * 执行指令，直到遇到 HALT 或者产生错误。
     */
    fun run() {
        running = true
        while (running) runNext()
    }

    /**
     * 加载程序到指定位置。
     */
    fun loadProgram(origin: UShort, content: Iterable<UShort>) {
        var addr = origin
        for (c in content) {
            // 在管理模式下填充程序
            memory.set(addr, c, Mode.SUPERVISOR)
            addr++
        }
    }
}

/**
 * 将给定的数字按给定位数解释为有符号数。
 */
private fun Number.toSigned(bits: Int): Short {
    val max = (1 shl (bits - 1)) - 1
    return toInt().let { if (it > max) it - (1 shl bits) else it }.toShort()
}