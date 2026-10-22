package com.cleaneditor.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.cleaneditor.app.AppViewModel
import com.cleaneditor.app.Tab

/** Etapa 2: camada visual de acesso rápido, preserva HomeScreen e suas ações originais. */
@Composable
fun MainFlowHome(vm: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 8.dp)) {
            ScreenTitle("CleanEditor", "Seu espaço para criar e organizar")
            GradientCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AutoAwesome, null, tint = Color.White)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Tudo em um só lugar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp)
                        Text("Continue seus projetos e anotações", color = Color.White.copy(alpha = .9f), fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            SectionLabel("Acesso rápido")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
            ) {
                Pill("Arquivos", false) { vm.tab = Tab.ARQUIVOS }
                Pill("Lembretes", false) { vm.tab = Tab.LEMBRETES }
                Pill("Ferramentas", false) { vm.tab = Tab.IA }
                Pill("Ajustes", false) { vm.tab = Tab.AJUSTES }
            }
        }
        // Preserva integralmente os widgets, documentos e ações da página original.
        Box(Modifier.weight(1f)) { HomeScreen(vm) }
    }
}
