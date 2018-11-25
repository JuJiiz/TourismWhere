package com.tourismwhere.tourismwhere.model

import android.os.Parcel
import android.os.Parcelable

class LocationModel() : Parcelable {
    var province: String? = null
    var amphur: String? = null
    var district: String? = null
    var region: String? = null
    var longitude: Double? = null
    var address: String? = null
    var latitude: Double? = null

    constructor(parcel: Parcel) : this() {
        province = parcel.readString()
        amphur = parcel.readString()
        district = parcel.readString()
        region = parcel.readString()
        longitude = parcel.readValue(Int::class.java.classLoader) as? Double
        address = parcel.readString()
        latitude = parcel.readValue(Int::class.java.classLoader) as? Double
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(province)
        parcel.writeString(amphur)
        parcel.writeString(district)
        parcel.writeString(region)
        parcel.writeValue(longitude)
        parcel.writeString(address)
        parcel.writeValue(latitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocationModel> {
        override fun createFromParcel(parcel: Parcel): LocationModel {
            return LocationModel(parcel)
        }

        override fun newArray(size: Int): Array<LocationModel?> {
            return arrayOfNulls(size)
        }
    }
}