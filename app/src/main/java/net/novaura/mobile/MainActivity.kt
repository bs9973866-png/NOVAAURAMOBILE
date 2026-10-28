package net.novaura.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private val Space = Color(0xFF080611)
private val Panel = Color(0xFF171128)
private val Purple = Color(0xFF8B5CF6)
private val Pink = Color(0xFFFF4FD8)
private val Muted = Color(0xFFA9A0BD)
private val White = Color(0xFFF7F3FF)

private data class MinecraftVersion(val id: String, val type: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Space) {
                    NovauraHome()
                }
            }
        }
    }
}

@Composable
private fun NovauraHome() {
    var versions by remember { mutableStateOf<List<MinecraftVersion>>(emptyList()) }
    var versionSearch by remember { mutableStateOf("") }
    var versionFilter by remember { mutableStateOf("release") }
    var selectedVersion by remember { mutableStateOf("Загрузка каталога…") }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showVersions by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Главная") }
    val scope = rememberCoroutineScope()

    suspend fun refreshVersions() {
        loading = true
        error = null
        try {
            val result = withContext(Dispatchers.IO) {
                val connection = (URL("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")
                    .openConnection() as HttpURLConnection).apply {
                    connectTimeout = 12000
                    readTimeout = 12000
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                }
                try {
                    if (connection.responseCode !in 200..299) throw IllegalStateException("HTTP ${connection.responseCode}")
                    val body = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(body)
                    val items = json.getJSONArray("versions")
                    buildList {
                        for (i in 0 until items.length()) {
                            val item = items.getJSONObject(i)
                            val id = item.optString("id")
                            val type = item.optString("type")
                            if (id.isNotBlank() && type in setOf("release", "snapshot", "old_beta", "old_alpha")) {
                                add(MinecraftVersion(id, type))
                            }
                        }
                    }
                } finally {
                    connection.disconnect()
                }
            }
            versions = result
            if (result.none { it.id == selectedVersion }) {
                selectedVersion = result.firstOrNull { it.type == "release" }?.id ?: result.firstOrNull()?.id ?: "Нет версий"
            }
        } catch (e: Exception) {
            error = "Не удалось получить каталог Minecraft. Проверь интернет и повтори попытку."
            if (versions.isEmpty()) selectedVersion = "Каталог недоступен"
        } finally {
            loading = false
        }
    }

    LaunchedEffect(Unit) { refreshVersions() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF17102C), Space, Color(0xFF090816))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(46.dp).background(
                        Brush.linearGradient(listOf(Pink, Purple, Color(0xFF4F7DFF))),
                        RoundedCornerShape(15.dp)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("N", color = White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("NOVAURA", color = White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Text("MOBILE LAUNCHER", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
                }
                Box(Modifier.size(10.dp).background(Color(0xFF54F1E5), CircleShape))
            }

            Column(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.linearGradient(listOf(Color(0xFF24163D), Panel, Color(0xFF11162D))),
                    RoundedCornerShape(26.dp)
                ).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("YOUR UNIVERSE.", color = Pink, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text("Minecraft,\nна твоих условиях.", color = White, fontSize = 29.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold)
                Text("Создавай профили. Настраивай игру. Исследуй космос.", color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("ВЫБРАННАЯ ВЕРСИЯ", color = Muted, fontSize = 9.sp, letterSpacing = 1.sp)
                        Text(selectedVersion, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Vanilla • Java Edition", color = Muted, fontSize = 11.sp)
                    }
                    TextButton(onClick = { showVersions = !showVersions }) {
                        Text(if (showVersions) "Скрыть" else "Версии", color = Pink)
                    }
                }
                Button(
                    onClick = { showVersions = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple, contentColor = White)
                ) {
                    androidx.compose.material3.Icon(Icons.Default.SportsEsports, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text("ИГРАТЬ", fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                }
                Text("Этап 1: интерфейс и каталог версий. Запуск игрового движка ещё не подключён.", color = Muted, fontSize = 10.sp)
            }

            if (showVersions) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("КАТАЛОГ MINECRAFT", color = White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    TextButton(onClick = { scope.launch { refreshVersions() } }) { Text("Обновить", color = Pink) }
                }
                OutlinedTextField(
                    value = versionSearch,
                    onValueChange = { versionSearch = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Поиск версии", color = Muted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = Pink,
                        unfocusedBorderColor = Color(0xFF514365),
                        cursorColor = Pink
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("release" to "Релизы", "snapshot" to "Снапшоты", "old" to "Beta / Alpha").forEach { (key, label) ->
                        TextButton(onClick = { versionFilter = key }) {
                            Text(label, color = if (versionFilter == key) Pink else Muted, fontSize = 11.sp)
                        }
                    }
                }
                when {
                    loading -> Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Pink, strokeWidth = 2.dp)
                        Spacer(Modifier.width(10.dp))
                        Text("Получаю официальный манифест Mojang…", color = Muted, fontSize = 12.sp)
                    }
                    error != null -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(error!!, color = Color(0xFFFF9AAE), fontSize = 12.sp)
                        TextButton(onClick = { scope.launch { refreshVersions() } }) { Text("Повторить", color = Pink) }
                    }
                    else -> {
                        val visibleVersions = versions.filter { version ->
                            val typeMatches = when (versionFilter) {
                                "snapshot" -> version.type == "snapshot"
                                "old" -> version.type == "old_beta" || version.type == "old_alpha"
                                else -> version.type == "release"
                            }
                            typeMatches && version.id.contains(versionSearch.trim(), ignoreCase = true)
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Найдено: ${visibleVersions.size}", color = Muted, fontSize = 10.sp)
                            LazyColumn(
                                modifier = Modifier.weight(1f, fill = false).height(220.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(visibleVersions.take(250), key = { it.id }) { version ->
                                    val active by animateColorAsState(if (version.id == selectedVersion) Color(0xFF382451) else Panel, label = "versionColor")
                                    Row(
                                        modifier = Modifier.fillMaxWidth().background(active, RoundedCornerShape(12.dp))
                                            .clickable { selectedVersion = version.id; showVersions = false }
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text(version.id, color = White, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                when (version.type) {
                                                    "snapshot" -> "Снапшот"
                                                    "old_beta" -> "Старая Beta"
                                                    "old_alpha" -> "Старая Alpha"
                                                    else -> "Релиз"
                                                },
                                                color = Muted, fontSize = 10.sp
                                            )
                                        }
                                        if (version.id == selectedVersion) Text("ВЫБРАНО", color = Pink, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text("БЫСТРЫЙ ДОСТУП", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickAction("Версии", Icons.Default.CloudDownload, Modifier.weight(1f)) { showVersions = true }
                    QuickAction("Моды", Icons.Default.Extension, Modifier.weight(1f)) { selectedTab = "Моды" }
                    QuickAction("Настройки", Icons.Default.Settings, Modifier.weight(1f)) { selectedTab = "Настройки" }
                    QuickAction("Ишка", Icons.Default.AutoAwesome, Modifier.weight(1f)) { selectedTab = "Ишка" }
                }
                if (selectedTab != "Главная") {
                    Text(selectedTab, color = Pink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        when (selectedTab) {
                            "Моды" -> "Менеджер модов будет подключён следующим этапом."
                            "Настройки" -> "Здесь появятся настройки графики, памяти и оформления."
                            else -> "ИИ-помощник будет работать через настраиваемый API; ключи не хранятся в APK."
                        },
                        color = Muted, fontSize = 12.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                Text("NOVAURA • EARLY DEVELOPMENT BUILD", color = Color(0xFF625977), fontSize = 9.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun QuickAction(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable(onClick = onClick)
            .background(Panel, RoundedCornerShape(16.dp)).padding(vertical = 14.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.material3.Icon(icon, contentDescription = label, tint = Pink, modifier = Modifier.size(22.dp))
        Text(label, color = White, fontSize = 10.sp, maxLines = 1)
    }
}
