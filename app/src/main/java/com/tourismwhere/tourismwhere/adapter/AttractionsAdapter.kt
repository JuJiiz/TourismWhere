package com.tourismwhere.tourismwhere.adapter

import android.location.Location
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.tourismwhere.tourismwhere.R
import com.tourismwhere.tourismwhere.model.AttractionModel
import com.tourismwhere.tourismwhere.safeLet
import javax.inject.Inject

class AttractionsAdapter @Inject constructor() : RecyclerView.Adapter<AttractionsAdapter.AttractionsViewHolder>() {
    var mCurrentLocation: LatLng? = null
    var mAttractionsList: List<AttractionModel> = listOf()

    interface OnItemClick {
        fun showAttraction(attraction: AttractionModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionsViewHolder = AttractionsViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.attraction_recycler_view_item,
            parent,
            false
        )
    )

    override fun getItemCount(): Int = mAttractionsList.size

    override fun onBindViewHolder(holder: AttractionsViewHolder, position: Int) {
        holder.bindView(mAttractionsList[position])
    }

    inner class AttractionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootItem: LinearLayout = itemView.findViewById(R.id.rootItem)
        var imvAttraction: ImageView = itemView.findViewById(R.id.imvAttraction)
        var tvAttractionName: TextView = itemView.findViewById(R.id.tvAttractionName)
        var tvAttractionDescription: TextView = itemView.findViewById(R.id.tvAttractionDescription)
        var tvAttractionDistance: TextView = itemView.findViewById(R.id.tvAttractionDistance)

        init {
            rootItem.setOnClickListener {
                if (itemView.context is OnItemClick) (itemView.context as OnItemClick).showAttraction(mAttractionsList[adapterPosition])
            }
        }

        fun bindView(attraction: AttractionModel) {
            if (!attraction.images.isEmpty()) {
                Glide.with(itemView.context)
                    .load(attraction.images.first())
                    .into(imvAttraction)
            } else {
                Glide.with(itemView.context)
                    .load("")
                    .into(imvAttraction)
            }

            safeLet(attraction.location, mCurrentLocation) { location, currentLocation ->
                safeLet(location.latitude, location.longitude) { latitude, longitude ->
                    val locationA = Location("current point")
                    locationA.latitude = currentLocation.latitude
                    locationA.longitude = currentLocation.longitude

                    val locationB = Location("target point")
                    locationB.latitude = latitude
                    locationB.longitude = longitude

                    Log.d("MLOG", "${attraction.name} distance: ${locationA.distanceTo(locationB) / 1000}")
                    tvAttractionDistance.text = "%.2f".format(locationA.distanceTo(locationB) / 1000)
                }
            }

            tvAttractionName.text = if (attraction.name != null) attraction.name else "-"
            tvAttractionDescription.text = if (attraction.history != null) attraction.history else "-"
        }
    }
}