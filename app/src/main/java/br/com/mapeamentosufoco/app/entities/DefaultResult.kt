package br.com.mapeamentosufoco.app.entities

import com.google.gson.annotations.SerializedName

class DefaultResult {
    @SerializedName("statusCode")
    val codStatus: String? = null

    @SerializedName("mensagem")
    val message: String? = null
}
