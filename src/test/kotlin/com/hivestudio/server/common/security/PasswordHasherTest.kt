package com.hivestudio.server.common.security

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordHasherTest {
    @Test
    fun hashAndVerifyPassword() {
        val password = "secret123"
        val hash = PasswordHasher.hash(password)

        assertNotEquals(password, hash)
        assertTrue(PasswordHasher.verify(password, hash))
        assertFalse(PasswordHasher.verify("wrong-password", hash))
        assertFalse(PasswordHasher.needsUpgrade(hash))
    }

    @Test
    fun legacyHashIsStillAcceptedForMigration() {
        val legacyHash = "hash:secret123"

        assertTrue(PasswordHasher.verify("secret123", legacyHash))
        assertTrue(PasswordHasher.needsUpgrade(legacyHash))
    }
}
