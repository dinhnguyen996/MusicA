package com.example.musicapp.listener

import android.view.View

interface IOnItemClick {
    fun <T> onItemClick(item: T, isLongClick: Boolean, view: View)
}