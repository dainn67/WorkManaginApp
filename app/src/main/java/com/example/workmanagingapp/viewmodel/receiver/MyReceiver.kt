package com.example.workmanagingapp.viewmodel.receiver

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.workmanagingapp.MainActivity
import com.example.workmanagingapp.R
import com.example.workmanagingapp.model.Constants.Companion.CHANNEL_ID
import com.example.workmanagingapp.model.Constants.Companion.TAG
import com.example.workmanagingapp.model.Work
import com.example.workmanagingapp.viewmodel.WorkJsonAdapter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

class MyReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "BR onReceive")
        val bundle = intent?.extras
        val json = bundle?.getString("json", "nothing")

        //decode json list
        val gson = GsonBuilder()
            .registerTypeAdapter(Work::class.java, WorkJsonAdapter())
            .create()
        val listType = object : TypeToken<List<Work>>() {}.type

        var list = gson.fromJson<List<Work>>(json, listType)
        list = list.filter{
            it.getTime().dayOfMonth == LocalDateTime.now().dayOfMonth
                    && it.getTime().month == LocalDateTime.now().month
                    && it.getTime().year == LocalDateTime.now().year
                    && !it.getStatus()
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            intent.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        //notification view
        val remoteView = RemoteViews(context?.packageName, R.layout.notification_layout)
        when (list.size){
            0 -> {
                remoteView.setTextViewText(R.id.tvNotiTitle, "There is no work to do today")
                remoteView.setTextViewText(R.id.tvNotiContent, "Tap to view all works")
            }
            1 -> {
                remoteView.setTextViewText(R.id.tvNotiTitle, "There is 1 work to do today")
                remoteView.setTextViewText(R.id.tvNotiContent, list[0].getTitle())
            }
            else -> {
                remoteView.setTextViewText(R.id.tvNotiTitle, "There are ${list.size} works to do today")
                var notificationContent = list[0].getTitle() + ", "
                for(i in 1 until list.size-2){
                    notificationContent += list[i].getTitle() + ", "
                }
                notificationContent += list.last().getTitle()

                remoteView.setTextViewText(R.id.tvNotiContent, notificationContent)
            }
        }

        val notification = NotificationCompat.Builder(
            context!!,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("Number of works today: ${list.size}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCustomContentView(remoteView).setContentIntent(pendingIntent)
            .build()


        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "Permission granted")
                notify(1, notification)
            }else{
                Log.i(TAG, "Permission not granted")
                val intent = Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
        }

        Log.i(TAG, "HERE")
    }
}