package com.platinumlist.sample.ui.showlist.common

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ShowAdapter(
    private val itemClick: (ShowUI) -> Unit
) : RecyclerView.Adapter<ShowHolder>() {

    private var _data: List<ShowUI> = emptyList()

    var data: List<ShowUI>
        get() = _data
        set(value) {
            _data = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowHolder {
        return ShowHolder.create(parent, itemClick)
    }

    override fun getItemCount() = _data.size

    override fun onBindViewHolder(holder: ShowHolder, position: Int) {
        _data[position].let {
            holder.bind(it)
        }
    }
}