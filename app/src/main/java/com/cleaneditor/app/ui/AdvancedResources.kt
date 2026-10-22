package com.cleaneditor.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Text
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
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp)) {
        ScreenTitle("Recursos avançados", "Organize, proteja e compartilhe seus documentos")
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
            "Backup e recuperação" to "Restaurar rascunhos e proteger alterações",
            "Histórico de versões" to "Comparar e recuperar versões anteriores",
            "Modelos" to "Iniciar documentos a partir de estruturas prontas",
            "Favoritos e tags" to "Agrupar e encontrar documentos rapidamente",
            "Compartilhar e exportar" to "Exportar documentos e compartilhar com outros aplicativos"
        ).forEach { (title, description) ->
            GlassCard(Modifier.fillMaxWidth()) {
                Text(title, color = p.text)
                Text(description, color = p.muted, fontSize = 12.sp)
                Text("Em desenvolvimento", color = p.accent, fontSize = 11.sp)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
