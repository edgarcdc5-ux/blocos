package com.cleaneditor.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.cleaneditor.app.data.DraftRecovery
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleaneditor.app.AppViewModel
import com.cleaneditor.app.Tab

@Composable
fun AdvancedToolsTab(vm: AppViewModel) {
    var advanced by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("Ferramentas", !advanced) { advanced = false }
            Pill("Recursos avançados", advanced) { advanced = true }
        }
        Box(Modifier.weight(1f)) {
            if (advanced) AdvancedResourcesScreen(vm) else ToolsScreen(vm)
        }
    }
}

/**
 * Etapa 4: ponto de entrada para recursos avançados.
 * O conteúdo abaixo identifica funcionalidades em preparação; não simula
 * sincronização na nuvem, histórico ou exportações ainda não implementadas.
 */
@Composable
fun AdvancedResourcesScreen(vm: AppViewModel) {
    val p = LocalPalette.current
    val ctx = LocalContext.current
    var section by remember { mutableStateOf("Início") }
    var refresh by remember { mutableIntStateOf(0) }
    var chosen by remember { mutableStateOf<DraftRecovery.Entry?>(null) }
    var selectedBackup by remember { mutableStateOf<DraftRecovery.Entry?>(null) }
    var pending by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("") }
    val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        val text = pending
        if (uri != null && text != null) {
            feedback = if (runCatching {
                ctx.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(text.toByteArray(Charsets.UTF_8))
                } ?: error("Arquivo não criado")
            }.isSuccess) "Cópia recuperada em novo arquivo" else "Não foi possível criar a cópia"
        }
        pending = null
    }
    val entries = remember(refresh, section) { DraftRecovery.entries(ctx) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp)) {
        ScreenTitle("Recursos avançados", "Organize, proteja e compartilhe seus documentos")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("Início", section == "Início") { section = "Início" }
            Pill("Backups", section == "Backups") { section = "Backups"; refresh++ }
            Pill("Histórico", section == "Histórico") { section = "Histórico"; refresh++ }
        }
        if (feedback.isNotBlank()) Text(feedback, color = p.accent)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Modelos", "Favoritos e tags", "Compartilhar e exportar").forEach { name ->
                Pill(name, section == name) { section = name }
            }
        }
        if (section in listOf("Modelos", "Favoritos e tags", "Compartilhar e exportar")) {
            AdvancedDocumentTools(vm, section)
        } else if (section != "Início") {
            Text("As cópias são locais. Recuperar cria um NOVO arquivo, sem sobrescrever o original.",
                color = p.muted, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            if (entries.isEmpty()) Text("Nenhum backup local disponível.", color = p.muted)
            entries.forEach { entry ->
                GlassCard(Modifier.fillMaxWidth(), onClick = {
                    if (section == "Histórico") selectedBackup = entry else chosen = entry
                }) {
                    Text(entry.name, color = p.text)
                    Text(SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt","BR")).format(Date(entry.savedAt)),
                        color = p.muted, fontSize = 12.sp)
                    Text(if (section == "Histórico") "Ver versões" else "Recuperar como novo arquivo",
                        color = p.accent, fontSize = 12.sp)
                }
                Spacer(Modifier.height(8.dp))
            }
            if (section == "Histórico" && selectedBackup != null) {
                SectionLabel("Versões: " + selectedBackup!!.name)
                val versions = remember(selectedBackup, refresh) {
                    DraftRecovery.versions(ctx, selectedBackup!!.key)
                }
                if (versions.isEmpty()) Text("Não há versões anteriores deste rascunho.", color = p.muted)
                versions.forEach { version ->
                    GlassCard(Modifier.fillMaxWidth(), onClick = { chosen = version }) {
                        Text(SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("pt","BR")).format(Date(version.savedAt)),
                            color = p.text)
                        Text("Recuperar esta versão como novo arquivo", color = p.accent, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        } else {
        SectionLabel("Disponível agora")
        GlassCard(Modifier.fillMaxWidth(), onClick = { vm.tab = Tab.ARQUIVOS }) {
            Row {
                GradientIcon(Icons.Outlined.FolderOpen, 44)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Arquivos e favoritos", color = p.text)
                    Text("Abra o explorador, gerencie seus documentos e favoritos", color = p.muted, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        SectionLabel("Próximas entregas")
        listOf(
            "Modelos" to "Iniciar documentos a partir de estruturas prontas",
            "Favoritos e tags" to "Agrupar e encontrar documentos rapidamente",
            "Compartilhar e exportar" to "Exportar documentos e compartilhar com outros aplicativos"
        ).forEach { (title, description) ->
            GlassCard(Modifier.fillMaxWidth(), onClick = { section = title }) {
                Text(title, color = p.text)
                Text(description, color = p.muted, fontSize = 12.sp)
                Text("Abrir módulo", color = p.accent, fontSize = 11.sp)
            }
            Spacer(Modifier.height(10.dp))
        }
        }
    }
    if (chosen != null) {
        AlertDialog(onDismissRequest = { chosen = null },
            title = { Text("Recuperar cópia?") },
            text = { Text("Será criado um novo arquivo de texto. O original não será alterado.") },
            confirmButton = { TextButton(onClick = {
                val entry = chosen!!
                val content = DraftRecovery.read(entry)
                if (content != null) {
                    pending = content
                    export.launch(entry.name.substringBeforeLast('.') + "-recuperado.txt")
                } else feedback = "Não foi possível ler o backup"
                chosen = null
            }) { Text("Criar cópia") } },
            dismissButton = { TextButton(onClick = { chosen = null }) { Text("Cancelar") } })
    }
}
