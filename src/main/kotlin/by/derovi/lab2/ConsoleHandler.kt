package by.derovi.lab2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.system.exitProcess

object ConsoleHandler {

    suspend fun start() {
        val input = Scanner(System.`in`)
        while (true) {
            val line = withContext(Dispatchers.IO) {
                 input.nextLine()
            }
            if (line.startsWith("!stop")) {
                mode.stop()
                exitProcess(0)
            }
            mode.handleInput(line)
        }
    }
}
