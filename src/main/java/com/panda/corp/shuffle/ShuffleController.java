package com.panda.corp.shuffle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShuffleController {

    private ShuffleWordService shuffleWordService;

    @Autowired
    public ShuffleController(ShuffleWordService shuffleWordService) {
        this.shuffleWordService = shuffleWordService;
    }

    @GetMapping("/getWord")
    public ResponseEntity<String> getWord() {
        return ResponseEntity.ok(shuffleWordService.getWordToGuess());
    }

    @GetMapping("/guess")
    public ResponseEntity<ShuffleResponseDTO> getWord(@RequestParam String key, @RequestParam String guessWord) {
        return ResponseEntity.ok(shuffleWordService.checkIfCorrectAnswer(key, guessWord));
    }
}
