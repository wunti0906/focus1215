package tw.edu.pu.csim.tcyang.focus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tw.edu.pu.csim.tcyang.focus.ui.theme.FocusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf("menu") }
    var selectedLevel by remember { mutableStateOf("易") }

    when (currentScreen) {
        "menu" -> MainMenuScreen(
            onStartGame = { level ->
                selectedLevel = level
                currentScreen = "game"
            },
            onOpenRules = { currentScreen = "rules" }
        )
        "game" -> {
            BackHandler(enabled = currentScreen == "game") {
                currentScreen = "menu"
            }
            GameScreen(
                level = selectedLevel,
                onBackToMenu = { currentScreen = "menu" }
            )
        }
        "rules" -> {
            BackHandler(enabled = currentScreen == "rules") {
                currentScreen = "menu"
            }
            RuleScreen(
                onBack = { currentScreen = "menu" }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(onStartGame: (String) -> Unit, onOpenRules: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFFB3E5FC), // 淺藍色背景
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onOpenRules,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF1E90FF), RoundedCornerShape(12.dp)) // 深藍色按鈕
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "遊戲規則",
                        tint = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "目光遊戲",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xFF1E90FF), RoundedCornerShape(16.dp)) // 深藍色標題背景
                    .padding(horizontal = 36.dp, vertical = 18.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            listOf("易", "中", "難").forEach { level ->
                Button(
                    onClick = { onStartGame(level) },
                    modifier = Modifier
                        .width(240.dp)
                        .height(80.dp)
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E90FF)), // 深藍色主按鈕
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = level, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFFB3E5FC) // 淺藍色背景
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 返回按鈕
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF1E90FF), RoundedCornerShape(12.dp)) // 深藍色按鈕
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // 標題
            Text(
                text = "遊戲規則",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 60.dp)
            )

            // 規則文字
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "本遊戲是在指定時間內，點擊畫面中閃爍的目標圓點來獲取分數。",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    lineHeight = 40.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}