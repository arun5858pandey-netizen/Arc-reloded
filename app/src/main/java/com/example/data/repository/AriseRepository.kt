package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AriseRepository {

    private val initialHabits = listOf(
        Habit(id = "h1", name = "Wake up at 05:00", streak = 10, isCompletedToday = true),
        Habit(id = "h2", name = "Running", streak = 8, isCompletedToday = false),
        Habit(id = "h3", name = "Studies", streak = 14, isCompletedToday = false),
        Habit(id = "h4", name = "No Junk Food", streak = 10, isCompletedToday = false),
        Habit(id = "h5", name = "Water intake", streak = 12, isCompletedToday = false),
        Habit(id = "h6", name = "Cold Shower", streak = 9, isCompletedToday = false),
        Habit(id = "h7", name = "Discipline", streak = 10, isCompletedToday = false)
    )

    val dayColumns = listOf(
        DayColumn(dayLabel = "MO", dateNum = 12, dateKey = "2026-10-12"),
        DayColumn(dayLabel = "TU", dateNum = 13, dateKey = "2026-10-13"),
        DayColumn(dayLabel = "WE", dateNum = 14, dateKey = "2026-10-14"),
        DayColumn(dayLabel = "TH", dateNum = 15, dateKey = "2026-10-15"),
        DayColumn(dayLabel = "FR", dateNum = 16, dateKey = "2026-10-16"),
        DayColumn(dayLabel = "SA", dateNum = 17, dateKey = "2026-10-17"),
        DayColumn(dayLabel = "SU", dateNum = 18, dateKey = "2026-10-18")
    )

    // Map: Pair(habitId, dayIndex) -> DayNodeStatus
    private val initialGridMatrix = mutableMapOf<Pair<String, Int>, DayNodeStatus>().apply {
        // Pre-fill realistic Solo Leveling habit matrix matching the screenshot
        put(Pair("h1", 0), DayNodeStatus.DONE)
        put(Pair("h1", 1), DayNodeStatus.DONE)
        put(Pair("h1", 2), DayNodeStatus.DONE)
        put(Pair("h1", 3), DayNodeStatus.DONE)
        put(Pair("h1", 4), DayNodeStatus.DONE)
        put(Pair("h1", 5), DayNodeStatus.NONE)
        put(Pair("h1", 6), DayNodeStatus.DONE)

        put(Pair("h2", 0), DayNodeStatus.DONE)
        put(Pair("h2", 1), DayNodeStatus.MISSED)
        put(Pair("h2", 2), DayNodeStatus.DONE)
        put(Pair("h2", 3), DayNodeStatus.NONE)
        put(Pair("h2", 4), DayNodeStatus.DONE)
        put(Pair("h2", 5), DayNodeStatus.NONE)
        put(Pair("h2", 6), DayNodeStatus.NONE)

        put(Pair("h3", 0), DayNodeStatus.DONE)
        put(Pair("h3", 1), DayNodeStatus.MISSED)
        put(Pair("h3", 2), DayNodeStatus.DONE)
        put(Pair("h3", 3), DayNodeStatus.DONE)
        put(Pair("h3", 4), DayNodeStatus.DONE)
        put(Pair("h3", 5), DayNodeStatus.NONE)
        put(Pair("h3", 6), DayNodeStatus.NONE)

        put(Pair("h4", 0), DayNodeStatus.MISSED)
        put(Pair("h4", 1), DayNodeStatus.MISSED)
        put(Pair("h4", 2), DayNodeStatus.DONE)
        put(Pair("h4", 3), DayNodeStatus.NONE)
        put(Pair("h4", 4), DayNodeStatus.NONE)
        put(Pair("h4", 5), DayNodeStatus.NONE)
        put(Pair("h4", 6), DayNodeStatus.NONE)

        put(Pair("h5", 0), DayNodeStatus.DONE)
        put(Pair("h5", 1), DayNodeStatus.DONE)
        put(Pair("h5", 2), DayNodeStatus.DONE)
        put(Pair("h5", 3), DayNodeStatus.NONE)
        put(Pair("h5", 4), DayNodeStatus.NONE)
        put(Pair("h5", 5), DayNodeStatus.NONE)
        put(Pair("h5", 6), DayNodeStatus.NONE)

        put(Pair("h6", 0), DayNodeStatus.DONE)
        put(Pair("h6", 1), DayNodeStatus.DONE)
        put(Pair("h6", 2), DayNodeStatus.DONE)
        put(Pair("h6", 3), DayNodeStatus.NONE)
        put(Pair("h6", 4), DayNodeStatus.NONE)
        put(Pair("h6", 5), DayNodeStatus.NONE)
        put(Pair("h6", 6), DayNodeStatus.NONE)

        put(Pair("h7", 0), DayNodeStatus.DONE)
        put(Pair("h7", 1), DayNodeStatus.DONE)
        put(Pair("h7", 2), DayNodeStatus.DONE)
        put(Pair("h7", 3), DayNodeStatus.DONE)
        put(Pair("h7", 4), DayNodeStatus.NONE)
        put(Pair("h7", 5), DayNodeStatus.NONE)
        put(Pair("h7", 6), DayNodeStatus.NONE)
    }

    private val initialTasks = listOf(
        ActiveTask(id = "t1", title = "Solve 45 Electrostatic Capacitance PYQs", isCompleted = false, priority = "HIGH"),
        ActiveTask(id = "t2", title = "Revise Organic Reaction Mechanisms (Aldehydes)", isCompleted = true, priority = "MEDIUM"),
        ActiveTask(id = "t3", title = "5km Night Sprint / Shadow Conditioning", isCompleted = false, priority = "HIGH")
    )

    private val initialSyllabus = listOf(
        // Class 12 Physics
        SyllabusChapter("c12_p1", 12, "Physics", "Electric Charges and Fields", hasLecture = true, hasNotes = true, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_p2", 12, "Physics", "Electrostatic Potential and Capacitance", hasLecture = true, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_p3", 12, "Physics", "Current Electricity", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_p4", 12, "Physics", "Moving Charges and Magnetism", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_p5", 12, "Physics", "Magnetism and Matter", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_p6", 12, "Physics", "Electromagnetic Induction", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_p7", 12, "Physics", "Alternating Current", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_p8", 12, "Physics", "Electromagnetic Waves", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_p9", 12, "Physics", "Ray Optics and Optical Instruments", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_p10", 12, "Physics", "Wave Optics", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        // Class 12 Chemistry
        SyllabusChapter("c12_c1", 12, "Chemistry", "Solutions", hasLecture = true, hasNotes = true, hasDpps = true, hasRevision = false),
        SyllabusChapter("c12_c2", 12, "Chemistry", "Electrochemistry", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_c3", 12, "Chemistry", "Chemical Kinetics", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_c4", 12, "Chemistry", "d- and f-Block Elements", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_c5", 12, "Chemistry", "Coordination Compounds", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        // Class 12 Mathematics
        SyllabusChapter("c12_m1", 12, "Mathematics", "Relations and Functions", hasLecture = true, hasNotes = true, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_m2", 12, "Mathematics", "Inverse Trigonometric Functions", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_m3", 12, "Mathematics", "Matrices and Determinants", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_m4", 12, "Mathematics", "Continuity and Differentiability", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        // Class 12 Biology
        SyllabusChapter("c12_b1", 12, "Biology", "Sexual Reproduction in Flowering Plants", hasLecture = true, hasNotes = true, hasDpps = true, hasRevision = true),
        SyllabusChapter("c12_b2", 12, "Biology", "Human Reproduction", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c12_b3", 12, "Biology", "Principles of Inheritance and Variation", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),

        // Class 11 Physics
        SyllabusChapter("c11_p1", 11, "Physics", "Units and Measurements", hasLecture = true, hasNotes = true, hasDpps = true, hasRevision = true),
        SyllabusChapter("c11_p2", 11, "Physics", "Motion in a Straight Line", hasLecture = true, hasNotes = true, hasDpps = false, hasRevision = false),
        SyllabusChapter("c11_p3", 11, "Physics", "Motion in a Plane", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c11_p4", 11, "Physics", "Laws of Motion", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c11_p5", 11, "Physics", "Work, Energy and Power", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c11_p6", 11, "Physics", "System of Particles and Rotational Motion", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c11_p7", 11, "Physics", "Gravitation", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c11_p8", 11, "Physics", "Mechanical Properties of Solids", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c11_p9", 11, "Physics", "Mechanical Properties of Fluids", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        // Class 11 Chemistry
        SyllabusChapter("c11_c1", 11, "Chemistry", "Some Basic Concepts of Chemistry", hasLecture = true, hasNotes = true, hasDpps = true, hasRevision = false),
        SyllabusChapter("c11_c2", 11, "Chemistry", "Structure of Atom", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c11_c3", 11, "Chemistry", "Classification of Elements", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false),
        // Class 11 Math
        SyllabusChapter("c11_m1", 11, "Mathematics", "Sets and Functions", hasLecture = true, hasNotes = false, hasDpps = false, hasRevision = false),
        SyllabusChapter("c11_m2", 11, "Mathematics", "Trigonometric Functions", hasLecture = false, hasNotes = false, hasDpps = false, hasRevision = false)
    )

    private val initialVaultItems = listOf(
        VaultItem("v1", "9 RULES TO CONTROL YOUR MIND", "REEL", "02:45", 220f, "DARK_SYNTH"),
        VaultItem("v2", "Solo Leveling Dark Awakening OST", "OST", "03:20", 330f, "EPIC_ORCHESTRAL"),
        VaultItem("v3", "Winter Arc Cold Conditioning", "LOCAL", "01:50", 174f, "DARK_SYNTH"),
        VaultItem("v4", "Deep Focus 40Hz Gamma Waves", "LOCAL", "45:00", 200f, "DEEP_BINAURAL_40HZ"),
        VaultItem("v5", "Shadow Monarch Phonk Beat", "PHONK", "02:30", 260f, "DARK_SYNTH"),
        VaultItem("v6", "JEE Physics One Shot Marathon", "LECTURE", "60:00", 220f, "RAIN_AMBIENT"),
        VaultItem("v7", "Rain on Obsidian Temple", "LOCAL", "30:00", 196f, "RAIN_AMBIENT"),
        VaultItem("v8", "Discipline Over Motivation", "REEL", "00:59", 293f, "DARK_SYNTH"),
        VaultItem("v9", "Electrostatics Fast Revision", "LECTURE", "90:00", 220f, "RAIN_AMBIENT"),
        VaultItem("v10", "Hunter Awakening War Cry", "OST", "04:12", 392f, "EPIC_ORCHESTRAL"),
        VaultItem("v11", "Cold Shower & Dopamine Reset", "REEL", "03:15", 220f, "DARK_SYNTH"),
        VaultItem("v12", "Midnight Cyberpunk Code Flow", "PHONK", "24:00", 246f, "DARK_SYNTH")
    )

    // State Flows
    val habits = MutableStateFlow(initialHabits)
    val gridMatrix = MutableStateFlow<Map<Pair<String, Int>, DayNodeStatus>>(initialGridMatrix)
    val tasks = MutableStateFlow(initialTasks)
    val syllabus = MutableStateFlow(initialSyllabus)
    val vaultItems = MutableStateFlow(initialVaultItems)
    val hunterProfile = MutableStateFlow(HunterProfile())

    val journalNotes = MutableStateFlow(listOf(
        JournalNote(
            id = "j1",
            date = "Oct 18, 2026",
            title = "Day 10: Awakening Phase",
            content = "Completed 05:00 wakeup and ice shower without hesitation. 4 hours uninterrupted study on Electrostatics and Capacitors completed. The shadow aura is settling in. Mind is sharp and silent."
        )
    ))

    val journalUnlocked = MutableStateFlow(false)
    val journalPin = MutableStateFlow("1234")

    // Quests Methods
    fun toggleHabitToday(habitId: String) {
        habits.update { list ->
            list.map { habit ->
                if (habit.id == habitId) {
                    val newCompleted = !habit.isCompletedToday
                    val newStreak = if (newCompleted) habit.streak + 1 else (habit.streak - 1).coerceAtLeast(0)
                    habit.copy(isCompletedToday = newCompleted, streak = newStreak)
                } else habit
            }
        }
        // Also update today's node in grid (Day 6 is Sunday 18)
        val currentStatus = gridMatrix.value[Pair(habitId, 6)] ?: DayNodeStatus.NONE
        val newStatus = if (currentStatus == DayNodeStatus.DONE) DayNodeStatus.NONE else DayNodeStatus.DONE
        setGridNode(habitId, 6, newStatus)
    }

    fun setGridNode(habitId: String, dayIndex: Int, newStatus: DayNodeStatus) {
        gridMatrix.update { map ->
            val updated = map.toMutableMap()
            updated[Pair(habitId, dayIndex)] = newStatus
            updated
        }
    }

    fun cycleGridNode(habitId: String, dayIndex: Int) {
        val current = gridMatrix.value[Pair(habitId, dayIndex)] ?: DayNodeStatus.NONE
        val next = when (current) {
            DayNodeStatus.NONE -> DayNodeStatus.DONE
            DayNodeStatus.DONE -> DayNodeStatus.MISSED
            DayNodeStatus.MISSED -> DayNodeStatus.NONE
        }
        setGridNode(habitId, dayIndex, next)
    }

    fun addHabit(name: String) {
        if (name.isBlank()) return
        val newId = "h_${System.currentTimeMillis()}"
        habits.update { it + Habit(id = newId, name = name.trim(), streak = 0, isCompletedToday = false) }
    }

    fun deleteHabit(habitId: String) {
        habits.update { it.filter { h -> h.id != habitId } }
    }

    // Tasks Methods
    fun addTask(title: String) {
        if (title.isBlank()) return
        val newTask = ActiveTask(id = "t_${System.currentTimeMillis()}", title = title.trim(), isCompleted = false)
        tasks.update { listOf(newTask) + it }
    }

    fun toggleTask(taskId: String) {
        tasks.update { list ->
            list.map { if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it }
        }
    }

    fun deleteTask(taskId: String) {
        tasks.update { list -> list.filter { it.id != taskId } }
    }

    // Journal Methods
    fun unlockJournal(enteredPin: String): Boolean {
        return if (enteredPin == journalPin.value) {
            journalUnlocked.value = true
            true
        } else {
            false
        }
    }

    fun lockJournal() {
        journalUnlocked.value = false
    }

    fun addJournalEntry(title: String, content: String) {
        if (content.isBlank()) return
        val note = JournalNote(
            id = "j_${System.currentTimeMillis()}",
            date = "Oct 18, 2026",
            title = if (title.isBlank()) "Arc Reflection" else title.trim(),
            content = content.trim()
        )
        journalNotes.update { listOf(note) + it }
    }

    // Syllabus Subtask Toggle
    fun toggleSubtask(chapterId: String, subtaskType: String) {
        syllabus.update { list ->
            list.map { ch ->
                if (ch.id == chapterId) {
                    when (subtaskType) {
                        "LECTURE" -> ch.copy(hasLecture = !ch.hasLecture)
                        "NOTES" -> ch.copy(hasNotes = !ch.hasNotes)
                        "DPPS" -> ch.copy(hasDpps = !ch.hasDpps)
                        "REVISION" -> ch.copy(hasRevision = !ch.hasRevision)
                        else -> ch
                    }
                } else ch
            }
        }
    }

    // Hunter Streak Methods
    fun markTodayPure() {
        hunterProfile.update {
            it.copy(
                streakDays = it.streakDays + 1,
                currentXp = (it.currentXp + 15).coerceAtMost(100)
            )
        }
    }

    fun incrementStreak() {
        hunterProfile.update { it.copy(streakDays = it.streakDays + 1) }
    }

    fun decrementStreak() {
        hunterProfile.update { it.copy(streakDays = (it.streakDays - 1).coerceAtLeast(0)) }
    }

    fun resetStreak() {
        hunterProfile.update { it.copy(streakDays = 0, currentXp = (it.currentXp - 20).coerceAtLeast(0)) }
    }

    // Vault Methods
    fun addVaultItem(title: String, url: String) {
        if (title.isBlank()) return
        val item = VaultItem(
            id = "v_${System.currentTimeMillis()}",
            title = title.trim(),
            category = "LOCAL",
            duration = "03:00",
            videoUrl = url.trim()
        )
        vaultItems.update { listOf(item) + it }
    }
}
