package by.derovi.lab2.modes

import by.derovi.lab2.tcpapi.Mode
import by.derovi.lab2.encryption.AESScrambler
import by.derovi.lab2.tcpapi.TCPClient
import by.derovi.lab2.tcpapi.tcpClient

class ClientMode : Mode {
    private lateinit var client: TCPClient

    override suspend fun start(args: Array<String>) {
        val address = args[1]
        val port = args[2].toInt()
        client = tcpClient(address, port, AESScrambler()) {
            println("Connecting to server $address:$port")

            onConnect {
                println("Connected to server")
            }
            onDisconnect {
                println("Disconnected from server")
            }
            onData { message ->
                println("Bob -> ${String(message)}")
            }
        }
    }

    override fun stop() {
        client.disconnect()
    }

    override fun handleInput(input: String) {
        client.sendData(input.toByteArray())
    }
}
