package com.platinumlist.sample.ui.showlist.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.platinumlist.sdk.R
import kotlinx.android.synthetic.main.list_item_show.view.*

class ShowHolder private constructor(
    v: View,
    private val itemClick: (ShowUI) -> Unit
) : RecyclerView.ViewHolder(v) {

    fun bind(item: ShowUI) {
        with(itemView) {
            title.text = item.calculateTitle()

            setOnClickListener {
                itemClick(item)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup, itemClick: (ShowUI) -> Unit): ShowHolder {
            return LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_show, parent, false)
                .let {
                    ShowHolder(it, itemClick)
                }
        }
    }
}