package by.derovi.lab2.tcpapi

import by.derovi.lab2.encryption.NoEncryptScrambler
import by.derovi.lab2.encryption.Scrambler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap

class TCPServer(val port: Int, val scrambler: Scrambler) {
    private val mutableClients: MutableSet<TCPClient> = ConcurrentHashMap.newKeySet()
    val clients: Set<TCPClient>
        get() = mutableClients

    lateinit var server: ServerSocket

    suspend fun start() {
        server = withContext(Dispatchers.IO) {
            ServerSocket(port)
        }
        onStartedHandler?.invoke()
        while (true) {
            val client = TCPClient(withContext(Dispatchers.Default) { server.accept() }, scrambler)
            clientMapper?.invoke(client)
            mutableClients.add(client)
        }
    }

    fun forgetClient(client: TCPClient) {
        mutableClients.remove(client)
    }

    fun stop() {
        server.close()
    }

    private var onStartedHandler: (() -> Unit)? = null
    private var onStoppedHandler: (() -> Unit)? = null
    private var clientMapper: (TCPClient.() -> Unit)? = null

    fun onStarted(handler: () -> Unit) {
        onStartedHandler = handler
    }

    fun clientMapper(init: TCPClient.() -> Unit) {
        clientMapper = init
    }

    fun onStopped(handler: () -> Unit) {
        onStoppedHandler = handler
    }
}

fun tcpServer(port: Int, scrambler: Scrambler = NoEncryptScrambler(), init: TCPServer.() -> Unit): TCPServer {
    return TCPServer(port, scrambler).also(init)
}
