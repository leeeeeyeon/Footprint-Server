package com.umc.footprint.src.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Photo")
public class Photo {

    @Id
    @Column(name = "photoIdx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int photoIdx;

    @Column(name = "imageUrl")
    private String imageUrl;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "userIdx")
    private int userIdx;

    @Column(name = "footprintIdx")
    private int footprintIdx;

    @Builder
    public Photo(int photoIdx, String imageUrl, String status, int userIdx, int footprintIdx) {
        this.photoIdx = photoIdx;
        this.imageUrl = imageUrl;
        this.status = status;
        this.userIdx = userIdx;
        this.footprintIdx = footprintIdx;
    }
}
