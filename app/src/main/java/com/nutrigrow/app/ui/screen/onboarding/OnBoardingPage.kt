package com.nutrigrow.app.ui.screen.onboarding

data class OnboardingPage(
    val title: String,
    val description: String,
    val iconResName: String
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Deteksi Penyakit Daun Instan",
        description = "Foto daun tanamanmu dan biarkan kecerdasan buatan AIGrow mendiagnosis penyakit serta solusinya secara presisi.",
        iconResName = "scan_leaf"
    ),
    OnboardingPage(
        title = "Rencana Tanam Terorganisir",
        description = "Atur linimasa proyek tanam harianmu, pantau perkembangannya, dan dapatkan pengingat perawatan.",
        iconResName = "planner"
    ),
    OnboardingPage(
        title = "Solusi Ramah Lingkungan",
        description = "Dapatkan saran racikan nutrisi dan pupuk organik lokal yang aman bagi tanah dan dompetmu.",
        iconResName = "organic"
    )
)