package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AriseViewModel
import com.example.ui.theme.*

@Composable
fun DisciplineScreen(
    viewModel: AriseViewModel,
    modifier: Modifier = Modifier
) {
    val hunterProfile by viewModel.hunterProfile.collectAsState()

    val disciplineQuotes = remember {
        listOf(
            "\"Arise, awake, and stop not till the goal is reached.\" — Swami Vivekananda",
            "\"I will be the one who levels up alone. Let the shadows rise.\" — Sung Jin-woo",
            "\"The weak will be devoured, but the one who arises will conquer their world.\" — Hunter System",
            "\"Discipline is choosing between what you want now and what you want most.\" — Marcus Aurelius",
            "\"When you think you're done, you're only at 40% of your potential.\" — David Goggins",
            "\"He who conquers himself is the mightiest warrior.\" — Confucius"
        )
    }

    var quoteIndex by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AriseBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        item {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "DISCIPLINE STREAK",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = ArisePurpleNeon
                )
                Text(
                    text = "BRAHMACHARYA / CELIBACY JOURNEY",
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp,
                    color = AriseTextPrimary
                )
            }
        }

        // Massive Streak Counter Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AriseDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(ArisePurple.copy(alpha = pulseGlow), AriseBorder)
                    )
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Milestone Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF261405))
                            .border(1.dp, AriseGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🏆 1 Week — Real momentum",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AriseGold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Massive Number Display
                    Text(
                        text = "${hunterProfile.streakDays}",
                        fontFamily = CinzelFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 68.sp,
                        lineHeight = 72.sp,
                        color = AriseTextPrimary
                    )

                    Text(
                        text = "days clean",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 2.sp,
                        color = ArisePurpleNeon
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quote Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AriseCardBg)
                            .border(1.dp, AriseBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                quoteIndex = (quoteIndex + 1) % disciplineQuotes.size
                            }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = disciplineQuotes[quoteIndex],
                                fontFamily = RajdhaniFamily,
                                fontStyle = FontStyle.Italic,
                                fontSize = 12.sp,
                                color = AriseTextSecondary,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            IconButton(
                                onClick = {
                                    quoteIndex = (quoteIndex + 1) % disciplineQuotes.size
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "New Quote",
                                    tint = AriseTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Quick Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.markTodayPure() },
                            colors = ButtonDefaults.buttonColors(containerColor = ArisePurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("mark_pure_button")
                        ) {
                            Text(
                                "✓ Mark Today Pure",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AriseBlack
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.incrementStreak() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AriseTextPrimary),
                            border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(AriseBorder, ArisePurpleNeon))),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(0.9f)
                        ) {
                            Text("+1 Day", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.decrementStreak() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AriseTextMuted),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(0.9f)
                        ) {
                            Text("-1 Day", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetStreak() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusMissedRed),
                            border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(StatusMissedRed, Color.Transparent))),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Text("↺ Reset", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = StatusMissedRed)
                        }
                    }
                }
            }
        }

        // Hunter Rank & Level Card ("10 Days to Level Up")
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AriseDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AriseBorder, AriseBorderGlow))),
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
                        Column {
                            Text(
                                text = "HUNTER PROGRESSION",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = ArisePurpleNeon
                            )
                            Text(
                                text = "${hunterProfile.daysToLevelUp} Days to Level Up",
                                fontFamily = CinzelFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AriseTextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ArisePurple)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "RANK ${hunterProfile.rank}",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = AriseBlack
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "LVL ${hunterProfile.level} · ${hunterProfile.title}",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = AriseTextSecondary
                        )
                        Text(
                            text = "${hunterProfile.currentXp}/${hunterProfile.maxXp} XP",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ArisePurpleNeon
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { (hunterProfile.currentXp / hunterProfile.maxXp.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = ArisePurple,
                        trackColor = AriseCardBg
                    )
                }
            }
        }

        // Consult Hunter System AI Banner Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF140D22)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ArisePurple, AriseCyan))),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openAiDialog() }
                    .testTag("consult_hunter_ai_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ArisePurpleNeon, modifier = Modifier.size(16.dp))
                            Text(
                                "HUNTER SYSTEM EVALUATION",
                                fontFamily = CinzelFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ArisePurpleNeon
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Get real-time discipline audit, tactical punishment quests, or study protocols powered by Gemini AI with High Thinking.",
                            fontFamily = RajdhaniFamily,
                            fontSize = 12.sp,
                            color = AriseTextSecondary
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = ArisePurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Solo Leveling Hunter Character Artwork Anchored Below Primary Meters
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AriseDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(ArisePurple.copy(alpha = 0.5f), Color.Transparent))
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    // Generated Hero Character Artwork
                    Image(
                        painter = painterResource(id = R.drawable.img_hunter_arise_1792297315512),
                        contentDescription = "Shadow Monarch Hunter",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Vignette gradient overlay to blend into dark UI
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0x99000000),
                                        AriseBlack
                                    ),
                                    startY = 100f
                                )
                            )
                    )

                    // Overlay Title "ARISE" and Shadow Monarch subtitle
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "A R I S E",
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            letterSpacing = 6.sp,
                            color = AriseTextPrimary
                        )
                        Text(
                            text = "SHADOW MONARCH · WINTER ARC SUPREMACY",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 2.sp,
                            color = ArisePurpleNeon
                        )
                    }
                }
            }
        }
    }
}
