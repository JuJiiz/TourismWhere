package com.tourismwhere.tourismwhere.model

import com.google.gson.annotations.SerializedName

class AttractionsResponse{
    @SerializedName("status")
    lateinit var status: String

    @SerializedName("result")
    lateinit var result: AttractionsResult
}