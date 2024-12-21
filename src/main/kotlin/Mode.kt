/**
 * 程序执行所处的模式。
 */
enum class Mode(val value: Int) {
    SUPERVISOR(0),
    USER(1);

    fun isUser() = this == USER

    fun isSupervisor() = this == SUPERVISOR
}