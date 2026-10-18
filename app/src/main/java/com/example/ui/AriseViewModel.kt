package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.FocusSoundGenerator
import com.example.data.model.*
import com.example.data.repository.AriseRepository
import com.example.remote.GeminiHunterService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AriseNavTab {
    QUESTS,
    SYLLABUS,
    DISCIPLINE,
    VAULT
}

class AriseViewModel(
    val repository: AriseRepository = AriseRepository()
) : ViewModel() {

    // Tab Navigation
    val currentTab = MutableStateFlow(AriseNavTab.QUESTS)

    // Repository Flows
    val habits = repository.habits
    val gridMatrix = repository.gridMatrix
    val tasks = repository.tasks
    val syllabus = repository.syllabus
    val vaultItems = repository.vaultItems
    val hunterProfile = repository.hunterProfile
    val journalNotes = repository.journalNotes
    val journalUnlocked = repository.journalUnlocked

    // Academic Syllabus Screen State
    val selectedClass = MutableStateFlow(12) // 11 or 12
    val selectedSubject = MutableStateFlow("ALL") // "ALL", "Physics", "Chemistry", "Mathematics", "Biology"
    val isJeeMode = MutableStateFlow(true) // true = JEE (PCM), false = NEET (PCB)
    val examDate = MutableStateFlow("24 Jan 2027")
    val daysLeft = MutableStateFlow(98)
    val daysGone = MutableStateFlow(27)

    // Audio & Vault State
    val isAudioPlaying = MutableStateFlow(false)
    val currentlyPlayingItem = MutableStateFlow<VaultItem?>(repository.vaultItems.value.firstOrNull())
    val vaultViewMode = MutableStateFlow("GALLERY") // "GALLERY" or "REELS"

    // Gemini AI Dialog State
    val isAiDialogOpen = MutableStateFlow(false)
    val aiPromptInput = MutableStateFlow("")
    val aiResponseText = MutableStateFlow<String?>(null)
    val isAiLoading = MutableStateFlow(false)
    val useHighThinking = MutableStateFlow(false)

    // UI Feedback Message (Snackbar/Notification)
    val snackbarMessage = MutableStateFlow<String?>(null)

    fun showMessage(msg: String) {
        snackbarMessage.value = msg
    }

    fun clearMessage() {
        snackbarMessage.value = null
    }

    // Tab Navigation
    fun setTab(tab: AriseNavTab) {
        currentTab.value = tab
    }

    // Habit Actions
    fun toggleHabitToday(habitId: String) {
        repository.toggleHabitToday(habitId)
    }

    fun cycleGridNode(habitId: String, dayIndex: Int) {
        repository.cycleGridNode(habitId, dayIndex)
    }

    fun addHabit(name: String) {
        repository.addHabit(name)
        showMessage("New Habit Added to Arc")
    }

    // Task Actions
    fun addTask(title: String) {
        repository.addTask(title)
        showMessage("Task Added to Quest Log")
    }

    fun toggleTask(taskId: String) {
        repository.toggleTask(taskId)
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
    }

    // Journal Actions
    fun unlockJournal(pin: String): Boolean {
        val success = repository.unlockJournal(pin)
        if (!success) {
            showMessage("Incorrect Security Code")
        }
        return success
    }

    fun lockJournal() {
        repository.lockJournal()
    }

    fun addJournalEntry(title: String, content: String) {
        repository.addJournalEntry(title, content)
        showMessage("Encrypted Entry Saved")
    }

    // Syllabus Actions
    fun toggleSubtask(chapterId: String, subtaskType: String) {
        repository.toggleSubtask(chapterId, subtaskType)
    }

    // Hunter Streak Actions
    fun markTodayPure() {
        repository.markTodayPure()
        showMessage("✓ Day Marked Pure! +15 XP")
    }

    fun incrementStreak() {
        repository.incrementStreak()
    }

    fun decrementStreak() {
        repository.decrementStreak()
    }

    fun resetStreak() {
        repository.resetStreak()
        showMessage("Streak Reset. Stand Back Up, Hunter.")
    }

    // Audio Playback
    fun toggleAudioPlayback(item: VaultItem? = null) {
        val targetItem = item ?: currentlyPlayingItem.value ?: repository.vaultItems.value.firstOrNull()
        if (targetItem != null) {
            currentlyPlayingItem.value = targetItem
            val willPlay = !isAudioPlaying.value
            if (willPlay) {
                FocusSoundGenerator.play(
                    baseFreq = targetItem.audioToneHz,
                    soundType = targetItem.soundType
                ) { playing ->
                    isAudioPlaying.value = playing
                }
                isAudioPlaying.value = true
                showMessage("Now Playing: ${targetItem.title}")
            } else {
                FocusSoundGenerator.stop { playing ->
                    isAudioPlaying.value = playing
                }
                isAudioPlaying.value = false
            }
        }
    }

    fun playNextVaultItem() {
        val list = vaultItems.value
        if (list.isEmpty()) return
        val currentIndex = list.indexOfFirst { it.id == currentlyPlayingItem.value?.id }
        val nextIndex = (currentIndex + 1) % list.size
        val nextItem = list[nextIndex]
        toggleAudioPlayback(nextItem)
    }

    fun playPreviousVaultItem() {
        val list = vaultItems.value
        if (list.isEmpty()) return
        val currentIndex = list.indexOfFirst { it.id == currentlyPlayingItem.value?.id }
        val prevIndex = if (currentIndex <= 0) list.size - 1 else currentIndex - 1
        val prevItem = list[prevIndex]
        toggleAudioPlayback(prevItem)
    }

    fun addVaultItem(title: String, url: String) {
        repository.addVaultItem(title, url)
        showMessage("Resource Added to Vault")
    }

    // AI Hunter System
    fun openAiDialog(initialPrompt: String = "") {
        aiPromptInput.value = initialPrompt
        isAiDialogOpen.value = true
    }

    fun closeAiDialog() {
        isAiDialogOpen.value = false
        aiResponseText.value = null
    }

    fun consultHunterAi(prompt: String) {
        if (prompt.isBlank()) return
        isAiLoading.value = true
        aiResponseText.value = null

        viewModelScope.launch {
            val habitsSummary = habits.value.joinToString(", ") { it.name }
            val result = GeminiHunterService.consultHunterSystem(
                userPrompt = prompt,
                enableHighThinking = useHighThinking.value,
                streakDays = hunterProfile.value.streakDays,
                habitSummary = habitsSummary
            )
            isAiLoading.value = false
            result.onSuccess { text ->
                aiResponseText.value = text
            }.onFailure { error ->
                aiResponseText.value = "[SYSTEM ERROR]: Unable to contact Shadow Monarch link. ${error.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        FocusSoundGenerator.stop()
    }
}
