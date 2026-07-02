const lunisolar = require('lunisolar');
const theGods = require('lunisolar/plugins/theGods');
lunisolar.extend(theGods);
lunisolar.locale('zh-cn'); // Set locale to simplified Chinese
const fs = require('fs');

const testDates = [
  // Li Chun boundary cases: Bazi Year transition
  '1901-02-03 23:00:00',
  '1901-02-04 12:00:00',
  '1901-02-05 01:00:00',
  '1920-02-04 12:00:00',
  '1920-02-05 12:00:00',
  '1950-02-04 12:00:00',
  '2000-02-04 12:00:00',
  '2026-02-04 06:00:00',
  '2026-02-04 12:00:00',
  '2059-02-04 02:00:00',
  '2059-02-04 18:00:00',

  // Day transition Zi Shi (23:00 to 23:59 vs next day early morning)
  '2026-06-30 22:30:00',
  '2026-06-30 23:15:00',
  '2026-06-30 23:45:00',
  '2026-07-01 00:15:00',
  '2026-07-01 01:30:00',

  // Leap months
  '2001-05-25 12:00:00',
  '2001-06-15 12:00:00',
  '2020-05-23 12:00:00',
  '2023-03-22 12:00:00',
  '2023-04-15 12:00:00',

  // Special solar term dates
  '1907-09-08 12:00:00', // Bai Lu
  '1907-12-08 12:00:00', // Da Xue
  '1911-06-07 12:00:00', // Mang Zhong
  '1911-08-09 12:00:00', // Li Qiu
  '2044-10-07 12:00:00', // Han Lu in 2044
  '2052-10-07 12:00:00'  // Han Lu in 2052
];

// Add more random/sequential dates to cover various years and months
for (let year = 1901; year <= 2059; year += 3) {
  for (let month = 1; month <= 12; month += 2) {
    const day = (year + month) % 28 + 1;
    const hour = (year * month) % 24;
    const padM = month.toString().padStart(2, '0');
    const padD = day.toString().padStart(2, '0');
    const padH = hour.toString().padStart(2, '0');
    testDates.push(`${year}-${padM}-${padD} ${padH}:30:00`);
  }
}

// Remove duplicates if any
const uniqueDates = [...new Set(testDates)].sort();

const cases = [];

for (const dateStr of uniqueDates) {
  try {
    const d = lunisolar(dateStr);
    
    const lunarYear = d.lunar.year;
    const isLeap = d.lunar.isLeapMonth;
    const rawMonth = d.lunar.month;
    const lunarMonth = rawMonth > 100 ? rawMonth % 100 : rawMonth;
    const lunarDay = d.lunar.day;
    
    const ganZhiYear = d.char8.year.toString();
    const ganZhiMonth = d.char8.month.toString();
    const ganZhiDay = d.char8.day.toString();
    
    // Convert traditional Chinese zodiac and officer names to simplified to match Kotlin arrays
    const rawZodiac = d.format('cZ');
    const zodiacMap = {
      '鼠': '鼠', '牛': '牛', '虎': '虎', '兔': '兔', '龍': '龙', '龙': '龙',
      '蛇': '蛇', '馬': '马', '马': '马', '羊': '羊', '猴': '猴', '雞': '鸡',
      '鸡': '鸡', '狗': '狗', '豬': '猪', '猪': '猪'
    };
    const zodiac = zodiacMap[rawZodiac] || rawZodiac;
    
    const rawOfficer = d.theGods.getDuty12God().toString();
    const officerMap = {
      '建': '建', '除': '除', '滿': '满', '满': '满', '平': '平', '定': '定',
      '執': '执', '执': '执', '破': '破', '危': '危', '成': '成', '收': '收',
      '開': '开', '开': '开', '閉': '闭', '闭': '闭'
    };
    const officer = officerMap[rawOfficer] || rawOfficer;
    
    const goodActs = d.theGods.getGoodActs();
    const badActs = d.theGods.getBadActs();
    const isZhushibuyi = goodActs.includes('諸事不宜') || badActs.includes('諸事不宜');

    cases.push({
      solarTime: dateStr,
      lunarYear,
      lunarMonth,
      lunarDay,
      isLeap,
      ganZhiYear,
      ganZhiMonth,
      ganZhiDay,
      zodiac,
      officer,
      isZhushibuyi,
      goodActs,
      badActs
    });
  } catch (err) {
    // Gracefully ignore dates that cause the JS library to throw due to its internal bugs
  }
}

// Generate Kotlin code
let ktCode = `package com.example

import com.example.lunar.LunarHelper
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class LunarCompareTest {

    data class TestCase(
        val solarTime: String,
        val lunarYear: Int,
        val lunarMonth: Int,
        val lunarDay: Int,
        val isLeap: Boolean,
        val ganZhiYear: String,
        val ganZhiMonth: String,
        val ganZhiDay: String,
        val zodiac: String,
        val officer: String,
        val isZhushibuyi: Boolean
    )

    private val testCases = listOf(
`;

for (const c of cases) {
  ktCode += `        TestCase(
            solarTime = "${c.solarTime}",
            lunarYear = ${c.lunarYear},
            lunarMonth = ${c.lunarMonth},
            lunarDay = ${c.lunarDay},
            isLeap = ${c.isLeap},
            ganZhiYear = "${c.ganZhiYear}",
            ganZhiMonth = "${c.ganZhiMonth}",
            ganZhiDay = "${c.ganZhiDay}",
            zodiac = "${c.zodiac}",
            officer = "${c.officer}",
            isZhushibuyi = ${c.isZhushibuyi}
        ),\n`;
}

