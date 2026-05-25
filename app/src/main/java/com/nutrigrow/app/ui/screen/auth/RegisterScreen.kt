package com.nutrigrow.app.ui.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterClick: (String, String, String) -> Unit
) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val emeraldColor = Color(0xFF059669)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Buat Akun",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = emeraldColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Mulai perjalanan swasembada pangan Anda",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Nama Lengkap") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Person Icon") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Kata Sandi") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon") },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Close else Icons.Default.Done
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = "Toggle Password Visibility")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = { onRegisterClick(displayName, email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = emeraldColor)
        ) {
            Text(text = "Daftar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Sudah punya akun?", color = Color.Gray)
            TextButton(onClick = onNavigateToLogin) {
                Text(text = "Masuk", color = emeraldColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}