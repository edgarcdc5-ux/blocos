package com.cleaneditor.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuroraBackground(content: @Composable BoxScope.() -> Unit) {
    val p = LocalPalette.current
    Box(Modifier.fillMaxSize().background(p.background)) {
        Box(Modifier.size(340.dp).blur(90.dp)
            .background(Brush.radialGradient(listOf(p.glow1, Color.Transparent)), CircleShape)
            .align(Alignment.TopEnd))
        Box(Modifier.size(300.dp).blur(90.dp)
            .background(Brush.radialGradient(listOf(p.glow2, Color.Transparent)), CircleShape)
            .align(Alignment.CenterStart))
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val p = LocalPalette.current
    val shape = RoundedCornerShape(22.dp)
    var m = modifier.shadow(2.dp, shape).clip(shape).background(p.surface)
        .border(1.dp, p.border, shape)
    if (onClick != null) m = m.clickable(onClick = onClick)
    Column(m.padding(padding), content = content)
}

@Composable
fun GradientCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit) {
    val p = LocalPalette.current
    val shape = RoundedCornerShape(24.dp)
    var m = modifier.clip(shape).background(p.brush)
    if (onClick != null) m = m.clickable(onClick = onClick)
    Column(m.padding(20.dp), content = content)
}

@Composable
fun GradientIcon(icon: ImageVector, size: Int = 44) {
    val p = LocalPalette.current
    Box(Modifier.size(size.dp).clip(RoundedCornerShape(14.dp)).background(p.brush),
        contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size((size * 0.5f).dp))
    }
}

@Composable
fun GradientButton(text: String, icon: ImageVector? = null,
    modifier: Modifier = Modifier, onClick: () -> Unit) {
    val p = LocalPalette.current
    Row(modifier.defaultMinSize(minHeight = 48.dp).clip(RoundedCornerShape(16.dp))
        .background(p.brush).clickable(onClick = onClick)
        .padding(horizontal = 20.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        if (icon != null) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun Pill(text: String, selected: Boolean, onClick: () -> Unit) {
    val p = LocalPalette.current
    val shape = RoundedCornerShape(16.dp)
    val base = Modifier.defaultMinSize(minHeight = 40.dp).clip(shape)
    val m = if (selected) base.background(p.brush)
        else base.background(p.surface).border(1.dp, p.border, shape)
    Text(text, color = if (selected) Color.White else p.text,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        maxLines = 1,
        modifier = m.clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp))
}

@Composable
fun SearchField(value: String, placeholder: String,
    onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val p = LocalPalette.current
    val shape = RoundedCornerShape(18.dp)
    Row(modifier.fillMaxWidth().defaultMinSize(minHeight = 52.dp)
        .clip(shape).background(p.surface).border(1.dp, p.border, shape)
        .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Search, null, tint = p.muted, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            if (value.isEmpty()) Text(placeholder, color = p.muted, fontSize = 15.sp)
            BasicTextField(value, onChange, singleLine = true,
                textStyle = TextStyle(color = p.text, fontSize = 15.sp),
                cursorBrush = SolidColor(p.accent),
                modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun ScreenTitle(title: String, subtitle: String? = null) {
    val p = LocalPalette.current
    Column(Modifier.padding(bottom = 8.dp)) {
        Text(title, color = p.text, fontSize = 29.sp,
            fontWeight = FontWeight.Bold, lineHeight = 35.sp)
        if (subtitle != null) Text(subtitle, color = p.muted, fontSize = 14.sp)
    }
}

@Composable
fun SectionLabel(text: String) {
    val p = LocalPalette.current
    Text(text.uppercase(), color = p.accent, fontSize = 12.sp,
        fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
}

@Composable
fun EmptyState(icon: ImageVector, title: String, text: String) {
    val p = LocalPalette.current
    Column(Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        GradientIcon(icon, 56)
        Spacer(Modifier.height(12.dp))
        Text(title, color = p.text, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        Text(text, color = p.muted, fontSize = 14.sp)
    }
}
