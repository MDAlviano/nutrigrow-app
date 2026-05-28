package com.nutrigrow.app.ui.screen.planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
    onNavigateBack: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var plantName by remember { mutableStateOf("") }
    var targetDays by remember { mutableStateOf("") }

    val mediaOptions = listOf("Tanah", "Hidroponik")
    var selectedMedia by remember { mutableStateOf(mediaOptions[0]) }

    val emeraldColor = Color(0xFF059669)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proyek Tanam Baru", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
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
                .padding(24.dp)
        ) {
            Text(
                "Detail Tanaman",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = plantName,
                onValueChange = { plantName = it },
                label = { Text("Nama Tanaman (Misal: Cabai Rawit)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                "Media Tanam",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)) {
                mediaOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = (option == selectedMedia),
                            onClick = { selectedMedia = option },
                            colors = RadioButtonDefaults.colors(selectedColor = emeraldColor)
                        )
                        Text(text = option)
                    }
                }
            }

            OutlinedTextField(
                value = targetDays,
                onValueChange = { targetDays = it },
                label = { Text("Target Panen (dalam hitungan hari)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = { onSubmit(plantName, selectedMedia, targetDays) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = emeraldColor)
            ) {
                Text("Buat Proyek", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}