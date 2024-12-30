package com.example.googlemapsdraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.googlemapsdraft.ui.theme.GoogleMapsDraftTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleMapsDraftTheme {
                GoogleMapScreen()
            }
        }
    }
}

@Composable
fun GoogleMapScreen() {
    // Define the location for Singapore
    val singapore = LatLng(1.35, 103.87)

    // Correct way to set the initial camera position
    val cameraPositionState = rememberCameraPositionState {
        this.position = CameraPosition.fromLatLngZoom(singapore, 12f)
    }

    // Display Google Map with the given CameraPositionState
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoogleMapsDraftTheme {
        GoogleMapScreen()
    }
}
