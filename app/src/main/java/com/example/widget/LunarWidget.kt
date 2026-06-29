package com.example.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity
import com.example.lunar.LunarHelper
import com.example.ui.theme.LunarWidgetTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LunarWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent(context)
        }
    }
}

@androidx.compose.runtime.Composable
private fun WidgetContent(context: Context) {
    // 1. Load active personalized theme
    val theme = LunarWidgetTheme.getSavedTheme(context)
    
    // 2. Fetch current time and Lunar calculations
    val now = Date()
    val lunarDate = LunarHelper.fromSolarDate(now)
    
    // Formatting Solar Date (e.g. "6月27日 周六")
    val solarFormatter = SimpleDateFormat("M月d日 EEEE", Locale.CHINESE)
    val solarStr = solarFormatter.format(now)

    // Convert hex colors to Glance/Compose Color objects
    val isSystemDark = (context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES

    val textColor = if (isSystemDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val subTextColor = if (isSystemDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val accentColor = if (isSystemDark) Color(0xFFFBBF24) else Color(0xFFB45309)
    val accentBgColor = if (isSystemDark) Color(0x26FBBF24) else Color(0x1AB45309)
    val dividerColor = if (isSystemDark) Color(0x26FFFFFF) else Color(0x1F000000)

    val suitableColor = if (isSystemDark) Color(0xFF4ADE80) else Color(0xFF15803D)
    val tabooColor = if (isSystemDark) Color(0xFFF87171) else Color(0xFFB91C1C)
    val chongColor = if (isSystemDark) Color(0xFFFBBF24) else Color(0xFFB45309)
    val jiColor = if (isSystemDark) Color(0xFF34D399) else Color(0xFF047857)

    val suitableBgColor = if (isSystemDark) Color(0x2622C55E) else Color(0x1A22C55E)
    val tabooBgColor = if (isSystemDark) Color(0x26EF4444) else Color(0x1AEF4444)
    val chongBgColor = if (isSystemDark) Color(0x26F59E0B) else Color(0x1AF59E0B)
    val jiBgColor = if (isSystemDark) Color(0x2610B981) else Color(0x1A10B981)

    val bgColorProvider = androidx.glance.unit.ColorProvider(Color.Transparent)
    val textColorProvider = androidx.glance.unit.ColorProvider(textColor)
    val subTextColorProvider = androidx.glance.unit.ColorProvider(subTextColor)
    val accentColorProvider = androidx.glance.unit.ColorProvider(accentColor)
    val accentBgColorProvider = androidx.glance.unit.ColorProvider(accentBgColor)
    val dividerColorProvider = androidx.glance.unit.ColorProvider(dividerColor)

    val suitableColorProvider = androidx.glance.unit.ColorProvider(suitableColor)
    val tabooColorProvider = androidx.glance.unit.ColorProvider(tabooColor)
    val chongColorProvider = androidx.glance.unit.ColorProvider(chongColor)
    val jiColorProvider = androidx.glance.unit.ColorProvider(jiColor)

    val suitableBgColorProvider = androidx.glance.unit.ColorProvider(suitableBgColor)
    val tabooBgColorProvider = androidx.glance.unit.ColorProvider(tabooBgColor)
    val chongBgColorProvider = androidx.glance.unit.ColorProvider(chongBgColor)
    val jiBgColorProvider = androidx.glance.unit.ColorProvider(jiBgColor)

    // Layout representation matching material widgets
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgColorProvider)
            .cornerRadius(16.dp)
            .padding(12.dp)
            .clickable(actionStartActivity(MainActivity::class.java)),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Header Row: Solar Date & Shichen Badge
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = solarStr,
                    style = TextStyle(
                        color = subTextColorProvider,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                
                // Shichen Badge
                Box(
                    modifier = GlanceModifier
                        .background(accentBgColorProvider)
                        .cornerRadius(6.dp)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${lunarDate.shichenName}",
                        style = TextStyle(
                            color = accentColorProvider,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(6.dp))

            // Main Display: Lunar Date
            Text(
                text = "${lunarDate.lunarMonthName}${lunarDate.lunarDayName}日${lunarDate.shichenName}",
                style = TextStyle(
                    color = textColorProvider,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            // Stem-Branch Year & Zodiac
            Text(
                text = "${lunarDate.ganZhiYear}${lunarDate.zodiac}年 ${lunarDate.ganZhiMonth}月 ${lunarDate.ganZhiDay}日",
                style = TextStyle(
                    color = subTextColorProvider,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Divider
            Spacer(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(dividerColorProvider)
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Yellow Calendar (宜忌) Section
            Column(modifier = GlanceModifier.fillMaxWidth()) {
                // Suitability Line (宜)
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .background(suitableBgColorProvider)
                            .cornerRadius(4.dp)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "宜",
                            style = TextStyle(
                                color = suitableColorProvider,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = lunarDate.suitable.take(6).joinToString(" · "),
                        style = TextStyle(
                            color = textColorProvider,
                            fontSize = 11.sp
                        ),
                        maxLines = 2
                    )
                }

                Spacer(modifier = GlanceModifier.height(4.dp))

                // Taboo Line (忌)
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .background(tabooBgColorProvider)
                            .cornerRadius(4.dp)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "忌",
                            style = TextStyle(
                                color = tabooColorProvider,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = lunarDate.taboo.take(6).joinToString(" · "),
                        style = TextStyle(
                            color = textColorProvider,
                            fontSize = 11.sp
                        ),
                        maxLines = 2
                    )
                }

                Spacer(modifier = GlanceModifier.height(4.dp))

                // Chong Sha Line (冲)
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .background(chongBgColorProvider)
                            .cornerRadius(4.dp)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "冲",
                            style = TextStyle(
                                color = chongColorProvider,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = lunarDate.chongSha,
                        style = TextStyle(
                            color = textColorProvider,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }

                Spacer(modifier = GlanceModifier.height(4.dp))

                // Auspicious Deities Line (吉)
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .background(jiBgColorProvider)
                            .cornerRadius(4.dp)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "吉",
                            style = TextStyle(
                                color = jiColorProvider,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = "财神:${lunarDate.caiShen} 喜神:${lunarDate.xiShen} 福神:${lunarDate.fuShen}",
                        style = TextStyle(
                            color = textColorProvider,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
