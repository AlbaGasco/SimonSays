package com.example.simonsays;

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simonsays.R
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var soundPool: SoundPool
    private lateinit var soundList: List<Int>
    private lateinit var playerMoves: MutableList<Int>
    private lateinit var gameMoves: MutableList<Int>

    private lateinit var greenButton: Button
    private lateinit var redButton: Button
    private lateinit var blueButton: Button
    private lateinit var yellowButton: Button
    private lateinit var startButton: Button

    private lateinit var random: Random
    private var isPlayersTurn: Boolean = false
    private var soundCounter: Int = 0
    private var counter: Int = 1
    private var isGameStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        greenButton = findViewById(R.id.green_button)
        redButton = findViewById(R.id.red_button)
        blueButton = findViewById(R.id.blue_button)
        yellowButton = findViewById(R.id.yellow_button)
        startButton = findViewById(R.id.start_button)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()

        soundList = listOf(
            soundPool.load(this, R.raw.green_sound, 1),
            soundPool.load(this, R.raw.red_sound, 1),
            soundPool.load(this, R.raw.blue_sound, 1),
            soundPool.load(this, R.raw.yellow_sound, 1)
        )

        playerMoves = mutableListOf()
        gameMoves = mutableListOf()

        random = Random(System.currentTimeMillis())

        greenButton.setOnClickListener { onPlayerMove(0) }
        redButton.setOnClickListener { onPlayerMove(1) }
        blueButton.setOnClickListener { onPlayerMove(2) }
        yellowButton.setOnClickListener { onPlayerMove(3) }

        startButton.setOnClickListener {
            if (!isGameStarted) {
                startGame()
                startButton.text = "Playing..."
                isGameStarted = true
            }
        }
    }

    private fun startGame() {
        isPlayersTurn = false
        gameMoves.clear()
        soundCounter = 0
        playNextSound()
    }

    private fun playNextSound() {
        Handler().postDelayed({
            val nextSound = random.nextInt(4)
            gameMoves.add(nextSound)
            playSound(nextSound)
            highlightButton(when (nextSound) {
                0 -> greenButton
                1 -> redButton
                2 -> blueButton
                else -> yellowButton
            })
            soundCounter++

            if (soundCounter < counter) {
                playNextSound()
            } else {
                isPlayersTurn = true
                Toast.makeText(this@MainActivity, "Your turn!", Toast.LENGTH_SHORT).show()
                startButton.text = "Start"
                isGameStarted = false
            }
        }, 1000)
    }

    private fun playSound(soundIndex: Int) {
        soundPool.play(soundList[soundIndex], 1.0f, 1.0f, 1, 0, 1.0f)
    }

    private fun onPlayerMove(move: Int) {
        if (isPlayersTurn) {
            playerMoves.add(move)
            playSound(move)

            if (playerMoves.size == gameMoves.size) {
                checkMoves()
            }
        }
    }

    private fun checkMoves() {
        if (playerMoves == gameMoves) {
            playerMoves.clear()
            isPlayersTurn = false
            Toast.makeText(this, "Correct! Next round...", Toast.LENGTH_SHORT).show()
            counter++
            startGame()
        } else {
            gameOver()
        }
    }

    private fun gameOver() {
        Toast.makeText(this, "Game over!", Toast.LENGTH_SHORT).show()
        startButton.text = "Start"
        isGameStarted = false
    }

    private fun highlightButton(button: Button) {
        button.alpha = 0.5f // Cambia la opacidad del botón para oscurecerlo
        Handler().postDelayed({ button.alpha = 1.0f }, 500) // Restaura la opacidad después de un tiempo
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}