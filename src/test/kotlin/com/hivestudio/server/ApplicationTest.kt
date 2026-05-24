package com.hivestudio.server

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
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

    @Test
    fun createBeatEndpointAcceptsJsonBody() = testApplication {
        val beforeResponse = client.get("/api/v1/beats")
        val beforeCount = extractObjectCount(beforeResponse.bodyAsText())

        val createResponse = client.post("/api/v1/beats") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "title": "North District",
                  "genre": "Drill",
                  "bpm": 144,
                  "price": 3190.0,
                  "description": "Aggressive drill beat",
                  "mp3FileName": "north-district.mp3"
                }
                """.trimIndent()
            )
        }

        val afterResponse = client.get("/api/v1/beats")
        val afterCount = extractObjectCount(afterResponse.bodyAsText())

        assertEquals(HttpStatusCode.Created, createResponse.status)
        assertTrue(createResponse.bodyAsText().contains("North District"))
        assertEquals(beforeCount + 1, afterCount)
    }

    @Test
    fun simulatePurchaseEndpointChangesStatistics() = testApplication {
        val beatId = "22222222-2222-2222-2222-222222222222"
        val beforeResponse = client.get("/api/v1/beats/$beatId/stats")
        val beforePurchases = extractIntField(beforeResponse.bodyAsText(), "purchasesCount")

        val simulateResponse = client.post("/api/v1/beats/$beatId/simulate/purchase")
        val afterResponse = client.get("/api/v1/beats/$beatId/stats")
        val afterPurchases = extractIntField(afterResponse.bodyAsText(), "purchasesCount")

        assertEquals(HttpStatusCode.OK, simulateResponse.status)
        assertTrue(simulateResponse.bodyAsText().contains("purchase"))
        assertEquals(beforePurchases + 1, afterPurchases)
    }
}

private fun extractObjectCount(json: String): Int =
    "\"id\"".toRegex().findAll(json).count()

private fun extractIntField(json: String, fieldName: String): Int {
    val regex = """"$fieldName"\s*:\s*(\d+)""".toRegex()
    return regex.find(json)?.groupValues?.get(1)?.toInt()
        ?: error("Field $fieldName not found in response: $json")
}
