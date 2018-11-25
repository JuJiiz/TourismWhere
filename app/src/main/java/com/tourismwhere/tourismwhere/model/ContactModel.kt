package com.tourismwhere.tourismwhere.model

import android.os.Parcel
import android.os.Parcelable

class ContactModel() : Parcelable {
    var website: String? = null
    var telnumber: String? = null
    var lineid: String? = null
    var facebook: String? = null
    var email: String? = null

    constructor(parcel: Parcel) : this() {
        website = parcel.readString()
        telnumber = parcel.readString()
        lineid = parcel.readString()
        facebook = parcel.readString()
        email = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(website)
        parcel.writeString(telnumber)
        parcel.writeString(lineid)
        parcel.writeString(facebook)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactModel> {
        override fun createFromParcel(parcel: Parcel): ContactModel {
            return ContactModel(parcel)
        }

        override fun newArray(size: Int): Array<ContactModel?> {
            return arrayOfNulls(size)
        }
    }
}