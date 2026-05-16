package samu.kiss.myapplication.models

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import samu.kiss.myapplication.utils.showNotification

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(
            "FirebaseApp", "Message Received!!!"
        )
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        if (title != null && body != null) {
            Log.i(
                "FirebaseApp", title
            )
            Log.i(
                "FirebaseApp", body
            )
            showNotification(title, body, this)
        }
    }
}