package tw.edu.pu.csim.tcyang.focus

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta)

    data class Config(val time: Int, val size: Dp, val distractors: Int, val interval: Long)
    val config = when (level) {
        "易" -> Config(30, 130.dp, 2, 2000L)
        "中" -> Config(25, 100.dp, 4, 1500L)
        else -> Config(20, 80.dp, 6, 1000L)
    }

    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(config.time) }
    var playing by remember { mutableStateOf(true) }
    var target by remember { mutableStateOf(Offset.Zero) }
    var distractors by remember { mutableStateOf(listOf<Offset>()) }
    var targetColor by remember { mutableStateOf(Color.Red) }

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
            val x = Random.nextFloat() * 900f + 150f
            val y = Random.nextFloat() * 1400f + 300f
            target = Offset(x, y)
            targetColor = colors.random()

            val list = mutableListOf<Offset>()
            repeat(config.distractors) {
                val angle = Random.nextFloat() * 360f
                val dist = 200f + Random.nextFloat() * 120f
                val dx = kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat() * dist
                val dy = kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat() * dist
                list.add(Offset(x + dx, y + dy))
            }
            distractors = list
        }
    }

    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB))
            .pointerInput(playing) {
                if (!playing) return@pointerInput
                detectTapGestures { offset ->
                    val distance = sqrt((offset.x - target.x).pow(2) + (offset.y - target.y).pow(2))
                    if (distance < config.size.value * 1.4f) {
                        score += when (level) {
                            "易" -> 10
                            "中" -> 20
                            else -> 30
                        }
                    }
                }
            }
    ) {
        // 返回按鈕
        IconButton(
            onClick = onBackToMenu,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(56.dp)
                .background(Color(0xFF42A5F5), CircleShape)
        ) {
            Text("返回", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }

        // 分數與時間
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("分數：$score", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(12.dp))
            Text("剩餘 $timeLeft 秒", fontSize = 32.sp, color = Color.White)
        }

        // 畫目標與干擾物
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (playing) {
                drawCircle(
                    color = targetColor,
                    radius = config.size.toPx() * scale / 2,
                    center = target
                )
                distractors.forEach { pos ->
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.6f),
                        radius = config.size.toPx() / 3f,
                        center = pos
                    )
                }
            }
        }

        // 遊戲結束畫面
        if (!playing) {
            Card(
                modifier = Modifier.align(Alignment.Center).size(340.dp, 420.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("遊戲結束！", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                    Spacer(Modifier.height(30.dp))
                    Text("最終得分", fontSize = 28.sp, color = Color.Gray)
                    Text("$score 分", fontSize = 72.sp, fontWeight = FontWeight.Bold, color = Color(0xFF42A5F5))
                    Spacer(Modifier.height(50.dp))
                    Button(
                        onClick = onBackToMenu,
                        modifier = Modifier.width(240.dp).height(70.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("回到選單", fontSize = 28.sp, color = Color.White)
                    }
                }
            }
        }
    }
}