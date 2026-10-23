package mobile.tests;

import base.TestBaseMob;
import config.TestConfig;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

@Epic("Мобильное приложение")
@Feature("Главная страница")
@Story("Отображение основных кнопок на главной странице")
@Owner("Elena Yatsenko")
public class MobileMainPageTest extends TestBaseMob {

    @Test
    @DisplayName("MOB-MAIN-001: Отображение основных кнопок на главной странице")
    @Description("""
            Цель: Проверить корректное отображение основных кнопок на главной странице мобильного приложения.
            Дано: Неавторизованный пользователь, приложение запущено. В ходе теста выполняется авторизация по номеру телефона и паролю.
            ОР: После авторизации на главной странице отображаются кнопки: 'Оплатить', 'Передать показания', 'Информация о счете', 'Открепить счет'.
            """)
    @Tags({@Tag("regression"), @Tag("mobile")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldDisplayAllActionButtonsOnMainPage() {
        step("MOB: Логинимся в мобильном приложении и переходим на 'Главную' страницу", () -> {
            loginScreen
                    .closeUpdateModalIfPresent()
                    .clickSearch()
                    .enterLogin(TestConfig.getTestUserPhone())
                    .enterPassword(TestConfig.getTestUserPassword())
                    .clickLogin()
                    .closePaymentsConfirmationModal();
        });
        step("MOB: проверяем наличие кнопок: " +
                "Оплатить, Передать показания, Информация о счете, Открепить счет", () -> {
            mainScreen.verifyAllActionButtonsVisible();
        });
    }
}
