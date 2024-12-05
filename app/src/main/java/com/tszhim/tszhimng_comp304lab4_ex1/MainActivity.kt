package com.tszhim.tszhimng_comp304lab4_ex1

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.tszhim.tszhimng_comp304lab4_ex1.Geofence.addGeofence
import com.tszhim.tszhimng_comp304lab4_ex1.Geofence.createGeofence
import com.tszhim.tszhimng_comp304lab4_ex1.LogDataWorker.LocationLogger
import com.tszhim.tszhimng_comp304lab4_ex1.RoomDB.AppDatabase
import com.tszhim.tszhimng_comp304lab4_ex1.View.GoogleMapView
import com.tszhim.tszhimng_comp304lab4_ex1.ViewModels.AppRepository
import com.tszhim.tszhimng_comp304lab4_ex1.ui.theme.TszHimNg_COMP304Lab4_Ex1Theme

class MainActivity : ComponentActivity() {

    private lateinit var workManager : WorkManager
    var idsForLog = emptyList<Int>()
    var latitudesForLog = emptyList<Double>()
    var longitudesForLog = emptyList<Double>()
    var timestampsForLog = emptyList<Long>()

    val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true
        ) {
            // Permissions granted
        } else {
            Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        workManager = WorkManager.getInstance(applicationContext)

        val database = AppDatabase.getInstance(applicationContext)

        val repository = database?.let { AppRepository(it.locationLogDao) }

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        )

        setContent {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
            }
            TszHimNg_COMP304Lab4_Ex1Theme {
                GoogleMapComposeApp(modifier = Modifier.fillMaxSize(), cameraPositionState)
//                GeofencingExampleApp()
            }
        }
    }


    override fun onPause() {
        super.onPause()

        var request = OneTimeWorkRequestBuilder<LocationLogger>().setInputData(
            workDataOf(
                "ids" to idsForLog.toTypedArray(),
                "latitudes" to latitudesForLog.toTypedArray(),
                "longitudes" to longitudesForLog.toTypedArray(),
                "timestamps" to timestampsForLog.toTypedArray()
            )).setConstraints(
            Constraints(
                // Not required as using RoomDB
                // requiredNetworkType = NetworkType.CONNECTED
            )
        ).build()

        workManager.enqueue(request)

        idsForLog = emptyList()
        latitudesForLog = emptyList()
        longitudesForLog = emptyList()
        timestampsForLog = emptyList()
    }


    @SuppressLint("MissingPermission")
    @Composable
    fun GoogleMapComposeApp(modifier: Modifier, cameraPositionState: CameraPositionState) {

        val context = LocalContext.current
        var userLocation by remember { mutableStateOf<LatLng?>(null) }
        var markerLocation by remember { mutableStateOf<LatLng?>(null) }
        var destination by remember { mutableStateOf<LatLng?>(null) }
        var marketClicked  by remember { mutableStateOf<LatLng?>(null) }
        val fusedLocationClient =
            remember { LocationServices.getFusedLocationProviderClient(context) }
        val locationRequest = remember {
            LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 1000 // Update every 10 seconds
                fastestInterval = 5000
            }
        }

        // Request location updates
        LaunchedEffect(Unit) {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val newLocation = LatLng(location.latitude, location.longitude)

                        if (newLocation != userLocation) {
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLng(newLocation)
                            )
                        }
                        userLocation = newLocation
                    }
                }
            }

            if(userLocation == null)
            {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    context.mainLooper
                )
            }

        }

        var isButtonVisible by remember { mutableStateOf(true) }

        Scaffold(modifier = Modifier.fillMaxSize(),
            floatingActionButton =
            {
                FloatingActionButton(modifier = Modifier.padding(bottom = 100.dp).size(40.dp), onClick = {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLng(userLocation!!)
                    )
                }) {
                    Icon(Icons.Default.Place, contentDescription = "Add Owner")
                }
            }) { innerPadding ->
            Box(
                modifier = modifier.padding(innerPadding)
            )
            {
                GoogleMapView(
                    modifier = Modifier.fillMaxSize(),
                    userLocation,
                    markerLocation,
                    destination,
                    cameraPositionState,
                    onMapClick = { latLng ->
                        if(destination == null)
                        {
                            marketClicked = null
                            if(markerLocation == null)
                            {
                                markerLocation = latLng
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLng(markerLocation!!)
                                )
                            }else
                            {
                                markerLocation = null
                            }

                            destination = null
                        }
                    },
                    onMarkerClick = { latLng ->
                        marketClicked = latLng
                        isButtonVisible = true
                    })

                marketClicked?.let { marker ->
                    if(isButtonVisible)
                    {
                        Button(onClick = {
                            if(destination != null)
                            {
                                destination = null
                                Toast.makeText(context, "Destination cancelled.", Toast.LENGTH_LONG).show()

                            }
                            else
                            {

                                createAndAddGeofence(marker) {
                                    destination = null
                                }
                                destination = marker
                            }
                            isButtonVisible = false
                        }, modifier = Modifier.align(Alignment.Center).padding(top = 100.dp)) {
                            if(destination != null)
                                Text("Cancel Destination")
                            else
                                Text("Set Destination")
                        }
                    }

                }
            }
        }
    }

    private fun createAndAddGeofence(location : LatLng, onEnterDestination : () -> Unit)
    {
        val geofenceId = "ExampleGeofence"
        val geofenceRadius = 200f // 200 meters
        val geofence = createGeofence(geofenceId, location, geofenceRadius)
        addGeofence(applicationContext, geofence, onEnterDestination)

        idsForLog += 0
        latitudesForLog += location.latitude
        longitudesForLog += location.longitude
        timestampsForLog += System.currentTimeMillis()
    }
}
