package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

object FocusSoundGenerator {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    var isPlaying: Boolean = false
        private set

    fun play(baseFreq: Float = 220f, soundType: String = "DARK_SYNTH", onStateChange: ((Boolean) -> Unit)? = null) {
        stop()

        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            isPlaying = true
            onStateChange?.invoke(true)

            playbackJob = scope.launch {
                val buffer = ShortArray(bufferSize / 2)
                var phase1 = 0.0
                var phase2 = 0.0
                var phaseLfo = 0.0

                val freq2 = when (soundType) {
                    "DEEP_BINAURAL_40HZ" -> baseFreq + 40f
                    "DARK_SYNTH" -> baseFreq * 1.5f
                    "RAIN_AMBIENT" -> baseFreq * 2.0f
                    else -> baseFreq * 1.25f
                }

                while (isActive && isPlaying) {
                    for (i in buffer.indices) {
                        val lfo = 0.6 + 0.4 * sin(phaseLfo)
                        val sample1 = sin(phase1)
                        val sample2 = sin(phase2) * 0.5
                        val mixed = (sample1 + sample2) * lfo * 0.35

                        buffer[i] = (mixed * Short.MAX_VALUE).toInt().coerceIn(-32768, 32767).toShort()

                        phase1 += 2.0 * Math.PI * baseFreq / sampleRate
                        phase2 += 2.0 * Math.PI * freq2 / sampleRate
                        phaseLfo += 2.0 * Math.PI * 0.5 / sampleRate // 0.5Hz slow rhythmic pulse

                        if (phase1 > 2.0 * Math.PI) phase1 -= 2.0 * Math.PI
                        if (phase2 > 2.0 * Math.PI) phase2 -= 2.0 * Math.PI
                        if (phaseLfo > 2.0 * Math.PI) phaseLfo -= 2.0 * Math.PI
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stop()
            onStateChange?.invoke(false)
        }
    }

    fun stop(onStateChange: ((Boolean) -> Unit)? = null) {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
        onStateChange?.invoke(false)
    }

    fun toggle(baseFreq: Float = 220f, soundType: String = "DARK_SYNTH", onStateChange: ((Boolean) -> Unit)? = null): Boolean {
        if (isPlaying) {
            stop(onStateChange)
            return false
        } else {
            play(baseFreq, soundType, onStateChange)
            return true
        }
    }
}
