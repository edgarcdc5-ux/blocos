package com.cleaneditor.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FindReplace
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleaneditor.app.AppViewModel
import com.cleaneditor.app.EditorDoc

/** Painel visual complementar: não altera os atalhos e a busca original do editor. */
@Composable
fun SearchReplacePanel(vm: AppViewModel, doc: EditorDoc, onDismiss: () -> Unit) {
    val p = LocalPalette.current
    var query by remember(doc.uri) { mutableStateOf("") }
    var replacement by remember(doc.uri) { mutableStateOf("") }
    var caseSensitive by remember { mutableStateOf(false) }
    var wholeWord by remember { mutableStateOf(false) }
    var regexMode by remember { mutableStateOf(false) }
    var selectedMatch by remember { mutableIntStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    val regex = remember(query, caseSensitive, wholeWord, regexMode) {
        try {
            val pattern = if (regexMode) query else Regex.escape(query)
            val value = if (wholeWord) "\\b(?:$pattern)\\b" else pattern
            error = null
            if (query.isEmpty()) null else Regex(value,
                if (caseSensitive) emptySet() else setOf(RegexOption.IGNORE_CASE))
        } catch (_: IllegalArgumentException) {
            error = "Expressão regular inválida"
            null
        }
    }
    val matches = remember(doc.text, regex) {
        regex?.findAll(doc.text)?.filter { it.range.first < it.range.last + 1 }?.take(10000)?.toList() ?: emptyList()
    }
    LaunchedEffect(matches.size) {
        if (selectedMatch >= matches.size) selectedMatch = 0
    }

    GlassCard(Modifier.fillMaxWidth().imePadding(), padding = PaddingValues(16.dp)) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                GradientIcon(Icons.Outlined.FindReplace, 42)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Buscar e substituir", color = p.text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Localize e edite com precisão", color = p.muted, fontSize = 12.sp)
                }
                IconButton(onClick = onDismiss) { Icon(Icons.Outlined.Close, "Fechar busca", tint = p.text) }
            }
            Spacer(Modifier.height(14.dp))
            SearchField(query, "Buscar no documento", { query = it; selectedMatch = 0 })
            Spacer(Modifier.height(10.dp))
            SearchField(replacement, "Substituir por", { replacement = it })
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = caseSensitive, onCheckedChange = { caseSensitive = it })
                Text("Maiúsculas", color = p.text, fontSize = 12.sp)
                Checkbox(checked = wholeWord, onCheckedChange = { wholeWord = it })
                Text("Palavra inteira", color = p.text, fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = regexMode, onCheckedChange = { regexMode = it })
                Text("Expressão regular", color = p.text, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                Text(if (query.isEmpty()) "Digite para buscar" else "${matches.size} resultado(s)",
                    color = p.muted, fontSize = 12.sp)
            }
            if (error != null) Text(error!!, color = p.high, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.weight(1f)) {
                    GradientButton("Substituir", modifier = Modifier.fillMaxWidth(), onClick = {
                        if (matches.isNotEmpty()) {
                            val match = matches[selectedMatch.coerceIn(matches.indices)]
                            val resolved = if (regexMode) match.value.replace(regex!!, replacement) else replacement
                            vm.replaceText(doc, doc.text.replaceRange(match.range, resolved))
                        }
                    })
                }
                Box(Modifier.weight(1f)) {
                    GradientButton("Substituir tudo", modifier = Modifier.fillMaxWidth(), onClick = {
                        if (regex != null && matches.isNotEmpty()) {
                            vm.replaceText(doc, regex.replace(doc.text, replacement))
                        }
                    })
                }
            }
        }
    }
}
