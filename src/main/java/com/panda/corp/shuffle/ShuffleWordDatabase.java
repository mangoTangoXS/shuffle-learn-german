package com.panda.corp.shuffle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ShuffleWordDatabase extends JpaRepository<VocabEntity, Long> {

}
