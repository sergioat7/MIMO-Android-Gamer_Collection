package es.upsa.mimo.gamercollection.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import kotlinx.android.synthetic.main.fragment_maps.*

class MapsFragment(
    private var location: LatLng?,
    private val onLocationSelected: OnLocationSelected
) : DialogFragment(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private val madrid = LatLng(40.4169019, -3.7056721)
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as GameDetailActivity)

        initializeUI()
    }

    override fun onMapReady(googleMap: GoogleMap) {

        this.googleMap = googleMap
        location?.let {
            addMarker(it)
        } ?: run {
            addMarker(madrid)
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    getUserLocation()
                }
            }
        }
        googleMap.setOnMarkerDragListener(this)
    }

    override fun onMarkerDragEnd(p0: Marker?) {

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(p0?.position))
        location = p0?.position
    }

    override fun onMarkerDragStart(p0: Marker?) {}

    override fun onMarkerDrag(p0: Marker?) {}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locateUser()
            }
        }
    }

    // MARK: - Private functions

    private fun initializeUI() {

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        button_location.setOnClickListener { locateUser() }

        button_save.setOnClickListener {
            onLocationSelected.setLocation(location)
            dismiss()
        }
        button_remove.setOnClickListener {
            onLocationSelected.setLocation(null)
            dismiss()
        }
    }

    private fun checkPermissions(): Boolean {

        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(activity as GameDetailActivity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }

    private fun isLocationEnabled(): Boolean {

        val locationManager = getSystemService(requireContext(), LocationManager::class.java)
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
        return false
    }

    private fun locateUser() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getUserLocation()
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {

        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->

            location?.let {
                addMarker(LatLng(it.latitude, it.longitude))
            } ?: run {
                requestNewLocationData()
            }
        }
    }

    @SuppressLint("MissingPermission", "RestrictedApi")
    private fun requestNewLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.requestLocationUpdates(mLocationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                val lastLocation: Location = locationResult.lastLocation
                val position = LatLng(lastLocation.latitude, lastLocation.longitude)
                addMarker(position)
            }
        }, Looper.myLooper())
    }

    private fun addMarker(position: LatLng){

        googleMap.clear()
        googleMap.addMarker(
            MarkerOptions()
            .position(position)
            .draggable(true)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position))
        location = position
    }
}
interface OnLocationSelected {
    fun setLocation(location: LatLng?)
}