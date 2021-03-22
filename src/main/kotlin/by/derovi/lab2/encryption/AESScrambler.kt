package by.derovi.lab2.encryption

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AESScrambler: Scrambler {

    private var secretKey: SecretKeySpec? = null
    private lateinit var key: ByteArray

    fun setKey(myKey: String) {
        var sha: MessageDigest? = null
        try {
            key = myKey.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = Arrays.copyOf(key, 16)
            secretKey = SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    fun encrypt(strToEncrypt: ByteArray, secret: String): ByteArray {
        setKey(secret)
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(strToEncrypt)
    }

    fun decrypt(strToDecrypt: ByteArray, secret: String): ByteArray {
        setKey(secret)
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(strToDecrypt)
    }

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
        return encrypt(data, "ssshhhhhhhhhhh!!!!")
    }

    override fun getDecryptedData(): ByteArray {
        if (data.size % 16 != 0) {
            return ByteArray(0)
        }
        return decrypt(data, "ssshhhhhhhhhhh!!!!").also { data = ByteArray(0) }
    }
}
