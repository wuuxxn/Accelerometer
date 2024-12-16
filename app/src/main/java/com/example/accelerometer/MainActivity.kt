package com.example.accelerometer

import android.content.Context.SENSOR_SERVICE
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AccelerometerTheme {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                MainScreen() // 主畫面
            }
        }
    }
}

@Composable
fun MainScreen() {
    var showSecond by remember { mutableStateOf(false) } // 控制是否顯示第二個畫面

    if (showSecond) {
        SecondScreen(onBack = { showSecond = false }) // 返回主畫面
    } else {
        FirstScreen(onNavigateToSecond = { showSecond = true }) // 進入第二個畫面
    }
}

@Composable
fun FirstScreen(onNavigateToSecond: () -> Unit) {
    var msg by remember { mutableStateOf("加速感應器實例") }
    var msg2 by remember { mutableStateOf("") }
    var xTilt by remember { mutableStateOf(0f) }
    var yTilt by remember { mutableStateOf(0f) }
    var zTilt by remember { mutableStateOf(0f) }

    val context = LocalContext.current
    val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Sensor event listener to update tilt data
    val sensorEventListener = rememberUpdatedState(
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    xTilt = event.values[0]
                    yTilt = event.values[1]
                    zTilt = event.values[2]
                    msg = "加速感應器實例\n" + String.format("x軸: %1.2f \n" + "y軸: %1.2f \n" + "z軸: %1.2f", xTilt, yTilt, zTilt)
                    if (Math.abs(xTilt) < 1 && Math.abs(yTilt) < 1 && zTilt < -9) {
                        msg2 = "朝下平放"
                    } else if (Math.abs(xTilt) + Math.abs(yTilt) + Math.abs(zTilt) > 32) {
                        msg2 = "手機搖晃"
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    )

    LaunchedEffect(Unit) {
        // Register the listener for accelerometer
        sensorManager.registerListener(
            sensorEventListener.value,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    // Unregister the sensor listener when composable leaves the composition
    DisposableEffect(Unit) {
        onDispose {
            sensorManager.unregisterListener(sensorEventListener.value)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Green),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(msg)
        Text(msg2)
        Button(onClick = { onNavigateToSecond() }) {
            Text(text = "跳轉畫面2")
        }
    }
}

@Composable
fun SecondScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Yellow),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { onBack() }) {
            Text(text = "返回畫面1")
        }
    }
}

