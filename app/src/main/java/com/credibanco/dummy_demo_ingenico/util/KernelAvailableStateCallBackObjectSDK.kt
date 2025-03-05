package com.credibanco.dummy_demo_ingenico.util

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

sealed class KernelAvailableStateCallBackObjectSDK{
    companion object{
        const val AVAILABLE_KERNEL = "AVAILABLE_KERNEL"
    }

    data class AvailableKernel(
        @SerializedName("callBacktype")
        val callBacktype: String? = AVAILABLE_KERNEL
    ) : KernelAvailableStateCallBackObjectSDK(), Parcelable {
        constructor(parcel: Parcel) : this(parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(callBacktype)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<AvailableKernel> {
            override fun createFromParcel(parcel: Parcel): AvailableKernel {
                return AvailableKernel(parcel)
            }

            override fun newArray(size: Int): Array<AvailableKernel?> {
                return arrayOfNulls(size)
            }
        }
    }
}