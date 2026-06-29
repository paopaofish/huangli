package com.example

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.example.lunar.LunarHelper
import com.example.ui.theme.LunarWidgetTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.widget.LunarWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LunarAlmanacApp(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunarAlmanacApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // States for theme management
    var currentTheme by remember { mutableStateOf(LunarWidgetTheme.getSavedTheme(context)) }
    
    // States for time and calculations
    var currentTime by remember { mutableStateOf(Date()) }
    val lunarDate = remember(currentTime) { LunarHelper.fromSolarDate(currentTime) }

    var isCalculating by remember { mutableStateOf(false) }
    var xiaoLiuRenResult by remember { mutableStateOf<XiaoLiuRenResult?>(null) }

    LaunchedEffect(isCalculating) {
        if (isCalculating) {
            delay(1200)
            xiaoLiuRenResult = calculateXiaoLiuRen(
                lunarDate.lunarMonth,
                lunarDate.lunarDay,
                lunarDate.shichenName
            )
            isCalculating = false
        }
    }

    // Live Clock Effect: Updates every 10 seconds to keep time and Shichen synced
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(10000)
        }
    }

    // Function to trigger Glance Widget update
    fun triggerWidgetUpdate() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(LunarWidget::class.java)
                for (glanceId in glanceIds) {
                    LunarWidget().update(context, glanceId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Title Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "黄历农历",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "传统干支 · 实时生肖 · 桌面小部件",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                )
            }
            
            // Refresh Button
            IconButton(
                onClick = {
                    currentTime = Date()
                    triggerWidgetUpdate()
                    Toast.makeText(context, "同步刷新成功", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .testTag("refresh_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "刷新",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 1. Live Traditional Dashboard Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header: Current Shichen & Digital Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime) + " 实时",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "${lunarDate.shichenName} (${lunarDate.shichenRange})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Core Date Content
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${lunarDate.lunarMonthName}${lunarDate.lunarDayName}日${lunarDate.shichenName}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 36.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${lunarDate.ganZhiYear}${lunarDate.zodiac}年 ${lunarDate.ganZhiMonth}月 ${lunarDate.ganZhiDay}日",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                // Huangli suitability and taboos details
                val isDark = isSystemInDarkTheme()
                val suitableColor = if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D)
                val suitableBg = if (isDark) Color(0xFF22C55E).copy(alpha = 0.15f) else Color(0xFF22C55E).copy(alpha = 0.08f)
                val tabooColor = if (isDark) Color(0xFFF87171) else Color(0xFFB91C1C)
                val tabooBg = if (isDark) Color(0xFFEF4444).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.08f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Suitable Card (宜)
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFF22C55E), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("宜", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("吉事推荐", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        
                        lunarDate.suitable.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowItems.forEach { item ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = 3.dp)
                                            .background(suitableBg, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = item,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = suitableColor,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                if (rowItems.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    // Taboo Card (忌)
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFEF4444), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("忌", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("诸事不宜", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        
                        lunarDate.taboo.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowItems.forEach { item ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = 3.dp)
                                            .background(tabooBg, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = item,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = tabooColor,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                if (rowItems.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "冲煞",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = lunarDate.chongSha,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(12.dp))

                // Three Fortune Deities Directions Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "吉神方位",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "每日吉神方位",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // God of Wealth (财神)
                    DeityDirectionBadge(
                        name = "财神",
                        direction = lunarDate.caiShen,
                        icon = Icons.Default.Star,
                        iconColor = Color(0xFFF59E0B), // Golden Yellow
                        backgroundColor = Color(0xFFF59E0B).copy(alpha = 0.12f),
                        modifier = Modifier.weight(1f)
                    )

                    // God of Joy (喜神)
                    DeityDirectionBadge(
                        name = "喜神",
                        direction = lunarDate.xiShen,
                        icon = Icons.Default.Favorite,
                        iconColor = Color(0xFFEC4899), // Pink/Rose
                        backgroundColor = Color(0xFFEC4899).copy(alpha = 0.12f),
                        modifier = Modifier.weight(1f)
                    )

                    // God of Fortune (福神)
                    DeityDirectionBadge(
                        name = "福神",
                        direction = lunarDate.fuShen,
                        icon = Icons.Default.LocationOn,
                        iconColor = Color(0xFF10B981), // Emerald Green
                        backgroundColor = Color(0xFF10B981).copy(alpha = 0.12f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Xiao Liu Ren Divination Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "小六壬",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "小六壬掌中妙算",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "掌上掐指神算 · 占问吉凶福祸",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Intro text
                Text(
                    text = "小六壬为传统指心神算，以当前的农历月份「${lunarDate.lunarMonthName}」、日期「${lunarDate.lunarDayName}」和时辰「${lunarDate.shichenName}」循环轮推，一秒便知当下行止吉凶。",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isCalculating) {
                    // Loading State
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "掐指推算中，请稍候...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                } else if (xiaoLiuRenResult != null) {
                    val result = xiaoLiuRenResult!!
                    // Result Content Display
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                BorderStroke(1.dp, result.levelColor.copy(alpha = 0.3f)),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        // Title row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "卦象：",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                )
                                Text(
                                    text = result.name,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = result.levelColor
                                    )
                                )
                            }
                            
                            // Badge
                            Box(
                                modifier = Modifier
                                    .background(result.levelColor, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = result.level,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stepper / Formula
                        Text(
                            text = "推演步骤：${result.formula}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Classic Poem
                        Text(
                            text = result.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Dimensions Grid
                        Text(
                            text = "多维度吉凶解读：",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        val dimensions = listOf(
                            Pair("🧭 有利方位", result.direction),
                            Pair("💼 谋事求财", result.mianyi),
                            Pair("🚶 行人动向", result.xingren),
                            Pair("🔍 寻人寻物", result.xunren),
                            Pair("🏥 身体健康", result.jibing),
                            Pair("⚖️ 官司纠纷", result.guansi)
                        )

                        dimensions.forEach { (label, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Advisory Tip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    result.levelColor.copy(alpha = 0.08f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "💡",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "行止指引",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = result.levelColor
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = result.tip,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Button to trigger or reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ElevatedButton(
                        onClick = {
                            isCalculating = true
                        },
                        modifier = Modifier.testTag("xiaoliuren_calculate_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "推算",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (xiaoLiuRenResult == null) "立即掐指推算当前时辰" else "重新推算当前时辰")
                    }

                    if (xiaoLiuRenResult != null) {
                        OutlinedButton(
                            onClick = {
                                xiaoLiuRenResult = null
                            }
                        ) {
                            Text("收起结果")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Interactive Theme Selector Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "选择小部件个性化背景",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Adaptive wrapping FlowRow for themes selection
        @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val isSystemDark = isSystemInDarkTheme()
            LunarWidgetTheme.values().forEach { theme ->
                val isSelected = currentTheme == theme
                
                val cardBackground = if (theme == LunarWidgetTheme.TRANSPARENT) {
                    Brush.verticalGradient(
                        colors = if (isSystemDark) {
                            listOf(Color(0xFF334155).copy(alpha = 0.5f), Color(0xFF1E293B).copy(alpha = 0.5f))
                        } else {
                            listOf(Color(0xFFE2E8F0).copy(alpha = 0.6f), Color(0xFFF1F5F9).copy(alpha = 0.6f))
                        }
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(theme.startColorHex).copy(alpha = 0.75f),
                            Color(theme.endColorHex).copy(alpha = 0.75f)
                        )
                    )
                }

                val cardTextColor = if (theme == LunarWidgetTheme.TRANSPARENT) {
                    if (isSystemDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
                } else {
                    Color(theme.textColorHex)
                }

                val cardAccentColor = if (theme == LunarWidgetTheme.TRANSPARENT) {
                    if (isSystemDark) Color(0xFFFBBF24) else Color(0xFFB45309)
                } else {
                    Color(theme.accentColorHex)
                }

                Card(
                    modifier = Modifier
                        .width(112.dp)
                        .clickable {
                            currentTheme = theme
                            LunarWidgetTheme.saveTheme(context, theme)
                            triggerWidgetUpdate()
                            Toast
                                .makeText(context, "主题已更新并同步到桌面", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .testTag("theme_card_${theme.key}"),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBackground)
                            .padding(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = theme.title.split(" (")[0],
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = cardTextColor
                                )
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini indicator circles of the colors
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(cardAccentColor, CircleShape)
                                )
                                
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "已选择",
                                        tint = cardTextColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Realistic Home-screen Widget Preview Panel
        Text(
            text = "桌面小部件实时预览 (Widget Live Preview)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "长按并添加到您的系统桌面上，样式会与此同步",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Widget Preview Card (reproducing the exact layout of the Glance widget with mock wallpaper)
        val isSystemDark = isSystemInDarkTheme()
        val isTransparent = currentTheme == LunarWidgetTheme.TRANSPARENT

        val previewBgBrush = if (isTransparent) {
            Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Transparent))
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(currentTheme.startColorHex).copy(alpha = 0.75f),
                    Color(currentTheme.endColorHex).copy(alpha = 0.75f)
                )
            )
        }

        val previewTextColor = if (isTransparent) {
            if (isSystemDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
        } else {
            Color(currentTheme.textColorHex)
        }

        val previewSubTextColor = if (isTransparent) {
            if (isSystemDark) Color(0xFFCBD5E1) else Color(0xFF475569)
        } else {
            Color(currentTheme.subTextColorHex)
        }

        val previewAccentColor = if (isTransparent) {
            if (isSystemDark) Color(0xFFFBBF24) else Color(0xFFB45309)
        } else {
            Color(currentTheme.accentColorHex)
        }

        val previewAccentBgColor = if (isTransparent) {
            if (isSystemDark) Color(0x26FBBF24) else Color(0x1AB45309)
        } else {
            Color(currentTheme.accentColorHex).copy(alpha = 0.15f)
        }

        val previewDividerColor = if (isTransparent) {
            if (isSystemDark) Color(0x26FFFFFF) else Color(0x1F000000)
        } else {
            Color(currentTheme.subTextColorHex).copy(alpha = 0.15f)
        }

        val previewIsLightContent = if (isTransparent) !isSystemDark else currentTheme.isLight

        val suitableColor = if (previewIsLightContent) Color(0xFF15803D) else Color(0xFF4ADE80)
        val tabooColor = if (previewIsLightContent) Color(0xFFB91C1C) else Color(0xFFF87171)
        val chongColor = if (previewIsLightContent) Color(0xFFB45309) else Color(0xFFFBBF24)
        val jiColor = if (previewIsLightContent) Color(0xFF047857) else Color(0xFF34D399)

        val suitableBgColor = if (previewIsLightContent) Color(0x1A22C55E) else Color(0x2622C55E)
        val tabooBgColor = if (previewIsLightContent) Color(0x1AEF4444) else Color(0x26EF4444)
        val chongBgColor = if (previewIsLightContent) Color(0x1AF59E0B) else Color(0x26F59E0B)
        val jiBgColor = if (previewIsLightContent) Color(0x1A10B981) else Color(0x2610B981)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (isSystemDark) {
                        Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF1E1B4B), // Deep Indigo
                                Color(0xFF0F172A), // Deep Slate
                                Color(0xFF311042), // Deep Purple
                                Color(0xFF1E1B4B)
                            )
                        )
                    } else {
                        Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFFEEF2F6), // Warm grey
                                Color(0xFFE0E7FF), // Soft Indigo
                                Color(0xFFFDF2F8), // Soft Pink
                                Color(0xFFEEF2F6)
                            )
                        )
                    }
                )
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(previewBgBrush, RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = if (isTransparent) Color.White.copy(alpha = 0.15f) else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = SimpleDateFormat("M月d日 EEEE", Locale.CHINESE).format(currentTime),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = previewSubTextColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        
                        // Shichen Badge
                        Box(
                            modifier = Modifier
                                .background(previewAccentBgColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${lunarDate.shichenName}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = previewAccentColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lunar Day & Month
                    Text(
                        text = "${lunarDate.lunarMonthName}${lunarDate.lunarDayName}日${lunarDate.shichenName}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = previewTextColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Stem-Branch Year
                    Text(
                        text = "${lunarDate.ganZhiYear}${lunarDate.zodiac}年 ${lunarDate.ganZhiMonth}月 ${lunarDate.ganZhiDay}日",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = previewSubTextColor,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(previewDividerColor)
                    ) {}
                    Spacer(modifier = Modifier.height(12.dp))

                    // Huangli Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(suitableBgColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "宜",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = suitableColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = lunarDate.suitable.take(6).joinToString(" · "),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = previewTextColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(tabooBgColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "忌",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = tabooColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = lunarDate.taboo.take(6).joinToString(" · "),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = previewTextColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(chongBgColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "冲",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = chongColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = lunarDate.chongSha,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = previewTextColor,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(jiBgColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "吉",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = jiColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "财神 ${lunarDate.caiShen} · 喜神 ${lunarDate.xiShen} · 福神 ${lunarDate.fuShen}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = previewTextColor,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Guide Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "如何将此小部件添加到您的系统桌面：",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. 返回到您的手机系统主界面 (Launcher)；\n" +
                           "2. 长按主屏幕空白区域，选择「小部件 / 小工具 (Widgets)」；\n" +
                           "3. 找到并选择「黄历农历」小部件；\n" +
                           "4. 将小部件拖动并摆放到合适的位置；\n" +
                           "5. 小部件支持长按缩放、自由调整尺寸。",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

data class XiaoLiuRenResult(
    val name: String,
    val level: String,
    val levelColor: Color,
    val description: String,
    val direction: String,
    val mianyi: String,
    val xunren: String,
    val jibing: String,
    val guansi: String,
    val xingren: String,
    val formula: String,
    val tip: String
)

fun calculateXiaoLiuRen(month: Int, day: Int, shichenName: String): XiaoLiuRenResult {
    val dzString = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
    val shichenChar = shichenName.take(1)
    val shichenIndex1Based = (dzString.indexOf(shichenChar).takeIf { it >= 0 } ?: 0) + 1
    
    val index = ((month + day + shichenIndex1Based - 3) % 6 + 6) % 6
    
    val stateNames = arrayOf("大安", "留连", "速喜", "赤口", "小吉", "空亡")
    
    val mState = ((month - 1) % 6 + 6) % 6
    val dState = ((mState + day - 1) % 6 + 6) % 6
    val hState = ((dState + shichenIndex1Based - 1) % 6 + 6) % 6
    
    val formulaText = "${month}月 (${stateNames[mState]}) ➔ ${day}日 (${stateNames[dState]}) ➔ $shichenName (${stateNames[hState]})"
    
    return when (index) {
        0 -> XiaoLiuRenResult(
            name = "大安",
            level = "大吉",
            levelColor = Color(0xFF22C55E), // Green
            description = "大安事事昌，求财在西方。失物去不远，宅舍保安康。\n行人身未动，病者无大妨。将军回田野，仔细好推详。",
            direction = "正西、西北（大安求财在西方，出行利静利合）",
            mianyi = "谋事主一、五、七，求谋大吉，顺理成章。",
            xunren = "寻人在近处，一般在原地、家中或办公室。",
            jibing = "疾病在身体下部或脾胃，主静，安心静养易痊愈。",
            guansi = "官事有理，对方讲理，纠纷自然消散。",
            xingren = "行人身未动，但平安无事，或即刻传来佳音。",
            formula = formulaText,
            tip = "大安主静。安泰安定，万事不宜急进。守静笃行，方可亨通。"
        )
        1 -> XiaoLiuRenResult(
            name = "留连",
            level = "半吉/小凶",
            levelColor = Color(0xFFF59E0B), // Amber
            description = "留连事难成，求谋日未明。官事只宜缓，去者未出行。\n失物南方见，急讨方称心。更防人口舌，留连最难寻。",
            direction = "正南、东南（事多阻滞，宜在南方或平静处寻找转机）",
            mianyi = "谋事主二、八、十，凡事阻滞难成，切忌急躁，需等待时机。",
            xunren = "寻人未见，多在暗处 or 他人处，难以立刻找到，多费周折。",
            jibing = "慢性疾病或旧疾，病情拖延、反复，需仔细调理和耐心检查。",
            guansi = "官事纠缠，难以了结，宜缓不宜急，建议寻求和解。",
            xingren = "行人未动，尚在旅途，常因事搁浅，暂无归期。",
            formula = formulaText,
            tip = "留连主慢。凡事拖延纠缠，求谋未明，宜静不宜动，保持耐心，以时间换空间。"
        )
        2 -> XiaoLiuRenResult(
            name = "速喜",
            level = "大吉/中吉",
            levelColor = Color(0xFFEC4899), // Pink/Rose
            description = "速喜喜临门，求财向南行。失物申未未，逢人路上寻。\n官事有雅理，病者无祸侵。田家多吉庆，人口更欢欣。",
            direction = "正南、西南（速喜求财、求名、办事，正南最为灵验）",
            mianyi = "谋事主三、六、九，谋事迅速成，常有意外喜讯，速战速决为佳。",
            xunren = "寻人立见，或者电话、微信等即刻传来极好的联络反馈。",
            jibing = "突发热病、心火，只要及时就医治疗，无祸侵，恢复极快。",
            guansi = "官事有福，多得贵人和长辈相助，快速解决，大事化小。",
            xingren = "行人动身，已经在回程路上，甚至已快到达目的地。",
            formula = formulaText,
            tip = "速喜主快。喜事立至，财源广进。机不可失，利于立刻采取行动，顺风顺水。"
        )
        3 -> XiaoLiuRenResult(
            name = "赤口",
            level = "小凶/多争",
            levelColor = Color(0xFFEF4444), // Red
            description = "赤口主口舌，官非切要防。失物急去寻，行人有惊慌。\n鸡犬多作怪，病者出西方。更防有害人，口舌且自防。",
            direction = "西方、西北（宜避开西方争执，出行需格外防口舌口角）",
            mianyi = "谋事难成，多有口舌是非或恶性竞争，宜谨言慎行、克制隐忍。",
            xunren = "寻人难寻，可能发生争执，关系冷淡或有矛盾，不宜硬碰硬。",
            jibing = "急性病、肺气不顺或外伤血光，宜尽快前往医院就医，防意外重症。",
            guansi = "官事凶，多口舌之争，有人背后中伤，须防范法务和合同漏洞。",
            xingren = "行人有阻，路途不顺，恐有惊慌、口舌争执或遇到麻烦滞留。",
            formula = formulaText,
            tip = "赤口主斗。口舌是非，惊扰防盗。凡事多有争执，不宜求谋，少言避祸，退一步海阔天空。"
        )
        4 -> XiaoLiuRenResult(
            name = "小吉",
            level = "小吉/中吉",
            levelColor = Color(0xFF3B82F6), // Blue
            description = "小吉最吉祥，双喜临门旁。求财往南方，求名在北方。\n失物东北寻，病者保平康。行人有音信，凡事皆顺畅。",
            direction = "正北、东北（求财办事利于北方，常得女性贵人相助）",
            mianyi = "谋事主双，容易得到女性亲友、贵人协助，渐入佳境，谋划可成。",
            xunren = "寻人在途中，或有熟人、红娘指引方向，可顺畅找到。",
            jibing = "属感冒风寒、轻微疾病，身体机能好转，能逐渐痊愈、保平康。",
            guansi = "官事和美，倾向于庭外和解，或有德高望重的中间人调停成功。",
            xingren = "行人已有确切好消息，正在路途，顺畅即达。",
            formula = formulaText,
            tip = "小吉主和。双喜临门，顺畅和美。人际关系极其融洽，多听取他人意见会大有收获。"
        )
        else -> XiaoLiuRenResult(
            name = "空亡",
            level = "大凶/空无",
            levelColor = Color(0xFF78716C), // Stone Grey
            description = "空亡事不祥，阴人少乖张。求财无利益，行人有灾殃。\n失物寻不见，官事有刑伤。病者重难起，且宜保安康。",
            direction = "不宜出行。如必须出行，宜守不宜攻，避开所有偏角方位。",
            mianyi = "谋事难成，易流于空想，或中途夭折，损失财物，不宜追加投资。",
            xunren = "寻人不见，杳无音讯，如同石沉大海，切忌盲目乱找。",
            jibing = "疾病沉重，精神衰弱、虚损或重症，须极度重视、科学求医和细致陪护。",
            guansi = "官事大凶，有刑伤或破财之灾，形势十分不利，宜低调认亏避险。",
            xingren = "行人有灾殃，音讯全无，多有不顺或受困阻碍，求神庇佑。",
            formula = formulaText,
            tip = "空亡主空。徒劳无功，万事成空。代表虚无、失落、破财。此状态下最宜守成修身，不作大举动。"
        )
    }
}

@Composable
fun DeityDirectionBadge(
    name: String,
    direction: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = iconColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = direction,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = iconColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            )
        }
    }
}
