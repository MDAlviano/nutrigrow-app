package com.nutrigrow.app.ui.screen.home

import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrigrow.app.data.remote.PlantingProject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLogoutClick: () -> Unit,
    onNavigateToAIGrow: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onProjectClick: (String) -> Unit
) {
    val emeraldColor = Color(0xFF059669)
    val state by viewModel.homeState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nutrigrow Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = emeraldColor),
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Aksi Cepat", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ElevatedCard(
                    onClick = onNavigateToAIGrow,
                    modifier = Modifier.weight(1f).height(100.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.Search, contentDescription = "AIGrow", tint = emeraldColor, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("AIGrow Scanner", fontWeight = FontWeight.SemiBold, color = emeraldColor)
                    }
                }

                ElevatedCard(
                    onClick = onNavigateToPlanner,
                    modifier = Modifier.weight(1f).height(100.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.DateRange, contentDescription = "Planner", tint = emeraldColor, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Proyek Baru", fontWeight = FontWeight.SemiBold, color = emeraldColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Proyek Tanam Anda", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

            when (state) {
                is HomeState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = emeraldColor)
                    }
                }
                is HomeState.Error -> {
                    val errorMsg = (state as HomeState.Error).message
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(48.dp))
                        Text(text = errorMsg, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                        Button(onClick = { viewModel.fetchProjects() }, colors = ButtonDefaults.buttonColors(containerColor = emeraldColor), modifier = Modifier.padding(top = 8.dp)) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is HomeState.Success -> {
                    val projects = (state as HomeState.Success).projects
                    if (projects.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada proyek tanam. Yuk buat baru!", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(projects) { project ->
                                Card(
                                    onClick = { onProjectClick(project.id) } // problem
                                ) {
                                    ProjectItemCard(project)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectItemCard(project: PlantingProject) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = project.plantName, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                // Badge Status
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (project.status == "active") Color(0xFFD1FAE5) else Color(0xFFF3F4F6)
                ) {
                    Text(
                        text = project.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (project.status == "active") Color(0xFF059669) else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Media: ${project.mediaType}", fontSize = 14.sp, color = Color.DarkGray)
            Text(text = "Target Panen: ${project.targetHarvestDays} Hari", fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}