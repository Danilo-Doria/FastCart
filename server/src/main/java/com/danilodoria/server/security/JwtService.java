package com.danilodoria.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long accessTokenExpiration;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.access-token-expiration-ms}") long accessTokenExpiration) {

        // Se convierte el String secret en una clave criptográfica, convirtiendo el texto a bytes,
        // UTF_8 especifica cómo convertir los caracteres a bytes
        // Keys.hmacShaKeyFor, JWT utiliza esos bytes para construir una clave apropiada para un algoritmo HMAC
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
    }

    // Generar el JWT
    // UserDetails es una interfaz de Spring Security que representa al usuario autenticado
    public String generateAccessToken(UserDetails user) {
        Date now = new Date();

        // Un JWT normalmente tiene tres partes: HEADER.PAYLOAD.SIGNATURE
        // El subject identifica a quién pertenece el token
        // issuedAt Indica cuándo se creó el token
        // expiration define cuándo expira el token
        // signWith Aquí esta firmando el JWT
        // compact lo construido en el String JWT
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpiration))
                .signWith(signingKey)
                .compact();
    }

    // Hace lo contrario recibe un JWT y extrae el usuario que está dentro
    // parseClaims su objetivo es parsear y verificar el JWT
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    // Claims claims = parseClaims(token) Parsea el token y verifica su firma, sino no continua
    public boolean isValid(String token, UserDetails user) {
        Claims claims = parseClaims(token);
        return claims.getSubject().equals(user.getUsername())
                && claims.getExpiration().after(new Date());
    }

    // metodo interno su objetivo es parsear y verificar el JWT
    // Jwts.parser() inicializa el constructor del analizador (parser) de la librería JJWT
    // verifyWith(signingKey) le entrega al analizador la clave secreta criptográfica
    // con la que el servidor firmó originalmente los tokens

    /*
    Toma la cadena de texto del JWT (token), la divide en sus tres partes, la decodifica de Base64Url y
    ejecuta las validaciones:
    Verifica si la firma es auténtica usando la clave proveída.
    Verifica si el token ya expiró (exp) comparándolo con el reloj del servidor.
    Resultado: Si todo está bien, devuelve un objeto contenedor llamado Jws<Claims>.
     */

    // getPayload() Extrae exclusivamente la sección central del JWT (los datos del usuario).
    // Devuelve el objeto Claims, que actúa como un mapa de Java del cual puedes extraer datos específicos
    // como el ID de usuario (.getSubject()), roles o cualquier atributo personalizado.
    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).getPayload();
    }
}
