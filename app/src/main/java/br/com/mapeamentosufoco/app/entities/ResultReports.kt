package br.com.mapeamentosufoco.app.entities

import com.google.gson.annotations.SerializedName
import kotlin.collections.ArrayList

class ResultReports {
    @SerializedName("statusCode")
    val codStatus: String? = null

    @SerializedName("reports")
    val reports: ArrayList<Report> = ArrayList()
}
