package com.shhatrat.loggerek.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by szymon on 06.07.17.
 */
data class LogRequest(val cache_code : String?,
                      val logtype : String? =null,
                      val comment : String? =null,
                      val logDate : String? =null,
                      val recommend : Boolean? =null,
                      val rating : Int? =null,
                      val password : String? =null) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<LogRequest> = object : Parcelable.Creator<LogRequest> {
            override fun createFromParcel(source: Parcel): LogRequest = LogRequest(source)
            override fun newArray(size: Int): Array<LogRequest?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readValue(Boolean::class.java.classLoader) as Boolean?,
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(cache_code)
        dest.writeString(logtype)
        dest.writeString(comment)
        dest.writeString(logDate)
        dest.writeValue(recommend)
        dest.writeValue(rating)
        dest.writeString(password)
    }
}