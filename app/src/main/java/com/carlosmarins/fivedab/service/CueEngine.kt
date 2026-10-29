package com.carlosmarins.fivedab.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import com.carlosmarins.fivedab.data.CompanionCuePayload
import java.util.Locale

class CueEngine(context: Context) {

    val companionBridge = WatchCompanionBridge(context)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var toneGenerator: ToneGenerator? = runCatching {
        ToneGenerator(AudioManager.STREAM_ALARM, 90)
    }.getOrNull()

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val watchSpeakerAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                tts?.setAudioAttributes(watchSpeakerAttributes)
                val result = tts?.setLanguage(Locale.US)
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                tts?.setSpeechRate(1.12f)
            }
        }
    }

    fun onRotaryTick(hapticEnabled: Boolean) {
        if (!hapticEnabled) return
        vibrateEffect(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
    }

    fun onRepCount(
        currentRep: Int,
        targetReps: Int,
        hapticEnabled: Boolean,
        audioEnabled: Boolean,
        voiceEnabled: Boolean,
        routeToPhoneCompanion: Boolean = true
    ) {
        if (hapticEnabled) {
            // Crisp two-stage cadence pulse on the wrist
            val timings = longArrayOf(0L, 45L, 35L, 25L)
            val amplitudes = intArrayOf(0, 210, 0, 140)
            vibrateWaveform(timings, amplitudes)
        }

        val remaining = targetReps - currentRep
        val isHalfway = targetReps >= 12 && currentRep == targetReps / 2
        val isMilestone = currentRep == 1 ||
            currentRep % 5 == 0 ||
            remaining in 1..3

        if (voiceEnabled && (isHalfway || isMilestone)) {
            val speech = when {
                remaining in 1..3 -> remaining.toString()
                isHalfway -> "Halfway, $currentRep"
                else -> currentRep.toString()
            }
            deliverAudioCue(
                speechText = speech,
                toneType = if (audioEnabled) ToneGenerator.TONE_PROP_BEEP else null,
                toneDurationMs = 70,
                routeToPhoneCompanion = routeToPhoneCompanion
            )
        } else if (audioEnabled) {
            deliverAudioCue(
                speechText = null,
                toneType = ToneGenerator.TONE_PROP_BEEP,
                toneDurationMs = 70,
                routeToPhoneCompanion = routeToPhoneCompanion
            )
        }
    }

    fun onHoldSecondTick(
        remainingSeconds: Int,
        totalSeconds: Int,
        hapticEnabled: Boolean,
        audioEnabled: Boolean,
        voiceEnabled: Boolean,
        routeToPhoneCompanion: Boolean = true
    ) {
        when {
            remainingSeconds in 1..3 -> {
                if (hapticEnabled) {
                    vibrateWaveform(longArrayOf(0L, 65L), intArrayOf(0, 230))
                }
                deliverAudioCue(
                    speechText = if (voiceEnabled) remainingSeconds.toString() else null,
                    toneType = if (audioEnabled) ToneGenerator.TONE_CDMA_PIP else null,
                    toneDurationMs = 90,
                    routeToPhoneCompanion = routeToPhoneCompanion
                )
            }
            totalSeconds >= 20 && remainingSeconds == totalSeconds / 2 -> {
                if (hapticEnabled) {
                    vibrateWaveform(longArrayOf(0L, 50L, 45L, 50L), intArrayOf(0, 180, 0, 180))
                }
                deliverAudioCue(
                    speechText = if (voiceEnabled) "Halfway, $remainingSeconds seconds" else null,
                    toneType = if (audioEnabled) ToneGenerator.TONE_PROP_ACK else null,
                    toneDurationMs = 110,
                    routeToPhoneCompanion = routeToPhoneCompanion
                )
            }
            remainingSeconds > 3 && remainingSeconds % 10 == 0 -> {
                if (hapticEnabled) {
                    vibrateWaveform(longArrayOf(0L, 30L), intArrayOf(0, 140))
                }
                deliverAudioCue(
                    speechText = if (voiceEnabled) "$remainingSeconds seconds" else null,
                    toneType = if (audioEnabled) ToneGenerator.TONE_PROP_BEEP else null,
                    toneDurationMs = 55,
                    routeToPhoneCompanion = routeToPhoneCompanion
                )
            }
        }
    }

    fun onPrepCountdownTick(
        secondsLeft: Int,
        hapticEnabled: Boolean,
        audioEnabled: Boolean,
        routeToPhoneCompanion: Boolean = true
    ) {
        if (secondsLeft in 1..3) {
            if (hapticEnabled) {
                vibrateWaveform(longArrayOf(0L, 55L), intArrayOf(0, 200))
            }
            if (audioEnabled) {
                deliverAudioCue(
                    speechText = null,
                    toneType = ToneGenerator.TONE_CDMA_PIP,
                    toneDurationMs = 80,
                    routeToPhoneCompanion = routeToPhoneCompanion
                )
            }
        }
    }

    fun onAnnounceStepStart(
        exerciseName: String,
        targetValue: Int,
        isTimed: Boolean,
        hapticEnabled: Boolean,
        audioEnabled: Boolean,
        voiceEnabled: Boolean,
        routeToPhoneCompanion: Boolean = true
    ) {
        if (hapticEnabled) {
            vibrateWaveform(longArrayOf(0L, 90L, 50L, 120L), intArrayOf(0, 200, 0, 255))
        }
        val unit = if (isTimed) "seconds" else "reps"
        val speech = if (voiceEnabled) "$exerciseName, $targetValue $unit. Go!" else null
        val tone = if (audioEnabled) ToneGenerator.TONE_PROP_ACK else null

        deliverAudioCue(
            speechText = speech,
            toneType = tone,
            toneDurationMs = 150,
            routeToPhoneCompanion = routeToPhoneCompanion,
            exerciseName = exerciseName,
            targetValue = targetValue,
            isTimed = isTimed
        )
    }

    fun onAnnounceRest(
        restSeconds: Int,
        nextExerciseName: String,
        hapticEnabled: Boolean,
        audioEnabled: Boolean,
        voiceEnabled: Boolean,
        routeToPhoneCompanion: Boolean = true
    ) {
        if (hapticEnabled) {
            vibrateWaveform(
                longArrayOf(0L, 70L, 60L, 70L, 60L, 140L),
                intArrayOf(0, 190, 0, 210, 0, 255)
            )
        }
        val speech = if (voiceEnabled) "Rest $restSeconds seconds. Next up: $nextExerciseName" else null
        val tone = if (audioEnabled) ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD else null

        deliverAudioCue(
            speechText = speech,
            toneType = tone,
            toneDurationMs = 180,
            routeToPhoneCompanion = routeToPhoneCompanion,
            exerciseName = nextExerciseName
        )
    }

    fun onWorkoutFinished(
        hapticEnabled: Boolean,
        audioEnabled: Boolean,
        voiceEnabled: Boolean,
        routeToPhoneCompanion: Boolean = true
    ) {
        if (hapticEnabled) {
            vibrateWaveform(
                longArrayOf(0L, 100L, 70L, 100L, 70L, 100L, 90L, 280L),
                intArrayOf(0, 200, 0, 220, 0, 240, 0, 255)
            )
        }
        val speech = if (voiceEnabled) "5D Ab workout complete! Great job." else null
        val tone = if (audioEnabled) ToneGenerator.TONE_CDMA_CONFIRM else null

        deliverAudioCue(
            speechText = speech,
            toneType = tone,
            toneDurationMs = 260,
            routeToPhoneCompanion = routeToPhoneCompanion
        )
    }

    private fun deliverAudioCue(
        speechText: String?,
        toneType: Int?,
        toneDurationMs: Int,
        routeToPhoneCompanion: Boolean,
        exerciseName: String? = null,
        targetValue: Int = 0,
        isTimed: Boolean = false
    ) {
        if (speechText == null && toneType == null) return

        val playLocallyOnWatch = {
            if (toneType != null && speechText == null) {
                playTone(toneType, toneDurationMs)
            } else if (speechText != null) {
                if (toneType != null) {
                    playTone(toneType, toneDurationMs)
                }
                if (isTtsReady) {
                    speak(speechText)
                }
            }
        }

        if (routeToPhoneCompanion) {
            val payload = CompanionCuePayload(
                speechText = speechText,
                toneType = toneType,
                toneDurationMs = toneDurationMs,
                exerciseName = exerciseName,
                targetValue = targetValue,
                isTimed = isTimed
            )
            companionBridge.dispatchCueToPhoneOrFallback(
                payload = payload,
                onFallbackWatchLocal = playLocallyOnWatch
            )
        } else {
            playLocallyOnWatch()
        }
    }

    private fun speak(text: String) {
        runCatching {
            val params = Bundle().apply {
                putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_ALARM)
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "cue_${System.currentTimeMillis()}")
        }
    }

    private fun playTone(toneType: Int, durationMs: Int) {
        runCatching {
            toneGenerator?.startTone(toneType, durationMs)
        }
    }

    private fun vibrateEffect(effect: VibrationEffect) {
        runCatching {
            if (vibrator?.hasVibrator() == true) {
                vibrator.vibrate(effect)
            }
        }
    }

    private fun vibrateWaveform(timings: LongArray, amplitudes: IntArray) {
        runCatching {
            if (vibrator?.hasVibrator() == true) {
                if (vibrator.hasAmplitudeControl()) {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                }
            }
        }
    }

    fun release() {
        runCatching { toneGenerator?.release() }
        runCatching {
            tts?.stop()
            tts?.shutdown()
        }
    }
}
