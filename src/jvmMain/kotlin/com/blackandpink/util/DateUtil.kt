package com.blackandpink.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Utility functions for date handling
 */
object DateUtil {
    private val fullFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
    
    /**
     * Gets the current date and time in ISO format
     */
    fun now(): String {
        return LocalDateTime.now().format(fullFormatter)
    }
    
    /**
     * Format a full ISO date string to just show the date part
     */
    fun formatDate(isoDate: String): String {
        return try {
            val dateTime = LocalDateTime.parse(isoDate, fullFormatter)
            dateTime.format(dateFormatter)
        } catch (e: Exception) {
            // Return original if can't parse
            isoDate
        }
    }
    
    /**
     * Format a full ISO date string to show date and time
     */
    fun formatDateTime(isoDate: String): String {
        return try {
            val dateTime = LocalDateTime.parse(isoDate, fullFormatter)
            dateTime.format(dateTimeFormatter)
        } catch (e: Exception) {
            // Return original if can't parse
            isoDate
        }
    }
    
    /**
     * Format a full ISO date string to just show the time part
     */
    fun formatTime(isoDate: String): String {
        return try {
            val dateTime = LocalDateTime.parse(isoDate, fullFormatter)
            dateTime.format(timeFormatter)
        } catch (e: Exception) {
            // Return original if can't parse
            isoDate
        }
    }
    
    /**
     * Parse an ISO date string to LocalDateTime
     */
    fun parseDateTime(isoDate: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(isoDate, fullFormatter)
        } catch (e: Exception) {
            null
        }
    }
}
