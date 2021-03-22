package by.derovi.lab2
import by.derovi.lab2.modes.ClientMode
import by.derovi.lab2.modes.ServerMode
import by.derovi.lab2.modes.TunnelMode
import by.derovi.lab2.tcpapi.Mode
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

lateinit var mode: Mode

val scanner = Scanner(System.`in`)

fun main(args: Array<String>) = runBlocking {

    mode = when (args[0].toLowerCase()) {
        "client", "alice" -> ClientMode()
        "server", "bob" -> ServerMode()
        "tunnel", "mike" -> TunnelMode()
        else -> {
            error("by.derovi.lab2.tcpapi.Mode not found, please try again (Client/ Server)")
        }
    }
    launch {
        ConsoleHandler.start()
    }
    launch {
        mode.start(args)
    }
    return@runBlocking
}
