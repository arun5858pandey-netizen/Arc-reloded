package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.AriseViewModel
import com.example.ui.theme.*

@Composable
fun QuestsScreen(
    viewModel: AriseViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.habits.collectAsState()
    val gridMatrix by viewModel.gridMatrix.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val journalNotes by viewModel.journalNotes.collectAsState()
    val isJournalUnlocked by viewModel.journalUnlocked.collectAsState()

    var newTaskText by remember { mutableStateOf("") }
    var enteredPin by remember { mutableStateOf("") }
    var newJournalTitle by remember { mutableStateOf("") }
    var newJournalContent by remember { mutableStateOf("") }
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }

    val dayCols = viewModel.repository.dayColumns

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AriseBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Section: Today's Quests Circular Cards (Ring Tap)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TODAY'S QUESTS",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        color = ArisePurpleNeon
                    )
                    TextButton(
                        onClick = { showAddHabitDialog = true },
                        modifier = Modifier.testTag("add_habit_button")
                    ) {
                        Text(
                            text = "+ Add Quest",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AriseCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(habits) { habit ->
                        val isDone = habit.isCompletedToday
                        Box(
                            modifier = Modifier
                                .width(115.dp)
                                .height(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(AriseCardBg)
                                .border(
                                    width = 1.dp,
                                    color = if (isDone) ArisePurple else AriseBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.toggleHabitToday(habit.id) }
                                .padding(10.dp)
                                .testTag("today_quest_${habit.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Circular Tap Ring
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDone) ArisePurple.copy(alpha = 0.3f) else Color.Transparent
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = if (isDone) ArisePurple else StatusEmptyRing,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completed",
                                            tint = ArisePurpleNeon,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "TAP",
                                            fontFamily = RajdhaniFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = AriseTextMuted
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = habit.name,
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isDone) AriseTextPrimary else AriseTextSecondary,
                                    maxLines = 2,
                                    lineHeight = 14.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (isDone) "🔥 ${habit.streak}d" else "TAP RING",
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    color = if (isDone) AriseFlame else AriseTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Last 7 Days Routine Matrix
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AriseDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(AriseBorderGlow.copy(alpha = 0.4f), AriseBorder))
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "LAST 7 DAYS",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        color = ArisePurpleNeon
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Grid Header: QUEST on left, Days on right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "QUEST",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = AriseTextMuted,
                            modifier = Modifier.weight(1.8f)
                        )

                        Row(
                            modifier = Modifier.weight(3.2f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            dayCols.forEach { col ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = col.dayLabel,
                                        fontFamily = RajdhaniFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = AriseTextMuted
                                    )
                                    Text(
                                        text = "${col.dateNum}",
                                        fontFamily = RajdhaniFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 9.sp,
                                        color = AriseTextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = AriseBorder.copy(alpha = 0.6f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Habit Rows with circular nodes
                    habits.forEach { habit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = habit.name,
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = AriseTextPrimary,
                                maxLines = 1,
                                modifier = Modifier.weight(1.8f)
                            )

                            Row(
                                modifier = Modifier.weight(3.2f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                dayCols.indices.forEach { dayIdx ->
                                    val status = gridMatrix[Pair(habit.id, dayIdx)] ?: DayNodeStatus.NONE
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (status) {
                                                    DayNodeStatus.DONE -> StatusDonePurple
                                                    DayNodeStatus.MISSED -> StatusMissedRed
                                                    DayNodeStatus.NONE -> Color.Transparent
                                                }
                                            )
                                            .border(
                                                width = 1.5.dp,
                                                color = when (status) {
                                                    DayNodeStatus.DONE -> ArisePurpleNeon
                                                    DayNodeStatus.MISSED -> StatusMissedRed
                                                    DayNodeStatus.NONE -> StatusEmptyRing
                                                },
                                                shape = CircleShape
                                            )
                                            .clickable { viewModel.cycleGridNode(habit.id, dayIdx) }
                                            .testTag("node_${habit.id}_$dayIdx")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = AriseBorder.copy(alpha = 0.6f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Legend & Instructions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusDonePurple))
                        Text(text = " done   ", fontSize = 10.sp, color = AriseTextMuted, fontFamily = RajdhaniFamily)

                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusMissedRed))
                        Text(text = " missed   ", fontSize = 10.sp, color = AriseTextMuted, fontFamily = RajdhaniFamily)

                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).border(1.dp, StatusEmptyRing, CircleShape))
                        Text(text = " no entry", fontSize = 10.sp, color = AriseTextMuted, fontFamily = RajdhaniFamily)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "tap once: done · double-tap: missed · tap once more: clear",
                        fontFamily = RajdhaniFamily,
                        fontSize = 10.sp,
                        color = AriseTextMuted.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        // Section: Active Tasks
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AriseDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AriseBorder, AriseCardBg))),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "TASKS",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        color = ArisePurpleNeon
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newTaskText,
                            onValueChange = { newTaskText = it },
                            placeholder = {
                                Text(
                                    "New task — e.g. solve 30 questions",
                                    fontFamily = RajdhaniFamily,
                                    fontSize = 13.sp,
                                    color = AriseTextMuted
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AriseCardBg,
                                unfocusedContainerColor = AriseCardBg,
                                focusedBorderColor = ArisePurple,
                                unfocusedBorderColor = AriseBorder,
                                focusedTextColor = AriseTextPrimary,
                                unfocusedTextColor = AriseTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (newTaskText.isNotBlank()) {
                                    viewModel.addTask(newTaskText)
                                    newTaskText = ""
                                }
                            }),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("task_input_field")
                        )

                        Button(
                            onClick = {
                                if (newTaskText.isNotBlank()) {
                                    viewModel.addTask(newTaskText)
                                    newTaskText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ArisePurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("add_task_button")
                        ) {
                            Text(
                                "Add",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                color = AriseBlack
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tasks List
                    if (tasks.isEmpty()) {
                        Text(
                            text = "No tasks yet — add one above, or conquer a quest.",
                            fontFamily = RajdhaniFamily,
                            fontSize = 12.sp,
                            color = AriseTextMuted,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        tasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AriseCardBg)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { viewModel.toggleTask(task.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = ArisePurple,
                                        uncheckedColor = AriseBorder,
                                        checkmarkColor = AriseBlack
                                    )
                                )
                                Text(
                                    text = task.title,
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (task.isCompleted) AriseTextMuted else AriseTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.deleteTask(task.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete",
                                        tint = AriseTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Secure Journal Card (PIN Protected)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AriseDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(AriseBorderGlow.copy(alpha = 0.5f), AriseBorder))
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "JOURNAL",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 2.sp,
                                color = ArisePurpleNeon
                            )
                            Icon(
                                imageVector = if (isJournalUnlocked) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                                contentDescription = "Lock",
                                tint = if (isJournalUnlocked) AriseCyan else AriseGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        if (isJournalUnlocked) {
                            TextButton(onClick = { viewModel.lockJournal() }) {
                                Text("Lock", color = AriseTextMuted, fontSize = 12.sp, fontFamily = RajdhaniFamily)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AnimatedVisibility(visible = !isJournalUnlocked) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = AriseGold,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Journal is locked",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AriseTextSecondary
                            )
                            Text(
                                text = "Enter code to access encrypted shadow reflections",
                                fontFamily = RajdhaniFamily,
                                fontSize = 11.sp,
                                color = AriseTextMuted
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = enteredPin,
                                    onValueChange = { enteredPin = it },
                                    placeholder = { Text("Enter code (e.g. 1234)", fontSize = 12.sp, color = AriseTextMuted) },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = AriseCardBg,
                                        unfocusedContainerColor = AriseCardBg,
                                        focusedBorderColor = ArisePurple,
                                        unfocusedBorderColor = AriseBorder,
                                        focusedTextColor = AriseTextPrimary,
                                        unfocusedTextColor = AriseTextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .width(180.dp)
                                        .testTag("journal_pin_input")
                                )

                                Button(
                                    onClick = {
                                        if (viewModel.unlockJournal(enteredPin)) {
                                            enteredPin = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ArisePurple),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("journal_unlock_button")
                                ) {
                                    Text("Unlock", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = AriseBlack)
                                }
                            }
                        }
                    }

                    AnimatedVisibility(visible = isJournalUnlocked) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = newJournalTitle,
                                onValueChange = { newJournalTitle = it },
                                placeholder = { Text("Entry Title...", fontSize = 13.sp, color = AriseTextMuted) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = AriseCardBg,
                                    unfocusedContainerColor = AriseCardBg,
                                    focusedBorderColor = ArisePurple,
                                    unfocusedBorderColor = AriseBorder,
                                    focusedTextColor = AriseTextPrimary,
                                    unfocusedTextColor = AriseTextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newJournalContent,
                                onValueChange = { newJournalContent = it },
                                placeholder = { Text("Log your psychological and arc reflections...", fontSize = 13.sp, color = AriseTextMuted) },
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = AriseCardBg,
                                    unfocusedContainerColor = AriseCardBg,
                                    focusedBorderColor = ArisePurple,
                                    unfocusedBorderColor = AriseBorder,
                                    focusedTextColor = AriseTextPrimary,
                                    unfocusedTextColor = AriseTextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (newJournalContent.isNotBlank()) {
                                        viewModel.addJournalEntry(newJournalTitle, newJournalContent)
                                        newJournalTitle = ""
                                        newJournalContent = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ArisePurple),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Save Note", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = AriseBlack)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            journalNotes.forEach { note ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AriseCardBg)
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(note.title, fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ArisePurpleNeon)
                                        Text(note.date, fontFamily = RajdhaniFamily, fontSize = 10.sp, color = AriseTextMuted)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(note.content, fontFamily = RajdhaniFamily, fontSize = 12.sp, color = AriseTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Habit Dialog
    if (showAddHabitDialog) {
        AlertDialog(
            onDismissRequest = { showAddHabitDialog = false },
            containerColor = AriseDarkSurface,
            titleContentColor = AriseTextPrimary,
            title = {
                Text(
                    "ADD NEW QUEST",
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ArisePurpleNeon
                )
            },
            text = {
                Column {
                    Text(
                        "Set a new daily standard for your Winter Arc.",
                        fontFamily = RajdhaniFamily,
                        fontSize = 13.sp,
                        color = AriseTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newHabitName,
                        onValueChange = { newHabitName = it },
                        placeholder = { Text("e.g. 100 Pushups, Reading...", color = AriseTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArisePurple,
                            unfocusedBorderColor = AriseBorder,
                            focusedTextColor = AriseTextPrimary,
                            unfocusedTextColor = AriseTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newHabitName.isNotBlank()) {
                            viewModel.addHabit(newHabitName)
                            newHabitName = ""
                            showAddHabitDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ArisePurple)
                ) {
                    Text("Add Quest", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = AriseBlack)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddHabitDialog = false }) {
                    Text("Cancel", color = AriseTextMuted, fontFamily = RajdhaniFamily)
                }
            }
        )
    }
}
