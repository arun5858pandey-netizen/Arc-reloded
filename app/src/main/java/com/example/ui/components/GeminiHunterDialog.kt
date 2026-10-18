package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.window.Dialog
import com.example.ui.AriseViewModel
import com.example.ui.theme.*

@Composable
fun GeminiHunterDialog(
    viewModel: AriseViewModel,
    onDismiss: () -> Unit
) {
    val promptInput by viewModel.aiPromptInput.collectAsState()
    val responseText by viewModel.aiResponseText.collectAsState()
    val isLoading by viewModel.isAiLoading.collectAsState()
    val useHighThinking by viewModel.useHighThinking.collectAsState()

    val quickPrompts = listOf(
        "Evaluate my daily habits & assess punishment quest",
        "Generate a 6-hour intensive JEE/NEET study protocol",
        "How to overcome brain fog and maintain pure discipline",
        "Awaken my dormant potential for the Winter Arc"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = AriseDarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(listOf(ArisePurpleNeon, AriseBorderGlow))
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Dialog Title Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ArisePurpleNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "HUNTER SYSTEM AI",
                                fontFamily = CinzelFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AriseTextPrimary
                            )
                            Text(
                                text = if (useHighThinking) "MODEL: gemini-4-argon (HIGH THINKING)" else "MODEL: gemini-3.8-flash",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = if (useHighThinking) AriseCyan else ArisePurpleNeon
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AriseTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // High Thinking Mode Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AriseCardBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = if (useHighThinking) AriseCyan else AriseTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "High Thinking Mode",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (useHighThinking) AriseCyan else AriseTextSecondary
                            )
                            Text(
                                text = "Deep tactical reasoning via gemini-4-argon",
                                fontFamily = RajdhaniFamily,
                                fontSize = 10.sp,
                                color = AriseTextMuted
                            )
                        }
                    }

                    Switch(
                        checked = useHighThinking,
                        onCheckedChange = { viewModel.useHighThinking.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AriseBlack,
                            checkedTrackColor = AriseCyan,
                            uncheckedThumbColor = AriseTextMuted,
                            uncheckedTrackColor = AriseCardElevated
                        ),
                        modifier = Modifier.testTag("high_thinking_switch")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Prompt Chips
                Text(
                    text = "SELECT DIRECTIVE",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = AriseTextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickPrompts.take(2).forEach { q ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AriseCardBg)
                                .border(1.dp, AriseBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.aiPromptInput.value = q
                                    viewModel.consultHunterAi(q)
                                }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = q,
                                fontFamily = RajdhaniFamily,
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                color = AriseTextSecondary,
                                maxLines = 2
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Response Area (Scrollable)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AriseCardBg)
                        .border(1.dp, AriseBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    if (isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = if (useHighThinking) AriseCyan else ArisePurple,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (useHighThinking) "HUNTER SYSTEM EVALUATING WITH HIGH THINKING..." else "COMMUNING WITH SHADOW MONARCH...",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = ArisePurpleNeon
                            )
                        }
                    } else if (responseText != null) {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Text(
                                text = responseText ?: "",
                                fontFamily = RajdhaniFamily,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = AriseTextPrimary
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ask the Hunter System for tactical evaluation, study protocols, or mindset conditioning.",
                                fontFamily = RajdhaniFamily,
                                fontSize = 12.sp,
                                color = AriseTextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Prompt Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { viewModel.aiPromptInput.value = it },
                        placeholder = { Text("Consult the System...", fontSize = 12.sp, color = AriseTextMuted) },
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
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_prompt_input")
                    )

                    Button(
                        onClick = { viewModel.consultHunterAi(promptInput) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (useHighThinking) AriseCyan else ArisePurple
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("ai_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = AriseBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
