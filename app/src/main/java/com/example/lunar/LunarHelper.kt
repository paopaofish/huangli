package com.example.lunar

import java.util.Calendar
import java.util.Date

class LunarDate(
    val lunarYear: Int,
    val lunarMonth: Int,
    val lunarDay: Int,
    val isLeap: Boolean,
    val ganZhiYear: String,
    val ganZhiMonth: String,
    val ganZhiDay: String,
    val zodiac: String,
    val lunarMonthName: String,
    val lunarDayName: String,
    val suitable: List<String>,
    val taboo: List<String>,
    val shichenName: String,
    val shichenRange: String,
    val chongSha: String,
    val caiShen: String,
    val xiShen: String,
    val fuShen: String
)

object LunarHelper {
    private val tgString = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
    private val dzString = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
    private val sxString = arrayOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")
    private val lunarMonthNames = arrayOf(
        "正月", "二月", "三月", "四月", "五月", "六月",
        "七月", "八月", "九月", "十月", "十一月", "腊月"
    )
    private val lunarDayNames = arrayOf(
        "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
        "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
        "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
    )

    // Standard Chinese Lunar Calendar parameters (1900 - 2059)
    private val lunarInfo = intArrayOf(
        0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, // 1900-1909
        0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977, // 1910-1919
        0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, // 1920-1929
        0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950, // 1930-1939
        0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, // 1940-1949
        0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0, // 1950-1959
        0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, // 1960-1969
        0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6, // 1970-1979
        0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570, // 1980-1989
        0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, // 1990-1999
        0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5, // 2000-2009
        0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, // 2010-2019
        0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530, // 2020-2029
        0x05aa0, 0x076a3, 0x096d0, 0x04afb, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, // 2030-2039
        0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0, // 2040-2049
        0x14b06, 0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06b20, 0x1a6c4, 0x0aa40  // 2050-2059
    )

    // Base reference date: 1900-01-31 which is Lunar 1900-01-01
    private val baseTimeInMillis: Long = -2206425600000L // 1900-01-31 00:00:00 UTC

    private fun getLunarYearDays(year: Int): Int {
        var sum = 348 // 12 * 29
        val info = lunarInfo[year - 1900]
        var mask = 0x8000
        for (m in 1..12) {
            if ((info and mask) != 0) {
                sum += 1
            }
            mask = mask shr 1
        }
        return sum + getLeapMonthDays(year)
    }

    private fun getLeapMonth(year: Int): Int {
        return lunarInfo[year - 1900] and 0xf
    }

    private fun getLeapMonthDays(year: Int): Int {
        if (getLeapMonth(year) != 0) {
            return if ((lunarInfo[year - 1900] and 0x10000) != 0) 30 else 29
        }
        return 0
    }

    private fun getLunarMonthDays(year: Int, month: Int): Int {
        val info = lunarInfo[year - 1900]
        return if ((info and (0x10000 shr month)) != 0) 30 else 29
    }

