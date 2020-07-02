package com.panda.corp.shuffle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShuffleWordService {

    private static final String ENGLISH = "ENG";
    private static final String GERMAN = "DE";

    private Map<String, String> wordsCollectionDE;
    private Map<String, String> wordsCollectionENG;
    private Map<String, String> difficultWords;

    // private Set<String> guessedWords;
    private Random random;
    private ShuffleWordDatabase database;
    private int numberOfNotGuessedDE;
    private int numberOfNotGuessedENG;

    @Autowired
    public ShuffleWordService(ShuffleWordDatabase shuffleWordDatabase) {
        this.database = shuffleWordDatabase;

        wordsCollectionDE = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        wordsCollectionENG = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        database.findAll().forEach(vocabEntity -> wordsCollectionDE.put(vocabEntity.getKey(), vocabEntity.getValue()));
        database.findAll().forEach(vocabEntity -> wordsCollectionENG.put(vocabEntity.getValue(), vocabEntity.getKey()));

        difficultWords = new HashMap<>();
        random = new Random();
    }

    String getWord(String language) {
        switch (language) {
            case ENGLISH:
                return getWordToGuesses(wordsCollectionDE);
            case GERMAN:
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
            case ENGLISH:
                return checkIfCorrectAnswer(key, inputValue, wordsCollectionDE);
            case GERMAN:
                return checkIfCorrectAnswer(key, inputValue, wordsCollectionENG);
            default:
                throw new RuntimeException("Wrong input");
        }
    }

    private ShuffleResponseDTO checkIfCorrectAnswer(String key, String inputValue, Map<String, String> wordsCollection) {
        if (!wordsCollection.containsKey(key)) {
            if (wordsCollection.equals(wordsCollectionDE)) {
                return createResponse(false, wordsCollection, numberOfNotGuessedDE);
            } else {
                numberOfNotGuessedENG++;
                return createResponse(false, wordsCollection, numberOfNotGuessedENG);
            }
        }

        if (inputValue.equalsIgnoreCase(wordsCollection.get(key))) {
            wordsCollection.remove(key);
            return createResponse(true, wordsCollection, 0);
        } else {
            difficultWords.put(key, wordsCollection.get(key)); //to be distinguished if ENG or DE
            if (wordsCollection.equals(wordsCollectionDE)) {
                numberOfNotGuessedDE++;
                return createResponse(false, wordsCollection, numberOfNotGuessedDE);
            } else {
                numberOfNotGuessedENG++;
                return createResponse(false, wordsCollection, numberOfNotGuessedDE);
            }
        }
    }

    private ShuffleResponseDTO createResponse(boolean result, Map<String, String> wordsCollection, int numberOfNotGuessed) {
        int numberOfCorrectAnswers = (int) (database.count() - wordsCollection.size());
        return ShuffleResponseDTO.of(result, numberOfCorrectAnswers, numberOfNotGuessed, wordsCollection.size());
    }
}
