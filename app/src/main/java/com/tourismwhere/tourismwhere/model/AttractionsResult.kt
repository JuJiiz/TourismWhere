package com.tourismwhere.tourismwhere.model

import com.google.gson.annotations.SerializedName

class AttractionsResult {
    @SerializedName("venues")
    lateinit var venues: ArrayList<AttractionModel>
}