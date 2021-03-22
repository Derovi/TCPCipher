package by.derovi.lab2.tcpapi

interface Mode {
    suspend fun start(args: Array<String>)

    fun handleInput(input: String)

    fun stop()
}
