package com.umc.footprint.src.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Hashtag")
public class Hashtag {

    @Id
    @Column(name = "hashtagIdx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hashtagIdx;

    @Column(name = "hashtag", length = 200, nullable = false)
    private String hashtag;
}
