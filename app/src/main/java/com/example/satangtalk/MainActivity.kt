package com.example.satangtalk


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.satangtalk.databinding.ActivityMainBinding
import kotlin.math.abs
import kotlinx.coroutines.launch


data class Character(var x: Float, var y: Float)

// 캐릭터가 병원 근처에 있는지 확인하는 함수
fun isCharacterNearHospital(character: Character): Boolean {
    val hospitalLeft = 36f //왼쪽 여백
    val hospitalTop = 128f // 위쪽 여백
    val hospitalWidth = 100f // 너비
    val hospitalHeight = 100f // 높이
    val nearRange = 50f // 근처 범위


    val hospitalCenterX = hospitalLeft + hospitalWidth / 2
    val hospitalCenterY = hospitalTop + hospitalHeight / 2

    return abs(character.x - hospitalCenterX) < nearRange && abs(character.y - hospitalCenterY) < nearRange
}

@Composable
fun Joystick(
    onDirectionChanged: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var touchX by remember { mutableStateOf(0f) } // 수정
    var touchY by remember { mutableStateOf(0f) } // 수정

    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    touchX = (change.position.x - 25.dp.toPx()).coerceIn(-25f..25f)
                    touchY = (change.position.y - 25.dp.toPx()).coerceIn(-25f..25f)
                    onDirectionChanged(touchX / 25f, touchY / 25f)
                }
            }
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .pointerInput(Unit) {
                }.offset { IntOffset(touchX.toInt(), touchY.toInt()) }
        )
    }
}



@Composable
fun CharacterView(character: Character) {
    Image(
        painter = painterResource(id = R.drawable.hobbyduck1),
        contentDescription = "Character",
        modifier = Modifier
            .size(100.dp)
            .offset { IntOffset(character.x.toInt() - 50, character.y.toInt() - 50) }
    )
}

@Composable
fun GameScreen() {
    var characterX by remember { mutableStateOf(450f) } // 수정
    var characterY by remember { mutableStateOf(1300f) }
    val character = Character(characterX, characterY)
    val coroutineScope= rememberCoroutineScope()
    val context = LocalContext.current


    Box(modifier = Modifier.fillMaxSize().background(Color.Yellow)) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "background",
            modifier = Modifier.fillMaxSize()
        )


        Image(
            painter = painterResource(id = R.drawable.hospital),
            contentDescription = "hospital",
            modifier = Modifier
                .size(100.dp)
                .offset(36.dp, 128.dp)

        )

        Image(
            painter = painterResource(id = R.drawable.coffeeshop),
            contentDescription = "coffeeshop",
            modifier = Modifier
                .size(100.dp)
                .offset(270.dp, 129.dp)
        )


        Image(
            painter = painterResource(id = R.drawable.convenience_store),
            contentDescription = "convenience_store",
            modifier = Modifier
                .size(100.dp)
                .offset(38.dp, 315.dp)
        )


        Image(
            painter = painterResource(id = R.drawable.myhouse),
            contentDescription = "myhouse",
            modifier = Modifier
                .size(100.dp)
                .offset(270.dp, 315.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            CharacterView(character)

            Joystick(
                modifier = Modifier
                    .align(Alignment.BottomStart) // 왼쪽 아래로 정렬
                    .padding(16.dp), // 여백 추가
                onDirectionChanged = { x, y ->
                    characterX += (x * 10)// 캐릭터의 X 좌표 업데이트
                    characterY += (y * 10) // 캐릭터의 Y 좌표 업데이트
                }
            )

            LaunchedEffect(character.x, character.y) {
                if (isCharacterNearHospital(character)) {
                    coroutineScope.launch {
                        val intent = Intent(context, HospitalActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private var character = mutableStateOf(Character(100f, 100f))
    private var startX = 0f
    private var startY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            GameScreen()
        }
    }

}





