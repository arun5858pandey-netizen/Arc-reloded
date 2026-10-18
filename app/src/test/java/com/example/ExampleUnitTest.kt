package com.example

import com.example.data.model.DayNodeStatus
import com.example.data.repository.AriseRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var repository: AriseRepository

    @Before
    fun setUp() {
        repository = AriseRepository()
    }

    @Test
    fun habit_toggleToday_incrementsStreak() {
        val initialStreak = repository.habits.value.first { it.id == "h2" }.streak
        repository.toggleHabitToday("h2")
        val updatedHabit = repository.habits.value.first { it.id == "h2" }
        assertTrue(updatedHabit.isCompletedToday)
        assertEquals(initialStreak + 1, updatedHabit.streak)
    }

    @Test
    fun gridMatrix_cycleNode_cyclesCorrectly() {
        // h1, day 5 is initially NONE
        assertEquals(DayNodeStatus.NONE, repository.gridMatrix.value[Pair("h1", 5)])
        repository.cycleGridNode("h1", 5)
        assertEquals(DayNodeStatus.DONE, repository.gridMatrix.value[Pair("h1", 5)])
        repository.cycleGridNode("h1", 5)
        assertEquals(DayNodeStatus.MISSED, repository.gridMatrix.value[Pair("h1", 5)])
        repository.cycleGridNode("h1", 5)
        assertEquals(DayNodeStatus.NONE, repository.gridMatrix.value[Pair("h1", 5)])
    }

    @Test
    fun task_addAndToggle_works() {
        val initialCount = repository.tasks.value.size
        repository.addTask("Shadow Sprint 10km")
        assertEquals(initialCount + 1, repository.tasks.value.size)
        val addedTask = repository.tasks.value.first()
        assertEquals("Shadow Sprint 10km", addedTask.title)
        assertFalse(addedTask.isCompleted)

        repository.toggleTask(addedTask.id)
        assertTrue(repository.tasks.value.first().isCompleted)
    }

    @Test
    fun journal_pinUnlock_succeedsOnlyWithCorrectCode() {
        assertFalse(repository.journalUnlocked.value)
        val failure = repository.unlockJournal("9999")
        assertFalse(failure)
        assertFalse(repository.journalUnlocked.value)

        val success = repository.unlockJournal("1234")
        assertTrue(success)
        assertTrue(repository.journalUnlocked.value)

        repository.lockJournal()
        assertFalse(repository.journalUnlocked.value)
    }

    @Test
    fun hunterStreak_pureDay_incrementsDaysAndXp() {
        val initialStreak = repository.hunterProfile.value.streakDays
        val initialXp = repository.hunterProfile.value.currentXp
        repository.markTodayPure()
        assertEquals(initialStreak + 1, repository.hunterProfile.value.streakDays)
        assertTrue(repository.hunterProfile.value.currentXp >= initialXp)
    }

    @Test
    fun syllabus_subtaskToggle_updatesCompletedCount() {
        val chapterId = "c12_p1"
        val chapter = repository.syllabus.value.first { it.id == chapterId }
        val initialSubtasks = chapter.completedSubtasksCount

        // Toggle DPPs on
        assertFalse(chapter.hasDpps)
        repository.toggleSubtask(chapterId, "DPPS")
        val updated = repository.syllabus.value.first { it.id == chapterId }
        assertTrue(updated.hasDpps)
        assertEquals(initialSubtasks + 1, updated.completedSubtasksCount)
    }
}
