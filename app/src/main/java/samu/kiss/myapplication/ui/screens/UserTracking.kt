package samu.kiss.myapplication.ui.screens

import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapColorScheme
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import samu.kiss.myapplication.R
import samu.kiss.myapplication.models.MyUsersViewModel
import samu.kiss.myapplication.models.UserLocationViewModel
import samu.kiss.myapplication.ui.components.MyScaffold
import samu.kiss.myapplication.ui.components.UserProfileBubble
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun UserTrackingScreen(
    navController: NavHostController,
    targetUid: String,
    locationViewModel: UserLocationViewModel,
    usersViewModel: MyUsersViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by locationViewModel.uiState.collectAsState()
    val users by usersViewModel.users.collectAsState()
    val permission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    val me = users.find { it.uid == FirebaseAuth.getInstance().currentUser?.uid }
    val target = users.find { it.uid == targetUid }

    var warnedUnavailable by remember { mutableStateOf(false) }
    var warnedMissing by remember { mutableStateOf(false) }

    if (target == null) {
        LaunchedEffect(users.size) {
            if (users.isNotEmpty() && !warnedMissing) {
                warnedMissing = true
                Toast.makeText(context, "Usuario no disponible", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }
        return
    }

    LaunchedEffect(target.available) {
        if (target.available != true && !warnedUnavailable) {
            warnedUnavailable = true
            Toast.makeText(context, "Usuario no disponible", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            locationViewModel.setAvailable(false)
        }
    }

    SideEffect {
        if (!permission.status.isGranted) {
            permission.launchPermissionRequest()
        }
    }
    if (permission.status.isGranted && !locationViewModel.permissionGranted) {
        locationViewModel.updateVel()
    }

    val followerPosition = if (state.latitude != 0.0 && state.longitude != 0.0) {
        LatLng(state.latitude, state.longitude)
    } else {
        null
    }
    val targetPosition = if (target.latitude != 0.0 && target.longitude != 0.0) {
        LatLng(target.latitude, target.longitude)
    } else {
        null
    }

    val distanceText = if (followerPosition != null && targetPosition != null) {
        val results = FloatArray(1)
        Location.distanceBetween(
            followerPosition.latitude,
            followerPosition.longitude,
            targetPosition.latitude,
            targetPosition.longitude,
            results
        )
        formatDistance(results[0])
    } else {
        "Calculando..."
    }

    val mapId = stringResource(R.string.map_id)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            targetPosition ?: LatLng(4.627293, -74.063228),
            13f
        )
    }

    var hasCenteredCamera by remember { mutableStateOf(false) }
    LaunchedEffect(followerPosition, targetPosition) {
        if (!hasCenteredCamera && followerPosition != null && targetPosition != null) {
            val bounds = LatLngBounds.builder()
                .include(followerPosition)
                .include(targetPosition)
                .build()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 250))
            hasCenteredCamera = true
        }
    }

    var followerBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var targetBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(me?.photoUrl) {
        if (me != null && me.photoUrl.isNotEmpty()) {
            val request = ImageRequest.Builder(context)
                .data(me.photoUrl)
                .size(150, 150)
                .allowHardware(false)
                .build()
            val result = context.imageLoader.execute(request)
            result.drawable?.toBitmap()?.asImageBitmap()?.let {
                followerBitmap = it
            }
        }
    }

    LaunchedEffect(target.photoUrl) {
        if (target.photoUrl.isNotEmpty()) {
            val request = ImageRequest.Builder(context)
                .data(target.photoUrl)
                .size(150, 150)
                .allowHardware(false)
                .build()
            val result = context.imageLoader.execute(request)
            result.drawable?.toBitmap()?.asImageBitmap()?.let {
                targetBitmap = it
            }
        }
    }

    MyScaffold(navController = navController, locationViewModel = locationViewModel) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = true,
                    mapToolbarEnabled = false,
                    compassEnabled = false
                ),
                googleMapOptionsFactory = {
                    GoogleMapOptions().apply {
                        mapId(mapId)
                        mapType(GoogleMap.MAP_TYPE_NORMAL)
                    }
                }
            ) {
                MapEffect(Unit) { googleMap ->
                    googleMap.mapColorScheme = MapColorScheme.FOLLOW_SYSTEM
                }

                if (me != null && followerPosition != null) {
                    val isFollowerImageReady = me.photoUrl.isEmpty() || followerBitmap != null
                    if (isFollowerImageReady) {
                        MarkerComposable(
                            keys = arrayOf(me.uid, me.photoUrl),
                            state = rememberUpdatedMarkerState(position = followerPosition),
                            title = "Tu ubicacion"
                        ) {
                            UserProfileBubble(user = me, preloadedBitmap = followerBitmap)
                        }
                    }
                }

                if (targetPosition != null) {
                    val isTargetImageReady = target.photoUrl.isEmpty() || targetBitmap != null
                    if (isTargetImageReady) {
                        MarkerComposable(
                            keys = arrayOf(target.uid, target.photoUrl),
                            state = rememberUpdatedMarkerState(position = targetPosition),
                            title = "${target.name} - $distanceText"
                        ) {
                            UserProfileBubble(user = target, preloadedBitmap = targetBitmap)
                        }
                    }
                }
            }
        }
    }
}

private fun formatDistance(meters: Float): String {
    return if (meters >= 1000f) {
        String.format(Locale.US, "%.1f km", meters / 1000f)
    } else {
        "${meters.toInt()} m"
    }
}
