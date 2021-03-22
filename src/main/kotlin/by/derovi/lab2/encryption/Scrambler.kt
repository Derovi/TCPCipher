package by.derovi.lab2.encryption

interface Scrambler {
    val isReady: Boolean
    fun accept(data: ByteArray)
    fun getResponse(): ByteArray
    fun encryptData(data: ByteArray): ByteArray
    fun getDecryptedData(): ByteArray
}