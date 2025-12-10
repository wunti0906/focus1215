package tw.edu.pu.csim.tcyang.focus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler // 確保有這個引入
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
        "menu" -> MainMenuScreen { level ->
            selectedLevel = level
            currentScreen = "game"
        }
        "game" -> {
            // 【修正 2：新增 BackHandler 支援系統返回鍵】
            BackHandler(enabled = currentScreen == "game") {
                currentScreen = "menu"
            }

            GameScreen(
                level = selectedLevel,
                onBackToMenu = { currentScreen = "menu" }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(onStartGame: (String) -> Unit) {
    Scaffold(
        containerColor = Color(0xFFBBDEFB),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { /* 設定功能 */ },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF6BB6FF), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_preferences),
                        contentDescription = "設定",
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
                    .background(Color(0xFF42A5F5), RoundedCornerShape(16.dp))
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = level, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}