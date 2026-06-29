package com.example.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.Color

enum class LunarWidgetTheme(
    val key: String,
    val title: String,
    // Hex colors for serializable sharing between App UI and Widget UI
    val startColorHex: Long,
    val endColorHex: Long,
    val textColorHex: Long,
    val subTextColorHex: Long,
    val accentColorHex: Long,
    val isLight: Boolean
) {
    CLASSIC_GOLD(
        key = "classic_gold",
        title = "极简流金 (Classic Gold)",
        startColorHex = 0xFF1A1A1A,
        endColorHex = 0xFF2A241A,
        textColorHex = 0xFFF3E5D8,
        subTextColorHex = 0xFFC5B39A,
        accentColorHex = 0xFFFFD700,
        isLight = false
    ),
    MODERN_SLATE(
        key = "modern_slate",
        title = "极简石板 (Modern Slate)",
        startColorHex = 0xFF1E293B,
        endColorHex = 0xFF0F172A,
        textColorHex = 0xFFF8FAFC,
        subTextColorHex = 0xFF94A3B8,
        accentColorHex = 0xFF38BDF8,
        isLight = false
    ),
    CRIMSON_JADE(
        key = "crimson_jade",
        title = "赤霄琼脂 (Crimson Jade)",
        startColorHex = 0xFF4A0E17,
        endColorHex = 0xFF2B050B,
        textColorHex = 0xFFFFF5F5,
        subTextColorHex = 0xFFE2A0A0,
        accentColorHex = 0xFFFFE1A8,
        isLight = false
    ),
    ZEN_INK(
        key = "zen_ink",
        title = "水墨禅意 (Zen Ink)",
        startColorHex = 0xFFF5F5F5,
        endColorHex = 0xFFE5E5E5,
        textColorHex = 0xFF1C1917,
        subTextColorHex = 0xFF57534E,
        accentColorHex = 0xFF78716C,
        isLight = true
    ),
    FOREST_MOSS(
        key = "forest_moss",
        title = "深林苍苔 (Forest Moss)",
        startColorHex = 0xFF14241B,
        endColorHex = 0xFF0B140F,
        textColorHex = 0xFFECFDF5,
        subTextColorHex = 0xFFA7F3D0,
        accentColorHex = 0xFF34D399,
        isLight = false
    );

    // Helpers to get Jetpack Compose colors for the App UI
    val startColor: Color get() = Color(startColorHex)
    val endColor: Color get() = Color(endColorHex)
    val textColor: Color get() = Color(textColorHex)
    val subTextColor: Color get() = Color(subTextColorHex)
    val accentColor: Color get() = Color(accentColorHex)

    companion object {
        private const val PREFS_NAME = "lunar_widget_prefs"
        private const val KEY_THEME = "selected_theme"

        fun getSavedTheme(context: Context): LunarWidgetTheme {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedKey = prefs.getString(KEY_THEME, CLASSIC_GOLD.key)
            return values().firstOrNull { it.key == savedKey } ?: CLASSIC_GOLD
        }

        fun saveTheme(context: Context, theme: LunarWidgetTheme) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_THEME, theme.key).apply()
        }
    }
}
