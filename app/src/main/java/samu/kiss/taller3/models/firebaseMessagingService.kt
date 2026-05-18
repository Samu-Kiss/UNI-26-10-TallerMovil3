package samu.kiss.taller3.models

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import samu.kiss.taller3.utils.showNotification


//https://stackoverflow.com/questions/54997485/android-notification-during-app-is-in-background-intent-data-is-empty
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.i("NotifExp", "Message Received!!!")

        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val targetUid = remoteMessage.data["uid"]

        targetUid?.let { Log.i("NotifExp", "FMC: $it") }

        if (title != null && body != null) {
            showNotification(
                title = title,
                message = body,
                context = this,
                targetUid = targetUid
            )
        }
    }
}