package com.kaizm.ecommerce_app.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Product(
    var id: String? = "",
    var image: String? = "",
    var name: String? = "",
    var price: Int = 0,
    var time: Long = 0,
    var category: String? = "",
    var quantity: Int = 0,
    var description: String? = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
    ) {
    }

    @Exclude
    fun updateProduct(): Map<String, Any?> {
        return mapOf(
            "image" to image,
            "name" to name,
            "price" to price,
            "timeStamp" to time,
            "category" to category,
            "quantity" to quantity,
            "description" to description
        )
    }

    @Exclude
    fun updateProductWithoutImg(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "price" to price,
            "timeStamp" to time,
            "category" to category,
            "quantity" to quantity,
            "description" to description
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeInt(price)
        parcel.writeLong(time)
        parcel.writeString(category)
        parcel.writeInt(quantity)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}