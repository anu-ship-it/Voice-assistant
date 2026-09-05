package com.alpha.voiceassistant

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast

class TriggerTileService : TileService() {

    private var sttHelper: SpeechRecognizerHelper? = null

    override fun onClick() {
        super.onClick()
        qsTile?.state = Tile.STATE_ACTIVE
        qsTile?.updateTile()

        sttHelper = SpeechRecognizerHelper(
            context = applicationContext,
            onResult = { text -> handleResult(text) },
            onError = { message ->
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                resetTile()
            }
        )
        sttHelper?.startListening()
    }

    private fun handleResult(text: String) {
        when (val action = CommandMatcher.matchCommand(text)) {
            is Action.Call -> CallAction.execute(applicationContext, action)
            is Action.Alarm -> AlarmAction.execute(applicationContext, action)
            is Action.PlaySpotify -> SpotifyAction.execute(applicationContext, action)
            null -> Toast.makeText(
                applicationContext,
                "Didn't understand: \"$text\"",
                Toast.LENGTH_SHORT
            ).show()
        }
        resetTile()
    }

    private fun resetTile() {
        qsTile?.state = Tile.STATE_INACTIVE
        qsTile?.updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
        sttHelper?.destroy()
    }
}