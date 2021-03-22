package by.derovi.lab2.encryption

class NoEncryptScrambler : Scrambler {
    override val isReady: Boolean
        get() = true

    private var data = ByteArray(0)

    override fun accept(data: ByteArray) {
        this.data += data
    }

    override fun getResponse(): ByteArray {
        return ByteArray(0)
    }

    override fun encryptData(data: ByteArray): ByteArray {
        return data
    }

    override fun getDecryptedData(): ByteArray {
        return data.also { data = ByteArray(0) }
    }
}
