package com.umc.footprint.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

// 프로젝트에서 사용하는 상수
@Getter
@AllArgsConstructor
public class Constant {
    public static final Long MINUTES_TO_SECONDS = 60L;
}
