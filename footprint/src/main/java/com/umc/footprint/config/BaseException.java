package com.umc.footprint.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseException extends Throwable {
    private BaseResponseStatus status;
}
