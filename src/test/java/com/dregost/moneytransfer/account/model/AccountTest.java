package com.dregost.moneytransfer.account.model;

import com.dregost.moneytransfer.account.model.command.*;
import com.dregost.moneytransfer.account.model.event.*;
import com.dregost.moneytransfer.transfer.model.TransferId;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@Test
public class AccountTest {

    private static final AccountId ACCOUNT_ID = AccountId.of("ACCOUNT_ID");
    private static final TransferId TRANSFER_ID = TransferId.of("TRANSFER_ID");

    public void open_shouldCreateAccount() {
        val initialBalance = BigDecimal.valueOf(100);
        val result = openAccount(initialBalance);

        val softly = new SoftAssertions();
        softly.assertThat(result.getId()).isEqualTo(ACCOUNT_ID);
        softly.assertThat(result.getBalance()).isEqualTo(initialBalance);
        softly.assertAll();
    }

    public void getPendingEvents_afterOpeningAccount_shouldContainAccountOpenedEvent() {
        val initialBalance = BigDecimal.valueOf(100);
        val accountOpened = AccountOpened.builder()
                .accountId(ACCOUNT_ID)
                .initialBalance(initialBalance)
                .build();

        val result = openAccount(initialBalance);

        assertThat(result.getPendingEvents()).contains(accountOpened);
    }

    public void credit_whenCreditAmountIsPositive_shouldIncreaseBalance() {
        val initialBalance = BigDecimal.valueOf(100);
        val account = openAccount(initialBalance);
        val creditAccountCommand = CreditAccountCommand.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(BigDecimal.valueOf(80))
                .build();

        val creditedAccount = account.credit(creditAccountCommand);

        assertThat(creditedAccount.getBalance()).isEqualTo(BigDecimal.valueOf(180));
    }

    public void getPendingEvents_afterSuccessfulCrediting_shouldContainsAccountCredited() {
        val initialBalance = BigDecimal.valueOf(100);
        val account = openAccount(initialBalance);
        val amount = BigDecimal.valueOf(80);
        val creditAccountCommand = CreditAccountCommand.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(amount)
                .build();
        val accountCredited = AccountCredited.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(amount)
                .build();

        val creditedAccount = account.credit(creditAccountCommand);

        assertThat(creditedAccount.getPendingEvents()).contains(accountCredited);
    }

    public void credit_whenCreditAmountIsNegative_shouldThrowException() {
        val initialBalance = BigDecimal.valueOf(100);
        val account = openAccount(initialBalance);
        val creditAccountCommand = CreditAccountCommand.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(BigDecimal.valueOf(-80))
                .build();

        assertThatThrownBy(() -> account.credit(creditAccountCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Negative amount");
    }

    public void debit_whenDebitAmountIsPositiveAndAccountHasSufficientBalance_shouldDecreaseBalance() {
        val initialBalance = BigDecimal.valueOf(100);
        val account = openAccount(initialBalance);
        val debitAccountCommand = DebitAccountCommand.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(BigDecimal.valueOf(80))
                .build();

        val creditedAccount = account.debit(debitAccountCommand);

        assertThat(creditedAccount.getBalance()).isEqualTo(BigDecimal.valueOf(20));
    }

    public void getPendingEvents_afterSuccessfulDebiting_shouldContainsAccountDebited() {
        val initialBalance = BigDecimal.valueOf(100);
        val account = openAccount(initialBalance);
        val amount = BigDecimal.valueOf(80);
        val debitAccountCommand = DebitAccountCommand.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(amount)
                .build();
        val accountDebited = AccountDebited.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(amount)
                .build();

        val creditedAccount = account.debit(debitAccountCommand);

        assertThat(creditedAccount.getPendingEvents()).contains(accountDebited);
    }

    public void debit_whenDebitAmountIsNegative_shouldThrowException() {
        val initialBalance = BigDecimal.valueOf(100);
        val account = openAccount(initialBalance);
        val debitAccountCommand = DebitAccountCommand.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(BigDecimal.valueOf(-80))
                .build();

        assertThatThrownBy(() -> account.debit(debitAccountCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Negative amount");
    }

    public void debit_whenDebitAmountIsPositiveAndAccountHasInsufficientBalance_shouldThrowException() {
        val initialBalance = BigDecimal.valueOf(100);
        val account = openAccount(initialBalance);
        val debitAccountCommand = DebitAccountCommand.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(BigDecimal.valueOf(120))
                .build();

        assertThatThrownBy(() -> account.debit(debitAccountCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("insufficient balance");
    }

    public void refund_whenCreditAmountIsPositive_shouldIncreaseBalance() {
        val initialBalance = BigDecimal.valueOf(100);
        val account = openAccount(initialBalance);
        val refundAccountCommand = RefundCommand.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(BigDecimal.valueOf(80))
                .build();

        val refundedAccount = account.refund(refundAccountCommand);

        assertThat(refundedAccount.getBalance()).isEqualTo(BigDecimal.valueOf(180));
    }

    public void getPendingEvents_afterSuccessfulRefunding_shouldContainsFundsReturned() {
        val initialBalance = BigDecimal.valueOf(100);
        val account = openAccount(initialBalance);
        val amount = BigDecimal.valueOf(80);
        val refundAccountCommand = RefundCommand.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(amount)
                .build();
        val fundsReturned = FundsReturned.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(amount)
                .build();

        val refundedAccount = account.refund(refundAccountCommand);

        assertThat(refundedAccount.getPendingEvents()).contains(fundsReturned);
    }

    public void refund_whenCreditAmountIsNegative_shouldThrowException() {
        val initialBalance = BigDecimal.valueOf(100);
        val account = openAccount(initialBalance);
        val refundAccountCommand = RefundCommand.builder()
                .accountId(ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .amount(BigDecimal.valueOf(-80))
                .build();

        assertThatThrownBy(() -> account.refund(refundAccountCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Negative amount");
    }

    private Account openAccount(BigDecimal initialBalance) {
        val openCommand = OpenAccountCommand.builder()
                .accountId(ACCOUNT_ID)
                .initialBalance(initialBalance)
                .build();

        return Account.open(openCommand);
    }
}