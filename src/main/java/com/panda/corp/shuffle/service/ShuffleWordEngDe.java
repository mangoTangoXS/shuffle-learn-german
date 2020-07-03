package com.panda.corp.shuffle.service;

import com.panda.corp.shuffle.ShuffleResponseDTO;
import com.panda.corp.shuffle.ShuffleWordDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShuffleWordEngDe implements ShuffleWord {

    private final Random random;
    private final Map<String, String> wordsCollection;
    private final Map<String, String> difficultWords;
    private final int vocabSize;

    private int numberOfNotGuessed;

    @Autowired
    public ShuffleWordEngDe(ShuffleWordDatabase shuffleWordDatabase) {
        random = new Random();
        wordsCollection = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        shuffleWordDatabase.findAll().forEach(vocabEntity -> wordsCollection.put(vocabEntity.getValue(), vocabEntity.getKey()));

        vocabSize = wordsCollection.size();
        difficultWords = new HashMap<>();
    }

    @Override
    public String getWord() {
        if (wordsCollection.size() == 0) {
            return "No words to guess left";
        }

        return new ArrayList<>(wordsCollection.keySet()).get(random.nextInt(wordsCollection.size()));
    }

    @Override
    public ShuffleResponseDTO checkIfCorrectAnswer(String key, String inputValue) {
        if (!wordsCollection.containsKey(key)) {
            numberOfNotGuessed++;
            return createResponse(false);
        }

        String value = wordsCollection.get(key);
        if (value.contains("/")) {
            String[] translation = value.split("/ ");
            return getShuffleResponseIfKeyContainsSlash(key, inputValue, translation);
        } else {

            if (inputValue.equalsIgnoreCase(wordsCollection.get(key))) {
                wordsCollection.remove(key);
                return createResponse(true);
            } else {
                difficultWords.put(key, wordsCollection.get(key));
                numberOfNotGuessed++;
                return createResponse(false);
            }
        }
    }

    private ShuffleResponseDTO getShuffleResponseIfKeyContainsSlash(String key, String inputValue, String[] translation) {
        if (Arrays.asList(translation).contains(inputValue)) {
            wordsCollection.remove(key);
            return createResponse(true);
        } else {
            difficultWords.put(key, wordsCollection.get(key));
            numberOfNotGuessed++;
            return createResponse(false);
        }
    }

    private ShuffleResponseDTO createResponse(boolean result) {
        int numberOfCorrectAnswers = vocabSize - wordsCollection.size();
        return ShuffleResponseDTO.of(result, numberOfCorrectAnswers, numberOfNotGuessed, wordsCollection.size());
    }
}
