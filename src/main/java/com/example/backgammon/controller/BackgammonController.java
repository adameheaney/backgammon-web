package com.example.backgammon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backgammon.Backgammon;

@RestController
public class BackgammonController {

    private final Backgammon game = new Backgammon();

    @PostMapping("/move")
    public String makeMove(@RequestBody String move) {
        String worked = game.playerTurn(move); // Replace with your logic

        System.out.println(move);

        return worked;
    }

    @GetMapping("/")
    public String home() {
        String[] gameStates = game.getGameState().split("\\n");
        String html = "";
        for (String gameState : gameStates) {
            html += "<h3>" + gameState + "</h3>";
        }
        return html;
    }

    @GetMapping("/state")
    public String getState() {
        String gameStates = game.getGameState();
        return gameStates;
    }
}
