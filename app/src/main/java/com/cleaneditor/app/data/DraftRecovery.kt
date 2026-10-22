package com.cleaneditor.app.data

import android.content.Context
import java.io.File
import java.security.MessageDigest

/**
 * Guarda um rascunho local antes do salvamento automático.
 * O diretório pertence ao app e não é compartilhado com outros aplicativos.
 */
object DraftRecovery {
    fun backup(context: Context, uri: String, name: String, text: String): Boolean {
        return runCatching {
            val dir = File(context.filesDir, "draft-backups").apply { mkdirs() }
            val key = MessageDigest.getInstance("SHA-256")
                .digest(uri.toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
            val destination = File(dir, "$key.txt")
            val temp = File(dir, "$key.tmp")
            temp.writeText(text, Charsets.UTF_8)
            if (!temp.renameTo(destination)) {
                temp.copyTo(destination, overwrite = true)
                temp.delete()
            }
            File(dir, "$key.name").writeText(name.take(200), Charsets.UTF_8)
            val versionsDir = File(context.filesDir, "draft-history/$key").apply { mkdirs() }
            val previous = versionsDir.listFiles()?.filter { it.extension == "txt" }
                ?.maxByOrNull { it.lastModified() }
            if (previous == null || previous.readText(Charsets.UTF_8) != text) {
                File(versionsDir, "${System.currentTimeMillis()}.txt").writeText(text, Charsets.UTF_8)
                versionsDir.listFiles()?.filter { it.extension == "txt" }
                    ?.sortedByDescending { it.lastModified() }?.drop(30)?.forEach { it.delete() }
            }
            true
        }.getOrDefault(false)
    }

    data class Entry(val key: String, val name: String, val file: File, val savedAt: Long)

    fun entries(context: Context): List<Entry> = listBackups(context).map { file ->
        val key = file.nameWithoutExtension
        val display = File(file.parentFile, "$key.name")
            .takeIf { it.exists() }?.readText(Charsets.UTF_8) ?: "Rascunho"
        Entry(key, display, file, file.lastModified())
    }

    fun versions(context: Context, key: String): List<Entry> {
        if (!key.matches(Regex("[0-9a-f]{64}"))) return emptyList()
        val directory = File(context.filesDir, "draft-history/$key")
        val display = File(context.filesDir, "draft-backups/$key.name")
            .takeIf { it.exists() }?.readText(Charsets.UTF_8) ?: "Rascunho"
        return directory.listFiles()?.filter { it.isFile && it.extension == "txt" }
            ?.sortedByDescending { it.lastModified() }
            ?.map { Entry(key, display, it, it.lastModified()) } ?: emptyList()
    }

    fun read(entry: Entry): String? = runCatching {
        if (entry.file.length() > 5_000_000L) null else entry.file.readText(Charsets.UTF_8)
    }.getOrNull()

    fun listBackups(context: Context): List<File> =
        File(context.filesDir, "draft-backups").listFiles()
            ?.filter { it.isFile && it.extension == "txt" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
}
