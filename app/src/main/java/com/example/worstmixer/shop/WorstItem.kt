package com.example.worstmixer.shop

import com.example.worstmixer.Worst

data class ShopItem(
    val id: String,
    val name: String,
    val price: Int,
    val worst: Worst
)
