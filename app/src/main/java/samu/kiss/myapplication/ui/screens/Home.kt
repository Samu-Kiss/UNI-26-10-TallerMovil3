package samu.kiss.myapplication.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import org.json.JSONObject
import samu.kiss.myapplication.ui.components.MyScaffold
import samu.kiss.myapplication.utils.loadLocations





@Composable
fun HomeScreen(controller: NavHostController) {
    val context = LocalContext.current
    val bogota = LatLng(4.627293, -74.063228)
    val bogotaMarkerState = rememberUpdatedMarkerState(position = bogota)
    val markers = loadLocations(context)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogota, 18f)
    }
    MyScaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                markers.forEach { markerData ->
                    Marker(
                        state = rememberUpdatedMarkerState(
                            position = LatLng(markerData.latitude, markerData.longitude)
                        ),
                        title = markerData.name
                    )
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