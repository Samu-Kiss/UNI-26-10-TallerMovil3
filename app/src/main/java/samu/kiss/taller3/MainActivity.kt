package samu.kiss.taller3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.compose.AppTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import samu.kiss.taller3.models.UserLocationViewModel
import samu.kiss.taller3.navigation.Navigation

lateinit var auth : FirebaseAuth


class MainActivity : ComponentActivity() {

    val locationViewModel: UserLocationViewModel by viewModels()
    private val _notificationTargetUid = MutableStateFlow<String?>(null)
    var targetUid: String? = null
    val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ActivityResultCallback {}
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        targetUid = intent.getStringExtra("targetUid")
        requestPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContent {
            Log.i("NotifExp", "onCreate targetUid: ${targetUid ?: "NULL"}")
            AppTheme() {
                Navigation(notificationTargetUid = targetUid)
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        _notificationTargetUid.value = intent.getStringExtra("targetUid")
    }
}