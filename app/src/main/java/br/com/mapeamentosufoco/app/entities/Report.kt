package br.com.mapeamentosufoco.app.entities

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

data class Report(
    var id: Int = 0,
    var userId: String? = "",
    var userLocation: String? = "0,0",
    var travelReason: String? = "",
    var travelTime: String? = "",
    var travelStatus: String? = "",
    var travelCategory: String? = "",
    var travelLine: String? = "",
    var travelStation: String? = "",
    var travelDate: String? = "",
    var sync: String? = ""
) : Parcelable {

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Report> {
            override fun createFromParcel(parcel: Parcel) = Report(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Report>(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        userId = parcel.readString(),
        userLocation = parcel.readString(),
        travelReason = parcel.readString(),
        travelTime = parcel.readString(),
        travelStatus = parcel.readString(),
        travelCategory = parcel.readString(),
        travelLine = parcel.readString(),
        travelStation = parcel.readString(),
        travelDate = parcel.readString(),
        sync = "S",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(userId)
        parcel.writeString(userLocation)
        parcel.writeString(travelReason)
        parcel.writeString(travelTime)
        parcel.writeString(travelStatus)
        parcel.writeString(travelCategory)
        parcel.writeString(travelLine)
        parcel.writeString(travelStation)
        parcel.writeString(travelDate)
        parcel.writeString(sync)
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(): String {
        var formatedDate = ""

        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("pt", "BR"))
            dateFormat.timeZone = TimeZone.getTimeZone("BET")
            val date = dateFormat.parse(travelDate)

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            formatedDate = sdf.format(date)

        } catch (e : Exception) {
            e.printStackTrace()
        }

        return formatedDate
    }

    override fun describeContents() = 0
}