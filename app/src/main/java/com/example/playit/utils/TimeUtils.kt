package com.example.playit.utils

import com.example.playit.core.Constants

object TimeUtils {
    fun formatTime(milliseconds: Long): String {
        if (milliseconds <= 0) return "0:00"
        
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val remainingSeconds = totalSeconds % 60
        return String.format(Constants.TIME_FORMAT_PATTERN, minutes, remainingSeconds)
    }
} 