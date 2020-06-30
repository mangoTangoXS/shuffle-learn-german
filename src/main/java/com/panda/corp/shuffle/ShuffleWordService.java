package com.panda.corp.shuffle;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class ShuffleWordService {

    private Map<String, String> wordsCollectionDE;
    private Map<String, String> wordsCollectionENG;
    private Map<String, String> difficultWords;

    // private Set<String> guessedWords;
    private Random random;
    //    private ShuffleWordDatabaseMock database = new ShuffleWordDatabaseMock();
    private ShuffleWordDatabase database;
    private int numberOfNotGuessed;

    @Autowired
    public ShuffleWordService(ShuffleWordDatabase shuffleWordDatabase) {
        this.database = shuffleWordDatabase;

        wordsCollectionDE = new HashMap<>();
        wordsCollectionENG = new HashMap<>();

        database.findAll().forEach(vocabEntity -> wordsCollectionDE.put(vocabEntity.getKey(), vocabEntity.getValue()));
        database.findAll().forEach(vocabEntity -> wordsCollectionENG.put(vocabEntity.getValue(), vocabEntity.getKey()));

        difficultWords = new HashMap<>(10);
        random = new Random();
    }

    String getWord(String language) {
        switch (language) {
            case "ENG":
                return getWordToGuesses(wordsCollectionDE);
            case "DE":
                return getWordToGuesses(wordsCollectionENG);
            default:
                return "wrong input";

        }

    }

    private String getWordToGuesses(Map<String, String> wordsCollection) {
        if (wordsCollection.size() == 0) {
            return "No words to guess left";
        }

        return new ArrayList<>(wordsCollection.keySet()).get(random.nextInt(wordsCollection.size()));
    }

    ShuffleResponseDTO getLanguageToBeChecked(String language, String key, String inputValue) {
        switch (language) {
            case "ENG":
                return checkIfCorrectAnswer(key, inputValue, wordsCollectionDE);
            case "DE":
                return checkIfCorrectAnswer(key, inputValue, wordsCollectionENG);
            default:
                throw new RuntimeException("Wrong input");
        }
    }

    private ShuffleResponseDTO checkIfCorrectAnswer(String key, String inputValue, Map<String, String> wordsCollection) {

        if (!wordsCollection.containsKey(key)) {
            numberOfNotGuessed++;
            return createResponse(false, wordsCollection);
        }

        if (inputValue.equals(wordsCollection.get(key))) {
            wordsCollection.remove(key);
            return createResponse(true, wordsCollection);
        } else {
            difficultWords.put(key, wordsCollection.get(key));
            numberOfNotGuessed++;
            return createResponse(false, wordsCollection);
        }
    }

    private ShuffleResponseDTO createResponse(boolean result, Map<String, String> wordsCollection) {
        int numberOfCorrectAnswers = (int) (database.count() - wordsCollection.size());
        return ShuffleResponseDTO.of(result, numberOfCorrectAnswers, numberOfNotGuessed, wordsCollection.size());
    }
}
