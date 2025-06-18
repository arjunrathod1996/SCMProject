package com.SCM.dtoForJWT;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTService {

    // Replace with your generated base64 secret key
    private static final String SECRET_KEY_STRING = "3d5BbXt3rD4f1Dd0Gx1Q3L2n6Z4v4H1d5J3l2K6m8N9p0Q=="; // e.g., "3d5BbXt3rD4f1Dd0Gx1Q3L2n6Z4v4H1d5J3l2K6m8N9p0Q=="
    private static final SecretKey SECRET_KEY = new SecretKeySpec(Base64.getDecoder().decode(SECRET_KEY_STRING), SignatureAlgorithm.HS256.getJcaName());
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);     
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract claim from token using resolver function
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    // Generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", "Stormpath");
        claims.put("name", "Micah Silverman");
        claims.put("scope", "admins");
        claims.put("sub", userDetails.getUsername());
        return createToken(claims, userDetails.getUsername());
    }

    // Generate token for username
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", "Stormpath");
        claims.put("name", "Micah Silverman");
        claims.put("scope", "admins");
        claims.put("sub", username);
        return createToken(claims, username);
    }
    
    public String generateToken(String phoneNumber, List<String> roles) {
        return Jwts.builder()
                .setSubject(phoneNumber)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // Create token with claims, subject, issue time, expiration time, and sign with secret key
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // Check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate token by checking username and expiration
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Extract all details from token for logging or debugging
    public Map<String, Object> getTokenDetails(String token) {
        Claims claims = extractAllClaims(token);
        Map<String, Object> tokenDetails = new HashMap<>();
        tokenDetails.put("body", claims);
        tokenDetails.put("header", Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getHeader());
        tokenDetails.put("signature", token.split("\\.")[2]);
        tokenDetails.put("status", "SUCCESS");
        return tokenDetails;
    }
}



//@Component
//public class JWTService {
//
//    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a secure key
//    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours
//
//    // Extract username from token
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    // Extract expiration date from token
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);  
//    }
//
//    // Extract claim from token using resolver function
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    // Extract all claims from token
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
//    }
//
//    // Generate token for user
//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("iss", "Stormpath");
//        claims.put("name", "Micah Silverman");
//        claims.put("scope", "admins");
//        claims.put("sub", userDetails.getUsername());
//        return createToken(claims, userDetails.getUsername());
//    }
//
//    // Create token with claims, subject, issue time, expiration time, and sign with secret key
//    private String createToken(Map<String, Object> claims, String subject) {
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SECRET_KEY)
//                .compact();
//    }
//
//    // Check if token is expired
//    private Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    // Validate token by checking username and expiration
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    // Extract all details from token for logging or debugging
//    public Map<String, Object> getTokenDetails(String token) {
//        Claims claims = extractAllClaims(token);
//        Map<String, Object> tokenDetails = new HashMap<>();
//        tokenDetails.put("body", claims);
//        tokenDetails.put("header", Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getHeader());
//        tokenDetails.put("signature", token.split("\\.")[2]);
//        tokenDetails.put("status", "SUCCESS");
//        return tokenDetails;
//    }
//}


//@Component
//public class JWTService {
//
//    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a secure key
//    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours
//
//    // Extract username from token
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    // Extract expiration date from token
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    // Extract claim from token using resolver function
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    // Extract all claims from token
//    private Claims extractAllClaims(String token) { 
//        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
//    }
//
//    // Generate token for user
//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        return createToken(claims, userDetails.getUsername());
//    }
//
//    // Create token with claims, subject, issue time, expiration time, and sign with secret key
//    private String createToken(Map<String, Object> claims, String subject) {
//        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SECRET_KEY).compact();
//    }
//
//    // Check if token is expired
//    private Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    // Validate token by checking username and expiration
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//}



//@Component
//public class JWTService {
//
//	private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a secure key
//    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours
//
//    // Extract username from token
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    // Extract expiration date from token
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    // Extract claim from token using resolver function
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    // Extract all claims from token
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
//    }
//
//    // Generate token for user
//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        return createToken(claims, userDetails.getUsername());
//    }
//
//    // Create token with claims, subject, issue time, expiration time, and sign with secret key
//    private String createToken(Map<String, Object> claims, String subject) {
//        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
//    }
//
//    // Check if token is expired
//    private Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    // Validate token by checking username and expiration
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//}


//@Component
//public class JWTService {
//
//    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a secure key
//    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours
//    
//    
//    // Extract token from request
//    public String resolveToken(HttpServletRequest request) {
//        String authorizationHeader = request.getHeader("Authorization");
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            return authorizationHeader.substring(7); // Remove "Bearer " prefix
//        }
//        return null;
//    }
//
//    // Extract username from token
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    // Extract expiration date from token
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    // Extract claim from token using resolver function
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    // Extract all claims from token
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
//    }
//
//    // Generate token for user
//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        return createToken(claims, userDetails.getUsername());
//    }
//
//    // Create token with claims, subject, issue time, expiration time, and sign with secret key
//    private String createToken(Map<String, Object> claims, String subject) {
//        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SECRET_KEY).compact();
//    }
//
//    // Check if token is expired
//    private Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    // Validate token by checking username and expiration
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//    
//  
//}
