package com.cleaneditor.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Boas-vindas somente na primeira abertura, sem exigir conta nem permissões adicionais. */
@Composable
fun WelcomeScreen(onEnter: () -> Unit) {
    val p = LocalPalette.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GradientIcon(Icons.Outlined.EditNote, 88)
            Spacer(Modifier.height(22.dp))
            Text("CleanEditor", color = p.text, fontWeight = FontWeight.Bold, fontSize = 34.sp)
            Text("Suas ideias merecem espaço.", color = p.muted, fontSize = 16.sp)
            Spacer(Modifier.height(26.dp))
            GradientCard(Modifier.fillMaxWidth()) {
                Text("Organize. Edite. Produza.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                Spacer(Modifier.height(8.dp))
                Text("Arquivos, textos e lembretes no mesmo lugar — inclusive offline.",
                    color = Color.White.copy(alpha = .92f), fontSize = 14.sp)
            }
            Spacer(Modifier.height(24.dp))
            WelcomeFeature(Icons.Outlined.FolderOpen, "Arquivos organizados", "Acesse os seus documentos sem perder nada")
            WelcomeFeature(Icons.Outlined.EditNote, "Editor completo", "Busque, substitua e continue editando")
            WelcomeFeature(Icons.Outlined.History, "Continue de onde parou", "Encontre facilmente os documentos recentes")
        }
        Column {
            GradientButton("Entrar no CleanEditor", Icons.Outlined.ArrowForward,
                modifier = Modifier.fillMaxWidth(), onClick = onEnter)
            Spacer(Modifier.height(8.dp))
            Text("Não é necessário criar uma conta.", color = p.muted, fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
private fun WelcomeFeature(icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String, subtitle: String) {
    val p = LocalPalette.current
    Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
        GradientIcon(icon, 44)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, color = p.text, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = p.muted, fontSize = 12.sp)
        }
    }
}
