package com.cleaneditor.app.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cleaneditor.app.AppViewModel
import com.cleaneditor.app.data.DraftRecovery

private val templates = linkedMapOf(
    "Anotações" to "# Anotações\n\nData: \n\n## Ideias\n\n## Próximos passos\n",
    "Reunião" to "# Ata de reunião\n\nData: \nParticipantes: \n\n## Pauta\n\n## Decisões\n\n## Responsáveis\n",
    "Checklist" to "# Checklist\n\n- [ ] Tarefa 1\n- [ ] Tarefa 2\n- [ ] Tarefa 3\n",
    "Diário" to "# Diário\n\nData: \n\n## O que aconteceu\n\n## Reflexões\n"
)

@Composable
fun AdvancedDocumentTools(vm: AppViewModel, section: String) {
    val ctx = LocalContext.current
    val p = LocalPalette.current
    val prefs = remember { ctx.getSharedPreferences("cleaneditor_advanced_tags", android.content.Context.MODE_PRIVATE) }
    var refresh by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<DraftRecovery.Entry?>(null) }
    var editTags by remember { mutableStateOf(false) }
    var tags by remember { mutableStateOf("") }
    var draft by remember { mutableStateOf<String?>(null) }
    var draftName by remember { mutableStateOf("documento.txt") }
    var feedback by remember { mutableStateOf("") }
    val save = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        val text = draft
        if (uri != null && text != null) {
            feedback = if (runCatching {
                ctx.contentResolver.openOutputStream(uri)?.use { it.write(text.toByteArray(Charsets.UTF_8)) }
                    ?: error("Não foi possível abrir destino")
            }.isSuccess) "Arquivo criado" else "Falha ao salvar arquivo"
        }
        draft = null
    }
    val entries = remember(refresh) { DraftRecovery.entries(ctx) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (feedback.isNotEmpty()) Text(feedback, color = p.accent)
        if (section == "Modelos") {
            Text("Escolha um modelo. O conteúdo será salvo como um novo arquivo TXT.", color = p.muted)
            templates.forEach { (name, content) ->
                GlassCard(Modifier.fillMaxWidth(), onClick = {
                    draft = content
                    save.launch(name.lowercase() + ".txt")
                }) {
                    Text(name, color = p.text)
                    Text("Criar documento a partir do modelo", color = p.muted)
                }
            }
        } else {
            Text("Organize e compartilhe cópias locais dos rascunhos. Os arquivos originais não serão alterados.", color = p.muted)
            if (entries.isEmpty()) Text("Nenhuma cópia local encontrada", color = p.muted)
            entries.forEach { e ->
                val stored = prefs.getString(e.key, "") ?: ""
                val favorite = stored.split(",").contains("★")
                val labels = stored.split(",").filter { it.isNotBlank() && it != "★" }.joinToString(", ")
                GlassCard(Modifier.fillMaxWidth(), onClick = {
                    selected = e
                    tags = labels
                    editTags = section == "Favoritos e tags"
                }) {
                    Text((if (favorite) "★ " else "") + e.name, color = p.text)
                    if (labels.isNotBlank()) Text("Tags: " + labels, color = p.muted)
                    Text(if (section == "Favoritos e tags") "Editar favorito e tags" else "Compartilhar ou exportar", color = p.accent)
                }
            }
        }
    }
    if (selected != null) {
        val e = selected!!
        val current = prefs.getString(e.key, "") ?: ""
        val fav = current.split(",").contains("★")
        AlertDialog(onDismissRequest = { selected = null },
            title = { Text(e.name) },
            text = {
                Column {
                    if (editTags) {
                        OutlinedTextField(tags, { tags = it }, label = { Text("Tags separadas por vírgula") })
                        Text(if (fav) "Marcado como favorito" else "Não favorito")
                    } else Text("Enviar o conteúdo do backup para outro aplicativo ou salvar uma cópia.")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editTags) {
                        val normalized = tags.split(",").map { it.trim() }.filter { it.isNotBlank() && it != "★" }.distinct().take(10)
                        prefs.edit().putString(e.key, (if (fav) listOf("★") else emptyList<String>()).plus(normalized).joinToString(",")).apply()
                        refresh++
                    } else {
                        val content = DraftRecovery.read(e)
                        if (content != null) {
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, e.name)
                                putExtra(Intent.EXTRA_TEXT, content)
                            }
                            ctx.startActivity(Intent.createChooser(send, "Compartilhar documento"))
                        } else feedback = "Não foi possível ler o backup"
                    }
                    selected = null
                }) { Text(if (editTags) "Salvar tags" else "Compartilhar") }
            },
            dismissButton = {
                Row {
                    if (editTags) TextButton(onClick = {
                        prefs.edit().putString(e.key,
                            (if (fav) tags else "★," + tags)).apply()
                        refresh++
                        selected = null
                    }) { Text(if (fav) "Desfavoritar" else "Favoritar") }
                    else TextButton(onClick = {
                        val content = DraftRecovery.read(e)
                        if (content != null) {
                            draft = content
                            save.launch(e.name.substringBeforeLast('.') + "-exportado.txt")
                        } else feedback = "Não foi possível ler o backup"
                        selected = null
                    }) { Text("Exportar TXT") }
                    TextButton(onClick = { selected = null }) { Text("Fechar") }
                }
            })
    }
}
