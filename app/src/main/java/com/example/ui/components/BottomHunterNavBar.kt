package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AriseNavTab
import com.example.ui.theme.*

data class NavTabItem(
    val tab: AriseNavTab,
    val label: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector,
    val testTag: String
)

@Composable
fun BottomHunterNavBar(
    selectedTab: AriseNavTab,
    onTabSelected: (AriseNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavTabItem(
            tab = AriseNavTab.QUESTS,
            label = "QUESTS",
            iconFilled = Icons.Filled.Close, // Stylized dual swords X / cross
            iconOutlined = Icons.Outlined.Close,
            testTag = "nav_tab_quests"
        ),
        NavTabItem(
            tab = AriseNavTab.DISCIPLINE,
            label = "DISCIPLINE",
            iconFilled = Icons.Filled.LocalFireDepartment,
            iconOutlined = Icons.Outlined.LocalFireDepartment,
            testTag = "nav_tab_discipline"
        ),
        NavTabItem(
            tab = AriseNavTab.SYLLABUS,
            label = "SYLLABUS",
            iconFilled = Icons.Filled.MenuBook,
            iconOutlined = Icons.Outlined.MenuBook,
            testTag = "nav_tab_syllabus"
        ),
        NavTabItem(
            tab = AriseNavTab.VAULT,
            label = "VAULT",
            iconFilled = Icons.Filled.PlayCircle,
            iconOutlined = Icons.Outlined.PlayCircleOutline,
            testTag = "nav_tab_vault"
        )
    )

    Surface(
        color = AriseDarkSurface,
        tonalElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(AriseBorderGlow.copy(alpha = 0.5f), Color.Transparent)
                ),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = selectedTab == item.tab

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(item.tab) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag(item.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) ArisePurple.copy(alpha = 0.2f) else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.iconFilled else item.iconOutlined,
                            contentDescription = item.label,
                            tint = if (isSelected) ArisePurpleNeon else AriseTextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        fontFamily = RajdhaniFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = if (isSelected) ArisePurpleNeon else AriseTextMuted
                    )

                    // Active indicator line
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .height(2.dp)
                            .width(16.dp)
                            .background(if (isSelected) ArisePurple else Color.Transparent)
                    )
                }
            }
        }
    }
}
