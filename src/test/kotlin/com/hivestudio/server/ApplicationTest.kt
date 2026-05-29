package com.hivestudio.server

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.util.UUID
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
        val token = loginAndExtractToken()
        val response = client.get("/api/v1/beats") {
            bearer(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("["))
        assertTrue(body.contains("]"))
    }

    @Test
    fun registerEndpointReturnsCreated() = testApplication {
        val email = "new-producer-${UUID.randomUUID()}@hive.dev"
        val response = client.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "email": "$email",
                  "password": "secret123",
                  "stageName": "North Hive"
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("North Hive"))
        assertTrue(response.bodyAsText().contains("token"))
    }

    @Test
    fun profileReflectsAuthorizedProducer() = testApplication {
        val token = registerAndExtractToken(
            email = "profile-check-${UUID.randomUUID()}@hive.dev",
            stageName = "Profile Check",
        )

        val response = client.get("/api/v1/profile") {
            bearer(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Profile Check"))
    }

    @Test
    fun profileRequiresBearerToken() = testApplication {
        val response = client.get("/api/v1/profile")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue(response.bodyAsText().contains("авторизация", ignoreCase = true))
    }

    @Test
    fun createBeatEndpointAcceptsJsonBody() = testApplication {
        val token = registerAndExtractToken()

        val beforeResponse = client.get("/api/v1/beats") {
            bearer(token)
        }
        val beforeCount = extractObjectCount(beforeResponse.bodyAsText())

        val createResponse = client.post("/api/v1/beats") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "title": "North District",
                  "genre": "Drill",
                  "bpm": 144,
                  "price": 3190.0,
                  "description": "Aggressive drill beat",
                  "mp3FileName": "north-district.mp3",
                  "coverImageFileName": "north-district-cover.jpg"
                }
                """.trimIndent()
            )
        }

        val afterResponse = client.get("/api/v1/beats") {
            bearer(token)
        }
        val afterCount = extractObjectCount(afterResponse.bodyAsText())

        assertEquals(HttpStatusCode.Created, createResponse.status)
        assertTrue(createResponse.bodyAsText().contains("North District"))
        assertEquals(beforeCount + 1, afterCount)
    }

    @Test
    fun simulatePurchaseEndpointChangesStatistics() = testApplication {
        val token = registerAndExtractToken()
        val createResponse = client.post("/api/v1/beats") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "title": "Purchase Check",
                  "genre": "Trap",
                  "bpm": 142,
                  "price": 3500.0,
                  "description": "Statistics check beat",
                  "mp3FileName": "purchase-check.mp3",
                  "coverImageFileName": "purchase-check-cover.jpg"
                }
                """.trimIndent()
            )
        }
        val beatId = extractStringField(createResponse.bodyAsText(), "id")

        val beforeResponse = client.get("/api/v1/beats/$beatId/stats") {
            bearer(token)
        }
        val beforePurchases = extractIntField(beforeResponse.bodyAsText(), "purchasesCount")

        val simulateResponse = client.post("/api/v1/beats/$beatId/simulate/purchase") {
            bearer(token)
        }
        val afterResponse = client.get("/api/v1/beats/$beatId/stats") {
            bearer(token)
        }
        val afterPurchases = extractIntField(afterResponse.bodyAsText(), "purchasesCount")

        assertEquals(HttpStatusCode.OK, simulateResponse.status)
        assertTrue(simulateResponse.bodyAsText().contains("purchase"))
        assertEquals(beforePurchases + 1, afterPurchases)
    }

    @Test
    fun historyEndpointReturnsDailyDynamics() = testApplication {
        val token = registerAndExtractToken()
        val createResponse = client.post("/api/v1/beats") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "title": "History Check",
                  "genre": "Lo-Fi",
                  "bpm": 88,
                  "price": 1990.0,
                  "description": "History check beat",
                  "mp3FileName": "history-check.mp3",
                  "coverImageFileName": "history-check-cover.jpg"
                }
                """.trimIndent()
            )
        }
        val beatId = extractStringField(createResponse.bodyAsText(), "id")
        client.post("/api/v1/beats/$beatId/simulate/play") {
            bearer(token)
        }

        val response = client.get("/api/v1/beats/$beatId/history?days=7") {
            bearer(token)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("playsCount"))
        assertTrue(response.bodyAsText().contains("date"))
    }

    @Test
    fun deleteBeatEndpointRemovesBeatAndStatsReturnNotFound() = testApplication {
        val token = registerAndExtractToken()
        val createResponse = client.post("/api/v1/beats") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "title": "Erase Me",
                  "genre": "Trap",
                  "bpm": 150,
                  "price": 4100.0,
                  "description": "Beat to delete",
                  "mp3FileName": "erase-me.mp3",
                  "coverImageFileName": "erase-me-cover.jpg"
                }
                """.trimIndent()
            )
        }
        val beatId = extractStringField(createResponse.bodyAsText(), "id")

        val deleteResponse = client.delete("/api/v1/beats/$beatId") {
            bearer(token)
        }
        val statsResponse = client.get("/api/v1/beats/$beatId/stats") {
            bearer(token)
        }

        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)
        assertEquals(HttpStatusCode.NotFound, statsResponse.status)
        assertTrue(statsResponse.bodyAsText().contains("not found", ignoreCase = true))
    }

    @Test
    fun updateBeatEndpointChangesBeatMetadata() = testApplication {
        val token = registerAndExtractToken()
        val createResponse = client.post("/api/v1/beats") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "title": "Editable Beat",
                  "genre": "Trap",
                  "bpm": 140,
                  "price": 2900.0,
                  "description": "Initial description",
                  "mp3FileName": "editable.mp3",
                  "coverImageFileName": "editable-cover.jpg"
                }
                """.trimIndent()
            )
        }
        val beatId = extractStringField(createResponse.bodyAsText(), "id")

        val updateResponse = client.put("/api/v1/beats/$beatId") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "title": "Editable Beat Revised",
                  "genre": "Drill",
                  "bpm": 148,
                  "price": 3700.0,
                  "description": "Updated description"
                }
                """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.OK, updateResponse.status)
        assertTrue(updateResponse.bodyAsText().contains("Editable Beat Revised"))
        assertTrue(updateResponse.bodyAsText().contains("3700.0"))
        assertTrue(updateResponse.bodyAsText().contains("Drill"))
    }

    @Test
    fun producersSeeOnlyOwnBeatCatalog() = testApplication {
        val firstToken = registerAndExtractToken(
            email = "first-${UUID.randomUUID()}@hive.dev",
            stageName = "First Hive",
        )
        client.post("/api/v1/beats") {
            bearer(firstToken)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "title": "Private Beat",
                  "genre": "Trap",
                  "bpm": 140,
                  "price": 2990.0,
                  "description": "Owned only by first producer",
                  "mp3FileName": "private.mp3",
                  "coverImageFileName": "private-cover.jpg"
                }
                """.trimIndent()
            )
        }

        val secondToken = registerAndExtractToken(
            email = "second-${UUID.randomUUID()}@hive.dev",
            stageName = "Second Hive",
        )
        val secondCatalog = client.get("/api/v1/beats") {
            bearer(secondToken)
        }

        assertEquals(HttpStatusCode.OK, secondCatalog.status)
        assertEquals(0, extractObjectCount(secondCatalog.bodyAsText()))
    }
}

private suspend fun ApplicationTestBuilder.registerAndExtractToken(
    email: String = "new-producer-${UUID.randomUUID()}@hive.dev",
    password: String = "secret123",
    stageName: String = "North Hive",
): String {
    val response = client.post("/api/v1/auth/register") {
        contentType(ContentType.Application.Json)
        setBody(
            """
            {
              "email": "$email",
              "password": "$password",
              "stageName": "$stageName"
            }
            """.trimIndent()
        )
    }
    val body = response.bodyAsText()
    assertEquals(HttpStatusCode.Created, response.status, body)
    return extractStringField(body, "token")
}

private suspend fun ApplicationTestBuilder.loginAndExtractToken(
    email: String = "producer@hivestudio.dev",
    password: String = "secret123",
): String {
    val response = client.post("/api/v1/auth/login") {
        contentType(ContentType.Application.Json)
        setBody(
            """
            {
              "email": "$email",
              "password": "$password"
            }
            """.trimIndent()
        )
    }
    val body = response.bodyAsText()
    assertEquals(HttpStatusCode.OK, response.status, body)
    return extractStringField(body, "token")
}

private fun io.ktor.client.request.HttpRequestBuilder.bearer(token: String) {
    header(HttpHeaders.Authorization, "Bearer $token")
}

private fun extractObjectCount(json: String): Int =
    "\"id\"".toRegex().findAll(json).count()

private fun extractIntField(json: String, fieldName: String): Int {
    val regex = """"$fieldName"\s*:\s*(\d+)""".toRegex()
    return regex.find(json)?.groupValues?.get(1)?.toInt()
        ?: error("Field $fieldName not found in response: $json")
}

private fun extractStringField(json: String, fieldName: String): String {
    val regex = """"$fieldName"\s*:\s*"([^"]+)"""".toRegex()
    return regex.find(json)?.groupValues?.get(1)
        ?: error("Field $fieldName not found in response: $json")
}
