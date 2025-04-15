package com.example.k2025_03_25_basic_radio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.k2025_03_25_basic_radio.ui.theme.K2025_03_25_basic_radioTheme

data class RadioStation(
    val name: String,
    val url: String,
    val imageRes: Int
)

class MainActivity : ComponentActivity() {

    private val radioStations = listOf(
        RadioStation("UCONN radio", "http://stream.whus.org:8000/whusfm", R.drawable.whus),
        RadioStation("Classic Vinyl HD", "https://icecast.walmradio.com:8443/classic", R.drawable.classic_vinyl),
        RadioStation("Dance Wave", "http://stream.dancewave.online:8080/", R.drawable.dance_wave),
        RadioStation("2000s", "https://0n-2000s.radionetz.de/0n-2000s.mp3", R.drawable.y2k),
        RadioStation("Europa Plus", "http://ep256.hostingradio.ru:8052/europaplus256.mp3", R.drawable.europa_plus),
        RadioStation("Deep House", "http://198.15.94.34:8006/stream", R.drawable.deep_house),
        RadioStation("Sleeping Pill", "http://radio.stereoscenic.com/asp-h", R.drawable.sleeping_pill),
        RadioStation("Top 100 Club", "https://breakz-2012-high.rautemusik.fm/?ref=radiobrowser-top100-clubcharts", R.drawable.top_hundred),
        RadioStation("CNN", "https://tunein.cdnstream1.com/2868_96.mp3", R.drawable.cnn),
        RadioStation("Radio Bollywood", "https://stream.zeno.fm/rm4i9pdex3cuv/", R.drawable.radio_bollywood)
    )

    // Get the ViewModel
    private val viewModel: RadioViewModel by viewModels()

    private var bound = false
    private lateinit var serviceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind serviceConnection to RadioService
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as RadioService.RadioBinder
                viewModel.radioService = binder.getService()
                bound = true
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                bound = false
                viewModel.radioService = null
            }
        }

        // Bind to the service
        Intent(this, RadioService::class.java).also { intent ->
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }

        setContent {
            K2025_03_25_basic_radioTheme {
                RadioScreen (
                    stations = radioStations,
                    onStationSelected = { station ->
                        // tell ViewModel to play selected station
                        viewModel.playRadio(station.url)
                    },
                    onStop = {
                        viewModel.stopRadio()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bound) {
            unbindService(serviceConnection)
            bound = false
            viewModel.radioService = null
        }
    }
}

@Composable
fun RadioScreen(
    stations: List<RadioStation>,
    onStationSelected: (RadioStation) -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Radio Station",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(stations) { station ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStationSelected(station) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = station.imageRes),
                        contentDescription = station.name,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 24.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onStop,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Stop Radio")
        }
    }
}
