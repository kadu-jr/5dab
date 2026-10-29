package com.carlosmarins.fivedab.phone.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.carlosmarins.fivedab.phone.data.CompanionCuePayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Plays workout voice cues and cadence tones on the Android Phone's connected
 * Bluetooth earbuds using [AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK].
 *
 * This smoothly lowers ("ducks") the volume of any playing phone video (YouTube,
 * TikTok, Instagram, browser, etc.) for ~1 second while speaking the rep count
 * or exercise name, and immediately restores 100% video volume without pausing!
 */
class PhoneAudioDuckingEngine private constructor(context: Context) {

    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val speechAudioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()

    private val duckingFocusRequest: AudioFocusRequest = AudioFocusRequest.Builder(
        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
    )
        .setAudioAttributes(speechAudioAttributes)
        .setWillPauseWhenDucked(false)
        .setOnAudioFocusChangeListener { /* Transient ducking only */ }
        .build()

    private var toneGenerator: ToneGenerator? = runCatching {
        ToneGenerator(AudioManager.STREAM_MUSIC, 85)
    }.getOrNull()

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setAudioAttributes(speechAudioAttributes)
                val result = tts?.setLanguage(Locale.US)
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                tts?.setSpeechRate(1.12f)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) = Unit

                    override fun onDone(utteranceId: String?) {
                        abandonDuckingFocus()
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        abandonDuckingFocus()
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        abandonDuckingFocus()
                    }
                })
            }
        }
    }

    fun playCueWithDucking(payload: CompanionCuePayload) {
        val speech = payload.speechText
        val tone = payload.toneType
        val durationMs = payload.toneDurationMs.coerceIn(40, 400)

        if (!speech.isNullOrBlank() && isTtsReady) {
            requestDuckingFocus()
            if (tone != null) {
                runCatching { toneGenerator?.startTone(tone, durationMs) }
            }
            val params = Bundle().apply {
                putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
            }
            val utteranceId = "phone_cue_${System.currentTimeMillis()}"
            val speakResult = runCatching {
                tts?.speak(speech, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            }.getOrDefault(TextToSpeech.ERROR)

            if (speakResult == TextToSpeech.ERROR) {
                abandonDuckingFocus()
            }
        } else if (tone != null) {
            scope.launch {
                requestDuckingFocus()
                runCatching { toneGenerator?.startTone(tone, durationMs) }
                delay(durationMs.toLong() + 90L)
                abandonDuckingFocus()
            }
        }
    }

    private fun requestDuckingFocus() {
        runCatching {
            audioManager.requestAudioFocus(duckingFocusRequest)
        }
    }

    private fun abandonDuckingFocus() {
        runCatching {
            audioManager.abandonAudioFocusRequest(duckingFocusRequest)
        }
    }

    companion object {
        @Volatile
        private var instance: PhoneAudioDuckingEngine? = null

        fun getInstance(context: Context): PhoneAudioDuckingEngine {
            return instance ?: synchronized(this) {
                instance ?: PhoneAudioDuckingEngine(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
