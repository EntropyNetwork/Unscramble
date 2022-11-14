package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String
    /* Declare backing properties, securing scope of mutables */
    private val _currentScrambledWord = MutableLiveData<String>()

    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    private val _currentWordCount = MutableLiveData<Int>(0)

    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private var _score = MutableLiveData<Int>(0)

    val score: LiveData<Int>
        get() = _score

    //Log GameViewModel life cycle information
    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    /* First time I knowingly improved efficiency of Google code in this block! */

    private fun getNextWord(){
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        /* Google's original code originally executes the 'while'
        before the if-else; instead the while should only execute
        during the else-condition.
         */
        if (wordsList.contains(currentWord)){
            getNextWord()
        } else {
            while (String(tempWord).equals(currentWord, false)){
                tempWord.shuffle()
            }
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }

    fun nextWord(): Boolean{
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS){
            getNextWord()
            true
        }else false
    }

    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean{
        if (playerWord.equals(currentWord, true)){
            increaseScore()
                return true
            }else{
                return false
        }
    }

    fun reinitializeData(){
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
}