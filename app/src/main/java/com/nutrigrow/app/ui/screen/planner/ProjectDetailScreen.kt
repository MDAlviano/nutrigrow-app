package com.nutrigrow.app.ui.screen.planner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nutrigrow.app.data.remote.PlantingProject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    project: PlantingProject?,
    viewModel: ProjectDetailViewModel,
    onNavigateBack: () -> Unit,
    onLogAdded: () -> Unit
) {
    val emeraldColor = Color(0xFF059669)
    val context = LocalContext.current
    val uploadState by viewModel.uploadState.collectAsState()

    var showNoteDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var logNotes by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            showNoteDialog = true
        }
    }

    LaunchedEffect(uploadState) {
        if (uploadState is LogUploadState.Success) {
            showNoteDialog = false
            logNotes = ""
            selectedImageUri = null
            viewModel.resetState()
            onLogAdded()
        }
    }

    if (project == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Proyek tidak ditemukan") }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project.plantName, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = emeraldColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { galleryLauncher.launch("image/*") },
                containerColor = emeraldColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Progress")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text("Linimasa Perkembangan", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

            if (project.logs.isNullOrEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada catatan. Yuk tambahkan foto hari ini!", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(project.logs.reversed()) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                if (!log.imageUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = log.imageUrl,
                                        contentDescription = "Log Image",
                                        modifier = Modifier.fillMaxWidth().height(200.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(log.notes, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))

                                    val statusColor = when (log.aiInsightStatus) {
                                        "Safe" -> Color(0xFF059669)
                                        "Warning" -> Color(0xFFD97706)
                                        "Danger" -> Color(0xFFDC2626)
                                        else -> Color.Gray
                                    }
                                    Text("Status AI: ${log.aiInsightStatus}", fontWeight = FontWeight.Bold, color = statusColor)
                                    Text(log.aiInsightMessage ?: "", fontSize = 14.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showNoteDialog) {
            AlertDialog(
                onDismissRequest = {
                    if (uploadState !is LogUploadState.Loading) showNoteDialog = false
                },
                title = { Text("Catatan Hari Ini") },
                text = {
                    OutlinedTextField(
                        value = logNotes,
                        onValueChange = { logNotes = it },
                        label = { Text("Bagaimana kondisi tanaman?") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedImageUri?.let { uri ->
                                viewModel.addLog(context, project.id, uri, logNotes)
                            }
                        },
                        enabled = uploadState !is LogUploadState.Loading
                    ) {
                        if (uploadState is LogUploadState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Simpan")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNoteDialog = false }) { Text("Batal") }
                }
            )
        }
    }
}