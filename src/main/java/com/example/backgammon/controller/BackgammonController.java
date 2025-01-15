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
        String[] boardStrings = game.getBoard().boardString().split("\\n");
        String rolls = game.getRolls();
        return "<h1>" + boardStrings[0] + "</h1> <h1>" + boardStrings[1] + "</h1> <h1>" + rolls + "</h1>";
    }

    @GetMapping("/state")
    public String getState() {
        String[] boardStrings = game.getBoard().boardString().split("\\n");
        String rolls = game.getRolls();
        return "<h1>" + boardStrings[0] + "</h1> <h1>" + boardStrings[1] + "</h1> <h1>" + rolls + "</h1>";
    }
}
