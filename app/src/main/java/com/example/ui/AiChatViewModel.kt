package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timeFormatted: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
)

class AiChatViewModel : ViewModel() {

    private val TAG = "AiChatViewModel"

    private val systemInstructionText = """
        You are 'Royal Concierge', a luxury optical stylist and optometrist assistant for 'Royal Specs' optical boutique (WhatsApp: +919946732958).
        Your role:
        1. Recommend frames (Round, Aviator, Wayfarer, Cat-Eye, Rectangle, Rimless) based on face shape (Round, Oval, Square, Heart).
        2. Explain optical lens technologies: Zero Power Blue-Cut (screen shield), Single Vision (distance/reading), Progressive / Multifocal (no line transition for all distances), Photochromic Transitions, and High-Index Thin (1.61, 1.67).
        3. Clarify eye prescriptions: SPH (Sphere), CYL (Cylinder for astigmatism), AXIS, and PD (Pupillary Distance).
        4. Mention that customers can order directly via WhatsApp at +91 99467 32958 with INR (₹) pricing and 1-Year Frame Warranty.
        Keep your advice warm, sophisticated, professional, concise, and helpful.
    """.trimIndent()

    private val model by lazy {
        try {
            Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                systemInstruction = content { text(systemInstructionText) }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Firebase.ai initialization caught", e)
            null
        }
    }

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                isUser = false,
                text = "Welcome to Royal Specs! 👑 I am your Royal AI Concierge & Optical Stylist. How may I assist you with frame styling, lens options, or understanding your eye prescription today?"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        val userMessage = ChatMessage(isUser = true, text = trimmed)
        val updatedList = _messages.value + userMessage
        _messages.value = updatedList
        _isLoading.value = true

        viewModelScope.launch {
            try {
                var aiReply: String? = null

                // Attempt generation via Firebase AI Logic (gemini-3.8-flash)
                try {
                    val aiModel = model
                    if (aiModel != null) {
                        val response = withTimeout(35_000L) {
                            val historyContent = updatedList.takeLast(8).map { msg ->
                                if (msg.isUser) {
                                    content("user") { text(msg.text) }
                                } else {
                                    content("model") { text(msg.text) }
                                }
                            }
                            val chat = aiModel.startChat(history = historyContent.dropLast(1))
                            chat.sendMessage(trimmed)
                        }
                        aiReply = response.text
                    }
                } catch (e: TimeoutCancellationException) {
                    Log.w(TAG, "Gemini generation timed out", e)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(TAG, "Firebase AI call caught, using optical fallback: ${e.message}")
                }

                // If remote response wasn't available or errored, use specialized domain responses
                val finalAnswer = if (!aiReply.isNullOrBlank()) {
                    aiReply
                } else {
                    generateIntelligentOpticalAdvice(trimmed)
                }

                _messages.value = _messages.value + ChatMessage(isUser = false, text = finalAnswer)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateIntelligentOpticalAdvice(query: String): String {
        val q = query.lowercase(Locale.ROOT)
        return when {
            q.contains("round face") -> {
                "For a **Round Face**, we recommend angular structures to create flattering balance and definition! Our best picks from the Royal Specs collection are **The Regal Sovereign Wayfarer** or **CyberShield Rectangle**. Avoid very small circular frames."
            }
            q.contains("oval face") -> {
                "You have the most versatile face shape! An **Oval Face** looks exceptional with **The Windsor Round** vintage acetate, our iconic **Crown Aviator Polarized**, or classic clubmaster browlines. Most geometric frames suit you naturally."
            }
            q.contains("square face") -> {
                "For a **Square Face** with prominent cheekbones or jawline, soften your contours with curved silhouettes like **The Windsor Round**, **Duchess Cat-Eye Luxe**, or **Kensington Titanium Rimless**."
            }
            q.contains("heart face") -> {
                "For a **Heart-Shaped Face**, bottom-heavy or light metal wireframes like **Crown Aviator** or subtle **Cat-Eye frames** bring harmonious balance to your temples and chin."
            }
            q.contains("blue") || q.contains("computer") || q.contains("screen") -> {
                "Our **Zero Power Blue-Cut Screen Shield** lenses block 98% of harmful high-energy blue-violet rays emitted by laptops, phones, and LEDs. They include hydrophobic anti-glare coating to eliminate digital eye fatigue and headaches."
            }
            q.contains("progressive") || q.contains("bifocal") -> {
                "**Progressive Multifocal Lenses** offer a seamless, line-free experience with 3 prescription corridors: top for far distance (driving), middle for intermediate (computer/screens), and bottom for near reading."
            }
            q.contains("sph") || q.contains("cyl") || q.contains("power") || q.contains("prescription") -> {
                "**SPH (Sphere)** indicates your nearsightedness (minus '-') or farsightedness (plus '+'). **CYL (Cylinder)** and **AXIS** correct astigmatism (corneal curvature). At Royal Specs, you can simply click a photo of your doctor's slip and send it to us on WhatsApp (+91 99467 32958)!"
            }
            q.contains("order") || q.contains("whatsapp") || q.contains("track") -> {
                "You can order any frame and lens configuration with 1 tap via WhatsApp to **+91 99467 32958**. All orders include 1-Year Frame Warranty, free lens cleaning kit, and express PAN-India delivery!"
            }
            else -> {
                "At Royal Specs, every frame is crafted with premium Italian acetate and titanium hinges. Whether you need zero-power computer blue-blockers, single vision prescription lenses, or progressive multifocals, our master opticians are ready to help at +91 99467 32958. Would you like a recommendation based on your face shape or screen time?"
            }
        }
    }
}
