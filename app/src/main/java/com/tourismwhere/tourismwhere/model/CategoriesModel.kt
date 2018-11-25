package com.tourismwhere.tourismwhere.model

import android.os.Parcel
import android.os.Parcelable

class CategoriesModel() : Parcelable {
    var subtype: List<String>? = null
    var name: String? = null

    constructor(parcel: Parcel) : this() {
        subtype = parcel.createStringArrayList()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(subtype)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoriesModel> {
        override fun createFromParcel(parcel: Parcel): CategoriesModel {
            return CategoriesModel(parcel)
        }

        override fun newArray(size: Int): Array<CategoriesModel?> {
            return arrayOfNulls(size)
        }
    }
}