package com.example.weathery

import androidx.compose.ui.graphics.Color

/**
 * fragment마다 toolbar의 요소를 업데이트하는 인터페이스
 */
interface ToolbarUpdater {
    fun updateToolbar(menuResId: Int, title: String, tvColor: Int, searchResId: Int)
}
