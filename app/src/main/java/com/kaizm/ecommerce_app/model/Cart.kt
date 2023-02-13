package com.kaizm.ecommerce_app.model

import android.os.Parcel
import android.os.Parcelable

data class Cart(
    var id: String? = null,
    var image: String? = null,
    var name: String? = null,
    var price: Int = 0,
    var quantity: Int = 1
) : Parcelable {

    constructor(id: String?, image: String?, name: String?, price: Int) : this() {
        this.id = id
        this.image = image
        this.name = name
        this.price = price
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    fun quantityMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["quantity"] = quantity
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeInt(price)
        parcel.writeInt(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cart> {
        override fun createFromParcel(parcel: Parcel): Cart {
            return Cart(parcel)
        }

        override fun newArray(size: Int): Array<Cart?> {
            return arrayOfNulls(size)
        }
    }
}