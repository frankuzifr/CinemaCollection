package space.frankuzi.cinemacollection.data

import android.os.Parcel
import android.os.Parcelable

class Film(
    val nameIdRes: Int,
    val descriptionIdRes: Int,
    val imageIdRes: Int,
    var isFavourite: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeInt(nameIdRes)
        p0?.writeValue(descriptionIdRes)
        p0?.writeValue(imageIdRes)
        p0?.writeValue(isFavourite)
    }

    companion object CREATOR : Parcelable.Creator<Film> {
        override fun createFromParcel(parcel: Parcel): Film {
            return Film(parcel)
        }

        override fun newArray(size: Int): Array<Film?> {
            return arrayOfNulls(size)
        }
    }
}