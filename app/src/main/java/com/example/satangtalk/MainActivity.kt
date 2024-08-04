package com.example.satangtalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.satangtalk.databinding.ActivityMainBinding
import com.example.satangtalk.ui.theme.SatangTalkTheme
import kotlin.math.abs

data class Character(var x: Float, var y: Float)

@Composable
fun CharacterView(character: MutableState<Character>) {
    var offsetX by remember { mutableStateOf(100f) }
    var offsetY by remember { mutableStateOf(100f) }


    Image(
        painter = painterResource(id = R.drawable.hobbyduck1),
        contentDescription = "Character",
        modifier = Modifier
            .fillMaxSize()
            .size(50.dp)
            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    character.value = Character(offsetX, offsetY)
                }
            }
    )
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
            CharacterView(character = character)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 터치 시작 지점 저장
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                // 터치 이동 거리 계산
                val dx = event.x - startX
                val dy = event.y - startY

                // 캐릭터 위치 업데이트
                character.value = Character(character.value.x + dx, character.value.y + dy)
                Log.d("Character", "Character position: ${character.value.x}, ${character.value.y}")

                // 터치 시작 지점 업데이트
                startX = event.x
                startY = event.y

                if (isCharacterNearHospital(character.value)) {
                    val intent = Intent(this, HospitalActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return true
    }

    // 캐릭터가 병원 근처에 있는지 확인하는 함수
    private fun isCharacterNearHospital(character: Character): Boolean {
        val hospitalLeft = 36f //왼쪽 여백
        val hospitalTop = 128f // 위쪽 여백
        val hospitalWidth = 100f // 너비
        val hospitalHeight = 100f // 높이
        val nearRange = 50f // 근처 범위


        val hospitalCenterX = hospitalLeft + hospitalWidth / 2
        val hospitalCenterY = hospitalTop + hospitalHeight / 2

        return abs(character.x - hospitalCenterX) < nearRange && abs(character.y - hospitalCenterY) < nearRange
    }
}





