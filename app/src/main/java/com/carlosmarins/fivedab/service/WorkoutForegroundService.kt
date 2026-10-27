package com.carlosmarins.fivedab.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.carlosmarins.fivedab.MainActivity
import com.carlosmarins.fivedab.R

class WorkoutForegroundService : LifecycleService() {

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_STOP -> {
                releaseWakeLock()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            else -> {
                val statusText = intent?.getStringExtra(EXTRA_STATUS_TEXT) ?: "5D Ab Workout Active"
                acquireWakeLock()
                startOngoingWorkoutNotification(statusText)
            }
        }
        return START_STICKY
    }

    private fun startOngoingWorkoutNotification(statusText: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.ongoing_workout_channel),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Keeps 5D Ab rep pacing and heart rate tracking active during workouts"
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)

        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(getString(R.string.ongoing_workout_title))
            .setContentText(statusText)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)

        val ongoingStatus = Status.Builder()
            .addTemplate(statusText)
            .build()

        val ongoingActivity = OngoingActivity.Builder(
            applicationContext,
            NOTIFICATION_ID,
            notificationBuilder
        )
            .setTouchIntent(pendingIntent)
            .setStatus(ongoingStatus)
            .setStaticIcon(android.R.drawable.ic_media_play)
            .build()

        ongoingActivity.apply(applicationContext)
        val notification: Notification = notificationBuilder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun acquireWakeLock() {
        if (wakeLock?.isHeld == true) return
        val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = pm?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "FiveDAbWear::WorkoutWakeLock"
        )?.apply {
            acquire(60 * 60 * 1000L) // Max 60 min safety timeout
        }
    }

    private fun releaseWakeLock() {
        runCatching {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        }
        wakeLock = null
    }

    override fun onDestroy() {
        releaseWakeLock()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "five_d_ab_workout_channel"
        private const val NOTIFICATION_ID = 5001
        const val ACTION_START_OR_UPDATE = "com.carlosmarins.fivedab.action.START_OR_UPDATE"
        const val ACTION_STOP = "com.carlosmarins.fivedab.action.STOP"
        const val EXTRA_STATUS_TEXT = "extra_status_text"

        fun startOrUpdate(context: Context, statusText: String) {
            runCatching {
                val intent = Intent(context, WorkoutForegroundService::class.java).apply {
                    action = ACTION_START_OR_UPDATE
                    putExtra(EXTRA_STATUS_TEXT, statusText)
                }
                context.startForegroundService(intent)
            }
        }

        fun stop(context: Context) {
            runCatching {
                val intent = Intent(context, WorkoutForegroundService::class.java).apply {
                    action = ACTION_STOP
                }
                context.startService(intent)
            }
        }
    }
}
