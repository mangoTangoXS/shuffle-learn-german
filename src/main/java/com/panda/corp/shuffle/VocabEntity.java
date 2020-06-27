package com.panda.corp.shuffle;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "jobs")
public class VocabEntity {

    @Id
    private long id;

    @Column(name = "GERMAN")
    private String key;

    @Column(name = "ENGLISH")
    private String value;
}
