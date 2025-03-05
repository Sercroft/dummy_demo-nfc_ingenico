package com.credibanco.dummy_demo_ingenico.dto

import android.os.Parcel
import android.os.Parcelable

data class PeripheralDTO(
    var model: String? = null,
    var printer: Boolean = false,
    var chip: Boolean = false,
    var band: Boolean = false,
    var nfc: Boolean = false,
    var camera: Boolean = false,
    var frontCamera: Boolean = false,
    var flash: Boolean = false
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(model)
        parcel.writeByte(if (printer) 1 else 0)
        parcel.writeByte(if (chip) 1 else 0)
        parcel.writeByte(if (band) 1 else 0)
        parcel.writeByte(if (nfc) 1 else 0)
        parcel.writeByte(if (camera) 1 else 0)
        parcel.writeByte(if (frontCamera) 1 else 0)
        parcel.writeByte(if (flash) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PeripheralDTO> {
        override fun createFromParcel(parcel: Parcel): PeripheralDTO {
            return PeripheralDTO(parcel)
        }

        override fun newArray(size: Int): Array<PeripheralDTO?> {
            return arrayOfNulls(size)
        }
    }
}
