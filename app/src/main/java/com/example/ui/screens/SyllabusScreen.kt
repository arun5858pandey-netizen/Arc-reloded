package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyllabusChapter
import com.example.ui.AriseViewModel
import com.example.ui.theme.*

@Composable
fun SyllabusScreen(
    viewModel: AriseViewModel,
    modifier: Modifier = Modifier
) {
    val syllabus by viewModel.syllabus.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val isJeeMode by viewModel.isJeeMode.collectAsState()
    val examDate by viewModel.examDate.collectAsState()
    val daysLeft by viewModel.daysLeft.collectAsState()
    val daysGone by viewModel.daysGone.collectAsState()

    var expandedChapterId by remember { mutableStateOf<String?>(null) }
    var showEditDateDialog by remember { mutableStateOf(false) }
    var tempExamDate by remember { mutableStateOf(examDate) }

    // Filter chapters according to Class, Exam Mode (JEE vs NEET), and Subject
    val filteredChapters = remember(syllabus, selectedClass, selectedSubject, isJeeMode) {
        syllabus.filter { chapter ->
            val matchesClass = chapter.classLevel == selectedClass
            val matchesExam = if (isJeeMode) chapter.subject != "Biology" else chapter.subject != "Mathematics"
            val matchesSubject = when (selectedSubject) {
                "ALL" -> true
                "Phys." -> chapter.subject == "Physics"
                "Chem" -> chapter.subject == "Chemistry"
                "Math" -> chapter.subject == "Mathematics"
                "Biol" -> chapter.subject == "Biology"
                else -> chapter.subject.equals(selectedSubject, ignoreCase = true)
            }
            matchesClass && matchesExam && matchesSubject
        }
    }

    val totalChapters = filteredChapters.size
    val fullDoneChapters = filteredChapters.count { it.isFullDone }
    val totalSubtasks = totalChapters * 4
    val completedSubtasks = filteredChapters.sumOf { it.completedSubtasksCount }
    val progressPercentage = if (totalSubtasks > 0) (completedSubtasks * 100) / totalSubtasks else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AriseBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Class Selector Toggle Bar ("⚡ CLASS 12" / "⚡ CLASS 11")
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Class 12 Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedClass == 12) ArisePurple.copy(alpha = 0.25f) else AriseCardBg)
                            .border(
                                1.dp,
                                if (selectedClass == 12) ArisePurple else AriseBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.selectedClass.value = 12 }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("class_12_toggle")
                    ) {
                        Text(
                            text = "⚡ CLASS 12",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (selectedClass == 12) ArisePurpleNeon else AriseTextSecondary
                        )
                    }

                    // Class 11 Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedClass == 11) ArisePurple.copy(alpha = 0.25f) else AriseCardBg)
                            .border(
                                1.dp,
                                if (selectedClass == 11) ArisePurple else AriseBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.selectedClass.value = 11 }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("class_11_toggle")
                    ) {
                        Text(
                            text = "⚡ CLASS 11",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (selectedClass == 11) ArisePurpleNeon else AriseTextSecondary
                        )
                    }
                }

                // Exam Toggle: JEE vs NEET
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AriseCardBg)
                        .border(1.dp, AriseBorder, RoundedCornerShape(10.dp))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isJeeMode) ArisePurpleDark else Color.Transparent)
                            .clickable { viewModel.isJeeMode.value = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("jee_mode_button")
                    ) {
                        Text(
                            text = "⚡ JEE",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isJeeMode) ArisePurpleNeon else AriseTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isJeeMode) AriseCyan.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { viewModel.isJeeMode.value = false }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("neet_mode_button")
                    ) {
                        Text(
                            text = "🧬 NEET",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (!isJeeMode) AriseCyan else AriseTextMuted
                        )
                    }
                }
            }
        }

        // Summary Progress Section & Countdown Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AriseDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(AriseBorderGlow.copy(alpha = 0.6f), AriseBorder))
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Countdown Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showEditDateDialog = true }
                        ) {
                            Text(
                                text = "EXAM: $examDate",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = ArisePurpleNeon
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Date",
                                tint = AriseTextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        Text(
                            text = "STARTED: 22 Sept 2026",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = AriseTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Stat Cards Row (Days Left, Days Gone, Productive, Full Done)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard("DAYS LEFT", "$daysLeft", ArisePurpleNeon, Modifier.weight(1f))
                        StatCard("DAYS GONE", "$daysGone", AriseTextSecondary, Modifier.weight(1f))
                        StatCard("PRODUCTIVE", "${completedSubtasks.coerceAtLeast(1)}", AriseCyan, Modifier.weight(1f))
                        StatCard("FULL DONE", "$fullDoneChapters", AriseGold, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Overview Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$fullDoneChapters/$totalChapters CHAPTERS",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AriseTextPrimary
                        )
                        Text(
                            text = "$progressPercentage% SUBTASKS",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ArisePurpleNeon
                        )
                        Text(
                            text = "$fullDoneChapters FULL DONE",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AriseCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Linear Progress Bar
                    LinearProgressIndicator(
                        progress = { (progressPercentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = ArisePurple,
                        trackColor = AriseCardBg
                    )
                }
            }
        }

        // Subject Filter Pills (ALL, Phys., Chem, Math/Biol)
        item {
            val subjects = if (isJeeMode) {
                listOf("ALL", "Phys.", "Chem", "Math")
            } else {
                listOf("ALL", "Phys.", "Chem", "Biol")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjects.forEach { subj ->
                    val isSelected = selectedSubject == subj
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) ArisePurple else AriseCardBg)
                            .border(1.dp, if (isSelected) ArisePurpleNeon else AriseBorder, RoundedCornerShape(16.dp))
                            .clickable { viewModel.selectedSubject.value = subj }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("filter_subject_$subj")
                    ) {
                        Text(
                            text = subj,
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) AriseBlack else AriseTextSecondary
                        )
                    }
                }
            }
        }

        // Collapsible Subject Curriculum List
        items(filteredChapters) { chapter ->
            val isExpanded = expandedChapterId == chapter.id

            Card(
                colors = CardDefaults.cardColors(containerColor = AriseDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(
                            if (chapter.isFullDone) AriseGold.copy(alpha = 0.5f) else AriseBorder,
                            AriseCardBg
                        )
                    )
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedChapterId = if (isExpanded) null else chapter.id
                    }
                    .testTag("chapter_card_${chapter.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 4 Mini Status Nodes preview on the left
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            MiniDot(chapter.hasLecture)
                            MiniDot(chapter.hasNotes)
                            MiniDot(chapter.hasDpps)
                            MiniDot(chapter.hasRevision)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Class ${chapter.classLevel} · ${chapter.subject}",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = ArisePurpleNeon.copy(alpha = 0.7f)
                            )
                            Text(
                                text = chapter.title,
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AriseTextPrimary
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ChevronRight,
                            contentDescription = "Expand",
                            tint = AriseTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Accordion Expanded Details (Interactive Subtask Nodes)
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                        ) {
                            HorizontalDivider(color = AriseBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "CHAPTER MASTERY MILESTONES",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = AriseCyan
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SubtaskToggleItem(
                                    label = "1. Lecture",
                                    isDone = chapter.hasLecture,
                                    onToggle = { viewModel.toggleSubtask(chapter.id, "LECTURE") }
                                )
                                SubtaskToggleItem(
                                    label = "2. Notes",
                                    isDone = chapter.hasNotes,
                                    onToggle = { viewModel.toggleSubtask(chapter.id, "NOTES") }
                                )
                                SubtaskToggleItem(
                                    label = "3. DPPs",
                                    isDone = chapter.hasDpps,
                                    onToggle = { viewModel.toggleSubtask(chapter.id, "DPPS") }
                                )
                                SubtaskToggleItem(
                                    label = "4. Revision",
                                    isDone = chapter.hasRevision,
                                    onToggle = { viewModel.toggleSubtask(chapter.id, "REVISION") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Exam Date Dialog
    if (showEditDateDialog) {
        AlertDialog(
            onDismissRequest = { showEditDateDialog = false },
            containerColor = AriseDarkSurface,
            title = {
                Text(
                    "TARGET EXAM DATE",
                    fontFamily = CinzelFamily,
                    color = ArisePurpleNeon,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        "Set your final battle date for the Winter Arc countdown.",
                        fontFamily = RajdhaniFamily,
                        color = AriseTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempExamDate,
                        onValueChange = { tempExamDate = it },
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
                        viewModel.examDate.value = tempExamDate
                        showEditDateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ArisePurple)
                ) {
                    Text("Update", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = AriseBlack)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDateDialog = false }) {
                    Text("Cancel", color = AriseTextMuted, fontFamily = RajdhaniFamily)
                }
            }
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AriseCardBg)
            .border(1.dp, AriseBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = color
            )
            Text(
                text = label,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = AriseTextMuted
            )
        }
    }
}

@Composable
private fun MiniDot(isDone: Boolean) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(if (isDone) StatusDonePurple else Color.Transparent)
            .border(1.dp, if (isDone) ArisePurpleNeon else StatusEmptyRing, CircleShape)
    )
}

@Composable
private fun SubtaskToggleItem(
    label: String,
    isDone: Boolean,
    onToggle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onToggle() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isDone) ArisePurple else AriseCardBg)
                .border(1.5.dp, if (isDone) ArisePurpleNeon else AriseBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = AriseBlack,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AriseBorder)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = if (isDone) AriseTextPrimary else AriseTextMuted
        )
    }
}
