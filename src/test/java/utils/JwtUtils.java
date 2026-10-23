package utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

/**
 * Утилита для декодирования JWT без проверки подписи.
 * Подходит для тестов, где токен получен от доверенного сервиса.
 */
public final class JwtUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JwtUtils() {
    }

    /**
     * Декодирует payload (вторую часть) JWT в Map.
     */
    public static Map<String, Object> decodePayload(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Токен пустой или null");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Невалидный JWT: ожидается 3 части, получено " + parts.length
            );
        }

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            return MAPPER.readValue(decoded, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось декодировать JWT payload", e);
        }
    }

    /**
     * Извлекает claim "sub" (идентификатор пользователя).
     */
    public static String extractSub(String token) {
        Object sub = decodePayload(token).get("sub");
        if (sub == null) {
            throw new IllegalStateException("В токене отсутствует claim 'sub'");
        }
        return sub.toString();
    }

    /**
     * Извлекает claim "sub" как Long.
     */
    public static Long extractUserId(String token) {
        return Long.parseLong(extractSub(token));
    }

    /**
     * Извлекает произвольный claim.
     */
    public static Object extractClaim(String token, String claimName) {
        return decodePayload(token).get(claimName);
    }
}