package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.AriseNavTab
import com.example.ui.AriseViewModel
import com.example.ui.components.BottomHunterNavBar
import com.example.ui.components.GeminiHunterDialog
import com.example.ui.components.TopHunterHeader
import com.example.ui.screens.DisciplineScreen
import com.example.ui.screens.QuestsScreen
import com.example.ui.screens.SyllabusScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.AriseBlack
import com.example.ui.theme.ArisePurpleNeon
import com.example.ui.theme.AriseTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AriseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AriseTheme {
                val currentTab by viewModel.currentTab.collectAsState()
                val hunterProfile by viewModel.hunterProfile.collectAsState()
                val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
                val isAiDialogOpen by viewModel.isAiDialogOpen.collectAsState()
                val snackbarMessage by viewModel.snackbarMessage.collectAsState()

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(snackbarMessage) {
                    snackbarMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessage()
                    }
                }

                // Hardware BackHandler: Return to Quests tab if on another screen
                BackHandler(enabled = currentTab != AriseNavTab.QUESTS) {
                    viewModel.setTab(AriseNavTab.QUESTS)
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AriseBlack)
                        .testTag("arise_scaffold"),
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.padding(bottom = 80.dp)
                        )
                    },
                    topBar = {
                        TopHunterHeader(
                            hunterProfile = hunterProfile,
                            isAudioPlaying = isAudioPlaying,
                            onSaveClick = {
                                viewModel.showMessage("✓ Hunter Arc Data Synced to Core")
                            },
                            onAudioToggle = {
                                viewModel.toggleAudioPlayback()
                            },
                            onAiClick = {
                                viewModel.openAiDialog()
                            },
                            onProfileClick = {
                                viewModel.setTab(AriseNavTab.DISCIPLINE)
                            },
                            modifier = Modifier.statusBarsPadding()
                        )
                    },
                    bottomBar = {
                        BottomHunterNavBar(
                            selectedTab = currentTab,
                            onTabSelected = { tab ->
                                viewModel.setTab(tab)
                            }
                        )
                    },
                    containerColor = AriseBlack
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Crossfade(
                            targetState = currentTab,
                            label = "tab_fade"
                        ) { tab ->
                            when (tab) {
                                AriseNavTab.QUESTS -> QuestsScreen(
                                    viewModel = viewModel
                                )
                                AriseNavTab.DISCIPLINE -> DisciplineScreen(
                                    viewModel = viewModel
                                )
                                AriseNavTab.SYLLABUS -> SyllabusScreen(
                                    viewModel = viewModel
                                )
                                AriseNavTab.VAULT -> VaultScreen(
                                    viewModel = viewModel
                                )
                            }
                        }
                    }

                    if (isAiDialogOpen) {
                        GeminiHunterDialog(
                            viewModel = viewModel,
                            onDismiss = { viewModel.closeAiDialog() }
                        )
                    }
                }
            }
        }
    }
}
