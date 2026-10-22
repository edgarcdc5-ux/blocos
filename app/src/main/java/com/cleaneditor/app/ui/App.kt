package com.cleaneditor.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleaneditor.app.AppViewModel
import com.cleaneditor.app.Tab

@Composable
fun CleanEditorApp(vm: AppViewModel) {
    CleanEditorTheme(vm.theme) {
        val snack = remember { SnackbarHostState() }
        LaunchedEffect(vm.message) {
            vm.message?.let { snack.showSnackbar(it); vm.message = null }
        }
        AuroraBackground {
            Box(Modifier.fillMaxSize().statusBarsPadding()) {
                if (vm.editorOpen && vm.currentDoc != null) {
                    BackHandler { vm.editorOpen = false }
                    MainFlowEditor(vm)
                } else {
                    BackHandler(enabled = vm.tab != Tab.INICIO) { vm.tab = Tab.INICIO }
                    AnimatedContent(targetState = vm.tab, label = "tab") { t ->
                        Box(Modifier.fillMaxSize()) {
                            when (t) {
                                Tab.INICIO -> MainFlowHome(vm)
                                Tab.ARQUIVOS -> MainFlowFiles(vm)
                                Tab.LEMBRETES -> RemindersScreen(vm)
                                Tab.IA -> ToolsScreen(vm)
                                Tab.AJUSTES -> SettingsScreen(vm)
                            }
                        }
                    }
                    FloatingNav(vm.tab, { vm.tab = it }, Modifier.align(Alignment.BottomCenter))
                }
                SnackbarHost(snack, Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp))
            }
        }
    }
}

private val tabIcons: Map<Tab, ImageVector> = mapOf(
    Tab.INICIO to Icons.Outlined.Home,
    Tab.ARQUIVOS to Icons.Outlined.Folder,
    Tab.LEMBRETES to Icons.Outlined.NotificationsNone,
    Tab.IA to Icons.Outlined.AutoAwesome,
    Tab.AJUSTES to Icons.Outlined.Settings,
)

@Composable
fun FloatingNav(selected: Tab, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    val p = LocalPalette.current
    val shape = RoundedCornerShape(24.dp)
    val bar = if (p.isDark) Color(0xF0151D36) else Color(0xF9FFFFFF)
    Row(
        modifier.navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth()
            .shadow(12.dp, shape).clip(shape).background(bar).border(1.dp, p.border, shape)
            .padding(horizontal = 5.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Tab.entries.forEach { t ->
            val active = t == selected
            val m = Modifier.weight(1f).clip(RoundedCornerShape(17.dp))
                .then(if (active) Modifier.background(p.brush) else Modifier)
                .clickable { onSelect(t) }.padding(vertical = 8.dp)
            Column(m, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(tabIcons.getValue(t), t.label, tint = if (active) Color.White else p.muted,
                    modifier = Modifier.size(22.dp))
                Spacer(Modifier.height(3.dp))
                Text(t.label, fontSize = 10.sp, maxLines = 1,
                    color = if (active) Color.White else p.muted,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
            }
        }
    }
}
