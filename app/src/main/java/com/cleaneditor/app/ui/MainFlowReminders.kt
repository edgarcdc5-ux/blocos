package com.cleaneditor.app.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleaneditor.app.AppViewModel
import com.cleaneditor.app.data.Priority
import com.cleaneditor.app.data.Reminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val localeBR = Locale("pt", "BR")
private fun dayKey(t: Long): Int {
    val c = Calendar.getInstance().apply { timeInMillis = t }
    return c.get(Calendar.YEAR) * 1000 + c.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun MainFlowReminders(vm: AppViewModel) {
    val p = LocalPalette.current
    var classic by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Todos") }
    var category by remember { mutableStateOf("Todas") }
    var selectedDay by remember { mutableStateOf<Long?>(null) }
    var monthOffset by remember { mutableIntStateOf(0) }
    val monthName = remember(monthOffset) { SimpleDateFormat("MMMM yyyy", localeBR).format(Calendar.getInstance().apply { add(Calendar.MONTH, monthOffset) }.time) }
    var editing by remember { mutableStateOf<Reminder?>(null) }
    var creating by remember { mutableStateOf(false) }
    val now = System.currentTimeMillis()
    val today = dayKey(now)
    val tomorrow = dayKey(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis)
    val categories = listOf("Todas") + vm.reminders.map { it.category.ifBlank { "Geral" } }.distinct().sorted()
    val shown = vm.reminders.filter { r ->
        val status = when(filter) {
            "Hoje" -> dayKey(r.timeMillis) == today || (r.repeatDaily && !r.done)
            "Amanhã" -> dayKey(r.timeMillis) == tomorrow || (r.repeatDaily && !r.done)
            "Pendentes" -> !r.done
            "Concluídos" -> r.done
            "Importantes" -> r.priority == Priority.ALTA
            else -> true
        }
        val textMatch = r.title.contains(query.trim(), true) || r.content.contains(query.trim(), true)
        val categoryMatch = category == "Todas" || r.category == category
        val dateMatch = selectedDay == null || dayKey(r.timeMillis) == dayKey(selectedDay!!) || (r.repeatDaily && !r.done)
        status && textMatch && categoryMatch && dateMatch
    }.sortedWith(compareBy<Reminder> { it.done }.thenBy { it.timeMillis })
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                ScreenTitle("Lembretes", "Seu planejamento em dia")
            }
            TextButton(onClick = { classic = !classic }) {
                Text(if (classic) "Novo visual" else "Visual clássico")
            }
        }
        if (classic) {
            Box(Modifier.weight(1f)) { RemindersScreen(vm) }
        } else {
            LazyColumn(Modifier.weight(1f).padding(horizontal = 18.dp),
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    GradientCard(Modifier.fillMaxWidth()) {
                        Text("Organize seus compromissos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${vm.reminders.count { !it.done }} pendentes • ${vm.reminders.count { it.done }} concluídos",
                            color = Color.White.copy(alpha = .9f), fontSize = 13.sp)
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(monthName.replaceFirstChar { it.uppercase(localeBR) },
                            color = p.text, fontWeight = FontWeight.Bold)
                        Row {
                            IconButton(onClick = { monthOffset-- }) {
                                Icon(Icons.Outlined.ChevronLeft, "Mês anterior", tint = p.text)
                            }
                            IconButton(onClick = { monthOffset++ }) {
                                Icon(Icons.Outlined.ChevronRight, "Próximo mês", tint = p.text)
                            }
                        }
                    }
                    MonthCalendar(monthOffset, selectedDay, vm.reminders.map { dayKey(it.timeMillis) }) {
                        selectedDay = if (selectedDay != null && dayKey(selectedDay!!) == dayKey(it)) null else it
                    }
                }
                item {
                    SearchField(query, "Pesquisar lembretes", { query = it })
                    if (selectedDay != null) TextButton(onClick = { selectedDay = null }) {
                        Text("Limpar data selecionada")
                    }
                }
                item {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Todos", "Hoje", "Amanhã", "Pendentes", "Concluídos", "Importantes").forEach { label ->
                            Pill(label, filter == label) { filter = label }
                        }
                    }
                }
                item {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.forEach { label -> Pill(label, category == label) { category = label } }
                    }
                }
                if (shown.isEmpty()) item {
                    EmptyState(Icons.Outlined.NotificationsNone, "Nenhum lembrete encontrado", "Mude os filtros ou crie um lembrete")
                }
                items(shown, key = { it.id }) { reminder ->
                    GlassCard(Modifier.fillMaxWidth(), onClick = { editing = reminder }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(reminder.title, color = p.text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text(SimpleDateFormat("dd/MM/yyyy • HH:mm", localeBR).format(reminder.timeMillis)
                                    + " • " + reminder.category + " • " + reminder.priority.label,
                                    color = p.muted, fontSize = 12.sp)
                                if (reminder.content.isNotBlank()) Text(reminder.content, color = p.muted, fontSize = 12.sp)
                            }
                            Checkbox(checked = reminder.done, onCheckedChange = {
                                vm.upsertReminder(reminder.copy(done = it))
                            })
                        }
                    }
                }
            }
            GradientButton("Novo lembrete", Icons.Outlined.Add,
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)) { creating = true }
        }
    }
    if (creating || editing != null) {
        ReminderForm(editing, onDismiss = { creating = false; editing = null },
            onSave = { vm.upsertReminder(it); creating = false; editing = null },
            onDelete = { vm.deleteReminder(it); editing = null })
    }
}

