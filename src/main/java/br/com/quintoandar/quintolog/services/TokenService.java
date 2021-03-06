package br.com.quintoandar.quintolog.services;

import br.com.quintoandar.quintolog.entity.LogUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    @Value("${app.jwt.expiration}")
    private String expiration;

    @Value("${app.jwt.secret}")
    private String secret;
    
    public String generateToken(Authentication authentication) {

        LogUser logged = (LogUser) authentication.getPrincipal();
        Date today = new Date();
        Date dateExpiration = new Date(today.getTime() + Long.parseLong(expiration));

        return Jwts.builder()
                .setIssuer("API microservice Logs")
                .setSubject(logged.getId().toString())
                .setIssuedAt(today)
                .setExpiration(dateExpiration)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try{
            Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);

            return true;
        } catch (Exception e){

            return false;
        }
    }

    public Long getIdUser(String token) {
      Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
      return Long.parseLong(claims.getSubject());
    }
}
