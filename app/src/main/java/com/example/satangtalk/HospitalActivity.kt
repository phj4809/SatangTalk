package com.example.satangtalk

import android.os.Bundle
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.satangtalk.databinding.ActivityHospitalBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST







// Gemini API 서비스 인터페이스
interface GeminiApiService {
    @POST("models/gemini-1.5-flash-latest:generateContent") // API 엔드포인트
    suspend fun generateText(@Body request: GenerateTextRequest): GenerateTextResponse
}

// API 요청 데이터 클래스
data class GenerateTextRequest(val prompt: String)

// API 응답 데이터 클래스
data class GenerateTextResponse(val text: String)


class HospitalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHospitalBinding
    private lateinit var geminiApiService: GeminiApiService
    private var errorCount = 0
    private var questSuccessCount = 0
    private var questFailCount = 0
    private val quest = generateRandomQuest() // 랜덤 퀘스트 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHospitalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // API 키 설정
        val apiKey = "AIzaSyDcDXq4AJL4EuMkvkBH2XuWHxKtk8mHvA0"

        // Interceptor 추가
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request: Request = original.newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(request)
            }
            .build()

        // Retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)// 실제 API 베이스 URL로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Gemini API 서비스 생성
        geminiApiService = retrofit.create(GeminiApiService::class.java)

        // 버튼 클릭 리스너
        binding.sendButton.setOnClickListener {
            val userInput = binding.userInputEditText.text.toString()

            // Gemini API 호출 (코루틴 사용)
            lifecycleScope.launch {
                try {
                    // Gemini API 요청 구성
                    val request = GenerateTextRequest(
                        """
                        당신은 친절하고 상냥한 말투를 사용하는 베테랑 Hospital 주인입니다...
                        
                 사용자의 한국어 문장: $userInput
                 문법 및 억양 피드백을 제공해주세요.
             """.trimIndent()
                    )

                    val response = geminiApiService.generateText(request) //request 변수 사용
                    val feedback = extractFeedback(response.text) // 피드백 추출

                    if (feedback.isNotEmpty()) {
                        errorCount++
                        if (errorCount >= 3) {
                            changeHospitalOwnerExpression() // 표정 변경
                        }
                        binding.responseTextView.text = feedback
                    } else {
                        if (checkQuestCompletion(userInput, quest)) { // 퀘스트 달성 확인
                            questSuccessCount++
                            // 퀘스트 성공 처리
                        }
                        binding.responseTextView.text = response.text
                    }
                    if (errorCount >= 5) {
                        questFailCount++
                        // 퀘스트 실패 처리 (MainActivity로 이동)
                        val intent = Intent(this@HospitalActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    // 오류 처리
                    val errorMessage = getString(R.string.error_message, e.message)
                    // 문자열 리소스 사용
                    binding.responseTextView.text = errorMessage

                }
            }
        }
    }

    private fun extractFeedback(text: String): String { // 정규 표현식 또는 자연어 처리 기술을 사용하여 피드백 추출
        val pattern = Regex("문법 오류|억양 오류")
        val matchResult = pattern.find(text)
        return if (matchResult != null) "문법 또는 억양 오류가 있습니다." else "" // 피드백 문자열 반환
    }

    private fun changeHospitalOwnerExpression() {
        if (binding.hospitalOwner.tag == "hospital_owner") {
            binding.hospitalOwner.setImageResource(R.drawable.hospital_owner_tired)
            binding.hospitalOwner.tag = "hospital_owner_tired"
        } else {
            binding.hospitalOwner.setImageResource(R.drawable.hospital_owner)
            binding.hospitalOwner.tag = "hospital_owner"
        }
    }

    private fun generateRandomQuest(): String {
        val quests = listOf(
            "Go to the hospital and have a conversation with the doctor for more than 5 sentences",
            "Go to the hospital, tell the doctor you have a stomachache, and get a prescription for medicine."
        )
        return quests.random()
    }


    private fun checkQuestCompletion(userInput: String, quest: String): Boolean {
        return when (quest) {
            "Go to the hospital and have a conversation with the doctor for more than 5 sentences" -> {
                // 5 문장 이상 대화했는지 확인하는 로직 (예: 문장 개수 세기)
                val sentences = userInput.split(".")
                sentences.size >= 5
            }

            "Go to the hospital, tell the doctor you have a stomachache, and get a prescription for medicine." -> {
                // 복통을 호소하고 처방전을 받았는지 확인하는 로직 (예: 키워드 확인)
                userInput.contains("stomachache") && userInput.contains("prescription")
            }

            else -> false
        }
    }
}


