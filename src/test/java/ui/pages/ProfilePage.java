package ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class ProfilePage {
    private final  SelenideElement toSignOut = $("#toSignOut");
    private final SelenideElement signOutButton = $("#cSignOutConfirm");
    private final SelenideElement firstAndLastName = $("#lFirstAndLastName");


    // Действия
    @Step("UI: Открыть страницу профиля")
    public ProfilePage openProfilePage(){
        open("/profile");
    return this;
    }

    @Step("[UI] Нажать кнопку 'Выйти в модальном окне'")
    public ProfilePage clickSignOutButton() {
        signOutButton.shouldBe(visible).click();
        return this;
    }

    @Step("[UI] Нажать кнопку 'Выйти из аккаунта'")
    public ProfilePage clickToSignOut() {
        toSignOut.shouldBe(visible).click();
        return this;
    }

    public String getUserHeaderNameText() {
        return firstAndLastName
                .shouldBe(visible)
                .shouldNotHave(exactText(""))           // ждёт, что текст появится
                .getText()
                .trim()
                .replaceAll("\\s+", " ");
    }

    // Проверки

}
