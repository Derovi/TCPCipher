package by.derovi.lab2.modes

import by.derovi.lab2.encryption.AESScrambler
import by.derovi.lab2.tcpapi.Mode
import by.derovi.lab2.tcpapi.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TunnelMode : Mode {
    lateinit var server: TCPServer
    lateinit var client: TCPClient

    override suspend fun start(args: Array<String>) {
        val selfPort = args[1].toInt()
        val dstAddress = args[2]
        val dstPort = args[3].toInt()

        tcpServer(selfPort) {
            println("Starting server on port $selfPort!")

            onStarted {
                println("Server started!")
            }
            onStopped {
                println("Server stopped")
            }
            clientMapper {
                onConnect {
                    println("Alice#${socket.port} connected!")
                }
                onDisconnect {
                    forgetClient(this)
                    println("Alice#${socket.port} disconnected!")
                }
                onData {
                    println("Alice#${socket.port} -> Bob: ${String(it)}")
                    client.sendData(it)
                }
            }
        }.also {
            server = it
            GlobalScope.launch { server.start() }
        }

        tcpClient(dstAddress, dstPort) {
            println("Connecting to Bob!")

            onConnect {
                println("Connected to Bob")
            }
            onDisconnect {
                println("Disconnected from Bob")
            }
            onData {
                for (client in server.clients) {
                    client.sendData(it)
                }
                println("Bob -> Alice: ${String(it)}")
            }
        }.also {
            client = it
        }
    }

    override fun handleInput(input: String) {
        when {
            input.startsWith("A-") ->
                for (client in server.clients) {
                    client.sendData(input.substring(2).toByteArray(), AESScrambler())
                }
            input.matches(Regex("A#\\d+-.+")) -> {
                val port = input.substring(1, input.lastIndex - 1).toInt()
                for (client in server.clients) {
                    if (client.socket.port == port) {
                        client.sendData(input.substring(2).toByteArray(), AESScrambler())
                    }
                }
            }
            input.startsWith("B-") ->
                client.sendData(input.substring(2).toByteArray(), AESScrambler())
            else -> {
                println("Example: ")
                println("A-%message% <--> Send message to Alices")
                println("A#%port%-%message% <--> Send message to Alices")
                println("B-%message% <--> Send message to Bob")
            }
        }
    }

    override fun stop() {
        server.stop()
        client.disconnect()
    }
}
