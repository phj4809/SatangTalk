package com.example.satangtalk

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.satangtalk.databinding.ActivityHospitalBinding
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// API 키 설정
private const val API_KEY = "YOUR_API_KEY_HERE"

// Interceptor 추가
val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val original = chain.request()
        val request: Request = original.newBuilder()
            .header("Authorization", "Bearer $API_KEY")
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }
    .build()

// Retrofit 인스턴스 생성
val retrofit = Retrofit.Builder()
    .baseUrl("https://generativelanguage.googleapis.com/")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

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
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            .build()

        // Retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/") // 실제 API 베이스 URL로 변경
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
                    val request = GenerateTextRequest("""
                        당신은 친절하고 상냥한 말투를 사용하는 베테랑 Hospital 주인입니다...
                        
                 사용자의 한국어 문장: $userInput
                 문법 및 억양 피드백을 제공해주세요.
             """.trimIndent())

                    val response = geminiApiService.generateText(request) //request 변수 사용
                    binding.responseTextView.text = response.text
                } catch (e: Exception) {
                    // 오류 처리
                    val errorMessage = getString(R.string.error_message, e.message)
                    // 문자열 리소스 사용
                    binding.responseTextView.text = errorMessage

                }
            }
        }
    }
}

