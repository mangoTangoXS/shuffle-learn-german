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
            case "english":
                return getWordToGuesses(wordsCollectionDE);
            case "german":
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

    ShuffleResponseDTO checkIfCorrectAnswer(String key, String inputValue) {
        if (!wordsCollectionDE.containsKey(key)) {
            numberOfNotGuessed++;
            return createResponse(false);
        }

        if (inputValue.equals(wordsCollectionDE.get(key))) {
            wordsCollectionDE.remove(key);
            return createResponse(true);
        } else {
            difficultWords.put(key, wordsCollectionDE.get(key));
            numberOfNotGuessed++;
            return createResponse(false);
        }
    }

    private ShuffleResponseDTO createResponse(boolean result) {
        int numberOfCorrectAnswers = (int) (database.count() - wordsCollectionDE.size());
        return ShuffleResponseDTO.of(result, numberOfCorrectAnswers, numberOfNotGuessed, wordsCollectionDE.size());
    }
}
