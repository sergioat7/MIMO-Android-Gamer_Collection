package es.upsa.mimo.gamercollection.adapters

import com.google.android.gms.maps.model.LatLng

interface OnLocationSelected {
    fun setLocation(location: LatLng?)
}