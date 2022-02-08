package com.umc.footprint.utils;
import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.secret.Secret;
import io.jsonwebtoken.*;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.TimeZone;

import static com.umc.footprint.config.BaseResponseStatus.*;

@Service
public class JwtService {
    /*
   JWT 생성
   @param userId
   @return String
    */
    public String createJwt(String userId){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        Date now = new Date();
        System.out.println("now = " + now);
        Date expiryDate = new Date(now.getTime() + 2592000000L);
        System.out.println("expiryDate = " + expiryDate);
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userId",userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate) // jwt 토큰 유효기간 한 달
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt(){
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
        System.out.println("1. JWT 추출");
        String accessToken = getJwt();
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }
        System.out.println("accessToken = " + accessToken);

        // 2. JWT parsing
        Jws<Claims> claims;
        try {
            System.out.println("2. JWT parsing");
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (ExpiredJwtException exception) {
            throw new BaseException(EXPIRED_JWT);
        } catch (Exception ignored) {
            System.out.println("토큰 잘못됨");
            throw new BaseException(INVALID_JWT);
        }

        // 3. userId 추출
        return claims.getBody().get("userId",String.class);  // jwt 에서 userId를 추출합니다.
    }
}
