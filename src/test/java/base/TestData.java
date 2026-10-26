package base;

public class TestData {
    // === ЭЛЕКТРИЧЕСТВО ===
    // Однотарифный ПУ
    public static final int SINGLE_TARIFF_METER = 944099;
    // Двухтарифный ПУ (день/ночь)
    public static final int TWO_TARIFF_METER = 790688;
    // Трёхтарифный ПУ (пик/полупик/ночь)
    public static final int THREE_TARIFF_METER = 807389;

    // === ВОДА ===
    public static final int WATER_METER = 454554887;

    // === ФКР ===
    public static final int FKR_METER = 441743243;
    public static final int EMPTY_METER = 441743243;

    // === Не существует аккаунт ===
    public static final int OTHER_USER_ACCOUNT = 666666666;

    // === Сообщения API ===
    public static final String SUCCESS_SUBMISSION_MESSAGE = "Показания переданы успешно";
    public static final String INVALID_READINGS_ERROR_MESSAGE = "Ошибка сохранения показания. Проверьте корректность переданных данных.";

    // === Параметры отправки ===
    public static final Long READING_INCREMENT = 10L;
    public static final Integer VALID_RECEIVING_METHOD = 41;

    // === AllMeters: даты и сообщения ===
    public static final String DATE_START = "2026-04-01";
    public static final String DATE_END = "2026-11-01";
    public static final String ERROR_MISSING = "Отсутствует обязательный параметр";
}
