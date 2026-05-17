package samu.kiss.taller3.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import samu.kiss.taller3.R
import samu.kiss.taller3.models.MyApp

fun showNotification(title:String, message:String, context: Context, target : Class<*>? = null){
    val notManager = getSystemService(context, NotificationManager::class.java)
    val notification : Notification
    if(target!=null) {
        val intent = Intent(context, target)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        notification = NotificationCompat.Builder(context, MyApp.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.outline_waving_hand_24)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
    }else{
        notification = NotificationCompat.Builder(context,
            MyApp.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.outline_waving_hand_24)
            .setAutoCancel(true)
            .build()
    }
    notManager?.notify(1, notification)
}