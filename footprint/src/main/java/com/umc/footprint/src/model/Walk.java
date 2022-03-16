package com.umc.footprint.src.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Walk")
public class Walk {

    @Id
    @Column(name = "walkIdx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int walkIdx;

    @Column(name = "startAt", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "endAt", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "distance", nullable = false)
    private double distance;

    @Column(name = "coordinate", nullable = false)
    private String coordinate;

    @Column(name = "pathImgUrl")
    private String pathImgUrl;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "userIdx", nullable = false)
    private int userIdx;

    @Column(name = "goalRate", nullable = false)
    private float goalRate;

    @Column(name = "calorie")
    private int calorie;

    @Builder
    public Walk(int walkIdx, LocalDateTime startAt, LocalDateTime endAt, double distance, String coordinate, String pathImgUrl, String status, int userIdx, float goalRate, int calorie) {
        this.walkIdx = walkIdx;
        this.startAt = startAt;
        this.endAt = endAt;
        this.distance = distance;
        this.coordinate = coordinate;
        this.pathImgUrl = pathImgUrl;
        this.status = status;
        this.userIdx = userIdx;
        this.goalRate = goalRate;
        this.calorie = calorie;
    }
}
