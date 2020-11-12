package com.platinumlist.sample.ui.showlist.common

data class ShowUI(
    val id: Long
){
    fun calculateTitle() = "Event show(id: ${id})"
}