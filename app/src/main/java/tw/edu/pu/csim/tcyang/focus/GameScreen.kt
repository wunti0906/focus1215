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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun GameScreen(level: String, onBackToMenu: () -> Unit) {
    val colors = listOf(
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF2196F3),
        Color(0xFF4CAF50), Color(0xFFFF9800)
    )

    data class Config(
        val time: Int,
        val size: Dp,
        val distractors: Int,
        val interval: Long,
        val points: Int
    )

    val config = when (level) {
        "易" -> Config(60, 110.dp, 3, 1800L, 10)
        "中" -> Config(45, 90.dp, 6, 1200L, 20)
        else -> Config(30, 70.dp, 9, 800L, 30)
    }

    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(config.time) }
    var isPlaying by remember { mutableStateOf(true) }

    var target by remember { mutableStateOf<Offset?>(null) }
    var distractors by remember { mutableStateOf(emptyList<Offset>()) }
    var targetColor by remember { mutableStateOf<Color?>(null) }

    // === 把 spawnNewTarget 函數移到這裡（LaunchedEffect 之前）===
    fun spawnNewTarget() {
        val x = Random.nextFloat() * 800f + 200f
        val y = Random.nextFloat() * 1200f + 400f

        target = Offset(x, y)
        targetColor = colors.random()

        val list = mutableListOf<Offset>()
        repeat(config.distractors) {
            val angle = Random.nextFloat() * 360f
            val dist = 160f + Random.nextFloat() * 140f
            val dx = cos(Math.toRadians(angle.toDouble())).toFloat() * dist
            val dy = sin(Math.toRadians(angle.toDouble())).toFloat() * dist
            list.add(Offset(x + dx, y + dy))
        }
        distractors = list
    }
    // ============================================================

    // 遊戲倒數計時
    LaunchedEffect(isPlaying) {
        while (timeLeft > 0 && isPlaying) {
            delay(1000L)
            timeLeft--
        }
        isPlaying = false
    }

    // 定時產生目標（現在可以正常呼叫 spawnNewTarget）
    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect

        // 立刻產生第一顆
        spawnNewTarget()

        // 定時循環
        while (true) {
            delay(config.interval)
            if (!isPlaying) break
            spawnNewTarget()
        }
    }

    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 0.9f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse)
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFB3E5FC))) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isPlaying) {
                    if (!isPlaying) return@pointerInput
                    detectTapGestures { offset ->
                        val currentTarget = target ?: return@detectTapGestures
                        val distance = sqrt((offset.x - currentTarget.x).pow(2) + (offset.y - currentTarget.y).pow(2))
                        if (distance < config.size.toPx() * 0.75f) {
                            score += config.points
                            spawnNewTarget()  // 點中立刻刷新
                        }
                    }
                }
        ) {
            if (isPlaying && target != null && targetColor != null) {
                drawCircle(
                    color = targetColor!!,
                    radius = config.size.toPx() * scale / 2,
                    center = target!!
                )
                distractors.forEach { pos ->
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.4f),
                        radius = config.size.toPx() * 0.6f,
                        center = pos
                    )
                }
            }
        }

        // UI 元素（返回、難度、計時、分數）
        Row(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToMenu,
                modifier = Modifier.background(Color(0xFF1E90FF), CircleShape)
            ) {
                Text("返回", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Card(
                colors = CardDefaults.cardColors(Color(0xFF1E90FF)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(" $level ", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            }
        }

        Text(
            text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            modifier = Modifier.align(Alignment.TopEnd).padding(24.dp)
        )

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
        if (!isPlaying) {
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