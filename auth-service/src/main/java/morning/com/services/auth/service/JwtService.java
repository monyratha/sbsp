package morning.com.services.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final long ttlMillis;
    private final String issuer;
    private final String kid;

    public JwtService(
            @Value("${security.jwt.rsa.public:}") String publicKeyStr,
            @Value("${security.jwt.rsa.private:}") String privateKeyStr,
            @Value("${security.jwt.ttl:PT15M}") String ttl,
            @Value("${security.jwt.issuer:auth-service}") String issuer
    ) {
        if (!publicKeyStr.isBlank() && !privateKeyStr.isBlank()) {
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                this.publicKey = (RSAPublicKey) kf.generatePublic(
                        new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr)));
                this.privateKey = (RSAPrivateKey) kf.generatePrivate(
                        new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr)));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new IllegalStateException("Invalid RSA keys", e);
            }
        } else {
            KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.RS256);
            this.privateKey = (RSAPrivateKey) pair.getPrivate();
            this.publicKey = (RSAPublicKey) pair.getPublic();
        }
        this.kid = UUID.randomUUID().toString();
        this.ttlMillis = Duration.parse(ttl).toMillis();
        this.issuer = issuer;
    }

    public String generateToken(String username) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMillis))
                .claim("scope", "user.read user.write")
                .claim("aud", java.util.List.of("user-service"))
                .setHeaderParam("kid", kid)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new IllegalArgumentException("auth.invalid.token", e);
        }
    }

    public Map<String, Object> jwk() {
        return Map.of(
                "kty", "RSA",
                "n", base64Url(publicKey.getModulus()),
                "e", base64Url(publicKey.getPublicExponent()),
                "kid", kid,
                "alg", "RS256"
        );
    }

    private String base64Url(java.math.BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes[0] == 0) {
            bytes = java.util.Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public long ttlMillis() {
        return ttlMillis;
    }
}

