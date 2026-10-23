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
@Feature("Авторизация")
@Story("Успешный вход по логину и паролю")
@Owner("Elena Yatsenko")
public class MobileLoginTest extends TestBaseMob {

    @Test
    @DisplayName("MOB-LOGIN-001: Успешная авторизация по номеру телефона и паролю")
    @Description("""
            Цель: Проверить успешную авторизацию в мобильном приложении по логину и паролю.
            Дано: Неавторизованный пользователь, приложение запущено.
            ОР: Пользователь успешно авторизуется, выполняется переход на Главную страницу.
            """)
    @Tags({@Tag("mobile"), @Tag("smoke")})
    @Severity(SeverityLevel.BLOCKER)
    public void shouldLoginWithValidCredentials() {
        step("MOB: закрываем модальное окно об обновлении приложения, если оно отображается", () -> {
            loginScreen.closeUpdateModalIfPresent();
        });
        step("MOB: открываем страницу авторизации", () -> {
            loginScreen.clickSearch();
        });
        step("MOB: вводим логин и пароль", () -> {
            loginScreen
                    .enterLogin(TestConfig.getTestUserPhone())
                    .enterPassword(TestConfig.getTestUserPassword());
        });
        step("MOB: Нажимаем кнопку 'Войти'", () -> {
            loginScreen.clickLogin();
        });
        step("MOB: Закрыть информационное окно о платежах в ФКР, если оно отображается", () -> {
            loginScreen.closePaymentsConfirmationModal();
        });
        step("MOB: Проверить, что авторизация выполнена — отображается Главная страница", () -> {
            mainScreen.verifyPageTitle();
        });
    }
}
