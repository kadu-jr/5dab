package com.carlosmarins.fivedab.phone

import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlosmarins.fivedab.phone.data.CompanionCuePayload
import com.carlosmarins.fivedab.phone.data.CompanionDataLayerPaths
import com.carlosmarins.fivedab.phone.data.ExerciseDefinition
import com.carlosmarins.fivedab.phone.data.PhonePresets
import com.carlosmarins.fivedab.phone.data.PhoneRepository
import com.carlosmarins.fivedab.phone.data.StepMode
import com.carlosmarins.fivedab.phone.data.WorkoutRoutine
import com.carlosmarins.fivedab.phone.data.WorkoutStep
import com.carlosmarins.fivedab.phone.service.PhoneAudioDuckingEngine
import com.carlosmarins.fivedab.phone.service.PhoneLiveStateHolder
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale
import java.util.UUID

private val OneUiHealthGreen = Color(0xFF2CE59B)
private val OneUiBlue = Color(0xFF4CA2FF)
private val OneUiCalorieOrange = Color(0xFFFF8A2B)
private val OneUiHeartRed = Color(0xFFFF4D6D)
private val OneUiCardDark = Color(0xFF1F2024)
private val OneUiElevatedDark = Color(0xFF2B2D33)
private val OneUiTextSecondary = Color(0xFFA9B0BA)

private val OneUiDarkScheme = darkColorScheme(
    primary = OneUiHealthGreen,
    onPrimary = Color.Black,
    secondary = OneUiBlue,
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = OneUiCardDark,
    onSurface = Color.White
)

class MainActivity : ComponentActivity() {

    private lateinit var repository: PhoneRepository
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = PhoneRepository(applicationContext)
        // Warm up TTS and audio ducking engine immediately
        PhoneAudioDuckingEngine.getInstance(applicationContext)

