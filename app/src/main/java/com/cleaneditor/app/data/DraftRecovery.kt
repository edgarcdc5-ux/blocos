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
            true
        }.getOrDefault(false)
    }

    fun listBackups(context: Context): List<File> =
        File(context.filesDir, "draft-backups").listFiles()
            ?.filter { it.isFile && it.extension == "txt" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
}
