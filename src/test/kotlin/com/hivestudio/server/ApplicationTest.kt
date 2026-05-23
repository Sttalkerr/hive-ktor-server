package com.hivestudio.server

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun healthEndpointReturnsOk() = testApplication {
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("ok"))
    }

    @Test
    fun beatListEndpointReturnsOk() = testApplication {
        val response = client.get("/api/v1/beats")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("["))
        assertTrue(body.contains("]"))
    }

    @Test
    fun registerEndpointReturnsCreated() = testApplication {
        val response = client.post("/api/v1/auth/register")
        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("demo-jwt-token"))
    }
}
