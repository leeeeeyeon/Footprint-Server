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
@Table(name = "Goal")
public class Goal {

    @Id
    @Column(name = "planIdx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planIdx;

    @Column(name = "userIdx", nullable = false)
    private int userIdx;

    @Column(name = "walkGoalTime", nullable = false)
    private int walkGoalTime;

    @Column(name = "walkTimeSlot", nullable = false)
    private int walkTimeSlot;

    @Column(name = "createAt", nullable = false)
    private LocalDateTime createAt;

    @Builder
    public Goal(int planIdx, int userIdx, int walkGoalTime, int walkTimeSlot, LocalDateTime createAt) {
        this.planIdx = planIdx;
        this.userIdx = userIdx;
        this.walkGoalTime = walkGoalTime;
        this.walkTimeSlot = walkTimeSlot;
        this.createAt = createAt;
    }
}
