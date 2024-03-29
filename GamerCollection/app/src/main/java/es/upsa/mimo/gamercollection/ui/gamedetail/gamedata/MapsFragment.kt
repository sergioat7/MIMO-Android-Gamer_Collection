package es.upsa.mimo.gamercollection.ui.gamedetail.gamedata

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
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
import es.upsa.mimo.gamercollection.interfaces.OnLocationSelected
import es.upsa.mimo.gamercollection.databinding.FragmentMapsBinding
import es.upsa.mimo.gamercollection.utils.Constants
import kotlin.math.max

class MapsFragment(
    private var location: LatLng?,
    private val onLocationSelected: OnLocationSelected
) : DialogFragment(), OnMapReadyCallback {

    //region Private properties
    private lateinit var binding: FragmentMapsBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //endregion

    //region Lifecycle methods
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.fragment = this
    }
    //endregion

    //region Interface methods
    override fun onMapReady(map: GoogleMap) {

        this.googleMap = map
        location?.let {
            addMarker(it)
        } ?: run {
            addMarker(Constants.DEFAULT_LOCATION)
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    getUserLocation()
                }
            }
        }

        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(p0: Marker) {
            }

            override fun onMarkerDrag(p0: Marker) {
            }

            override fun onMarkerDragEnd(p0: Marker) {
                addMarker(p0.position)
            }
        })

        googleMap.setOnPoiClickListener {
            addMarker(it.latLng)
        }

        googleMap.setOnMapLongClickListener {
            addMarker(it)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locateUser()
        }
    }
    //endregion

    //region Public methods
    fun locateUser() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getUserLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.turn_on_location),
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    fun setLocation() {

        onLocationSelected.setLocation(location)
        dismiss()
    }
    //endregion

    //region Private methods
    private fun checkPermissions(): Boolean {

        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity as AppCompatActivity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1
        )
    }

    private fun isLocationEnabled(): Boolean {

        val locationManager = getSystemService(requireContext(), LocationManager::class.java)
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

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

                locationResult.lastLocation?.let { lastLocation ->
                    val position = LatLng(lastLocation.latitude, lastLocation.longitude)
                    addMarker(position)
                }
            }
        }, Looper.myLooper())
    }

    private fun addMarker(position: LatLng) {

        googleMap.clear()
        googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .draggable(true)
        )
        val zoom = max(googleMap.cameraPosition.zoom, 10F)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoom))
        location = position
    }
    //endregion
}