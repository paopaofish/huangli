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
    val bgColor = Color(theme.startColorHex)
    val textColor = Color(theme.textColorHex)
    val subTextColor = Color(theme.subTextColorHex)
    val accentColor = Color(theme.accentColorHex)

    // Layout representation matching material widgets
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(androidx.glance.unit.ColorProvider(bgColor))
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
                        color = androidx.glance.unit.ColorProvider(subTextColor),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                
                // Shichen Badge
                Box(
                    modifier = GlanceModifier
                        .background(androidx.glance.unit.ColorProvider(Color((theme.accentColorHex and 0x00FFFFFF) or 0x33000000)))
                        .cornerRadius(6.dp)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${lunarDate.shichenName}",
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(accentColor),
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
                    color = androidx.glance.unit.ColorProvider(textColor),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            // Stem-Branch Year & Zodiac
            Text(
                text = "${lunarDate.ganZhiYear}${lunarDate.zodiac}年 ${lunarDate.ganZhiMonth}月 ${lunarDate.ganZhiDay}日",
                style = TextStyle(
                    color = androidx.glance.unit.ColorProvider(subTextColor),
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Divider
            Spacer(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(androidx.glance.unit.ColorProvider(Color((theme.subTextColorHex and 0x00FFFFFF) or 0x26000000)))
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
                            .background(androidx.glance.unit.ColorProvider(Color(0x2622C55E)))
                            .cornerRadius(4.dp)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "宜",
                            style = TextStyle(
                                color = androidx.glance.unit.ColorProvider(Color(0xFF4ADE80)),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = lunarDate.suitable.take(6).joinToString(" · "),
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(textColor),
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
                            .background(androidx.glance.unit.ColorProvider(Color(0x26EF4444)))
                            .cornerRadius(4.dp)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "忌",
                            style = TextStyle(
                                color = androidx.glance.unit.ColorProvider(Color(0xFFF87171)),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = lunarDate.taboo.take(6).joinToString(" · "),
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(textColor),
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
                            .background(androidx.glance.unit.ColorProvider(Color(0x26F59E0B)))
                            .cornerRadius(4.dp)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "冲",
                            style = TextStyle(
                                color = androidx.glance.unit.ColorProvider(Color(0xFFFBBF24)),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = lunarDate.chongSha,
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(textColor),
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
                            .background(androidx.glance.unit.ColorProvider(Color(0x2610B981)))
                            .cornerRadius(4.dp)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "吉",
                            style = TextStyle(
                                color = androidx.glance.unit.ColorProvider(Color(0xFF34D399)),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = "财神:${lunarDate.caiShen} 喜神:${lunarDate.xiShen} 福神:${lunarDate.fuShen}",
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(textColor),
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
