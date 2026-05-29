package com.hivestudio.server.common.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    private const val algorithm = "PBKDF2WithHmacSHA256"
    private const val iterations = 120_000
    private const val keyLength = 256
    private const val saltLength = 16
    private const val prefix = "pbkdf2"
    private const val legacyPrefix = "hash:"
    private val secureRandom = SecureRandom()

    fun hash(password: String): String {
        val salt = ByteArray(saltLength).also(secureRandom::nextBytes)
        val derived = derive(password, salt, iterations)
        return buildString {
            append(prefix)
            append('$')
            append(iterations)
            append('$')
            append(Base64.getEncoder().encodeToString(salt))
            append('$')
            append(Base64.getEncoder().encodeToString(derived))
        }
    }

    fun verify(password: String, storedHash: String): Boolean =
        when {
            storedHash.startsWith("$prefix$") -> verifyPbkdf2(password, storedHash)
            storedHash.startsWith(legacyPrefix) -> storedHash == "$legacyPrefix$password"
            else -> false
        }

    fun needsUpgrade(storedHash: String): Boolean = !storedHash.startsWith("$prefix$")

    private fun verifyPbkdf2(password: String, storedHash: String): Boolean {
        val parts = storedHash.split('$')
        if (parts.size != 4) return false

        val parsedIterations = parts[1].toIntOrNull() ?: return false
        val salt = runCatching { Base64.getDecoder().decode(parts[2]) }.getOrNull() ?: return false
        val expected = runCatching { Base64.getDecoder().decode(parts[3]) }.getOrNull() ?: return false
        val actual = derive(password, salt, parsedIterations)
        return MessageDigest.isEqual(expected, actual)
    }

    private fun derive(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
        return SecretKeyFactory.getInstance(algorithm).generateSecret(spec).encoded
    }
}
