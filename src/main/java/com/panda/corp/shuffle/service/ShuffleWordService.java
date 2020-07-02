package com.panda.corp.shuffle.service;

import com.panda.corp.shuffle.ShuffleResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class ShuffleWordService {

    private final ShuffleWord shuffleWordEngDe;
    private final ShuffleWord shuffleWordDeEng;


    private static final String ENGLISH = "ENG";
    private static final String GERMAN = "DE";


    public ShuffleWordService(ShuffleWord shuffleWordEngDe, ShuffleWord shuffleWordDeEng) {
        this.shuffleWordEngDe = shuffleWordEngDe;
        this.shuffleWordDeEng = shuffleWordDeEng;
    }

    public String getWordForLanguage(String language) {
        switch (language) {
            case ENGLISH:
                return shuffleWordDeEng.getWord();
            case GERMAN:
                return shuffleWordEngDe.getWord();
            default:
                return "wrong input";
        }
    }

    public ShuffleResponseDTO checkAnswerForLanguage(String language, String key, String guessWord) {
        switch (language) {
            case ENGLISH:
                return shuffleWordDeEng.checkIfCorrectAnswer(key,guessWord);
            case GERMAN:
                return shuffleWordEngDe.checkIfCorrectAnswer(key,guessWord);
            default:
                throw new RuntimeException("Wrong input");
        }
    }

}
