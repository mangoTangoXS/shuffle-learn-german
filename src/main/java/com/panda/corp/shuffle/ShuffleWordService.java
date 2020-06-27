package com.panda.corp.shuffle;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShuffleWordService {

    private Map<String, String> wordsCollection;
    private Map<String, String> difficultWords;
    // private Set<String> guessedWords;
    private Random random;
    //    private ShuffleWordDatabaseMock database = new ShuffleWordDatabaseMock();
    private ShuffleWordDatabase database;
    private int numberOfNotGuessed;

    @Autowired
    public ShuffleWordService(ShuffleWordDatabase shuffleWordDatabase) {
        this.database = shuffleWordDatabase;

        wordsCollection = new HashMap<>();

        database.findAll().forEach(vocabEntity -> wordsCollection.put(vocabEntity.getKey(), vocabEntity.getValue()));

        difficultWords = new HashMap<>(10);
        random = new Random();
    }

    public void setWordsCollection(Map<String, String> wordsCollection) {
        this.wordsCollection = wordsCollection;
    }

    String getWordToGuess() {
        if (wordsCollection.size() == 0) {
           return "No words to guess left";
        }

        return new ArrayList<>(wordsCollection.keySet()).get(random.nextInt(wordsCollection.size()));
    }

    public ShuffleResponseDTO checkIfCorrectAnswer(String key, String inputValue) {
        if (!wordsCollection.containsKey(key)) {
            numberOfNotGuessed++;
            return createResponse(false);
        }

        if (inputValue.equals(wordsCollection.get(key))) {
            wordsCollection.remove(key);
            return createResponse(true);
        } else {
            difficultWords.put(key, wordsCollection.get(key));
            numberOfNotGuessed++;
            return createResponse(false);
        }
    }

    private ShuffleResponseDTO createResponse(boolean result) {
        int numberOfCorrectAnswers = (int) (database.count() - wordsCollection.size());
        return ShuffleResponseDTO.of(result, numberOfCorrectAnswers, numberOfNotGuessed, wordsCollection.size());
    }
}
