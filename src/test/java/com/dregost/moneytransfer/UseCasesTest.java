package com.dregost.moneytransfer;

import lombok.val;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@Test
public class UseCasesTest {
    private final static int PORT = 9001;
    private final static String URL = String.format("http://localhost:%s", PORT);
    private final static String OPEN_ACCOUNT_URL = URL + "/account";
    private final static String GET_ACCOUNT_URL = OPEN_ACCOUNT_URL + "/{id}";
    private final static String TRANSFER_URL = URL + "/transfer";
    private final static String GET_TRANSFER_URL = TRANSFER_URL + "/{id}";
    private static final String ID = "id";
    private static final String BALANCE = "balance";
    private static final String FROM_ACCOUNT = "fromAccountId";
    private static final String TO_ACCOUNT = "toAccountId";
    private static final String AMOUNT = "amount";
    private static final String STATUS = "status";
    private static final String FAILED = "FAILED";
    private ApplicationServer server;

    @BeforeClass
    public void setUp() {
        server = new ApplicationServer(PORT);
        server.start();
    }

    @AfterClass
    public void tearDown() {
        server.stop();
    }

    public void gettingAccount_afterCreatingAccount_shouldReturnAccountDetails() {
        val initialBalance = 100;
        val accountId = given().body(makeOpenAccountBody(initialBalance))
                .when().post(OPEN_ACCOUNT_URL)
                .then().statusCode(201)
                .extract().<String>path(ID);

        given().pathParam(ID, accountId)
                .when().get(GET_ACCOUNT_URL)
                .then().statusCode(200)
                .body(BALANCE, is(initialBalance));
    }

    public void gettingAccount_whenAccountDoesNotExist_shouldReturnNotFound() {
        given().pathParam(ID, "MISSING_ID")
                .when().get(GET_ACCOUNT_URL)
                .then().statusCode(404);
    }

    public void transfer_whenAccountsExistAndHaveSufficientBalances_shouldCompleteSuccessfullyAndChangeAccountBalances() {
        val fromAccountInitialBalance = 500;
        val toAccountInitialBalance = 600;
        val amount = 200;

        val fromAccountId = given().body(makeOpenAccountBody(fromAccountInitialBalance))
                .when().post(OPEN_ACCOUNT_URL)
                .then().statusCode(201)
                .extract().<String>path(ID);
        val toAccountId = given().body(makeOpenAccountBody(toAccountInitialBalance))
                .when().post(OPEN_ACCOUNT_URL)
                .then().statusCode(201)
                .extract().<String>path(ID);
        val transferId = given().body(makeCreateTransferBody(fromAccountId, toAccountId, amount))
                .when().post(TRANSFER_URL)
                .then().statusCode(202)
                .extract().<String>path(ID);

        given().pathParam(ID, fromAccountId)
                .when().get(GET_ACCOUNT_URL)
                .then().statusCode(200)
                .body(BALANCE, is(fromAccountInitialBalance - amount));
        given().pathParam(ID, toAccountId)
                .when().get(GET_ACCOUNT_URL)
                .then().statusCode(200)
                .body(BALANCE, is(toAccountInitialBalance + amount));
        given().pathParam(ID, transferId)
                .when().get(GET_TRANSFER_URL)
                .then().statusCode(200)
                .body(ID, is(transferId))
                .body(FROM_ACCOUNT, is(fromAccountId))
                .body(TO_ACCOUNT, is(toAccountId))
                .body(AMOUNT, is(amount))
                .body(STATUS, is("COMPLETED"));
    }

