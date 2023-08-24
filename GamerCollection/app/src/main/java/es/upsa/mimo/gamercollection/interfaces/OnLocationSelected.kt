package es.upsa.mimo.gamercollection.interfaces

import com.google.android.gms.maps.model.LatLng

interface OnLocationSelected {
    fun setLocation(location: LatLng?)
}