package com.umc.footprint.utils;
import com.umc.footprint.config.BaseException;
import io.jsonwebtoken.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.TimeZone;

import static com.umc.footprint.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret-key}")
    private String JwtSecretKey;
    /*
   JWT 생성
   @param userId
   @return String
    */
    public String createJwt(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 2592000000L);
        log.debug("만기 날짜: {}", expiryDate);

        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate) // jwt 토큰 유효기간 한 달
                .signWith(SignatureAlgorithm.HS256, JwtSecretKey)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /*
    JWT에서 userId 추출
    @return String
    @throws BaseException
     */
    public String getUserId() throws BaseException {
        //1. JWT 추출
        log.debug("1. JWT 추출");
        String accessToken = getJwt();
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(EMPTY_JWT);
        }
        log.debug("accessToken = " + accessToken);

        // 2. JWT parsing
        Jws<Claims> claims;
        try {
            log.debug("2. JWT parsing");
            claims = Jwts.parser()
                    .setSigningKey(JwtSecretKey)
                    .parseClaimsJws(accessToken);
        } catch (ExpiredJwtException exception) {
            throw new BaseException(EXPIRED_JWT);
        } catch (Exception ignored) {
            log.error("토큰 잘못됨");
            throw new BaseException(INVALID_JWT);
        }

        // 3. userId 추출
        return claims.getBody().get("userId", String.class);  // jwt 에서 userId를 추출합니다.
    }
}
