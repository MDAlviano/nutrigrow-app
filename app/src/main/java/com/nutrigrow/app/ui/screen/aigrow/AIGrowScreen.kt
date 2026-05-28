package com.nutrigrow.app.ui.screen.aigrow

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIGrowScreen(
    viewModel: AIGrowViewModel,
    onNavigateBack: () -> Unit
) {
    val emeraldColor = Color(0xFF059669)
    val context = LocalContext.current
    val state by viewModel.aigrowState.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AIGrow Scanner", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = emeraldColor)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Unggah Foto Daun", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Gambar Daun",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Menu, contentDescription = "Galeri", modifier = Modifier.size(64.dp), tint = emeraldColor)
                            Text("Belum ada gambar", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Pilih dari Galeri", color = emeraldColor)
            }

            Button(
                onClick = {
                    selectedImageUri?.let { uri ->
                        viewModel.analyzeImage(context, uri, "Tanaman Umum")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = selectedImageUri != null && state !is AIGrowState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = emeraldColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state is AIGrowState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Analisis dengan AI", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (state is AIGrowState.Success) {
                val data = (state as AIGrowState.Success).data
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Hasil Analisis", color = emeraldColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Text("Penyakit:", fontWeight = FontWeight.Bold)
                        Text(data.diseaseName, fontSize = 18.sp, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))

                        Text("Tingkat Keyakinan (Confidence):", fontWeight = FontWeight.Bold)
                        Text("${(data.confidenceScore * 100).toInt()}%", modifier = Modifier.padding(bottom = 8.dp))

                        Text("Analisis AI:", fontWeight = FontWeight.Bold)
                        Text(data.aiReasoning, modifier = Modifier.padding(bottom = 8.dp))

                        Text("Solusi Organik:", fontWeight = FontWeight.Bold, color = emeraldColor)
                        data.organicSolutions.forEach { solusi ->
                            Text("• $solusi", modifier = Modifier.padding(bottom = 4.dp))
                        }
                    }
                }
            } else if (state is AIGrowState.Error) {
                Text(
                    text = (state as AIGrowState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}