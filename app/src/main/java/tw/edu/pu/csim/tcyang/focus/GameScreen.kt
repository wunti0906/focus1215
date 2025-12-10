package tw.edu.pu.csim.tcyang.focus

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun GameScreen(level: String, onBackToMenu: () -> Unit) {
    val colors = listOf(Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFFF9800))

    data class Config(val time: Int, val size: Dp, val distractors: Int, val interval: Long)
    val config = when (level) {
        "易" -> Config(60, 110.dp, 3, 1800L)
        "中" -> Config(45, 90.dp, 6, 1200L)
        else -> Config(30, 70.dp, 9, 800L)
    }

    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(config.time) }
    var playing by remember { mutableStateOf(true) }
    var target by remember { mutableStateOf(Offset.Zero) }
    var distractors by remember { mutableStateOf(listOf<Offset>()) }
    var targetColor by remember { mutableStateOf(colors.random()) }

    // 倒數計時
    LaunchedEffect(playing) {
        while (timeLeft > 0 && playing) {
            delay(1000L)
            timeLeft--
        }
        playing = false
    }

    // 產生目標
    LaunchedEffect(playing) {
        while (playing) {
            delay(config.interval)
            val x = Random.nextFloat() * 800f + 200f
            val y = Random.nextFloat() * 1200f + 400f
            target = Offset(x, y)
            targetColor = colors.random()

            val list = mutableListOf<Offset>()
            repeat(config.distractors) {
                val angle = Random.nextFloat() * 360f
                val dist = 160f + Random.nextFloat() * 140f
                val dx = kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat() * dist
                val dy = kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat() * dist
                list.add(Offset(x + dx, y + dy))
            }
            distractors = list
        }
    }

    // 閃爍動畫
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 0.9f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse)
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFB3E5FC))) {

        // 【修正 1：將 Canvas 移到最前/最底層】
        // 確保點擊偵測不會蓋住上層的按鈕和 UI 元素
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(playing) {
                    if (!playing) return@pointerInput
                    detectTapGestures { offset ->
                        val d = sqrt((offset.x - target.x).pow(2) + (offset.y - target.y).pow(2))
                        if (d < config.size.value * 1.3f) {
                            score += when (level) { "易" -> 10; "中" -> 20; else -> 30 }
                        }
                    }
                }
        ) {
            if (playing) {
                // 目標（閃爍）
                drawCircle(
                    color = targetColor,
                    radius = config.size.toPx() * scale / 2,
                    center = target
                )
                // 干擾物（灰色半透明）
                distractors.forEach {
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.4f),
                        radius = config.size.toPx() * 0.6f,
                        center = it
                    )
                }
            }
        }
        // -------------------------------------------------------------

        // 返回 + 難度標籤 (現在位於 Canvas 上方，可點擊)
        Row(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToMenu,
                modifier = Modifier.background(Color(0xFF42A5F5), CircleShape)
            ) {
                Text("返回", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Card(
                colors = CardDefaults.cardColors(Color(0xFF42A5F5)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(" $level ", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            }
        }

        // 計時器
        Text(
            text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            modifier = Modifier.align(Alignment.TopEnd).padding(24.dp)
        )

        // 分數
        Card(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp),
            colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.9f)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Text(
                " 分數：$score ",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
            )
        }

        // 遊戲結束
        if (!playing) {
            Card(
                modifier = Modifier.align(Alignment.Center).size(360.dp, 480.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("遊戲結束！", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                    Spacer(Modifier.height(32.dp))
                    Text("最終得分", fontSize = 32.sp, color = Color.Gray)
                    Text("$score 分", fontSize = 80.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
                    Spacer(Modifier.height(48.dp))
                    Button(
                        onClick = onBackToMenu,
                        modifier = Modifier.width(260.dp).height(70.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("回到選單", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}