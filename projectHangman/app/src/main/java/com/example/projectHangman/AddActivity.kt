package com.example.projectHangman

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectHangman.GameConstants.words
import com.example.projectHangman.databinding.ActivityAddBinding


class AddActivity : AppCompatActivity(){
    private lateinit var binding: ActivityAddBinding
    private lateinit var word: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.inputWord.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//            override fun afterTextChanged(editable: Editable) {
//                if (editable.isNotEmpty()) {
//                    binding.btnAdd.setClickable(true)
//                    binding.btnAdd.setBackgroundColor(Color.BLUE)
//                } else {
//                    binding.btnAdd.setClickable(false)
//                    binding.btnAdd.setBackgroundColor(Color.GRAY)
//                }
//            }
//        })


        binding.btnAdd.setOnClickListener {
            word = binding.inputWord.text.toString()
            Log.d("Word", word)
            words.add(word)
            Log.d("Word", words.toString())
            Toast.makeText(this,"You have add word " + word + " in the list",Toast.LENGTH_LONG).show()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnBack.setOnClickListener{
            val intent = Intent(this, MainActivity:: class.java)
            startActivity(intent)
        }
    }
}