package com.kaizm.ecommerce_app.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

data class Address(
    val id: String? = "",
    val name: String? = "",
    val address: String? = "",
    val phoneNumber: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(phoneNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }

    @Exclude
    fun updateMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "address" to address,
            "phone_number" to phoneNumber
        )
    }
}