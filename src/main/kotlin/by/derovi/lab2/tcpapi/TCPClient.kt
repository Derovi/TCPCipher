package by.derovi.lab2.tcpapi

import by.derovi.lab2.encryption.NoEncryptScrambler
import by.derovi.lab2.encryption.Scrambler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.Socket

class TCPClient(val socket: Socket, val scrambler: Scrambler) {
    init {
        GlobalScope.launch {
            val buffer = ByteArray(10)
            try {
                while (true) {
                    val bytesRead: Int = socket.getInputStream().read(buffer)
                    if (bytesRead == -1) break
                    scrambler.accept(buffer.copyOf(bytesRead))
                    if (scrambler.isReady) {
                        val data = scrambler.getDecryptedData()
                        if (data.isNotEmpty()) {
                            onDataHandler?.invoke(data)
                        }
                    } else {
                        val response = scrambler.getResponse()
                        if (response.isNotEmpty()) {
                            sendData(response)
                        }
                    }
                }
            } catch (e: IOException) {
                println("Connection broken!")
            }
            onDisconnectHandler?.invoke()
        }
    }

    fun disconnect() {
        socket.close()
    }

    fun onConnect(handler: () -> Unit) {
        handler()
    }

    fun onDisconnect(handler: () -> Unit) {
        onDisconnectHandler = handler
    }

    fun onResponse(handler: (message: String) -> Unit) {
        onResponseHandler = handler
    }

    fun onData(handler: (message: ByteArray) -> Unit) {
        onDataHandler = handler
    }

    var onDisconnectHandler: (() -> Unit)? = null
    var onResponseHandler: ((message: String) -> Unit)? = null
    var onDataHandler: ((message: ByteArray) -> Unit)? = null

    fun sendData(message: ByteArray, localScrambler: Scrambler = scrambler): Boolean {
        try {
            socket.getOutputStream().run {
                write(localScrambler.encryptData(message))
                flush()
                return true
            }
        } catch (exception: Exception) {
            return false
        }
    }
}

suspend fun tcpClient(address: String, port: Int, scrambler: Scrambler = NoEncryptScrambler(), init: TCPClient.() -> Unit): TCPClient {
    return tcpClient(
        withContext(Dispatchers.Default) {
            Socket(address, port)
        },
        scrambler, init
    )
}

fun tcpClient(socket: Socket, scrambler: Scrambler = NoEncryptScrambler(), init: TCPClient.() -> Unit): TCPClient {
    return TCPClient(socket, scrambler).also(init)
}
