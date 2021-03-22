package by.derovi.lab2.modes

import by.derovi.lab2.tcpapi.Mode
import by.derovi.lab2.encryption.AESScrambler
import by.derovi.lab2.tcpapi.TCPServer
import by.derovi.lab2.tcpapi.tcpServer

class ServerMode : Mode {
    lateinit var server: TCPServer
    override suspend fun start(args: Array<String>) {
         tcpServer(port = args[1].toInt(), AESScrambler()) {
            println("Server started on port $port")
            onStarted {
                println("Server started!")
            }
            onStopped {
                println("Server stopped, ${clients.size} connections aborted!")
            }
            clientMapper {
                onConnect {
                    println("\rClient ${socket.port} connected")
                }
                onDisconnect {
                    println("\rClient ${socket.port} disconnected")
                }
                onData { message ->
                    println("\rAlice#${socket.port} -> ${String(message)}")
                }
            }
        }.also { server = it; it.start(); }
    }

    override fun stop() {
        server.stop()
    }

    override fun handleInput(input: String) {
        for (client in server.clients) {
            client.sendData(input.toByteArray())
        }
    }
}
