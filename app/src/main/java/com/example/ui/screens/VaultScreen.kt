package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.model.VaultItem
import com.example.ui.AriseViewModel
import com.example.ui.theme.*

@Composable
fun VaultScreen(
    viewModel: AriseViewModel,
    modifier: Modifier = Modifier
) {
    val vaultItems by viewModel.vaultItems.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val currentItem by viewModel.currentlyPlayingItem.collectAsState()
    val viewMode by viewModel.vaultViewMode.collectAsState()

    var inputUrlOrTitle by remember { mutableStateOf("") }
    var inputMode by remember { mutableStateOf("YOUTUBE") } // "YOUTUBE" or "LOCAL"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AriseBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & View Toggle ("🗂 Gallery" vs "▶ Reels")
        item {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "FUEL — MOTIVATION & FOCUS VAULT",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = ArisePurpleNeon
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewMode == "GALLERY") ArisePurpleDark else AriseCardBg)
                            .border(1.dp, if (viewMode == "GALLERY") ArisePurple else AriseBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.vaultViewMode.value = "GALLERY" }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("vault_gallery_mode")
                    ) {
                        Text(
                            text = "🗂 Gallery",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (viewMode == "GALLERY") ArisePurpleNeon else AriseTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewMode == "REELS") ArisePurpleDark else AriseCardBg)
                            .border(1.dp, if (viewMode == "REELS") ArisePurple else AriseBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.vaultViewMode.value = "REELS" }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("vault_reels_mode")
                    ) {
                        Text(
                            text = "▶ Reels",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (viewMode == "REELS") ArisePurpleNeon else AriseTextSecondary
                        )
                    }
                }
            }
        }

        // Active Media Player Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AriseDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(ArisePurple.copy(alpha = 0.6f), AriseBorder))
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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ArisePurple.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = currentItem?.category ?: "MEDIA",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = ArisePurpleNeon
                            )
                        }

                        Text(
                            text = if (isAudioPlaying) "PLAYING FOCUS AUDIO" else "PAUSED",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = if (isAudioPlaying) AriseCyan else AriseTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentItem?.title ?: "Select a motivation track",
                        fontFamily = CinzelFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AriseTextPrimary,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Player Progress & Time
                    LinearProgressIndicator(
                        progress = { if (isAudioPlaying) 0.45f else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape),
                        color = ArisePurple,
                        trackColor = AriseCardBg
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAudioPlaying) "01:14" else "00:00",
                            fontFamily = RajdhaniFamily,
                            fontSize = 10.sp,
                            color = AriseTextMuted
                        )
                        Text(
                            text = currentItem?.duration ?: "03:00",
                            fontFamily = RajdhaniFamily,
                            fontSize = 10.sp,
                            color = AriseTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Controls Row (Previous, Play/Pause, Next, Soundscape mode)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.playPreviousVaultItem() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = AriseTextPrimary)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Large Play/Pause Button
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(ArisePurple)
                                .clickable { viewModel.toggleAudioPlayback() }
                                .testTag("vault_main_play_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isAudioPlaying) "Pause" else "Play",
                                tint = AriseBlack,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = { viewModel.playNextVaultItem() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = AriseTextPrimary)
                        }
                    }
                }
            }
        }

        // 3x4 Grid Interface (12 Slate-Colored Boxes with Play Triangle Icon)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "REPOSITORIES (3x4 MATRIX)",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    color = ArisePurpleNeon
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Custom 3-column rows
                val chunkedItems = vaultItems.take(12).chunked(3)
                chunkedItems.forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { item ->
                            val isCurrent = currentItem?.id == item.id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent && isAudioPlaying) Color(0xFF1E1035) else AriseCardBg)
                                    .border(
                                        1.dp,
                                        if (isCurrent) ArisePurple else AriseBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        viewModel.toggleAudioPlayback(item)
                                    }
                                    .padding(8.dp)
                                    .testTag("vault_grid_item_${item.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = item.category,
                                            fontFamily = RajdhaniFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = AriseCyan
                                        )
                                        Text(
                                            text = item.duration,
                                            fontFamily = RajdhaniFamily,
                                            fontSize = 8.sp,
                                            color = AriseTextMuted
                                        )
                                    }

                                    // Small Media Play Icon (Triangle)
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isCurrent && isAudioPlaying) ArisePurple else Color(0xFF232338))
                                            .border(1.dp, if (isCurrent) ArisePurpleNeon else AriseBorder, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isCurrent && isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            tint = if (isCurrent && isAudioPlaying) AriseBlack else AriseTextPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Text(
                                        text = item.title,
                                        fontFamily = RajdhaniFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 10.sp,
                                        color = if (isCurrent) ArisePurpleNeon else AriseTextPrimary,
                                        maxLines = 1,
                                        lineHeight = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dynamic Add Resource Form Section ("+ Add" at bottom right)
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "YouTube Link",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (inputMode == "YOUTUBE") ArisePurpleNeon else AriseTextMuted,
                            modifier = Modifier.clickable { inputMode = "YOUTUBE" }
                        )
                        Text(
                            text = "My Downloaded Video",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (inputMode == "LOCAL") ArisePurpleNeon else AriseTextMuted,
                            modifier = Modifier.clickable { inputMode = "LOCAL" }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputUrlOrTitle,
                            onValueChange = { inputUrlOrTitle = it },
                            placeholder = {
                                Text(
                                    if (inputMode == "YOUTUBE") "Paste YouTube URL or video ID..." else "Enter audio/video name...",
                                    fontSize = 12.sp,
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
                            modifier = Modifier
                                .weight(1f)
                                .testTag("vault_input_field")
                        )

                        Button(
                            onClick = {
                                if (inputUrlOrTitle.isNotBlank()) {
                                    viewModel.addVaultItem(inputUrlOrTitle, inputUrlOrTitle)
                                    inputUrlOrTitle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ArisePurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("vault_add_button")
                        ) {
                            Text(
                                "+ Add",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                color = AriseBlack
                            )
                        }
                    }
                }
            }
        }
    }
}
