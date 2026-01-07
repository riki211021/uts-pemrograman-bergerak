package com.example.jadwalkambing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jadwalkambing.data.database.AppDatabase
import com.example.jadwalkambing.data.entity.DataKambingEntity
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/* ---------- LUXURY THEME COLORS ---------- */
val LuxuryGreen = Color(0xFF1B4D3E)       // Deep Emerald
val LuxuryGold = Color(0xFFD4AF37)        // Muted Gold
val SurfaceWhite = Color(0xFFFFFFFF)      // Pure White
val BackgroundSoft = Color(0xFFF4F6F4)    // Very Light Sage/Grey
val TextPrimary = Color(0xFF1A1C19)
val TextSecondary = Color(0xFF6F7975)
val InputBackground = Color(0xFFF5F5F5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Setup Theme Sederhana
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = LuxuryGreen,
                    background = BackgroundSoft,
                    surface = SurfaceWhite,
                    onSurface = TextPrimary
                )
            ) {
                Scaffold(
                    containerColor = BackgroundSoft,
                    floatingActionButton = {
                        // Opsional: Jika ingin FAB, tapi tombol simpan sudah ada di form
                    }
                ) { padding ->
                    ModernFormKambing(Modifier.padding(padding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernFormKambing(modifier: Modifier = Modifier) {
    // --- STATE (Tidak berubah) ---
    var indukan by remember { mutableStateOf("") }
    var pejantan by remember { mutableStateOf("") }
    var kandang by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var editId by remember { mutableStateOf<Int?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val dao = db.kambingDao()
    val scope = rememberCoroutineScope()
    val listData by dao.getAll().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // --- HEADER SECTION ---
        item {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Text(
                    text = "Breeding Tracker",
                    color = LuxuryGold,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Manajemen\nTernak Modern",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = LuxuryGreen
                    ),
                    lineHeight = 40.sp
                )
            }
        }

        // --- FORM SECTION (Clean Card Look) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (editId == null) "Input Data Baru" else "Edit Data",
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontSize = 16.sp
                    )

                    LuxuryInput(
                        value = indukan,
                        onValueChange = { indukan = it },
                        label = "Kode Indukan",
                        icon = Icons.Default.Face
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            LuxuryInput(
                                value = pejantan,
                                onValueChange = { pejantan = it },
                                label = "Pejantan",
                                icon = Icons.Default.Face                             // Ganti icon biar variatif
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            LuxuryInput(
                                value = kandang,
                                onValueChange = { kandang = it },
                                label = "Kandang",
                                icon = Icons.Default.Home

                            )
                        }
                    }

                    // Date Picker Custom
                    LuxuryDateSelector(
                        selectedDate = tanggal,
                        onClick = { showDatePicker = true }
                    )

                    // Action Button
                    Button(
                        onClick = {
                            if (indukan.isNotEmpty() && tanggal.isNotEmpty()) {
                                val hasil = hitungPerkiraan(tanggal)
                                val entity = DataKambingEntity(
                                    id = editId ?: 0,
                                    indukan = indukan,
                                    pejantan = pejantan,
                                    kandang = kandang,
                                    kawin = tanggal,
                                    lahir = hasil.first,
                                    vaksin = hasil.second,
                                    cek = hasil.third
                                )
                                scope.launch {
                                    if (editId == null) dao.insert(entity) else dao.update(entity)
                                }
                                // Reset
                                indukan = ""; pejantan = ""; kandang = ""; tanggal = ""; editId = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxuryGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (editId == null) Icons.Rounded.Add else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (editId == null) "Simpan Jadwal" else "Perbarui Data",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // --- LIST HEADER ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Ternak (${listData.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // --- LIST ITEMS ---
        items(listData) { item ->
            LuxuryInfoCard(
                item = item,
                onEdit = {
                    indukan = item.indukan
                    pejantan = item.pejantan
                    kandang = item.kandang
                    tanggal = item.kawin
                    editId = item.id
                },
                onDelete = { scope.launch { dao.delete(item) } }
            )
        }
    }

    // --- DATE PICKER DIALOG (Logic Tetap) ---
    if (showDatePicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            colors = DatePickerDefaults.colors(containerColor = SurfaceWhite),
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    state.selectedDateMillis?.let { millis ->
                        tanggal = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate().toString()
                    }
                }) { Text("Pilih", color = LuxuryGreen, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal", color = TextSecondary) }
            }
        ) {
            DatePicker(state = state, colors = DatePickerDefaults.colors(
                selectedDayContainerColor = LuxuryGreen,
                todayDateBorderColor = LuxuryGreen,
                todayContentColor = LuxuryGreen
            ))
        }
    }
}

/* ---------- CUSTOM UI COMPONENTS (The "Luxury" Look) ---------- */

@Composable
fun LuxuryInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = LuxuryGreen.copy(alpha = 0.7f)) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Transparent, RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            disabledContainerColor = InputBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedLabelColor = LuxuryGreen,
            unfocusedLabelColor = TextSecondary
        ),
        singleLine = true
    )
}

@Composable
fun LuxuryDateSelector(
    selectedDate: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(InputBackground)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = "Tanggal Kawin",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (selectedDate.isEmpty()) "Pilih Tanggal..." else formatDateDisplay(selectedDate),
                style = MaterialTheme.typography.bodyLarge,
                color = if (selectedDate.isEmpty()) Color.Gray else TextPrimary,
                fontWeight = if (selectedDate.isEmpty()) FontWeight.Normal else FontWeight.SemiBold
            )
        }
        Icon(
            imageVector = Icons.Outlined.DateRange,
            contentDescription = null,
            tint = LuxuryGreen
        )
    }
}

@Composable
fun LuxuryInfoCard(
    item: DataKambingEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.LightGray.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = LuxuryGreen.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.indukan.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = LuxuryGreen
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.indukan,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Kandang ${item.kandang} • ${item.pejantan}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                // Action Buttons (Minimalist)
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = LuxuryGold)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color(0xFFE57373))
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            // Timeline Info Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DateInfoItem("Kawin", item.kawin, Color(0xFF607D8B))
                DateInfoItem("Vaksin", item.vaksin, LuxuryGold)
                DateInfoItem("Lahir", item.lahir, LuxuryGreen, isHighlight = true)
            }
        }
    }
}

@Composable
fun DateInfoItem(label: String, date: String, color: Color, isHighlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = formatDateDisplayShort(date), // Helper baru untuk tampilan pendek
            fontSize = if (isHighlight) 15.sp else 14.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
            color = color
        )
    }
}

/* ---------- LOGIC (Tidak Berubah, hanya helper format display ditambah) ---------- */

fun hitungPerkiraan(tanggal: String): Triple<String, String, String> {
    val tgl = LocalDate.parse(tanggal)
    return Triple(
        tgl.plusDays(150).toString(), // lahir
        tgl.plusDays(30).toString(),  // vaksin
        tgl.plusDays(90).toString()   // cek
    )
}

fun formatDateDisplay(date: String): String {
    return try {
        LocalDate.parse(date).format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    } catch (e: Exception) { date }
}

fun formatDateDisplayShort(date: String): String {
    return try {
        LocalDate.parse(date).format(DateTimeFormatter.ofPattern("dd MMM yy"))
    } catch (e: Exception) { date }
}