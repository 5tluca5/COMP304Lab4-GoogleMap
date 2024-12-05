package com.tszhim.tszhimng_comp304lab4_ex1.View

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline

@Composable
fun GoogleMapView(modifier: Modifier,
                  userLocation: LatLng?,
                  markerLocation: LatLng?,
                  destination: LatLng?,
                  cameraPositionState: CameraPositionState,
                  onMapClick : (LatLng) -> Unit,
                  onMarkerClick: (LatLng?) -> Unit) {

    var selectedMarker by remember { mutableStateOf<LatLng?>(null) }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(myLocationButtonEnabled = true),
        onMapClick = { latLng ->
            onMapClick(latLng)
        }
    ) {
        markerLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Marker at %.6f, %.6f".format(it.latitude, it.longitude),
                onClick = {
                    onMarkerClick(markerLocation)
                    false
                },
                alpha = 0.8f
            )
        }

        userLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "You are here",
                onClick = {
                    onMarkerClick(null)
                    false
                }
            )
        }

        destination?.let {
            Polyline(
                points = listOf(userLocation!!, destination),
                color = Color.Red,
                width = 10f,
                geodesic = true
            )
        }
    }
}