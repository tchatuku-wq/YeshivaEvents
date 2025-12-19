package com.example.yeshivaevents.ui.state


sealed class UIState<out T> {

    object Loading : UIState<Nothing>()

    object Empty : UIState<Nothing>()

    data class Success<T>(val data: T) : UIState<T>()

    data class Error(val throwable: Throwable) : UIState<Nothing>()
}
