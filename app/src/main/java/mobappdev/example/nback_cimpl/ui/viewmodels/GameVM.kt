package mobappdev.example.nback_cimpl.ui.viewmodels

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.GameApplication
import mobappdev.example.nback_cimpl.NBackHelper
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository
import mobappdev.example.nback_cimpl.ui.theme.Blue80
import java.util.Locale


/**
 * This is the GameViewModel.
 *
 * It is good practice to first make an interface, which acts as the blueprint
 * for your implementation. With this interface we can create fake versions
 * of the viewmodel, which we can use to test other parts of our app that depend on the VM.
 *
 * Our viewmodel itself has functions to start a game, to specify a gametype,
 * and to check if we are having a match
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */

interface GameViewModel {
    val buttonColor: StateFlow<Color>
    val gameState: StateFlow<GameState>
    val score: StateFlow<Int>
    val highscore: StateFlow<Int>
    val nBack: Int

    fun setGameType(gameType: GameType)
    fun startGame(context: Context)
    fun checkMatch()
    fun initializerOn()
    fun initializerOff()
    fun resetEventValue()
    fun addCurrentEventValue()
    fun resetCurrentEventValue()
}

class GameVM(
    private val userPreferencesRepository: UserPreferencesRepository

): GameViewModel, ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    override val gameState: StateFlow<GameState>
        get() = _gameState.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int>
        get() = _score

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int>
        get() = _highscore

    private val _buttonColor = MutableStateFlow<Color>(Blue80)
    override val buttonColor: StateFlow<Color>
        get() = _buttonColor.asStateFlow()

    // nBack is currently hardcoded
    override val nBack: Int = 2

    private var job: Job? = null  // coroutine job for the game event
    private val eventInterval: Long = 2000L  // 2000 ms (2s)

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var events = emptyArray<Int>()  // Array with all events

    private var gotScore = false
    private var textToSpeech: TextToSpeech? = null

    override fun resetEventValue(){
        _gameState.value.eventValue = -1
    }

    override fun addCurrentEventValue(){
        _gameState.value.curentEventValue +=1
    }

    override fun resetCurrentEventValue() {
        _gameState.value.curentEventValue = 0
    }

    override fun initializerOn(){
        _gameState.value = _gameState.value.copy(initializer = true)
    }

    override fun initializerOff(){
        _gameState.value = _gameState.value.copy(initializer = false)
    }

    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun startGame(context: Context) {

        job?.cancel()  // Cancel any existing game loop

        // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
        events = nBackHelper.generateNBackString(10, 9, 30, nBack).toList()
            .toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input

        Log.d("GameVM", "The following sequence was generated: ${events.contentToString()}")
        Log.d("GameType", " ${gameState.value.gameType}")
        Log.d("GameType", " ${gameState.value.eventValue}")

        job = viewModelScope.launch {
            when (gameState.value.gameType) {
                GameType.Audio -> runAudioGame(context)
                GameType.AudioVisual -> runAudioVisualGame()
                GameType.Visual -> runVisualGame(events)
            }
            //update highscore if score is higher
            if (_score.value > _highscore.value) {
                _highscore.value = _score.value
                viewModelScope.launch {
                    userPreferencesRepository.saveHighScore(_score.value)
                }
            }
        }
    }

    override fun checkMatch() {

        if (!gotScore) {
            if (_gameState.value.curentEventValue in 2..9) {
                if (events[_gameState.value.curentEventValue] == events[_gameState.value.curentEventValue - nBack]) {
                    _score.value += 1
                    gotScore = true
                    _buttonColor.value = Color.Green
                } else {
                    _buttonColor.value = Color.Red
                }

            }
        }
    }

    /**
     * Todo: This function should check if there is a match when the user presses a match button
     * Make sure the user can only register a match once for each event.
     */

    private suspend fun runAudioGame(context: Context) {

        resetCurrentEventValue()
        _score.value = 0


        for (value in events) {
            if(!_gameState.value.initializer){
                resetEventValue()
                return
            }

            _gameState.value = _gameState.value.copy(eventValue = value)

            val generatedChar = convertValueToLetter(value)
            TextTospeech(context, generatedChar)

            delay(eventInterval)
            addCurrentEventValue()
            gotScore = false
            _buttonColor.value = Blue80
        }
    }


fun TextTospeech (context: Context, char: Char){
    textToSpeech = TextToSpeech(context) {
        if (it == TextToSpeech.SUCCESS) {
            textToSpeech?.let{ txtToSpeech ->
                txtToSpeech.language = Locale.US
                txtToSpeech.setSpeechRate(0.7f)
                txtToSpeech.speak(char.toString(), TextToSpeech.QUEUE_ADD, null, null)

            }
        }
    }
}
// Todo: Make work for Basic grade

    fun convertValueToLetter(value: Int): Char {
        val startAscii = 'A'.toInt()
        val endAscii = 'Z'.toInt()
        val alphabetSize = endAscii - startAscii + 1

        val adjustedValue = (value % alphabetSize + alphabetSize) % alphabetSize
        return (startAscii + adjustedValue).toChar()
    }


    private suspend fun runVisualGame(events: Array<Int>){

        resetCurrentEventValue()
        _score.value = 0

        for (value in events) {
            if(!_gameState.value.initializer){
                resetEventValue()
                return
            }
            addCurrentEventValue()
            _gameState.value = _gameState.value.copy(eventValue = value)
            delay(eventInterval)
            gotScore = false
            _buttonColor.value = Blue80
        }
    }

    private fun runAudioVisualGame(){
// Todo: Make work for Higher grade
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GameApplication)
                GameVM(application.userPreferencesRespository)
            }
        }
    }

    init {
// Code that runs during creation of the vm
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect {
                _highscore.value = it
            }
        }
    }

}

// Class with the different game types
enum class GameType{
    Audio,
    Visual,
    AudioVisual
}

data class GameState(
// You can use this state to push values from the VM to your UI.
    val gameType: GameType = GameType.Visual,  // Type of the game
    var eventValue: Int = -1,  // The value of the array string
    var curentEventValue: Int = 0,
    var buttonColor: Color = Blue80,
    var initializer: Boolean = false
)

class FakeVM: GameViewModel {

    private val defaultButtonColor: Color = Blue80
    override val buttonColor: StateFlow<Color>
        get() = MutableStateFlow(defaultButtonColor).asStateFlow()
    override val gameState: StateFlow<GameState>
        get() = MutableStateFlow(GameState()).asStateFlow()
    override val score: StateFlow<Int>
        get() = MutableStateFlow(2).asStateFlow()
    override val highscore: StateFlow<Int>
        get() = MutableStateFlow(42).asStateFlow()

    override val nBack: Int
        get() = 2

    override fun setGameType(gameType: GameType) {
    }

    override fun startGame(context: Context) {
    }

    override fun checkMatch() {
    }

    override fun initializerOn(){

    }

    override fun initializerOff() {
    }

    override fun resetEventValue(){
    }

    override fun addCurrentEventValue(){
    }

    override fun resetCurrentEventValue() {
    }


}

