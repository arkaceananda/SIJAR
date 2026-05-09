package com.example.sijar.api.utils

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sijar.api.model.data.WaktuPeminjaman
import com.example.sijar.worker.PeminjamanReminderWorker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    /**
     * Schedule 2 notifikasi untuk satu peminjaman:
     * - 60 menit sebelum jam terakhir berakhir
     * - 30 menit sebelum jam terakhir berakhir
     *
     * @param context        Android context
     * @param peminjamanId   ID peminjaman (untuk tag WorkManager & notif ID)
     * @param namaBarang     Nama barang yang dipinjam (ditampilkan di notifikasi)
     * @param waktuDipilih   List waktu yang dipilih user di form
     */
    fun schedule(
        context: Context,
        peminjamanId: Int,
        namaBarang: String,
        waktuDipilih: List<WaktuPeminjaman>
    ) {
        if (waktuDipilih.isEmpty()) return

        // Ambil jam terakhir — end_time jam dengan jam_ke terbesar
        val jamTerakhir = waktuDipilih.maxByOrNull { it.jamKe } ?: return
        val endTime = jamTerakhir.endTime // format "HH:mm" atau "HH:mm:ss"

        val formatter = DateTimeFormatter.ofPattern(
            if (endTime.length > 5) "HH:mm:ss" else "HH:mm"
        )

        // Asumsikan peminjaman hari ini — karena store() langsung set tanggal = now()
        val endDateTime = LocalDateTime.of(
            LocalDate.now(),
            LocalTime.parse(endTime, formatter)
        )

        val now = LocalDateTime.now()

        // Schedule notifikasi 60 menit sebelum
        scheduleReminder(
            context = context,
            peminjamanId = peminjamanId,
            namaBarang = namaBarang,
            endTime = endTime,
            menitSebelum = 60,
            triggerAt = endDateTime.minusMinutes(60),
            now = now
        )

        // Schedule notifikasi 30 menit sebelum
        scheduleReminder(
            context = context,
            peminjamanId = peminjamanId,
            namaBarang = namaBarang,
            endTime = endTime,
            menitSebelum = 30,
            triggerAt = endDateTime.minusMinutes(30),
            now = now
        )
    }

    private fun scheduleReminder(
        context: Context,
        peminjamanId: Int,
        namaBarang: String,
        endTime: String,
        menitSebelum: Int,
        triggerAt: LocalDateTime,
        now: LocalDateTime
    ) {
        // Hitung delay dalam milidetik dari sekarang
        val delayMs = java.time.Duration.between(now, triggerAt).toMillis()

        // Kalau waktu trigger sudah lewat, tidak perlu schedule
        if (delayMs <= 0) return

        val inputData = Data.Builder()
            .putInt(PeminjamanReminderWorker.KEY_PEMINJAMAN_ID, peminjamanId)
            .putString(PeminjamanReminderWorker.KEY_NAMA_BARANG, namaBarang)
            .putString(PeminjamanReminderWorker.KEY_END_TIME, endTime)
            .putInt(PeminjamanReminderWorker.KEY_MENIT_SEBELUM, menitSebelum)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<PeminjamanReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("peminjaman_${peminjamanId}")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    /**
     * Cancel semua notifikasi untuk peminjaman tertentu.
     * Panggil ini saat peminjaman diselesaikan/dibatalkan lebih awal.
     */
    fun cancel(context: Context, peminjamanId: Int) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("peminjaman_${peminjamanId}")
    }
}