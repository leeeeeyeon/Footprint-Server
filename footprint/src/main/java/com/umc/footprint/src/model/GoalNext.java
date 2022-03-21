package com.umc.footprint.src.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "GoalNext")
public class GoalNext {

    @Id
    @Column(name = "planIdx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planIdx;

    @Column(name = "userIdx", nullable = false)
    private int userIdx;

    @Column(name = "walkGoalTime")
    private int walkGoalTime;

    @Column(name = "walkTimeSlot", nullable = false)
    private int walkTimeSlot;

    @Column(name = "createAt", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "updateAt", nullable = false)
    private LocalDateTime updateAt;

    @Builder
    public GoalNext(int planIdx, int userIdx, int walkGoalTime, int walkTimeSlot, LocalDateTime createAt, LocalDateTime updateAt) {
        this.planIdx = planIdx;
        this.userIdx = userIdx;
        this.walkGoalTime = walkGoalTime;
        this.walkTimeSlot = walkTimeSlot;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }
}
