package com.hivestudio.server.storage

import io.ktor.http.content.streamProvider
import io.ktor.http.content.PartData
import java.io.File
import java.util.UUID

class FileStorageService(
    private val settings: StorageSettings,
) {
    private val uploadRoot: File by lazy {
        File(settings.uploadDir).apply { mkdirs() }
    }

    fun save(part: PartData.FileItem, prefix: String): String {
        val originalName = part.originalFileName?.substringAfterLast('/')?.substringAfterLast('\\')
            ?.ifBlank { null }
            ?: "$prefix.bin"
        val savedName = "${UUID.randomUUID()}-$originalName"
        val target = File(uploadRoot, savedName)

        part.streamProvider().use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return savedName
    }
}
