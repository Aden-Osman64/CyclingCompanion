package com.example.googlemapsdraft

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.googlemapsdraft.ui.theme.GoogleMapsDraftTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.data.kml.KmlLayer
import com.opencsv.CSVReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleMapsDraftTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf("home") }

    Scaffold(
        containerColor = Color.LightGray,
        bottomBar = {
            BottomNavigationBar(selectedTab, onTabSelected = { selectedTab = it }, navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("map") { GoogleMapScreen() }
            composable("search") { RepairScreen() }
            composable("favorites") { FavoritesScreen() }
            composable("profile") { ProfileScreen() }
            composable("routes") { RoutesScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    navController: NavHostController
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedTab == "home",
            onClick = {
                onTabSelected("home")
                navController.navigate("home") { popUpTo("home") { inclusive = true } }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Build, contentDescription = "Repair") },
            label = { Text("Repair") },
            selected = selectedTab == "search",
            onClick = {
                onTabSelected("search")
                navController.navigate("search") { popUpTo("search") { inclusive = true } }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Place, contentDescription = "Routes") },
            label = { Text("Routes") },
            selected = selectedTab == "routes",
            onClick = {
                onTabSelected("routes")
                navController.navigate("routes") { popUpTo("routes") { inclusive = true } }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedTab == "profile",
            onClick = {
                onTabSelected("profile")
                navController.navigate("profile") { popUpTo("profile") { inclusive = true } }
            }
        )
    }
}

@Composable
fun HomeScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Current Location: Bristol",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 124.dp)
        )

        val bikeGifPainter = rememberImagePainter(R.drawable.bike)
        Image(
            painter = bikeGifPainter,
            contentDescription = "Bike GIF",
            modifier = Modifier
                .size(200.dp)
                .border(4.dp, Color.Gray)
        )

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Fun Fact: The world's fastest bicycle, called the Aerovelo Eta, can reach speeds of over 89 mph (144 km/h), powered solely by a human pedaling!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 126.dp)
        )
    }
}

@Composable
fun GoogleMapScreen() {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(51.45, -2.58), 12f)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            val position = LatLng(51.45, -2.58)
            Marker(
                state = MarkerState(position = position),
                title = "Sample Location"
            )
        }
    }
}

@Composable
fun RepairScreen() {
    val context = LocalContext.current
    val markers = remember { mutableStateListOf<Pair<LatLng, String>>() }

    LaunchedEffect(Unit) {
        val inputStream = context.resources.openRawResource(R.raw.cycle_shops_and_repairs)
        val reader = CSVReader(InputStreamReader(inputStream))
        reader.use { csvReader ->
            val lines = csvReader.readAll()
            for (line in lines.drop(1)) {
                val lat = line.getOrNull(0)?.toDoubleOrNull()
                val lng = line.getOrNull(1)?.toDoubleOrNull()
                val name = line.getOrNull(3) ?: "Unknown Location"

                if (lat != null && lng != null) {
                    markers.add(LatLng(lat, lng) to name)
                    Log.d("CSVData", "Loaded: $name at $lat, $lng")
                }
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(51.45, -2.58), 12f)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .border(4.dp, Color.Gray),
            cameraPositionState = cameraPositionState
        ) {
            markers.forEach { (position, name) ->
                Marker(
                    state = MarkerState(position = position),
                    title = name
                )
            }
        }
    }
}

@Composable
fun RoutesScreen() {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(51.45, -2.58), 12f)
    }
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .border(4.dp, Color.Gray),
            cameraPositionState = cameraPositionState
        ) {
            MapEffect(cameraPositionState.projection) { map ->
                try {
                    val kmlLayer = KmlLayer(map, R.raw.cycling_commuter_routes_n_somerset, context)
                    kmlLayer.addLayerToMap()
                } catch (e: Exception) {
                    Log.e("RoutesScreen", "Error loading KML Layer", e)
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Text(
            "Favorites Screen",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(4.dp, Color.Gray, CircleShape)
                .background(Color.Gray, shape = CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "John Doe",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = { /* Handle Settings or Logout */ }) {
            Text(text = "Settings")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoogleMapsDraftTheme {
        MainScreen()
    }
}