@Composable
private fun MonthCalendar(offset: Int, selected: Long?, activeDays: List<Int>, onPick: (Long) -> Unit) {
    val p = LocalPalette.current
    val start = Calendar.getInstance().apply {
        add(Calendar.MONTH, offset); set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0)
    }
    val count = start.getActualMaximum(Calendar.DAY_OF_MONTH)
    val pad = start.get(Calendar.DAY_OF_WEEK) - 1
    val todayKey = dayKey(System.currentTimeMillis())
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { Text(it, color = p.muted, fontSize = 12.sp) }
        }
        repeat((pad + count + 6) / 7) { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                repeat(7) { weekday ->
                    val day = week * 7 + weekday - pad + 1
                    Box(Modifier.weight(1f).height(42.dp), contentAlignment = Alignment.Center) {
                        if (day in 1..count) {
                            val cal = (start.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
                            val isSelected = selected != null && dayKey(selected) == dayKey(cal.timeInMillis)
                            val hasItems = dayKey(cal.timeInMillis) in activeDays
                            val isToday = dayKey(cal.timeInMillis) == todayKey
                            Text(day.toString() + if (hasItems) " •" else "",
                                color = if (isSelected) p.accent else p.text,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.clickable { onPick(cal.timeInMillis) }.padding(7.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderForm(initial: Reminder?, onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit, onDelete: (Reminder) -> Unit) {
    val ctx = LocalContext.current
    var title by remember(initial?.id) { mutableStateOf(initial?.title ?: "") }
    var content by remember(initial?.id) { mutableStateOf(initial?.content ?: "") }
    var category by remember(initial?.id) { mutableStateOf(initial?.category ?: "Geral") }
    var priority by remember(initial?.id) { mutableStateOf(initial?.priority ?: Priority.MEDIA) }
    var repeatDaily by remember(initial?.id) { mutableStateOf(initial?.repeatDaily ?: false) }
    var confirmDelete by remember(initial?.id) { mutableStateOf(false) }
    var time by remember(initial?.id) {
        mutableLongStateOf(initial?.timeMillis ?: Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) }.timeInMillis)
    }
    if (confirmDelete && initial != null) {
        AlertDialog(onDismissRequest = { confirmDelete = false },
            title = { Text("Excluir lembrete?") },
            text = { Text("Esta ação removerá o lembrete e seu agendamento.") },
            confirmButton = { TextButton(onClick = { onDelete(initial); confirmDelete = false }) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") } })
    }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Novo lembrete" else "Editar lembrete") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Título") }, singleLine = true)
                OutlinedTextField(content, { content = it }, label = { Text("Descrição") })
                OutlinedTextField(category, { category = it }, label = { Text("Categoria") }, singleLine = true)
                Text("Defina a data, a hora e a prioridade do aviso", color = LocalPalette.current.muted, fontSize = 12.sp)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill(SimpleDateFormat("dd/MM/yyyy", localeBR).format(time), false) {
                        val c = Calendar.getInstance().apply { timeInMillis = time }
                        DatePickerDialog(ctx, { _, y, m, d ->
                            time = Calendar.getInstance().apply { timeInMillis = time; set(y, m, d) }.timeInMillis
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
                    }
                    Pill(SimpleDateFormat("HH:mm", localeBR).format(time), false) {
                        val c = Calendar.getInstance().apply { timeInMillis = time }
                        TimePickerDialog(ctx, { _, h, m ->
                            time = Calendar.getInstance().apply { timeInMillis = time; set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, m); set(Calendar.SECOND, 0) }.timeInMillis
                        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
                    }
                }
                Text("Prioridade")
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { p -> Pill(p.label, priority == p) { priority = p } }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Repetir diariamente", Modifier.weight(1f))
                    Switch(repeatDaily, { repeatDaily = it })
                }
            }
        },
        confirmButton = {
            TextButton(enabled = title.isNotBlank() && time > System.currentTimeMillis(), onClick = {
                if (title.isNotBlank()) onSave(Reminder(initial?.id ?: System.currentTimeMillis(),
                    title.trim(), content.trim(), time, repeatDaily, priority,
                    category.ifBlank { "Geral" }, initial?.done ?: false))
            }) { Text("Salvar") }
        },
        dismissButton = {
            Row {
                if (initial != null) TextButton(onClick = { confirmDelete = true }) { Text("Excluir") }
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        }
    )
}
