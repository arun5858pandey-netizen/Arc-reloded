package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HunterProfile
import com.example.ui.theme.*

@Composable
fun TopHunterHeader(
    hunterProfile: HunterProfile,
    isAudioPlaying: Boolean,
    onSaveClick: () -> Unit,
    onAudioToggle: () -> Unit,
    onAiClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AriseBlack)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Utility Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HUNTER STATUS",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 2.5.sp,
                    color = ArisePurpleNeon.copy(alpha = 0.8f)
                )
                Text(
                    text = "A R I S E",
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    letterSpacing = 4.sp,
                    color = AriseTextPrimary,
                    modifier = Modifier.testTag("app_title_arise")
                )
            }

            // Action Utility Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Save pill button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AriseCardBg)
                        .border(1.dp, AriseBorder, RoundedCornerShape(8.dp))
                        .clickable { onSaveClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("save_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = AriseTextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Save",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AriseTextPrimary
                        )
                    }
                }

                // Focus Ambient Audio Toggle
                IconButton(
                    onClick = onAudioToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isAudioPlaying) ArisePurpleDark else AriseCardBg)
                        .border(
                            1.dp,
                            if (isAudioPlaying) ArisePurpleNeon else AriseBorder,
                            CircleShape
                        )
                        .testTag("audio_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isAudioPlaying) Icons.Default.MusicNote else Icons.Default.Headphones,
                        contentDescription = "Focus Audio",
                        tint = if (isAudioPlaying) ArisePurpleNeon else AriseTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // AI Hunter Advisor
                IconButton(
                    onClick = onAiClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AriseCardBg)
                        .border(1.dp, ArisePurple.copy(alpha = glowAlpha), CircleShape)
                        .testTag("ai_advisor_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini Hunter System",
                        tint = ArisePurple,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status Badges Row (Streak + Hunter Rank)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1308))
                    .border(1.dp, AriseFlame.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "🔥", fontSize = 13.sp)
                Text(
                    text = "${hunterProfile.streakDays} day streak",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = AriseFlame
                )
            }

            // Hunter Rank & LVL Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF160D27))
                    .border(1.dp, ArisePurple.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .clickable { onProfileClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("rank_badge"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(ArisePurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = hunterProfile.rank,
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = AriseBlack
                    )
                }
                Text(
                    text = "RANK ${hunterProfile.rank} · LVL ${hunterProfile.level}",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ArisePurpleNeon
                )
            }
        }
    }
}
