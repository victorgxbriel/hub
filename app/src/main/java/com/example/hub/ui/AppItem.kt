package com.example.hub.ui

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

data class AppItem(
    val name: String,
    @DrawableRes val iconRes: Int,
    @IdRes val navigationAction: Int
)