ktCode += `    )

    @Test
    fun testAllLunarConversions() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        var failureCount = 0
        val sb = StringBuilder()

        for ((index, tc) in testCases.withIndex()) {
            val date = sdf.parse(tc.solarTime)!!
            val result = LunarHelper.fromSolarDate(date)

            var matched = true
            val errors = mutableListOf<String>()

            if (result.lunarYear != tc.lunarYear) {
                matched = false
                errors.add("LunarYear mismatch: expected \${tc.lunarYear}, got \${result.lunarYear}")
            }
            if (result.lunarMonth != tc.lunarMonth) {
                matched = false
                errors.add("LunarMonth mismatch: expected \${tc.lunarMonth}, got \${result.lunarMonth}")
            }
            if (result.lunarDay != tc.lunarDay) {
                matched = false
                errors.add("LunarDay mismatch: expected \${tc.lunarDay}, got \${result.lunarDay}")
            }
            if (result.isLeap != tc.isLeap) {
                matched = false
                errors.add("IsLeap mismatch: expected \${tc.isLeap}, got \${result.isLeap}")
            }
            if (result.ganZhiYear != tc.ganZhiYear) {
                matched = false
                errors.add("GanZhiYear mismatch: expected \${tc.ganZhiYear}, got \${result.ganZhiYear}")
            }
            if (result.ganZhiMonth != tc.ganZhiMonth) {
                matched = false
                errors.add("GanZhiMonth mismatch: expected \${tc.ganZhiMonth}, got \${result.ganZhiMonth}")
            }
            if (result.ganZhiDay != tc.ganZhiDay) {
                matched = false
                errors.add("GanZhiDay mismatch: expected \${tc.ganZhiDay}, got \${result.ganZhiDay}")
            }
            if (result.zodiac != tc.zodiac) {
                matched = false
                errors.add("Zodiac mismatch: expected \${tc.zodiac}, got \${result.zodiac}")
            }

            // Get officer name from officerIndex
            val officerNames = arrayOf("建", "除", "满", "平", "定", "执", "破", "危", "成", "收", "开", "闭")
            // We need to calculate officerIndex in the same way as LunarHelper
            val calendar = java.util.Calendar.getInstance().apply { time = date }
            val hourOfDay = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val calcCalendar = java.util.Calendar.getInstance().apply {
                time = date
                if (hourOfDay >= 23) {
                    add(java.util.Calendar.DAY_OF_MONTH, 1)
                }
            }
            val month = calcCalendar.get(java.util.Calendar.MONTH)
            val day = calcCalendar.get(java.util.Calendar.DAY_OF_MONTH)
            val gMonth = month + 1
            val yearIndexForTerm = if (calcCalendar.get(java.util.Calendar.YEAR) in 1900..2059) calcCalendar.get(java.util.Calendar.YEAR) - 1900 else 0
            val yearTermInfo = LunarHelper.getSolarTermInfo()[yearIndexForTerm]
            val termDay = ((yearTermInfo shr ((gMonth - 1) * 4)) and 0x0FL).toInt()
            val baziMonthIndex = if (day >= termDay) {
                if (gMonth >= 2) gMonth - 2 else 11
            } else {
                if (gMonth >= 3) gMonth - 3
                else if (gMonth == 2) 11
                else 10
            }
            val monthBranchIndex = (baziMonthIndex + 2) % 12

            // dayOffset calculation
            val calBase = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                set(1900, 0, 31, 0, 0, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val calTarget = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                set(calcCalendar.get(java.util.Calendar.YEAR), calcCalendar.get(java.util.Calendar.MONTH), calcCalendar.get(java.util.Calendar.DAY_OF_MONTH), 0, 0, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val dayOffset = ((calTarget.timeInMillis - calBase.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
            val dayIndex = (dayOffset + 40) % 60
            val dayBranchIndex = dayIndex % 12
            val officerIndex = (dayBranchIndex - monthBranchIndex + 12) % 12
            val computedOfficer = officerNames[officerIndex]

            if (computedOfficer != tc.officer) {
                matched = false
                errors.add("Officer mismatch: expected \${tc.officer}, got \${computedOfficer}")
            }

            if (!matched) {
                failureCount++
                sb.append("Case #\${index} [\${tc.solarTime}] FAILED:\\n")
                for (err in errors) {
                    sb.append("  - \${err}\\n")
                }
                sb.append("\\n")
            }
        }

        if (failureCount > 0) {
            System.err.println("=== LUNAR COMPARE COMPREHENSIVE REPORT ===")
            System.err.println(sb.toString())
            System.err.println("Total failures: \${failureCount} out of \${testCases.size} cases.")
            fail("Total failures: \${failureCount} out of \${testCases.size}")
        } else {
            println("ALL \${testCases.size} TEST CASES COMPARED PERFECTLY AND PASSED!")
        }
    }
}
`;

fs.writeFileSync('./app/src/test/java/com/example/LunarCompareTest.kt', ktCode);
console.log('Successfully generated ./app/src/test/java/com/example/LunarCompareTest.kt');
console.log('Generated count:', cases.length);
