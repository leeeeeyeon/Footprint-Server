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
@Table(name = "GoalDay")
public class GoalDay {

    @Id
    @Column(name = "planIdx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planIdx;

    @Column(name = "userIdx", nullable = false)
    private int userIdx;

    @Column(name = "sun", nullable = false)
    private int sun;

    @Column(name = "mon", nullable = false)
    private int mon;

    @Column(name = "tue", nullable = false)
    private int tue;

    @Column(name = "wed", nullable = false)
    private int wed;

    @Column(name = "thu", nullable = false)
    private int thu;

    @Column(name = "fri", nullable = false)
    private int fri;

    @Column(name = "sat", nullable = false)
    private int sat;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Builder
    public GoalDay(int planIdx, int userIdx, int sun, int mon, int tue, int wed, int thu, int fri, int sat, LocalDateTime createAt) {
        this.planIdx = planIdx;
        this.userIdx = userIdx;
        this.sun = sun;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.createAt = createAt;
    }
}