    // Convert solar date to LunarDate
    fun fromSolarDate(solarDate: Date): LunarDate {
        val calendar = Calendar.getInstance().apply { time = solarDate }
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Calculate offset days from 1900-01-31 in a timezone-independent calendar date way
        val calBase = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
            set(1900, Calendar.JANUARY, 31, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calSolar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
            set(year, month, day, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        var offset = ((calSolar.timeInMillis - calBase.timeInMillis) / (24 * 60 * 60 * 1000L)).toInt()

        var lunarYear = 1900
        var daysInYear = getLunarYearDays(lunarYear)

        while (lunarYear < 2059 && offset >= daysInYear) {
            offset -= daysInYear
            lunarYear++
            daysInYear = getLunarYearDays(lunarYear)
        }

        val leapMonth = getLeapMonth(lunarYear)
        var isLeap = false
        var lunarMonth = 1
        var daysInMonth: Int

        for (m in 1..12) {
            // Check regular month
            daysInMonth = getLunarMonthDays(lunarYear, m)
            if (offset < daysInMonth) {
                lunarMonth = m
                break
            }
            offset -= daysInMonth

            // Check leap month
            if (m == leapMonth) {
                daysInMonth = getLeapMonthDays(lunarYear)
                if (offset < daysInMonth) {
                    lunarMonth = m
                    isLeap = true
                    break
                }
                offset -= daysInMonth
            }
        }

        val lunarDay = offset + 1

        // Calculation of Stem-Branch (干支) and Zodiac (生肖)
        val yearIndex = (lunarYear - 4) % 60
        val ganZhiYear = tgString[yearIndex % 10] + dzString[yearIndex % 12]
        val zodiac = sxString[yearIndex % 12]

        // Date calculation for Day Stem-Branch (using timezone-independent calendar day offset)
        // Reference: Epoch day stem-branch.
        // 1900-01-31 was 甲戌日 (Stem index 0, Branch index 10)
        val dayOffset = ((calSolar.timeInMillis - calBase.timeInMillis) / (24 * 60 * 60 * 1000L)).toInt()
        val dayIndex = (dayOffset + 10) % 60
        val ganZhiDay = tgString[dayIndex % 10] + dzString[dayIndex % 12]

        // Month stem-branch
        // Year stem determines starting stem of the month
        val yearStemIndex = yearIndex % 10
        val startMonthStemIndex = (yearStemIndex * 2 + 2) % 10
        val monthIndex = (startMonthStemIndex + lunarMonth - 1) % 60
        val ganZhiMonth = tgString[monthIndex % 10] + dzString[(lunarMonth + 1) % 12] // Lunar Jan is Month of Tiger (寅: index 2)

        // Month names
        val lunarMonthName = if (isLeap) "闰${lunarMonthNames[lunarMonth - 1]}" else lunarMonthNames[lunarMonth - 1]
        val lunarDayName = lunarDayNames[lunarDay - 1]

        // Hourly celestial branches and names (十二时辰)
        val (shichenName, shichenRange) = getShichenInfo(hourOfDay)

        // Taboos & Suitabilities (宜忌) based on Twelve Day Officers (建除十二神)
        val monthBranchIndex = (lunarMonth + 1) % 12 // Lunar Jan is Month of Tiger (寅: index 2)
        val dayBranchIndex = dayIndex % 12
        val officerIndex = (dayBranchIndex - monthBranchIndex + 12) % 12
        val (suitable, taboo) = getHuangliEvents(officerIndex)

        val chongAnimal = sxString[(dayBranchIndex + 6) % 12]
        val shaDir = when (dayBranchIndex) {
            0, 4, 8 -> "南"
            1, 5, 9 -> "东"
            2, 6, 10 -> "北"
            3, 7, 11 -> "西"
            else -> "北"
        }
        val chongSha = "冲${chongAnimal}煞${shaDir}"

        // Calculate positions of Three Auspicious Deities based on Daily Heavenly Stem
        val stemIndex = dayIndex % 10
        val caiShen = when (stemIndex) {
            0 -> "东北"
            1 -> "西南"
            2, 3 -> "正西"
            4, 5 -> "正北"
            6, 7 -> "正东"
            8, 9 -> "正南"
            else -> "正南"
        }
        val xiShen = when (stemIndex) {
            0, 5 -> "东北"
            1, 6 -> "西北"
            2, 7 -> "西南"
            3, 8 -> "正南"
            4, 9 -> "东南"
            else -> "正南"
        }
        val fuShen = when (stemIndex) {
            0, 5 -> "正北"
            1, 6 -> "西南"
            2, 7 -> "西北"
            3, 8 -> "东南"
            4, 9 -> "东北"
            else -> "东北"
        }

        return LunarDate(
            lunarYear = lunarYear,
            lunarMonth = lunarMonth,
            lunarDay = lunarDay,
            isLeap = isLeap,
            ganZhiYear = ganZhiYear,
            ganZhiMonth = ganZhiMonth,
            ganZhiDay = ganZhiDay,
            zodiac = zodiac,
            lunarMonthName = lunarMonthName,
            lunarDayName = lunarDayName,
            suitable = suitable,
            taboo = taboo,
            shichenName = shichenName,
            shichenRange = shichenRange,
            chongSha = chongSha,
            caiShen = caiShen,
            xiShen = xiShen,
            fuShen = fuShen
        )
    }

    private fun getShichenInfo(hour: Int): Pair<String, String> {
        val index = ((hour + 1) % 24) / 2
        val name = dzString[index] + "时"
        val start = if (index == 0) 23 else (index * 2 - 1)
        val end = (index * 2 + 1) % 24
        val range = String.format("%02d:00-%02d:00", start, end)
        return Pair(name, range)
    }

    private fun getHuangliEvents(officerIndex: Int): Pair<List<String>, List<String>> {
        return when (officerIndex) {
            0 -> Pair( // 建
                listOf("出行", "交易", "塞穴", "扫舍"),
                listOf("开仓", "动土", "破土", "安葬", "嫁娶", "筑墙")
            )
            1 -> Pair( // 除
                listOf("祭祀", "祈福", "沐浴", "治病", "扫舍", "裁衣"),
                listOf("嫁娶", "出行", "移徙", "入宅", "开市", "动土")
            )
            2 -> Pair( // 满
                listOf("祭祀", "祈福", "沐浴", "结网", "取鱼", "扫舍"),
                listOf("嫁娶", "动土", "移徙", "入宅", "安葬", "词讼")
            )
            3 -> Pair( // 平
                listOf("祭祀", "治病", "破土", "安葬", "修饰垣墙", "扫舍"),
                listOf("嫁娶", "出行", "移徙", "入宅", "开市", "祈福")
            )
            4 -> Pair( // 定
                listOf("祭祀", "祈福", "嫁娶", "订盟", "纳采", "冠笄", "裁衣", "合帐", "交易", "立券", "安床"),
                listOf("出行", "词讼", "治病", "移徙", "入宅", "栽种", "掘井")
            )
            5 -> Pair( // 执
                listOf("祭祀", "祈福", "求嗣", "沐浴", "塞穴", "捕鱼", "捕捉", "扫舍"),
                listOf("嫁娶", "开市", "交易", "立券", "搬家", "移徙", "出行")
            )
            6 -> Pair( // 破
                listOf("破屋", "坏垣", "扫舍", "治病", "破土", "安葬"),
                listOf("嫁娶", "出行", "移徙", "入宅", "开市", "祈福")
            )
            7 -> Pair( // 危
                listOf("祭祀", "祈福", "求嗣", "沐浴", "治病", "扫舍", "塞穴", "捕捉"),
                listOf("登高", "行船", "安葬", "动土", "移徙", "入宅", "嫁娶")
            )
            8 -> Pair( // 成
                listOf("嫁娶", "订盟", "纳采", "出行", "祭祀", "祈福", "动土", "破土", "安葬"),
                listOf("词讼", "治病")
            )
            9 -> Pair( // 收
                listOf("祭祀", "扫舍", "捕鱼", "乘船", "捕捉", "纳财", "开仓"),
                listOf("嫁娶", "出行", "安葬", "动土", "移徙", "入宅", "祈福")
            )
            10 -> Pair( // 开
                listOf("嫁娶", "纳采", "祭祀", "祈福", "出行", "立券", "移徙", "入宅", "动土", "破土", "安葬"),
                listOf("开光", "作灶", "盖屋", "架马", "开仓")
            )
            11 -> Pair( // 闭
                listOf("祭祀", "筑墙", "塞穴", "捕鱼", "捕捉", "扫舍"),
                listOf("针灸", "出行", "迁移", "动土", "破土", "嫁娶")
            )
            else -> Pair(
                listOf("祭祀", "祈福", "出行", "扫舍", "沐浴", "交易"),
                listOf("动土", "嫁娶", "安葬", "开市", "移徙", "词讼")
            )
        }
    }
}
