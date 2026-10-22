package com.cleaneditor.app.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleaneditor.app.AppViewModel

/** Etapa 2: explorar arquivos e acessar recentes sem alterar ações legadas. */
@Composable
fun MainFlowFiles(vm: AppViewModel) {
    var recentMode by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("Explorar arquivos", !recentMode) { recentMode = false }
            Pill("Documentos recentes", recentMode) { recentMode = true }
        }
        Box(Modifier.weight(1f)) {
            if (recentMode) RecentDocumentsScreen(vm) else FilesScreen(vm)
        }
    }
}

@Composable
fun RecentDocumentsScreen(vm: AppViewModel) {
    val p = LocalPalette.current
    var query by remember { mutableStateOf("") }
    var favoritesOnly by remember { mutableStateOf(false) }
    val filtered = vm.recents.filter {
        (!favoritesOnly || it.favorite) && it.name.contains(query.trim(), ignoreCase = true)
    }.sortedByDescending { it.openedAt }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ScreenTitle("Documentos recentes", "Continue exatamente de onde parou") }
        item { SearchField(query, "Buscar nos documentos", { query = it }) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill("Todos", !favoritesOnly) { favoritesOnly = false }
                Pill("Favoritos", favoritesOnly) { favoritesOnly = true }
            }
        }
        if (filtered.isEmpty()) {
            item { EmptyState(Icons.Outlined.History, "Nenhum documento encontrado", "Abra ou crie um arquivo para começar") }
        }
        items(filtered, key = { it.uri }) { item ->
            GlassCard(Modifier.fillMaxWidth(), onClick = { vm.open(Uri.parse(item.uri)) }) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    GradientIcon(Icons.Outlined.Description, 40)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.name, color = p.text, fontWeight = FontWeight.SemiBold,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(relativeTime(item.openedAt), color = p.muted, fontSize = 12.sp)
                    }
                    IconButton(onClick = { vm.toggleFavorite(item.uri, item.name) }) {
                        Icon(if (item.favorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Alternar favorito",
                            tint = if (item.favorite) p.medium else p.muted)
                    }
                }
            }
        }
    }
}

/** Mantém editor, abas, busca/substituição, salvamento, seleção e exportação originais. */
@Composable
fun MainFlowEditor(vm: AppViewModel) {
    val p = LocalPalette.current
    val doc = vm.currentDoc ?: return
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(doc.name, color = p.text, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(if (doc.dirty) "Alterações pendentes" else "Salvo",
                    color = if (doc.dirty) p.medium else p.low, fontSize = 12.sp)
            }
            Text("EDITOR", color = p.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.weight(1f)) { EditorScreen(vm) }
    }
}
