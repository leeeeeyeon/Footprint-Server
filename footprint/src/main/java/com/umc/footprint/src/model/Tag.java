package com.umc.footprint.src.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Tag")
public class Tag {

    @Id
    @Column(name = "tagIdx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tagIdx;

    @Column(name = "hashtagIdx", nullable = false)
    private int hashtagIdx;

    @Column(name = "footprintIdx", nullable = false)
    private int footprintIdx;

    @Column(name = "userIdx", nullable = false)
    private int userIdx;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Builder
    public Tag(int tagIdx, int hashtagIdx, int footprintIdx, int userIdx, String status) {
        this.tagIdx = tagIdx;
        this.hashtagIdx = hashtagIdx;
        this.footprintIdx = footprintIdx;
        this.userIdx = userIdx;
        this.status = status;
    }
}