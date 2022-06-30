package com.example.projectHangman

import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.projectHangman.databinding.ActivityGameBinding
import com.example.projectHangman.databinding.AlertdialogBinding
import kotlin.random.Random


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    private lateinit var wordToGuess: String
    private lateinit var underbarWord: String
    private lateinit var record : String
    private lateinit var name: String
    private var maxTry = 6
    private var currentTry = 0
    private var draw: Int = R.drawable.hangman0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRestart.setOnClickListener {
            restart()
        }
        showDialog()

        binding.alphabetBoard.children.forEach { alphabet ->
            if (alphabet is LinearLayout) {
                alphabet.children.forEach { alphabet1 ->
                    if (alphabet1 is Button) {
                        alphabet1.setOnClickListener {
                            val gameState = playGame((alphabet1).text[0])
                            Log.d("Text from Btn", (alphabet1).text[0].toString())
                            updateGame(gameState)
                            alphabet1.isEnabled = false
                        }
                    }
                }
            }
        }
    }
    fun showDialog(){
        val builder = AlertDialog.Builder(this)
        val builderItem = AlertdialogBinding.inflate(layoutInflater)
        val editText = builderItem.editText
        with(builder){
            setTitle("Input Name")
            setMessage("이름을 입력하세요.")
            setView(builderItem.root)
            setPositiveButton("OK"){ dialogInterface: DialogInterface, i:Int->
                name = editText.text.toString()
                Log.d("TAG", name)
                Toast.makeText(applicationContext, name+" has started the game", Toast.LENGTH_SHORT).show()
                val gameState = startGame()
                updateGame(gameState)

            }
            show()
        }
    }

    private fun endDialogPopUp(win: Boolean) {
        val builder = AlertDialog.Builder(this@GameActivity)
        if (win) {
            record = getTime()
            builder.setTitle(name + " your record is "+ record)
        } else {
            binding.hangmanDraw.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hangman6))
            builder.setTitle(name + " you Hanged a man!")
        }
        builder.setMessage("The answer was "+ wordToGuess +"!")
        builder.setPositiveButton("RePlay") { _, _ ->
            val gameState = startGame()
            updateGame(gameState)
            Toast.makeText(applicationContext, "New game started!", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") { _, _ ->
            finish()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun playGame(alphabet: Char): GameState {

        val indexes = mutableListOf<Int>()

        wordToGuess.forEachIndexed { index, char ->
            if (char.equals(alphabet, true)) {
                indexes.add(index)
            }
        }
        var finalUnderscoreWord = "" + underbarWord
        indexes.forEach { index ->
            val sb = StringBuilder(finalUnderscoreWord).also { it.setCharAt(index, alphabet) }
            finalUnderscoreWord = sb.toString()
        }

        if (indexes.isEmpty()) {
            currentTry++
        }

        underbarWord = finalUnderscoreWord
        return getGameState()
    }

    private fun drawHangman(): Int {
        return when (currentTry) {
            0 -> R.drawable.hangman0
            1 -> R.drawable.hangman1
            2 -> R.drawable.hangman2
            3 -> R.drawable.hangman3
            4 -> R.drawable.hangman4
            5 -> R.drawable.hangman5
            6 -> R.drawable.hangman6
            else -> R.drawable.hangman6
        }
    }

    fun startGame(): GameState {
        binding.chrono.base = SystemClock.elapsedRealtime()
        binding.chrono.start()
        currentTry = 0
        draw = R.drawable.hangman6
        val randomIndex = Random.nextInt(GameConstants.words.size)
        Log.d("TAG", randomIndex.toString())
        wordToGuess = GameConstants.words[randomIndex]
        makeUnderbar(wordToGuess)
        binding.alphabetBoard.children.forEach { alphabet ->
            if (alphabet is LinearLayout) {
                alphabet.children.forEach { alphabet1 ->
                    if (alphabet1 is Button) {
                        alphabet1.isEnabled = true
                    }
                }
            }
        }
        return getGameState()
    }

    fun restart() {
        binding.chrono.base = SystemClock.elapsedRealtime()
        binding.chrono.start()
        val gameState = startGame()
        binding.alphabetBoard.children.forEach { alphabet ->
            if (alphabet is LinearLayout) {
                alphabet.children.forEach { alphabet1 ->
                    if (alphabet1 is Button) {
                        alphabet1.isEnabled = true
                    }
                }
            }
        }
        updateGame(gameState)
    }

    fun makeUnderbar(word: String) {
        val sb = StringBuilder()
        word.forEach { char ->
            sb.append("_")
        }
        underbarWord = sb.toString()
    }

    fun updateGame(gameState: GameState) {
        when (gameState) {
            is GameState.Running -> {
                binding.guessWord.text = gameState.underscoreWord
                binding.hangmanDraw.setImageDrawable(ContextCompat.getDrawable(this, gameState.drawable))
            }
            is GameState.Lose -> endDialogPopUp(false)
            is GameState.Win -> endDialogPopUp(true)

        }

    }

    private fun getTime(): String {
        val current = SystemClock.elapsedRealtime() - binding.chrono.getBase()
        val time: Long = (current / 1000)
        val min = time % (60 * 60) / 60
        val sec = time % 60
        return ("%s min %s sec").format(min, sec)
    }

    private fun getGameState(): GameState {
        if (underbarWord.equals(wordToGuess, true)) {
            binding.chrono.stop()
            return GameState.Win()
        }
        if (currentTry == maxTry) {
            binding.chrono.stop()
            return GameState.Lose()
        }
        draw = drawHangman()
        return GameState.Running(underbarWord, draw)
    }

}
