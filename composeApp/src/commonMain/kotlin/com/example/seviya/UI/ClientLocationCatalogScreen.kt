package com.example.seviya.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.theme.AppBackground
import com.example.seviya.theme.AvatarBlueSoft
import com.example.seviya.theme.BlueGrayText
import com.example.seviya.theme.BorderSoft
import com.example.seviya.theme.BrandBlue
import com.example.seviya.theme.BrandRed
import com.example.seviya.theme.CardSurface
import com.example.seviya.theme.DividerSoft
import com.example.seviya.theme.SubtitleOnBlue
import com.example.seviya.theme.TextPrimary
import com.example.seviya.theme.TextSecondary
import com.example.seviya.theme.White
import com.example.shared.domain.entity.Address
import com.example.shared.presentation.clientLocationCatalog.ClientLocationCatalogViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.Briefcase
import compose.icons.tablericons.Pencil
import compose.icons.tablericons.Home
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Trash
import compose.icons.tablericons.X
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientLocationCatalogScreen(
    clientId: String,
    viewModel: ClientLocationCatalogViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var showSheet by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<Address?>(null) }

    LaunchedEffect(clientId) {
        viewModel.loadAddresses(clientId)
    }

    Scaffold(
        containerColor = AppBackground
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // --- Header ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = BrandBlue,
                            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                        .systemBarsPadding()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.2f))
                            .clickable { onBack() }
                            .align(Alignment.CenterStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = TablerIcons.ArrowLeft,
                            contentDescription = "Volver",
                            tint = White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Mis Ubicaciones",
                        color = White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // --- Content ---
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandBlue)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.addresses.isEmpty()) {
                            item {
                                EmptyLocationsState()
                            }
                        } else {
                            items(uiState.addresses) { address ->
                                LocationCard(
                                    address = address,
                                    onEdit = {
                                        editingAddress = address
                                        showSheet = true
                                    },
                                    onDelete = {
                                        viewModel.deleteAddress(clientId, address.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // --- FAB: Agregar ubicación ---
            Button(
                onClick = {
                    editingAddress = null
                    showSheet = true
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp, start = 32.dp, end = 32.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "+ Agregar Nueva Ubicación",
                    color = White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // --- Bottom Sheet: Agregar / Editar ---
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = CardSurface
        ) {
            AddEditLocationForm(
                initial = editingAddress,
                isSaving = uiState.isSaving,
                onSave = { address ->
                    viewModel.saveAddress(clientId, address)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showSheet = false
                    }
                },
                onCancel = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showSheet = false
                    }
                }
            )
        }
    }
}

@Composable
private fun LocationCard(
    address: Address,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .background(CardSurface, RoundedCornerShape(20.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon background
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AvatarBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (address.alias.lowercase().contains("trabajo") ||
                    address.alias.lowercase().contains("oficina")
                ) TablerIcons.Briefcase else TablerIcons.Home,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = address.alias.ifBlank { "Sin nombre" },
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildString {
                    if (address.district.isNotBlank()) append(address.district)
                    if (address.district.isNotBlank() && address.province.isNotBlank()) append(", ")
                    if (address.province.isNotBlank()) append(address.province)
                }.ifBlank { "Sin dirección" },
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (address.reference.isNotBlank()) {
                Text(
                    text = address.reference,
                    color = BlueGrayText,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Edit button
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable { onEdit() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = TablerIcons.Pencil,
                contentDescription = "Editar",
                tint = BrandBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        // Delete button
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = TablerIcons.Trash,
                contentDescription = "Eliminar",
                tint = BrandRed,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyLocationsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(AvatarBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = TablerIcons.MapPin,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sin ubicaciones guardadas",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Agrega tus direcciones frecuentes para solicitar citas más rápido.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun AddEditLocationForm(
    initial: Address?,
    isSaving: Boolean,
    onSave: (Address) -> Unit,
    onCancel: () -> Unit
) {
    val isEditing = initial != null

    var alias by remember(initial) { mutableStateOf(initial?.alias ?: "") }
    var province by remember(initial) { mutableStateOf(initial?.province ?: "") }
    var district by remember(initial) { mutableStateOf(initial?.district ?: "") }
    var canton by remember(initial) { mutableStateOf(initial?.canton ?: "") }
    var reference by remember(initial) { mutableStateOf(initial?.reference ?: "") }
    var latitude by remember(initial) { mutableStateOf(initial?.latitude ?: 0.0) }
    var longitude by remember(initial) { mutableStateOf(initial?.longitude ?: 0.0) }
    var locationLabel by remember(initial) {
        mutableStateOf(
            if ((initial?.latitude ?: 0.0) != 0.0) "%.5f, %.5f".format(initial!!.latitude, initial.longitude)
            else "Sin ubicación GPS"
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sheet handle / title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isEditing) "Editar Ubicación" else "Nueva Ubicación",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppBackground)
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = TablerIcons.X,
                    contentDescription = "Cerrar",
                    tint = BlueGrayText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Step 1: Map placeholder
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
                .background(CardSurface, RoundedCornerShape(20.dp))
                .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BrandBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SELECCIONAR EN MAPA",
                    color = BrandBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AvatarBlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = TablerIcons.MapPin,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = locationLabel,
                        color = BlueGrayText,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CurrentLocationButton(
                onLocationObtained = { lat, lng, prov, cant, dist ->
                    latitude = lat
                    longitude = lng
                    locationLabel = "%.5f, %.5f".format(lat, lng)
                    if (prov.isNotBlank()) province = prov
                    if (cant.isNotBlank()) canton = cant
                    if (dist.isNotBlank()) district = dist
                },
                onError = {
                    locationLabel = "No se pudo obtener la ubicación"
                }
            )
        }

        // Step 2: Form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
                .background(CardSurface, RoundedCornerShape(20.dp))
                .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BrandBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DETALLES DE LA UBICACIÓN",
                    color = BrandBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            FormField(
                label = "NOMBRE / ALIAS",
                value = alias,
                onValueChange = { alias = it },
                placeholder = "Ej. Mi Casa, Oficina, Gimnasio"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FormField(
                        label = "PROVINCIA",
                        value = province,
                        onValueChange = { province = it },
                        placeholder = "Ej. San José"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    FormField(
                        label = "CANTÓN",
                        value = canton,
                        onValueChange = { canton = it },
                        placeholder = "Ej. Escazú"
                    )
                }
            }

            FormField(
                label = "DISTRITO",
                value = district,
                onValueChange = { district = it },
                placeholder = "Ej. San Rafael"
            )

            FormField(
                label = "REFERENCIAS",
                value = reference,
                onValueChange = { reference = it },
                placeholder = "Ej. Portón blanco frente al parque",
                minLines = 3
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (latitude == 0.0 || longitude == 0.0) {
            Text(
                text = "Debes usar tu ubicación actual para poder guardar",
                color = BrandRed,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                onSave(
                    Address(
                        id = initial?.id ?: "",
                        alias = alias.trim(),
                        province = province.trim(),
                        district = district.trim(),
                        canton = canton.trim(),
                        reference = reference.trim(),
                        latitude = latitude,
                        longitude = longitude,
                        isDefault = initial?.isDefault ?: false
                    )
                )
            },
            enabled = alias.isNotBlank() && latitude != 0.0 && longitude != 0.0 && !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(color = White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (isEditing) "Actualizar Ubicación" else "Guardar Ubicación",
                    color = White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = BlueGrayText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, color = BlueGrayText, fontSize = 13.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            maxLines = if (minLines > 1) 5 else 1,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandBlue,
                unfocusedBorderColor = BorderSoft,
                focusedContainerColor = White,
                unfocusedContainerColor = AppBackground,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}
