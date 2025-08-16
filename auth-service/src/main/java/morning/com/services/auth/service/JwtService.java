package morning.com.services.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;
    private final long ttlMillis;
    private final String issuer;
    private final String keyId;

    public JwtService(
            @Value("${security.jwt.private-key:}") String privateKey,
            @Value("${security.jwt.public-key:}") String publicKey,
            @Value("${security.jwt.key-id:}") String keyId,
            @Value("${security.jwt.ttl:PT15M}") String ttl,
            @Value("${security.jwt.issuer:auth-service}") String issuer
    ) {
        RSAPrivateKey priv;
        RSAPublicKey pub;
        if (!privateKey.isBlank() && !publicKey.isBlank()) {
            try {
                var kf = KeyFactory.getInstance("RSA");
                priv = (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Decoders.BASE64.decode(privateKey)));
                pub = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(Decoders.BASE64.decode(publicKey)));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to load RSA keys", e);
            }
        } else {
            try {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);
                KeyPair kp = kpg.generateKeyPair();
                priv = (RSAPrivateKey) kp.getPrivate();
                pub = (RSAPublicKey) kp.getPublic();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to generate RSA keys", e);
            }
        }
        this.privateKey = priv;
        this.publicKey = pub;
        this.keyId = (keyId == null || keyId.isBlank()) ? UUID.randomUUID().toString() : keyId;
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

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public long ttlMillis() {
        return ttlMillis;
    }

    public Map<String, Object> jwk() {
        Map<String, Object> jwk = new HashMap<>();
        jwk.put("kty", "RSA");
        jwk.put("n", base64Url(publicKey.getModulus()));
        jwk.put("e", base64Url(publicKey.getPublicExponent()));
        jwk.put("kid", keyId);
        jwk.put("alg", "RS256");
        return jwk;
    }

    private static String base64Url(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            bytes = tmp;
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}