    public void transfer_whenFromAccountsDoesNotExist_shouldFailAndNotChangeAccountBalance() {
        val toAccountInitialBalance = 600;
        val amount = 200;

        val fromAccountId = "NONEXISTENT_FROM_ID";
        val toAccountId = given().body(makeOpenAccountBody(toAccountInitialBalance))
                .when().post(OPEN_ACCOUNT_URL)
                .then().statusCode(201)
                .extract().<String>path(ID);
        val transferId = given().body(makeCreateTransferBody(fromAccountId, toAccountId, amount))
                .when().post(TRANSFER_URL)
                .then().statusCode(202)
                .extract().<String>path(ID);

        given().pathParam(ID, toAccountId)
                .when().get(GET_ACCOUNT_URL)
                .then().statusCode(200)
                .body(BALANCE, is(toAccountInitialBalance));
        given().pathParam(ID, transferId)
                .when().get(GET_TRANSFER_URL)
                .then().statusCode(200)
                .body(ID, is(transferId))
                .body(FROM_ACCOUNT, is(fromAccountId))
                .body(TO_ACCOUNT, is(toAccountId))
                .body(AMOUNT, is(amount))
                .body(STATUS, is(FAILED));
    }

    public void transfer_whenToAccountsDoesNotExist_shouldFailAndNotChangeAccountBalance() {
        val fromAccountInitialBalance = 400;
        val amount = 200;

        val fromAccountId = given().body(makeOpenAccountBody(fromAccountInitialBalance))
                .when().post(OPEN_ACCOUNT_URL)
                .then().statusCode(201)
                .extract().<String>path(ID);
        val toAccountId = "NONEXISTENT_FROM_ID";
        val transferId = given().body(makeCreateTransferBody(fromAccountId, toAccountId, amount))
                .when().post(TRANSFER_URL)
                .then().statusCode(202)
                .extract().<String>path(ID);

        given().pathParam(ID, fromAccountId)
                .when().get(GET_ACCOUNT_URL)
                .then().statusCode(200)
                .body(BALANCE, is(fromAccountInitialBalance));
        given().pathParam(ID, transferId)
                .when().get(GET_TRANSFER_URL)
                .then().statusCode(200)
                .body(ID, is(transferId))
                .body(FROM_ACCOUNT, is(fromAccountId))
                .body(TO_ACCOUNT, is(toAccountId))
                .body(AMOUNT, is(amount))
                .body(STATUS, is(FAILED));
    }

    public void transfer_whenFromAccountDoesNotHaveSufficientBalance_shouldFailAndNotChangeAccountBalance() {
        val fromAccountInitialBalance = 500;
        val toAccountInitialBalance = 600;
        val amount = 1000;

        val fromAccountId = given().body(makeOpenAccountBody(fromAccountInitialBalance))
                .when().post(OPEN_ACCOUNT_URL)
                .then().statusCode(201)
                .extract().<String>path(ID);
        val toAccountId = given().body(makeOpenAccountBody(toAccountInitialBalance))
                .when().post(OPEN_ACCOUNT_URL)
                .then().statusCode(201)
                .extract().<String>path(ID);
        val transferId = given().body(makeCreateTransferBody(fromAccountId, toAccountId, amount))
                .when().post(TRANSFER_URL)
                .then().statusCode(202)
                .extract().<String>path(ID);

        given().pathParam(ID, fromAccountId)
                .when().get(GET_ACCOUNT_URL)
                .then().statusCode(200)
                .body(BALANCE, is(fromAccountInitialBalance));
        given().pathParam(ID, toAccountId)
                .when().get(GET_ACCOUNT_URL)
                .then().statusCode(200)
                .body(BALANCE, is(toAccountInitialBalance));
        given().pathParam(ID, transferId)
                .when().get(GET_TRANSFER_URL)
                .then().statusCode(200)
                .body(ID, is(transferId))
                .body(FROM_ACCOUNT, is(fromAccountId))
                .body(TO_ACCOUNT, is(toAccountId))
                .body(AMOUNT, is(amount))
                .body(STATUS, is(FAILED));
    }

    private String makeOpenAccountBody(final int initialBalance) {
        return String.format("{'initialBalance' : %s}", initialBalance);
    }

    private String makeCreateTransferBody(final String fromAccountId, final String toAccountId, final int amount) {
        return String.format("{'fromAccountId' : '%s', 'toAccountId' : '%s', 'amount' : '%s'}", fromAccountId, toAccountId, amount);
    }

}