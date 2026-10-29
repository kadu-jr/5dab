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
import java.util.Locale

class CueEngine(context: Context) {

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
        voiceEnabled: Boolean
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

        if (voiceEnabled && isTtsReady && (isHalfway || isMilestone)) {
            val speech = when {
                remaining in 1..3 -> remaining.toString()
                isHalfway -> "Halfway, $currentRep"
                else -> currentRep.toString()
            }
            speak(speech)
        } else if (audioEnabled) {
            playTone(ToneGenerator.TONE_PROP_BEEP, 70)
        }
    }

    fun onHoldSecondTick(
        remainingSeconds: Int,
        totalSeconds: Int,
        hapticEnabled: Boolean,
        audioEnabled: Boolean,
        voiceEnabled: Boolean
    ) {
        when {
            remainingSeconds in 1..3 -> {
                if (hapticEnabled) {
                    vibrateWaveform(longArrayOf(0L, 65L), intArrayOf(0, 230))
                }
                if (voiceEnabled && isTtsReady) {
                    speak(remainingSeconds.toString())
                } else if (audioEnabled) {
                    playTone(ToneGenerator.TONE_CDMA_PIP, 90)
                }
            }
            totalSeconds >= 20 && remainingSeconds == totalSeconds / 2 -> {
                if (hapticEnabled) {
                    vibrateWaveform(longArrayOf(0L, 50L, 45L, 50L), intArrayOf(0, 180, 0, 180))
                }
                if (voiceEnabled && isTtsReady) {
                    speak("Halfway, $remainingSeconds seconds")
                } else if (audioEnabled) {
                    playTone(ToneGenerator.TONE_PROP_ACK, 110)
                }
            }
            remainingSeconds > 3 && remainingSeconds % 10 == 0 -> {
                if (hapticEnabled) {
                    vibrateWaveform(longArrayOf(0L, 30L), intArrayOf(0, 140))
                }
                if (voiceEnabled && isTtsReady) {
                    speak("$remainingSeconds seconds")
                } else if (audioEnabled) {
                    playTone(ToneGenerator.TONE_PROP_BEEP, 55)
                }
            }
        }
    }

    fun onPrepCountdownTick(
        secondsLeft: Int,
        hapticEnabled: Boolean,
        audioEnabled: Boolean
    ) {
        if (secondsLeft in 1..3) {
            if (hapticEnabled) {
                vibrateWaveform(longArrayOf(0L, 55L), intArrayOf(0, 200))
            }
            if (audioEnabled) {
                playTone(ToneGenerator.TONE_CDMA_PIP, 80)
            }
        }
    }

    fun onAnnounceStepStart(
        exerciseName: String,
        targetValue: Int,
        isTimed: Boolean,
        hapticEnabled: Boolean,
        audioEnabled: Boolean,
        voiceEnabled: Boolean
    ) {
        if (hapticEnabled) {
            vibrateWaveform(longArrayOf(0L, 90L, 50L, 120L), intArrayOf(0, 200, 0, 255))
        }
        if (audioEnabled) {
            playTone(ToneGenerator.TONE_PROP_ACK, 150)
        }
        if (voiceEnabled && isTtsReady) {
            val unit = if (isTimed) "seconds" else "reps"
            speak("$exerciseName, $targetValue $unit. Go!")
        }
    }

    fun onAnnounceRest(
        restSeconds: Int,
        nextExerciseName: String,
        hapticEnabled: Boolean,
        audioEnabled: Boolean,
        voiceEnabled: Boolean
    ) {
        if (hapticEnabled) {
            vibrateWaveform(
                longArrayOf(0L, 70L, 60L, 70L, 60L, 140L),
                intArrayOf(0, 190, 0, 210, 0, 255)
            )
        }
        if (audioEnabled) {
            playTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 180)
        }
        if (voiceEnabled && isTtsReady) {
            speak("Rest $restSeconds seconds. Next up: $nextExerciseName")
        }
    }

    fun onWorkoutFinished(
        hapticEnabled: Boolean,
        audioEnabled: Boolean,
        voiceEnabled: Boolean
    ) {
        if (hapticEnabled) {
            vibrateWaveform(
                longArrayOf(0L, 100L, 70L, 100L, 70L, 100L, 90L, 280L),
                intArrayOf(0, 200, 0, 220, 0, 240, 0, 255)
            )
        }
        if (audioEnabled) {
            playTone(ToneGenerator.TONE_CDMA_CONFIRM, 260)
        }
        if (voiceEnabled && isTtsReady) {
            speak("5D Ab workout complete! Great job.")
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
