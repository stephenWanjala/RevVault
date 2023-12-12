package com.github.stephenwanjala.auth.security.hashing

import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

class SHA256HashingService:HashingService {
    override fun generateSaltedHash(value: String, saltLength: Int): SaltedHash {
        val salt = generateSalt(saltLength)
        val hash = generateSHA256Hash(salt + value)
        return SaltedHash(hash = hash, salt = salt)
    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        val generatedHash = generateSHA256Hash(saltedHash.salt + value)
        return generatedHash == saltedHash.hash
    }

    private fun generateSalt(length: Int): String {
        val random = SecureRandom()
        val salt = ByteArray(length)
        random.nextBytes(salt)
        return bytesToHex(salt)
    }

    private fun generateSHA256Hash(input: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(input.toByteArray())
        return bytesToHex(digest)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val bigInteger = BigInteger(1, bytes)
        return bigInteger.toString(16).padStart(2 * bytes.size, '0')
    }
}