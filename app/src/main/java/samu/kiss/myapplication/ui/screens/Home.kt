package samu.kiss.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapColorScheme
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import samu.kiss.myapplication.R
import samu.kiss.myapplication.models.Location
import samu.kiss.myapplication.models.MyUsersViewModel
import samu.kiss.myapplication.models.UserLocationViewModel
import samu.kiss.myapplication.ui.components.MyScaffold
import samu.kiss.myapplication.ui.components.UserProfileBubble
import samu.kiss.myapplication.utils.loadLocations
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest

@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun HomeScreen(
    controller: NavHostController,
    locationViewModel: UserLocationViewModel,
    usersViewModel: MyUsersViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by locationViewModel.uiState.collectAsState()

    val users by usersViewModel.users.collectAsState()
    val myUid = FirebaseAuth.getInstance().currentUser?.uid
    val me = users.find { it.uid == myUid }

    val bogota = LatLng(4.627293, -74.063228)
    val markers = loadLocations(context)
    val mapId = stringResource(R.string.map_id)
    val permission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogota, 12f)
    }

    rememberUpdatedMarkerState(position = bogota)
    var hasCenteredCamera by remember { mutableStateOf(false) }
    var userPosition: Location? = null
    LaunchedEffect(state.latitude, state.longitude) {
        if (!hasCenteredCamera && state.latitude != 0.0 && state.longitude != 0.0) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(state.latitude, state.longitude), 15f
            )
            hasCenteredCamera = true
        }
    }
    LaunchedEffect(Unit) {
        locationViewModel.observeAvailableFromFirebase()
    }

    var userProfileBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(me?.photoUrl) {
        if (me != null && me.photoUrl.isNotEmpty()) {
            val request = ImageRequest.Builder(context)
                .data(me.photoUrl)
                .size(150, 150)
                .allowHardware(false)
                .build()
            val result = context.imageLoader.execute(request)
            result.drawable?.toBitmap()?.asImageBitmap()?.let {
                userProfileBitmap = it
            }
        }
    }


    SideEffect {
        if (!permission.status.isGranted) {
            permission.launchPermissionRequest()
        }
    }
    if (permission.status.isGranted) {
        if (!locationViewModel.permissionGranted) locationViewModel.updateVel()
        userPosition = Location(state.latitude, state.longitude, "User")
    }
    MyScaffold(
        navController = controller, locationViewModel = locationViewModel
    ) {
        me?.let { me ->
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
                    }) {
                    MapEffect(Unit) { googleMap ->
                        googleMap.mapColorScheme = MapColorScheme.FOLLOW_SYSTEM
                    }
                    markers.forEach { markerData ->
                        Marker(
                            state = rememberUpdatedMarkerState(
                                position = LatLng(markerData.latitude, markerData.longitude)
                            ), title = markerData.name
                        )
                    }
                    //https://stackoverflow.com/questions/76403898/how-to-use-an-custom-composable-function-as-a-marker-icon-for-maps-in-jetpack-co
                    userPosition?.let { pos ->
                        val isImageReady = me.photoUrl.isEmpty() || userProfileBitmap != null
                        if (isImageReady) {
                            MarkerComposable(
                                keys = arrayOf(me.uid, me.photoUrl),
                                state = rememberUpdatedMarkerState(
                                    position = LatLng(pos.latitude, pos.longitude)
                                ), title = "${me.name} (Tú)"
                            ) {
                                UserProfileBubble(
                                    user = me,
                                    preloadedBitmap = userProfileBitmap
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
AppTheme {
HomeScreen(rememberNavController())
}
}
 */