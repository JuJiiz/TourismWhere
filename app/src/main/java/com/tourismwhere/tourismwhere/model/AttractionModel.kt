package com.tourismwhere.tourismwhere.model

import android.os.Parcel
import android.os.Parcelable

class AttractionModel() : Parcelable {
    var name: String? = null
    var contact: ContactModel? = null
    var location: LocationModel? = null
    var images: List<String> = arrayListOf()
    var id: Int? = null
    var categories: CategoriesModel? = null
    var history: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        contact = parcel.readParcelable(ContactModel::class.java.classLoader)
        location = parcel.readParcelable(LocationModel::class.java.classLoader)
        images = parcel.createStringArrayList()
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        categories = parcel.readParcelable(CategoriesModel::class.java.classLoader)
        history = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeParcelable(contact, flags)
        parcel.writeParcelable(location, flags)
        parcel.writeStringList(images)
        parcel.writeValue(id)
        parcel.writeParcelable(categories, flags)
        parcel.writeString(history)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AttractionModel> {
        override fun createFromParcel(parcel: Parcel): AttractionModel {
            return AttractionModel(parcel)
        }

        override fun newArray(size: Int): Array<AttractionModel?> {
            return arrayOfNulls(size)
        }
    }
}