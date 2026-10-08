package com.earthquakealert.app.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.earthquakealert.app.domain.model.UserLocation
import com.earthquakealert.app.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationRepositoryImpl(
    private val context: Context,
    private val fusedClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
) : LocationRepository {

    private val _locationFlow = MutableStateFlow<UserLocation?>(null)

    override fun observeLocation(): Flow<UserLocation?> {
        return _locationFlow.asStateFlow()
    }

    override suspend fun getLastKnownLocation(): UserLocation? {
        if (!hasLocationPermission()) {
            return _locationFlow.value
        }

        // Try getting cached/last known location first (battery efficient)
        val fusedLoc = getFusedLastLocation()
        if (fusedLoc != null) {
            val userLoc = fusedLoc.toUserLocation()
            _locationFlow.value = userLoc
            return userLoc
        }

        // Fallback to system LocationManager last known
        val systemLoc = getSystemLastLocation()
        if (systemLoc != null) {
            val userLoc = systemLoc.toUserLocation()
            _locationFlow.value = userLoc
            return userLoc
        }

        return _locationFlow.value
    }

    override suspend fun refreshLocation(): UserLocation? {
        if (!hasLocationPermission()) {
            return _locationFlow.value
        }

        return try {
            val freshLoc = getCurrentFusedLocation() ?: getSystemLastLocation()
            if (freshLoc != null) {
                val userLoc = freshLoc.toUserLocation()
                _locationFlow.value = userLoc
                userLoc
            } else {
                _locationFlow.value
            }
        } catch (_: Exception) {
            _locationFlow.value
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    @SuppressLint("MissingPermission")
    private suspend fun getFusedLastLocation(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            fusedClient.lastLocation
                .addOnSuccessListener { loc ->
                    if (continuation.isActive) continuation.resume(loc)
                }
                .addOnFailureListener {
                    if (continuation.isActive) continuation.resume(null)
                }
        } catch (_: Exception) {
            if (continuation.isActive) continuation.resume(null)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentFusedLocation(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            // Balanced power accuracy: avoids high-frequency battery drain
            fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    if (continuation.isActive) continuation.resume(loc)
                }
                .addOnFailureListener {
                    if (continuation.isActive) continuation.resume(null)
                }
        } catch (_: Exception) {
            if (continuation.isActive) continuation.resume(null)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getSystemLastLocation(): Location? {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: return null

            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null

            for (provider in providers) {
                val loc = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                    bestLocation = loc
                }
            }
            bestLocation
        } catch (_: Exception) {
            null
        }
    }

    private fun Location.toUserLocation(): UserLocation {
        return UserLocation(
            latitude = latitude,
            longitude = longitude,
            accuracyMeters = accuracy,
            updatedAtMillis = time.takeIf { it > 0 } ?: System.currentTimeMillis()
        )
    }
}
