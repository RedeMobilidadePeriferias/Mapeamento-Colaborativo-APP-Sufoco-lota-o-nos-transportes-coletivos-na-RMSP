package br.com.mapeamentosufoco.app.services

import br.com.mapeamentosufoco.app.entities.Report
import br.com.mapeamentosufoco.app.entities.DefaultResult
import br.com.mapeamentosufoco.app.entities.ResultReports

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReportService {
    @GET("report")
    fun listReports(): Call<ResultReports>

    @POST("report")
    fun postReport(@Body report: Report): Call<DefaultResult>
}