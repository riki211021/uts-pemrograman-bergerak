package com.example.jadwalkambing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// --- DATA MODEL ---
data class DataKambing(
    val indukan: String,
    val pejantan: String,
    val kandang: String,
    val kawin: String,
    val lahir: String,
    val vaksin: String,
    val cek: String
)

// --- COLOR PALETTE ---
val PrimaryGreen = Color(0xFF2E5942) // Hijau Hutan
val SecondaryGold = Color(0xFFBFA36F) // Emas Kalem
val BackgroundCream = Color(0xFFF9F9F7) // Putih Gading
val SurfaceWhite = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1A1C19)
val TextGray = Color(0xFF707070)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = PrimaryGreen,
                    background = BackgroundCream,
                    surface = SurfaceWhite
                )
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = BackgroundCream
                ) { innerPadding ->
                    ModernFormKambing(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernFormKambing(modifier: Modifier = Modifier) {
    // STATES
    var indukan by remember { mutableStateOf("") }
    var pejantan by remember { mutableStateOf("") }
    var kandang by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var listData by remember { mutableStateOf(listOf<DataKambing>()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 48.dp)
    ) {
        // --- HEADER ---
        item {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(
                    text = "Breeding Tracker",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Jadwal Ternak",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                )
                Text(
                    text = "Kelola data reproduksi kambing",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }

        // --- FORM INPUTS ---
        item {
            // Gunakan Icons.Default yang pasti ada
            ElegantTextField(
                value = indukan,
                onValueChange = { indukan = it },
                label = "Indukan Betina",
                icon = Icons.Default.Face // Ikon wajah
            )
            Spacer(Modifier.height(16.dp))

            ElegantTextField(
                value = pejantan,
                onValueChange = { pejantan = it },
                label = "Pejantan",
                icon = Icons.Default.Person // Ikon orang
            )
            Spacer(Modifier.height(16.dp))

            ElegantTextField(
                value = kandang,
                onValueChange = { kandang = it },
                label = "Nomor Kandang",
                icon = Icons.Default.Home // Ikon rumah
            )
            Spacer(Modifier.height(16.dp))

            // Date Picker Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                ElegantTextField(
                    value = if(tanggal.isEmpty()) "" else formatDateDisplay(tanggal),
                    onValueChange = {},
                    label = "Tanggal Kawin",
                    icon = Icons.Default.DateRange,
                    readOnly = true
                )
                Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
            }

            Spacer(Modifier.height(32.dp))

            // --- SAVE BUTTON ---
            Button(
                onClick = {
                    if (tanggal.isNotEmpty() && indukan.isNotEmpty()) {
                        val hasil = hitungPerkiraan(tanggal)
                        val data = DataKambing(
                            indukan = indukan,
                            pejantan = pejantan,
                            kandang = kandang,
                            kawin = tanggal,
                            lahir = hasil.first,
                            vaksin = hasil.second,
                            cek = hasil.third
                        )
                        listData = listOf(data) + listData

                        // Reset
                        indukan = ""
                        pejantan = ""
                        kandang = ""
                        tanggal = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Simpan Jadwal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            Spacer(Modifier.height(40.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f)) // Divider terbaru
            Spacer(Modifier.height(20.dp))

            Text(
                text = "Daftar Terkini (${listData.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(Modifier.height(16.dp))
        }

        // --- LIST ITEMS ---
        items(listData) { item ->
            JadwalCard(item)
            Spacer(Modifier.height(16.dp))
        }
    }

    // --- POPUP DATE PICKER ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        tanggal = date.toString()
                    }
                }) { Text("Pilih", color = PrimaryGreen, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal", color = TextGray)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = SurfaceWhite)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = PrimaryGreen,
                    todayDateBorderColor = PrimaryGreen,
                    todayContentColor = PrimaryGreen
                )
            )
        }
    }
}

// --- KOMPONEN PENDUKUNG ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElegantTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    readOnly: Boolean = false
) {
    // Menggunakan colors() yang standar, bukan outlinedTextFieldColors yang lama
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (value.isNotEmpty()) PrimaryGreen else TextGray
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceWhite,
            unfocusedContainerColor = SurfaceWhite,
            disabledContainerColor = SurfaceWhite,
            focusedIndicatorColor = PrimaryGreen,
            unfocusedIndicatorColor = Color.LightGray.copy(alpha = 0.5f),
            focusedLabelColor = PrimaryGreen,
            unfocusedLabelColor = TextGray
        ),
        singleLine = true,
        readOnly = readOnly
    )
}

@Composable
fun JadwalCard(item: DataKambing) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Kartu
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryGreen.copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryGreen)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = item.indukan,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Jantan: ${item.pejantan} • Kdg: ${item.kandang}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            Spacer(Modifier.height(16.dp))

            // Grid Tanggal
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DateInfoItem("Kawin", formatDateDisplay(item.kawin), Icons.Default.DateRange, TextGray)
                DateInfoItem("Vaksin", formatDateDisplay(item.vaksin), Icons.Default.CheckCircle, SecondaryGold)
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DateInfoItem("Cek USG", formatDateDisplay(item.cek), Icons.Default.Info, TextGray)
                DateInfoItem("Lahir", formatDateDisplay(item.lahir), Icons.Default.Home, PrimaryGreen, true)
            }
        }
    }
}

@Composable
fun DateInfoItem(
    label: String,
    date: String,
    icon: ImageVector,
    color: Color,
    isHighlight: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(140.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = color
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = TextGray)
            Text(
                text = date,
                fontSize = 13.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
                color = if (isHighlight) PrimaryGreen else TextDark
            )
        }
    }
}

// --- LOGIC HELPERS ---

fun hitungPerkiraan(tanggal: String): Triple<String, String, String> {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val tgl = LocalDate.parse(tanggal, formatter)
        Triple(
            tgl.plusDays(150).toString(),
            tgl.plusDays(30).toString(),
            tgl.plusDays(90).toString()
        )
    } catch (e: Exception) {
        Triple("-", "-", "-")
    }
}

fun formatDateDisplay(isoDate: String): String {
    if (isoDate.isEmpty() || isoDate == "-") return "-"
    return try {
        val input = LocalDate.parse(isoDate)
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        input.format(outputFormatter)
    } catch (e: Exception) {
        isoDate
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewModernForm() {
    MaterialTheme(colorScheme = lightColorScheme(primary = PrimaryGreen)) {
        ModernFormKambing()
    }
}