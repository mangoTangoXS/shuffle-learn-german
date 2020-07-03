package com.panda.corp.shuffle.service;

import com.panda.corp.shuffle.ShuffleResponseDTO;
import com.panda.corp.shuffle.ShuffleWordDatabase;
import com.panda.corp.shuffle.VocabEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ShuffleWordDeEng implements ShuffleWord {

    private final Random random;
    private final Map<String, String> wordsCollection;
    private final Map<String, String> difficultWords;
    private final int vocabSize;

    private int numberOfNotGuessed;

    @Autowired
    public ShuffleWordDeEng(ShuffleWordDatabase shuffleWordDatabase) {
        random = new Random();

        wordsCollection = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        shuffleWordDatabase.findAll().forEach(this::initWords);
        vocabSize = wordsCollection.size();
        difficultWords = new HashMap<>();
    }

    private void initWords(VocabEntity vocabEntity) {
        if (vocabEntity.getKey().contains("/")) {
            Arrays.stream(vocabEntity.getKey().split("/"))
                    .forEach(key -> wordsCollection.put(key.trim(), vocabEntity.getValue()));
        } else {
            wordsCollection.put(vocabEntity.getKey(), vocabEntity.getValue());
        }
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
        if (wordsCollection.containsKey(key)) {
            if (inputValue.equalsIgnoreCase(wordsCollection.get(key))) {
                wordsCollection.remove(key);
                return createResponse(true);
            } else {
                difficultWords.put(key, wordsCollection.get(key));
                numberOfNotGuessed++;
                return createResponse(false);
            }
        } else {
            numberOfNotGuessed++;
            return createResponse(false);
        }

    }

    private ShuffleResponseDTO createResponse(boolean result) {
        int numberOfCorrectAnswers = (int) (vocabSize - wordsCollection.size());
        System.out.println("DeEng " + numberOfCorrectAnswers);
        return ShuffleResponseDTO.of(result, numberOfCorrectAnswers, numberOfNotGuessed, wordsCollection.size());
    }
}
