package com.alpha.voiceassistant

sealed class Action {
    data class Call(val contactName: String) : Action()
    data class Alarm(val hour: Int, val minute: Int) : Action()
    data class PlaySpotify(val query: String) : Action()
}

object CommandMatcher {

    private val alarmRegex = Regex(
        "set alarm for (\\d{1,2})(?::(\\d{2}))? ?(am|pm)?",
        RegexOption.IGNORE_CASE
    )

    /** Null means "didn't understand" — caller must prompt again, never no-op silently. */
    fun matchCommand(rawText: String): Action? {
        val text = rawText.trim().lowercase()

        if (text.startsWith("call ")) {
            val name = text.removePrefix("call ").trim()
            if (name.isNotEmpty()) return Action.Call(contactName = name)
        }

        alarmRegex.find(text)?.let { m ->
            val hourRaw = m.groupValues[1].toIntOrNull() ?: return@let
            val minute = m.groupValues[2].toIntOrNull() ?: 0
            val meridiem = m.groupValues[3]

            var hour = hourRaw
            if (meridiem.equals("pm", ignoreCase = true) && hour < 12) hour += 12
            if (meridiem.equals("am", ignoreCase = true) && hour == 12) hour = 0

            return Action.Alarm(hour = hour, minute = minute)
        }

        if (text.startsWith("play ") && text.contains(" on spotify")) {
            val query = text.removePrefix("play ").replace(" on spotify", "").trim()
            if (query.isNotEmpty()) return Action.PlaySpotify(query = query)
        }

        return null
    }
}