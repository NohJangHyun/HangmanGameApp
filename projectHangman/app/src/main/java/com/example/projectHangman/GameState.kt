package com.example.projectHangman

sealed class GameState {
    class Running(val underscoreWord: String, val drawable: Int) : GameState()
    class Lose() : GameState()
    class Win() : GameState()

}
