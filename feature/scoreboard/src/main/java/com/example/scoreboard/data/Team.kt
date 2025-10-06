package com.example.scoreboard.data

import androidx.annotation.ColorInt

data class Team(
    val name: String,
    @ColorInt val primaryColor: Int,
    @ColorInt val textColor: Int
)
