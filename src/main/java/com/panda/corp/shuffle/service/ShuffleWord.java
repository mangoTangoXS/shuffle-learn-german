package com.panda.corp.shuffle.service;

import com.panda.corp.shuffle.ShuffleResponseDTO;

public interface ShuffleWord {

    String getWord();
    ShuffleResponseDTO checkIfCorrectAnswer(String key, String inputValue);
}
