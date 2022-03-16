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
@Table(name = "Footprint")
public class Footprint {

    @Id
    @Column(name = "footprintIdx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int footprintIdx;

    @Column(name = "coordinate", length = 100, nullable = false)
    private String coordinate;

    @Column(name = "write", length = 500)
    private String write;

    @Column(name = "recordAt", nullable = false)
    private LocalDateTime recordAt;

    @Column(name = "walkIdx", nullable = false)
    private int walkIdx;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "updateAt")
    private LocalDateTime updateAt;

    @Column(name = "onWalk", nullable = false)
    private boolean onWalk;

    @Builder
    public Footprint(int footprintIdx, String coordinate, String write, LocalDateTime recordAt, int walkIdx, String status, LocalDateTime updateAt, boolean onWalk) {
        this.footprintIdx = footprintIdx;
        this.coordinate = coordinate;
        this.write = write;
        this.recordAt = recordAt;
        this.walkIdx = walkIdx;
        this.status = status;
        this.updateAt = updateAt;
        this.onWalk = onWalk;
    }
}
