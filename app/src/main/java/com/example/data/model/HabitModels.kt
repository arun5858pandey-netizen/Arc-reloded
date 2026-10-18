package com.example.data.model

data class Habit(
    val id: String,
    val name: String,
    val streak: Int = 1,
    val isCompletedToday: Boolean = false
)

enum class DayNodeStatus {
    NONE,
    DONE,
    MISSED
}

data class DayColumn(
    val dayLabel: String, // "MO", "TU", etc.
    val dateNum: Int,     // 12, 13, etc.
    val dateKey: String   // "2026-10-12"
)

data class ActiveTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: String = "HIGH"
)

data class JournalNote(
    val id: String,
    val date: String,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SyllabusChapter(
    val id: String,
    val classLevel: Int, // 11 or 12
    val subject: String, // "Physics", "Chemistry", "Mathematics", "Biology"
    val title: String,
    val hasLecture: Boolean = false,
    val hasNotes: Boolean = false,
    val hasDpps: Boolean = false,
    val hasRevision: Boolean = false
) {
    val completedSubtasksCount: Int
        get() = (if (hasLecture) 1 else 0) +
                (if (hasNotes) 1 else 0) +
                (if (hasDpps) 1 else 0) +
                (if (hasRevision) 1 else 0)

    val isFullDone: Boolean
        get() = completedSubtasksCount == 4
}

data class HunterProfile(
    val level: Int = 34,
    val rank: String = "A",
    val title: String = "Shadow Monarch Aspirant",
    val streakDays: Int = 10,
    val currentXp: Int = 68,
    val maxXp: Int = 100,
    val daysToLevelUp: Int = 10,
    val arcPhase: String = "Winter Arc · Phase 2"
)

data class VaultItem(
    val id: String,
    val title: String,
    val category: String, // "LOCAL", "OST", "PHONK", "LECTURE", "REEL"
    val duration: String,
    val audioToneHz: Float = 440f, // Frequency for synthesized focus sound
    val soundType: String = "DARK_SYNTH", // "DARK_SYNTH", "DEEP_BINAURAL_40HZ", "RAIN_AMBIENT", "EPIC_ORCHESTRAL"
    val videoUrl: String? = null
)
