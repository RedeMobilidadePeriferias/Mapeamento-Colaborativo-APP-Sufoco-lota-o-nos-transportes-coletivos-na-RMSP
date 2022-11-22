package br.com.mapeamentosufoco.app.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import br.com.mapeamentosufoco.app.dao.DatabaseHelper
import br.com.mapeamentosufoco.app.entities.Report
import br.com.mapeamentosufoco.app.entities.DefaultResult
import br.com.mapeamentosufoco.app.util.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StartSync : Service() {
    private val milliseconds: Long = 1000
    private val minute: Long = 60

    override fun onCreate() {
        super.onCreate()
        val context = this.baseContext
        startSyncReports(context)
        Log.d("Service", "StartLocationAndVisitService Started")
    }

    override fun onDestroy() {
        Log.d("SERVICE", "onDestroy: StartLocationAndVisitService")
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun startSyncReports(context: Context) {
        val interval = 1 * minute * milliseconds
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                try {
                    val databaseHelper = DatabaseHelper(context)
                    val reports: ArrayList<Report> = databaseHelper.findReportsNotSync()

                    for (report in reports) {
                        val service: ReportService = ServiceGenerator.createService(ReportService::class.java)
                        val resultCall: Call<DefaultResult> = service.postReport(report)

                        resultCall.enqueue(object : Callback<DefaultResult> {
                            override fun onResponse(
                                call: Call<DefaultResult>,
                                response: Response<DefaultResult>
                            ) {
                                if (response.isSuccessful) {
                                    databaseHelper.updateReport(report)
                                }
                            }

                            override fun onFailure(
                                call: Call<DefaultResult>,
                                t: Throwable
                            ) {
                                t.printStackTrace()
                            }
                        })
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 0, interval)
    }
}


