package com.umc.footprint.src.users.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "Goal")
public class Goal {
    //필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planIdx", unique = true, nullable = false)
    private int planIdx;

    @Column(name = "walkGoalTime", nullable = false)
    private int walkGoalTime;

    @Column(name = "walkTimeSlot", nullable = false)
    private int walkTimeSlot;

    @Column(name = "createAt", nullable = false)
    private Timestamp createAt;


    //빌더
    @Builder
    public Goal(int planIdx, int walkGoalTime, int walkTimeSlot, Timestamp createAt) {
        this.planIdx = planIdx;
        this.walkGoalTime = walkGoalTime;
        this.walkTimeSlot = walkTimeSlot;
        this.createAt = createAt;
    }

}
