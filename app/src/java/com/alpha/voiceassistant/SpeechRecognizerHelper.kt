package com.alpha.voiceassistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

/**
 * Thin wrapper over the system SpeechRecognizer. Uses the on-device/cloud
 * hybrid recognizer already present on the phone — no bundled model,
 * no extra dependency. Swap for Vosk/Whisper.cpp later only if accuracy
 * on contact names proves to be a real problem in practice.
 */
class SpeechRecognizerHelper(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private var recognizer: SpeechRecognizer? = null

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available on this device")
            return
        }

        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle) {
                    val matches = results.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )
                    val best = matches?.firstOrNull()
                    if (best != null) onResult(best) else onError("No speech recognized")
                    destroy()
                }

                override fun onError(error: Int) {
                    onError("Recognizer error code: $error")
                    destroy()
                }

                // Required overrides, no-ops for this use case
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            }
            startListening(intent)
        }
    }

    fun destroy() {
        recognizer?.destroy()
        recognizer = null
    }
}