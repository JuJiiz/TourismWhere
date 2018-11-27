package com.tourismwhere.tourismwhere.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.tourismwhere.tourismwhere.R
import com.tourismwhere.tourismwhere.model.AttractionModel
import javax.inject.Inject

class SearchAdapter @Inject constructor() : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    var mSearchList: List<AttractionModel> = listOf()
    private var callback: OnItemClick? = null

    interface OnItemClick {
        fun showAttraction(attraction: AttractionModel)
    }

    fun setOnItemClickListener(callback: OnItemClick) {
        this.callback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder = SearchViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.search_recycler_view_item,
            parent,
            false
        )
    )

    override fun getItemCount(): Int = mSearchList.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bindView(mSearchList[position])
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAttractionName: TextView = itemView.findViewById(R.id.tvAttractionName)
        var btnNavigate: ImageView = itemView.findViewById(R.id.btnNavigate)

        init {
            btnNavigate.setOnClickListener {
                callback?.showAttraction(mSearchList[adapterPosition])
            }
        }

        fun bindView(attraction: AttractionModel) {
            tvAttractionName.text = "${attraction.name}"
        }
    }
}