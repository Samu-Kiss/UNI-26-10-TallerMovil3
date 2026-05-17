package samu.kiss.taller3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.compose.AppTheme
import com.google.firebase.auth.FirebaseAuth
import samu.kiss.taller3.navigation.Navigation

lateinit var auth : FirebaseAuth


class MainActivity : ComponentActivity() {
    val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ActivityResultCallback {}
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContent {
            AppTheme() {
                Navigation()
            }
        }
    }
}