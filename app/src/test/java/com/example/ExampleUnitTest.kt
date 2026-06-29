package com.example

import com.example.lunar.LunarHelper
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.TimeZone

class ExampleUnitTest {
  
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testTodayAlmanacAccuracy() {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").apply {
      timeZone = TimeZone.getTimeZone("GMT+8")
    }
    
    // Test June 29, 2026 (Today)
    val testDate = sdf.parse("2026-06-29 12:00:00")!!
    val lunar = LunarHelper.fromSolarDate(testDate)
    
    // Assert general lunar date information for June 29, 2026
    assertEquals(2026, lunar.lunarYear)
    assertEquals(5, lunar.lunarMonth)
    assertEquals(15, lunar.lunarDay)
    assertEquals("五月", lunar.lunarMonthName)
    assertEquals("十五", lunar.lunarDayName)
    
    // Assert GanZhi and Zodiac
    assertEquals("丙午", lunar.ganZhiYear)
    assertEquals("甲午", lunar.ganZhiMonth)
    assertEquals("甲辰", lunar.ganZhiDay)
    assertEquals("马", lunar.zodiac)
    
    // Assert suitable and taboo lists for June 29, 2026 (开日)
    val expectedSuitable = listOf("嫁娶", "纳采", "祭祀", "祈福", "出行", "立券", "移徙", "入宅", "动土", "破土", "安葬")
    val expectedTaboo = listOf("开光", "作灶", "盖屋", "架马", "开仓")
    assertEquals(expectedSuitable, lunar.suitable)
    assertEquals(expectedTaboo, lunar.taboo)
    
    // Assert other daily predictions
    assertEquals("冲狗煞南", lunar.chongSha)
    assertEquals("东北", lunar.caiShen)
    assertEquals("东北", lunar.xiShen)
    assertEquals("正北", lunar.fuShen)
  }

  @Test
  fun testChineseNewYearAlmanacAccuracy() {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").apply {
      timeZone = TimeZone.getTimeZone("GMT+8")
    }
    
    // Test Chinese New Year 2026 (February 17, 2026)
    val testDate = sdf.parse("2026-02-17 12:00:00")!!
    val lunar = LunarHelper.fromSolarDate(testDate)
    
    // Assert general lunar date info for CNY 2026
    assertEquals(2026, lunar.lunarYear)
    assertEquals(1, lunar.lunarMonth)
    assertEquals(1, lunar.lunarDay)
    assertEquals("正月", lunar.lunarMonthName)
    assertEquals("初一", lunar.lunarDayName)
    
    // Assert GanZhi and Zodiac
    assertEquals("丙午", lunar.ganZhiYear)
    assertEquals("庚寅", lunar.ganZhiMonth)
    assertEquals("壬辰", lunar.ganZhiDay)
    assertEquals("马", lunar.zodiac)
    
    // Assert suitable and taboo lists for CNY 2026 (满日)
    val expectedSuitable = listOf("祭祀", "祈福", "沐浴", "结网", "取鱼", "扫舍")
    val expectedTaboo = listOf("嫁娶", "动土", "移徙", "入宅", "安葬", "词讼")
    assertEquals(expectedSuitable, lunar.suitable)
    assertEquals(expectedTaboo, lunar.taboo)
    
    // Assert other daily predictions for 壬辰日 (stemIndex 8, branchIndex 4)
    assertEquals("冲狗煞南", lunar.chongSha)
    assertEquals("正南", lunar.caiShen)
    assertEquals("正南", lunar.xiShen)
    assertEquals("东南", lunar.fuShen)
  }

  @Test
  fun testTimezoneAndHourIndependence() {
    // Save original default timezone
    val originalDefault = TimeZone.getDefault()
    
    try {
      // 1. Test in GMT+8 (Beijing Time)
      TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"))
      val sdfGMT8 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      
      val gmt8_early = sdfGMT8.parse("2026-06-29 00:30:00")!!
      val gmt8_midday = sdfGMT8.parse("2026-06-29 12:00:00")!!
      val gmt8_night = sdfGMT8.parse("2026-06-29 23:45:00")!!
      
      val l1 = LunarHelper.fromSolarDate(gmt8_early)
      val l2 = LunarHelper.fromSolarDate(gmt8_midday)
      val l3 = LunarHelper.fromSolarDate(gmt8_night)
      
      assertEquals(2026, l1.lunarYear)
      assertEquals(5, l1.lunarMonth)
      assertEquals(15, l1.lunarDay)
      assertEquals("甲辰", l1.ganZhiDay)
      
      assertEquals(l1.lunarYear, l2.lunarYear)
      assertEquals(l1.lunarMonth, l2.lunarMonth)
      assertEquals(l1.lunarDay, l2.lunarDay)
      assertEquals(l1.ganZhiDay, l2.ganZhiDay)
      
      assertEquals(l1.lunarYear, l3.lunarYear)
      assertEquals(l1.lunarMonth, l3.lunarMonth)
      assertEquals(l1.lunarDay, l3.lunarDay)
      assertEquals(l1.ganZhiDay, l3.ganZhiDay)
      
      // 2. Test in America/Los_Angeles (Pacific Time)
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"))
      val sdfLA = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      
      val la_early = sdfLA.parse("2026-06-29 00:15:00")!!
      val la_midday = sdfLA.parse("2026-06-29 12:00:00")!!
      val la_night = sdfLA.parse("2026-06-29 23:59:00")!!
      
      val l4 = LunarHelper.fromSolarDate(la_early)
      val l5 = LunarHelper.fromSolarDate(la_midday)
      val l6 = LunarHelper.fromSolarDate(la_night)
      
      assertEquals(2026, l4.lunarYear)
      assertEquals(5, l4.lunarMonth)
      assertEquals(15, l4.lunarDay)
      assertEquals("甲辰", l4.ganZhiDay)
      
      assertEquals(l4.lunarYear, l5.lunarYear)
      assertEquals(l4.lunarMonth, l5.lunarMonth)
      assertEquals(l4.lunarDay, l5.lunarDay)
      assertEquals(l4.ganZhiDay, l5.ganZhiDay)
      
      assertEquals(l4.lunarYear, l6.lunarYear)
      assertEquals(l4.lunarMonth, l6.lunarMonth)
      assertEquals(l4.lunarDay, l6.lunarDay)
      assertEquals(l4.ganZhiDay, l6.ganZhiDay)
      
    } finally {
      // Restore original default timezone
      TimeZone.setDefault(originalDefault)
    }
  }
}
