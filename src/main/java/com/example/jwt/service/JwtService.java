package com.example.jwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
public class JwtService {

    private static String secretKey = "java11SpringBootJWTTokenIssueMethod";

    public String create( // 토큰 생성 메서드
                          HashMap<String, Object> claims,
                          LocalDateTime expireAt
    ){

        var key = Keys.hmacShaKeyFor(secretKey.getBytes());
        var _expireAt = Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant()); // LocalDateTime 을 Date 타입으로 바꿔준다

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setClaims(claims)
                .setExpiration(_expireAt)
                .compact(); // String Type 리턴
    }

    public void validation(String token){ // 토큰 검증 메서드
        var key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var parser = Jwts.parserBuilder() // JWT 토큰 parsing 하는 parser 생성
                .setSigningKey(key)
                .build();

        try {
            var result = parser.parseClaimsJws(token); // parser 결과값
            result.getBody().entrySet().forEach(value -> {
                log.info("key : {}, value : {}", value.getKey(), value.getValue());
            });
        }catch (Exception e){
            if (e instanceof SignatureException){ // 토큰이 일치하지 않을 때
                throw new RuntimeException("JWT Token Valid Exception");
            }
            else if (e instanceof ExpiredJwtException) { // 토큰이 만료됐을 때
                throw new RuntimeException("JWT Token Expired Exception");
            }
            else { // 그 외 예외처리
                throw new RuntimeException("JWT Token Validation Exception");
            }
        }
    }
}
