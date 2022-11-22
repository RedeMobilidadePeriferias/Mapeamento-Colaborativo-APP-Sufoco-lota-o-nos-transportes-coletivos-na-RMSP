package br.com.mapeamentosufoco.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import br.com.mapeamentosufoco.app.entities.Report
import br.com.mapeamentosufoco.app.entities.ResultReports
import br.com.mapeamentosufoco.app.services.ReportService
import br.com.mapeamentosufoco.app.services.StartSync
import br.com.mapeamentosufoco.app.util.ServiceGenerator
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import java.util.*
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationUpdates()

        val btReport = findViewById<Button>(R.id.btReport)

        btReport.setOnClickListener{
            getLocation("newReport", btReport)
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.empty)
        setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.project -> {
                val dialog = Dialog(this, R.style.AppTheme)
                dialog.setContentView(R.layout.about_project)
                dialog.show()

                val appBar = dialog.findViewById<Toolbar>(R.id.toolbar)

                appBar.setOnClickListener {
                    dialog.dismiss()
                }

                val openSite = dialog.findViewById<TextView>(R.id.site)

                openSite.setOnClickListener {
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse("https://www.mapeamentosufoco.com.br")
                    startActivity(openURL)
                }
                true
            }
            R.id.aboutUs -> {
                val dialog = Dialog(this, R.style.AppTheme)
                dialog.setContentView(R.layout.about_us)
                dialog.show()

                val appBar = dialog.findViewById<Toolbar>(R.id.toolbar)

                appBar.setOnClickListener {
                    dialog.dismiss()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(action: String, btReport : View? = null) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (action === "newReport") {
                    val reportDialog = ReportDialog(this, mMap)
                    if (location != null) {
                        reportDialog.startDialog(LatLng(location.latitude, location.longitude), btReport)
                    } else {
                        Snackbar.make(btReport!!, "Erro ao carregar localização, tente novamente!", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location!!.latitude, location.longitude), 15f))
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                -23.55958133,
                                -46.63593292
                            ), 13f
                        )
                    )
                }
            }.addOnFailureListener {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )

                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            -23.55958133,
                            -46.63593292
                        ), 13f
                    )
                )
            }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        getLocation("moveMap")

        mMap.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker?): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View? {
                val info = LinearLayout(baseContext)
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(baseContext)
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(baseContext)
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(title)
                info.addView(snippet)
                return null
            }
        })

        addMarkers()
        startService(Intent(this, StartSync::class.java))
    }

    private fun addMarkers() {
        AddMarkersAsync(mMap).execute()

        val interval = 2 * 60 * 1000L
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                try {
                    AddMarkersAsync(mMap).execute()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 0, interval)
    }

    class AddMarkersAsync(private val mMap: GoogleMap) : AsyncTask<Void, Void, String>() {

        var reportList: ArrayList<Report> = ArrayList()

        override fun doInBackground(vararg params: Void?): String? {
            try {
                val service: ReportService =
                    ServiceGenerator.createService(ReportService::class.java)
                val resultCall: Call<ResultReports> = service.listReports()

                reportList = resultCall.execute().body()!!.reports
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (reportList.size != 0) {
                mMap.clear()
            }

            for (report in reportList) {
                addMarker(report, mMap)
            }
        }
    }

    companion object {
        fun addMarker(report: Report, mMap: GoogleMap)
        {
            val latlong = report.userLocation!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val latitude = java.lang.Double.parseDouble(latlong[0])
            val longitude = java.lang.Double.parseDouble(latlong[1])

            if (report.travelCategory == "Ônibus") {
                mMap.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(latitude, longitude)
                        )
                        .title(report.travelCategory)
                        .snippet(report.travelLine + " - " + report.formatDate() + " - " + report.travelStatus)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                )
            } else if (report.travelCategory == "Metrô") {
                mMap.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(latitude, longitude)
                        )
                        .title(report.travelCategory)
                        .snippet(report.travelLine + " - " + report.formatDate() + " - " + report.travelStatus)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
            } else {
                mMap.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(latitude, longitude)
                        )
                        .title(report.travelCategory)
                        .snippet(report.travelLine + " - " + report.formatDate() + " - " + report.travelStatus)
                        .icon(BitmapDescriptorFactory.defaultMarker(70f))
                )
            }
        }
    }
    /**
     * call this method in onCreate
     * onLocationResult call when location is changed
     */
    private fun getLocationUpdates()
    {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
            }
        }
    }

    //start location updates
    private fun startLocationUpdates() {
        
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            requestCode -> {
                return
            }
        }
    }
}
