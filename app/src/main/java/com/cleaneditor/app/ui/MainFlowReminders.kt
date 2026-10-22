package com.cleaneditor.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleaneditor.app.AppViewModel

/**
 * Etapa 3: estrutura visual para lembretes, preservando RemindersScreen
 * e suas ações de criação, edição, agendamento e cancelamento de alarmes.
 */
@Composable
fun MainFlowReminders(vm: AppViewModel) {
    val palette = LocalPalette.current
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 12.dp)) {
            ScreenTitle("Lembretes", "Organize seus compromissos sem perder o foco")
            GradientCard(Modifier.fillMaxWidth()) {
                Row {
                    GradientIcon(Icons.Outlined.EventNote, 40)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Seu planejamento em dia", color = androidx.compose.ui.graphics.Color.White)
                        Text("Crie, edite e acompanhe os lembretes abaixo.",
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.90f),
                            fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        // Tela legada funcional: mantém calendário, dados, botões, horários
        // e notificações até a migração gradual das seções internas.
        Box(Modifier.weight(1f)) { RemindersScreen(vm) }
    }
}
