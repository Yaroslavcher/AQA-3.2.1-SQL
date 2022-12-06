package ru.netology.banklogin.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
    private SelenideElement header = $("[data-test-id=dashboard]");

    public DashboardPage() {
        header.shouldBe(visible);
    }
}