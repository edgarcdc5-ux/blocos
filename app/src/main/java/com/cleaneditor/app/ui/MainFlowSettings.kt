package com.cleaneditor.app.ui

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
import androidx.compose.ui.unit.sp
import com.cleaneditor.app.AppViewModel

/** Etapa 5: as configurações já funcionais permanecem na tela original. */
@Composable
fun MainFlowSettings(vm: AppViewModel) {
    val p = LocalPalette.current
    val ctx = LocalContext.current
    var section by remember { mutableStateOf("Ajustes") }
    var showHelp by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
            ScreenTitle("Configurações", "Personalize sua experiência no CleanEditor")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill("Ajustes", section == "Ajustes") { section = "Ajustes" }
                Pill("Ajuda", section == "Ajuda") { section = "Ajuda" }
                Pill("Sobre", section == "Sobre") { section = "Sobre" }
            }
        }
        when(section) {
            "Ajustes" -> Box(Modifier.weight(1f)) { SettingsScreen(vm) }
            "Ajuda" -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassCard(Modifier.fillMaxWidth(), onClick = { showHelp = !showHelp }) {
                    Text("Como usar o editor", color = p.text)
                    Text("Toque para " + (if (showHelp) "ocultar" else "ver") + " as orientações", color = p.muted)
                }
                if (showHelp) GlassCard(Modifier.fillMaxWidth()) {
                    Text("Abra Arquivos, escolha um documento ou crie um novo. Edite e salve normalmente. Use Buscar e substituir para localizar textos.", color = p.text)
                }
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("Backups e histórico", color = p.text)
                    Text("Abra Ferramentas → Recursos avançados → Backups ou Histórico para recuperar cópias locais sem sobrescrever o original.", color = p.muted)
                }
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("Lembretes", color = p.text)
                    Text("Abra a aba Lembretes para criar avisos, ajustar datas, marcar tarefas e alternar para o visual clássico.", color = p.muted)
                }
            }
            else -> Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("CleanEditor", color = p.text)
                    val info = remember(ctx) {
                        @Suppress("DEPRECATION")
                        ctx.packageManager.getPackageInfo(ctx.packageName, 0)
                    }
                    Text("Versão ${info.versionName}", color = p.muted)
                    Text("Identificador: ${ctx.packageName}", color = p.muted, fontSize = 12.sp)
                    Text("Documentos e backups são armazenados localmente. Não há sincronização em nuvem nesta etapa.", color = p.muted, fontSize = 12.sp)
                }
            }
        }
    }
}
