package com.panda.corp.shuffle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ShuffleResponseDTO {

    private boolean result;
    private int correctGuesses;
    private int incorrectGuesses;
    private int toGuess;
}
