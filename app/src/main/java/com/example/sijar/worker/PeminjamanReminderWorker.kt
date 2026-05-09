package com.example.sijar.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sijar.R

class PeminjamanReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_PEMINJAMAN_ID = "peminjaman_id"
        const val KEY_NAMA_BARANG   = "nama_barang"
        const val KEY_END_TIME      = "end_time"
        const val KEY_MENIT_SEBELUM = "minute_before"

        const val CHANNEL_ID   = "peminjaman_reminder"
        const val CHANNEL_NAME = "Pengingat Peminjaman"
    }

    override suspend fun doWork(): Result {
        val peminjamanId  = inputData.getInt(KEY_PEMINJAMAN_ID, -1)
        val namaBarang    = inputData.getString(KEY_NAMA_BARANG) ?: "Barang"
        val endTime       = inputData.getString(KEY_END_TIME) ?: ""
        val menitSebelum  = inputData.getInt(KEY_MENIT_SEBELUM, 30)

        if (peminjamanId == -1) return Result.success()

        val title = "Pengingat Peminjaman"
        val message = "$menitSebelum menit lagi peminjaman $namaBarang berakhir pada $endTime"

        showNotification(
            notifId = peminjamanId * 10 + (if (menitSebelum == 60) 0 else 1),
            title = title,
            message = message
        )

        return Result.success()
    }

    private fun showNotification(notifId: Int, title: String, message: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat batas waktu peminjaman barang"
            }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.sijar_icon_background)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(notifId, notification)
    }
}