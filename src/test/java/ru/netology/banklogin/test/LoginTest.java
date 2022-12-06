package ru.netology.banklogin.test;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.banklogin.data.DataHelper;
import ru.netology.banklogin.data.SQLHelper;
import ru.netology.banklogin.page.LoginPage;
import ru.netology.banklogin.page.VerificationPage;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static ru.netology.banklogin.data.SQLHelper.cleanDatabase;

public class LoginTest {
    LoginPage loginPage;
    SelenideElement header = $("[data-test-id=dashboard]");
    DataHelper.AuthInfo authInfo;
    VerificationPage verificationPage;
    DataHelper.VerificationCode verificationCode;

    @BeforeEach
    void setUp() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @AfterAll
    static void teardown() {
        cleanDatabase();
    }

    @Test
    @DisplayName("1. Successfully login with existing login and password from base")
    void shouldLogin() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    @DisplayName("2. Error notification displayed if user absent in base")
    void shouldNotifyErrorIfRandomUserAbsentInBase() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotificationVisibility();
    }

    @Test
    @DisplayName("3. Error notification displayed if valid login and password but random code")
    void shouldNotifyErrorIfValidUserButRandomCode() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotificationVisibility();
    }

    @Test
    @DisplayName("4. Block notification displayed after three invalid logins")
    void shouldNotLoginAfterThreeInvalidLogins() {
        authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotificationVisibility();
        authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotificationVisibility();
        authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorTextVisible();
        loginPage.compareErrorTexts();
    }

    @Test
    @DisplayName("5. Block notification displayed at 4th valid login")
    void shouldNotLoginForthLoginWithOneValidUser() {
        authInfo = DataHelper.getAuthInfoWithTestData();
        verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
        int i = 0;
        while (i != 3) {
            i++;
            loginPage = open("http://localhost:9999", LoginPage.class);
            authInfo = DataHelper.getAuthInfoWithTestData();
            verificationPage = loginPage.validLogin(authInfo);
            verificationPage.verifyVerificationPageVisibility();
            var verificationCode_ = SQLHelper.getVerificationCode();
            verificationPage.verify(verificationCode_.getCode());
        }
        verificationPage.verifyErrorNotificationVisibility();
    }
}