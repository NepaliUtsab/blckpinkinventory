package com.blackandpink.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import java.time.LocalDateTime

class DateUtilTest {
    
    @Test
    fun `test now returns properly formatted date string`() {
        val dateStr = DateUtil.now()
        assertNotNull(dateStr)
        assertTrue(dateStr.contains("T"))
        val dateTime = DateUtil.parseDateTime(dateStr)
        assertNotNull(dateTime)
    }
    
    @Test
    fun `test formatDate returns date in proper format`() {
        val now = LocalDateTime.now()
        val isoDate = now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formattedDate = DateUtil.formatDate(isoDate)
        assertTrue(formattedDate.contains(","))
    }
    
    @Test
    fun `test formatDateTime returns date and time in proper format`() {
        val now = LocalDateTime.now()
        val isoDate = now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formattedDateTime = DateUtil.formatDateTime(isoDate)
        assertTrue(formattedDateTime.contains(","))
        assertTrue(formattedDateTime.contains(":"))
    }
    
    @Test
    fun `test formatTime returns time in proper format`() {
        val now = LocalDateTime.now()
        val isoDate = now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formattedTime = DateUtil.formatTime(isoDate)
        assertTrue(formattedTime.contains(":"))
    }
    
    @Test
    fun `test parseDateTime returns valid LocalDateTime for valid input`() {
        val now = LocalDateTime.now()
        val isoDate = now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val parsed = DateUtil.parseDateTime(isoDate)
        assertNotNull(parsed)
        assertEquals(now.year, parsed.year)
        assertEquals(now.month, parsed.month)
        assertEquals(now.dayOfMonth, parsed.dayOfMonth)
    }
    
    @Test
    fun `test parseDateTime returns null for invalid input`() {
        val parsed = DateUtil.parseDateTime("not-a-date")
        assertEquals(null, parsed)
    }
}