        setContent {
            MaterialTheme(colorScheme = OneUiDarkScheme) {
                Surface(
                    color = Color.Black,
                    modifier = Modifier.fillMaxSize()
                ) {
                    PhoneCompanionScreen(
                        repository = repository,
                        onTestDuckingCue = {
                            PhoneAudioDuckingEngine.getInstance(applicationContext)
                                .playCueWithDucking(
                                    CompanionCuePayload(
                                        speechText = "Halfway, 20 reps. Keep going!",
                                        toneType = ToneGenerator.TONE_PROP_ACK,
                                        toneDurationMs = 110
                                    )
                                )
                            PhoneLiveStateHolder.updateLastCue("Tested: \"Halfway, 20 reps. Keep going!\"")
                        },
                        onCheckConnectedWatch = {
                            runCatching {
                                val nodes = Wearable.getNodeClient(applicationContext).connectedNodes.await()
                                if (nodes.isNotEmpty()) {
                                    for (node in nodes) {
                                        Wearable.getMessageClient(applicationContext).sendMessage(
                                            node.id,
                                            CompanionDataLayerPaths.PATH_REQUEST_ROUTINES,
                                            ByteArray(0)
                                        ).await()
                                    }
                                    nodes.joinToString { it.displayName }
                                } else {
                                    null
                                }
                            }.getOrNull()
                        },
                        onPushRoutinesToWatch = { routines ->
                            runCatching {
                                val nodes = Wearable.getNodeClient(applicationContext).connectedNodes.await()
                                if (nodes.isEmpty()) return@runCatching false
                                val payload = json.encodeToString(routines).encodeToByteArray()
                                for (node in nodes) {
                                    Wearable.getMessageClient(applicationContext).sendMessage(
                                        node.id,
                                        CompanionDataLayerPaths.PATH_SYNC_ROUTINES,
                                        payload
                                    ).await()
                                }
                                true
                            }.getOrDefault(false)
                        },
                        onStartRoutineOnWatch = { routineId ->
                            runCatching {
                                val nodes = Wearable.getNodeClient(applicationContext).connectedNodes.await()
                                if (nodes.isEmpty()) return@runCatching false
                                val bytes = routineId.encodeToByteArray()
                                for (node in nodes) {
                                    Wearable.getMessageClient(applicationContext).sendMessage(
                                        node.id,
                                        CompanionDataLayerPaths.PATH_START_ROUTINE_ON_WATCH,
                                        bytes
                                    ).await()
                                }
                                true
                            }.getOrDefault(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneCompanionScreen(
    repository: PhoneRepository,
    onTestDuckingCue: () -> Unit,
    onCheckConnectedWatch: suspend () -> String?,
    onPushRoutinesToWatch: suspend (List<WorkoutRoutine>) -> Boolean,
    onStartRoutineOnWatch: suspend (String) -> Boolean
) {
    val scope = rememberCoroutineScope()
    val routines by repository.routinesFlow.collectAsState(initial = PhonePresets.allPresetRoutines)
    val relayEnabled by repository.relayEnabledFlow.collectAsState(initial = true)
    val liveTelemetry by PhoneLiveStateHolder.telemetry.collectAsState()
    val lastCueSummary by PhoneLiveStateHolder.lastCueSummary.collectAsState()

    var connectedWatchName by remember { mutableStateOf<String?>(null) }
    var statusBanner by remember { mutableStateOf<String?>(null) }
    var editingRoutineId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        connectedWatchName = onCheckConnectedWatch()
    }

    val editingRoutine = routines.find { it.id == editingRoutineId }
    if (editingRoutine != null) {
        PhoneRoutineEditorScreen(
            routine = editingRoutine,
            onBack = { editingRoutineId = null },
            onSaveRoutine = { updated ->
                scope.launch {
                    val nextList = repository.saveRoutine(updated)
                    val synced = onPushRoutinesToWatch(nextList)
                    statusBanner = if (synced) {
                        "✓ Saved & synced \"${updated.name}\" to Watch8"
                    } else {
                        "Saved on phone (Watch8 will sync when connected)"
                    }
                    editingRoutineId = null
                }
            }
        )
        return
    }

    Scaffold(
        containerColor = Color.Black
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 20.dp,
                bottom = innerPadding.calculateBottomPadding() + 32.dp,
                start = 18.dp,
                end = 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // One UI Header
            item {
                Column {
                    Text(
                        text = "5D Ab Companion",
                        color = OneUiHealthGreen,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Galaxy Watch8 Classic • Earbuds Audio Ducking & Routine Builder",
                        color = OneUiTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            // Status Toast Banner
            statusBanner?.let { msg ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OneUiHealthGreen.copy(alpha = 0.16f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = msg,
                            color = OneUiHealthGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Card 1: Earbuds Audio Relay & Watch Connection
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = OneUiCardDark),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (connectedWatchName != null) {
                                        "Connected: $connectedWatchName"
                                    } else {
                                        "Watch8 Bluetooth Link"
                                    },
                                    color = if (connectedWatchName != null) OneUiHealthGreen else Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = lastCueSummary,
                                    color = OneUiTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        connectedWatchName = onCheckConnectedWatch()
                                        statusBanner = if (connectedWatchName != null) {
                                            "✓ Connected to $connectedWatchName"
                                        } else {
                                            "No active Watch connection found yet"
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Watch Connection",
                                    tint = OneUiBlue
                                )
                            }
                        }

                        // Toggle: Mix Voice Cues with Phone Videos
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(OneUiElevatedDark, RoundedCornerShape(18.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Earbuds Audio Relay (Duck Videos)",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Lowers phone video volume briefly while speaking watch rep counts in your earbuds",
                                    color = OneUiTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = relayEnabled,
                                onCheckedChange = { enabled ->
                                    scope.launch { repository.setRelayEnabled(enabled) }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = OneUiHealthGreen
                                )
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = onTestDuckingCue,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Test Voice Over Video",
                                    color = OneUiHealthGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        val ok = onPushRoutinesToWatch(routines)
                                        statusBanner = if (ok) {
                                            "✓ All routines synced to Galaxy Watch8 Classic!"
                                        } else {
                                            "Make sure Watch8 is connected via Bluetooth"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OneUiBlue,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Sync All to Watch8",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Card 2: Live Workout Telemetry (when Watch8 is running a workout)
            if (liveTelemetry.isActive) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = OneUiCardDark),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "LIVE ON WATCH8 • ${liveTelemetry.phase}",
                                    color = OneUiHealthGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Set ${liveTelemetry.stepIndex + 1}/${liveTelemetry.totalSteps}",
                                    color = OneUiTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = liveTelemetry.exerciseName,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val unit = if (liveTelemetry.isTimed) "s" else " reps"
                                Text(
                                    text = "${liveTelemetry.currentProgress} / ${liveTelemetry.currentTarget}$unit",
                                    color = OneUiHealthGreen,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = OneUiHeartRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (liveTelemetry.heartRateBpm > 0) "${liveTelemetry.heartRateBpm} bpm" else "-- bpm",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = String.format(Locale.US, "%.0f kcal", liveTelemetry.caloriesKcal),
                                        color = OneUiCalorieOrange,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section Title: Weekly Routines
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Weekly Routines (Mon–Fri)",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val count = routines.count { !it.isPreset } + 1
                                val updated = repository.createCustomRoutine("Custom Set #$count")
                                onPushRoutinesToWatch(updated)
                            }
                        },
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = OneUiHealthGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Routine", color = OneUiHealthGreen, fontSize = 12.sp)
                    }
                }
            }

            // Routine Cards
            items(routines, key = { it.id }) { routine ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = OneUiCardDark),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { editingRoutineId = routine.id }
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = routine.name,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = routine.subtitle,
                                    color = OneUiHealthGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { editingRoutineId = routine.id },
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Routine",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Sets", color = Color.White, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {
                                        scope.launch {
                                            onPushRoutinesToWatch(routines)
                                            val started = onStartRoutineOnWatch(routine.id)
                                            statusBanner = if (started) {
                                                "▶ Started \"${routine.name}\" on Galaxy Watch8!"
                                            } else {
                                                "Open 5D Ab on Watch8 or check Bluetooth connection"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = OneUiHealthGreen,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Start on Watch",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Watch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Exercise Pills Summary
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            routine.steps.forEachIndexed { idx, step ->
                                val ex = PhonePresets.getExercise(step.exerciseId)
                                val unit = if (ex.mode == StepMode.TIMED) "s" else " reps"
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(OneUiElevatedDark, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${idx + 1}. ${ex.name}",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${step.targetValue}$unit • Rest ${step.restAfterSeconds}s",
                                        color = OneUiTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneRoutineEditorScreen(
    routine: WorkoutRoutine,
    onBack: () -> Unit,
    onSaveRoutine: (WorkoutRoutine) -> Unit
) {
    var steps by remember(routine.id) { mutableStateOf(routine.steps) }
    var editingStepIndex by remember { mutableStateOf<Int?>(null) }
    var showAddExerciseSheet by remember { mutableStateOf(false) }

    if (showAddExerciseSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddExerciseSheet = false },
            containerColor = OneUiCardDark
        ) {
            ExercisePickerSheetContent(
                onSelectExercise = { ex ->
                    val newStep = WorkoutStep(
                        stepId = UUID.randomUUID().toString().take(8),
                        exerciseId = ex.id,
                        targetValue = ex.defaultTarget,
                        repPaceMs = ex.defaultRepPaceMs,
                        restAfterSeconds = 20,
                        roundLabel = "Set ${steps.size + 1}"
                    )
                    steps = steps + newStep
                    showAddExerciseSheet = false
                }
            )
        }
    }

    editingStepIndex?.let { idx ->
        val step = steps.getOrNull(idx)
        if (step != null) {
            ModalBottomSheet(
                onDismissRequest = { editingStepIndex = null },
                containerColor = OneUiCardDark
            ) {
                StepEditSheetContent(
                    step = step,
                    onUpdateStep = { updatedStep ->
                        steps = steps.toMutableList().also { it[idx] = updatedStep }
                        editingStepIndex = null
                    }
                )
            }
        }
    }

    Scaffold(
        containerColor = Color.Black
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 36.dp,
                start = 18.dp,
                end = 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = routine.name,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap any exercise to customize reps, pace, or swap exercise",
                                color = OneUiTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            itemsIndexed(steps, key = { _, s -> s.stepId }) { index, step ->
                val ex = PhonePresets.getExercise(step.exerciseId)
                val unit = if (ex.mode == StepMode.TIMED) "s" else " reps"

                Card(
                    colors = CardDefaults.cardColors(containerColor = OneUiCardDark),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { editingStepIndex = index }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${index + 1}. ${ex.name}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ex.subtitle,
                                color = OneUiTextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (ex.mode == StepMode.TIMED) {
                                    "Hold ${step.targetValue}s • Rest ${step.restAfterSeconds}s"
                                } else {
                                    "${step.targetValue}$unit • Pace ${String.format(Locale.US, "%.1fs", step.repPaceMs / 1000.0)} • Rest ${step.restAfterSeconds}s"
                                },
                                color = OneUiHealthGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (steps.size > 1) {
                            IconButton(
                                onClick = {
                                    steps = steps.filterIndexed { i, _ -> i != index }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Set",
                                    tint = OneUiHeartRed
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { showAddExerciseSheet = true },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = OneUiBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add Exercise to ${routine.name}",
                        color = OneUiBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        val updatedSubtitle = steps.joinToString(" + ") {
                            PhonePresets.getExercise(it.exerciseId).name
                        }
                        onSaveRoutine(
                            routine.copy(
                                subtitle = updatedSubtitle,
                                steps = steps
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OneUiHealthGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save & Sync to Watch8",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StepEditSheetContent(
    step: WorkoutStep,
    onUpdateStep: (WorkoutStep) -> Unit
) {
    var selectedExerciseId by remember(step.stepId) { mutableStateOf(step.exerciseId) }
    var targetValue by remember(step.stepId) { mutableIntStateOf(step.targetValue) }
    var repPaceMs by remember(step.stepId) { mutableLongStateOf(step.repPaceMs) }
    var restSeconds by remember(step.stepId) { mutableIntStateOf(step.restAfterSeconds) }
    var showCatalog by remember { mutableStateOf(false) }

    val ex = PhonePresets.getExercise(selectedExerciseId)
    val isTimed = ex.mode == StepMode.TIMED

    if (showCatalog) {
        ExercisePickerSheetContent(
            onSelectExercise = { chosen ->
                selectedExerciseId = chosen.id
                targetValue = chosen.defaultTarget
                repPaceMs = chosen.defaultRepPaceMs
                showCatalog = false
            }
        )
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = ex.name,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = ex.subtitle,
            color = OneUiTextSecondary,
            fontSize = 13.sp
        )

        OutlinedButton(
            onClick = { showCatalog = true },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Swap Exercise (20 in Catalog)", color = OneUiBlue)
        }

        PhoneAdjusterRow(
            label = if (isTimed) "Hold Duration (Seconds)" else "Target Repetitions",
            valueDisplay = if (isTimed) "${targetValue}s" else "$targetValue reps",
            onMinus = {
                val delta = if (isTimed) 5 else 2
                targetValue = (targetValue - delta).coerceAtLeast(5)
            },
            onPlus = {
                val delta = if (isTimed) 5 else 2
                targetValue = (targetValue + delta).coerceAtMost(300)
            }
        )

        if (!isTimed) {
            PhoneAdjusterRow(
                label = "Auto-Rep Pace",
                valueDisplay = String.format(Locale.US, "%.1fs / rep", repPaceMs / 1000.0),
                onMinus = { repPaceMs = (repPaceMs - 200L).coerceAtLeast(1000L) },
                onPlus = { repPaceMs = (repPaceMs + 200L).coerceAtMost(4500L) }
            )
        }

        PhoneAdjusterRow(
            label = "Rest After Set",
            valueDisplay = "${restSeconds}s",
            onMinus = { restSeconds = (restSeconds - 5).coerceAtLeast(0) },
            onPlus = { restSeconds = (restSeconds + 5).coerceAtMost(180) }
        )

        Button(
            onClick = {
                onUpdateStep(
                    step.copy(
                        exerciseId = selectedExerciseId,
                        targetValue = targetValue,
                        repPaceMs = repPaceMs,
                        restAfterSeconds = restSeconds
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = OneUiHealthGreen,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Apply Changes", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PhoneAdjusterRow(
    label: String,
    valueDisplay: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(OneUiElevatedDark, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = onMinus,
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(34.dp)
            ) {
                Text("-", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = valueDisplay,
                color = OneUiHealthGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            OutlinedButton(
                onClick = onPlus,
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(34.dp)
            ) {
                Text("+", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ExercisePickerSheetContent(
    onSelectExercise: (ExerciseDefinition) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(480.dp)
    ) {
        item {
            Text(
                text = "Select Exercise (5D Ab Catalog)",
                color = OneUiHealthGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        items(PhonePresets.exerciseCatalog, key = { it.id }) { ex ->
            val unit = if (ex.mode == StepMode.TIMED) "s" else " reps"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OneUiElevatedDark)
                    .clickable { onSelectExercise(ex) }
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ex.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${ex.dimension.code} ${ex.dimension.shortTitle} • ${ex.subtitle}",
                        color = OneUiTextSecondary,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "${ex.defaultTarget}$unit",
                    color = OneUiHealthGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
