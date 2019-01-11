package com.dregost.moneytransfer.account.model;

import com.dregost.moneytransfer.common.model.Aggregate;
import com.dregost.moneytransfer.account.model.command.*;
import com.dregost.moneytransfer.account.model.event.*;
import com.dregost.moneytransfer.common.event.EventStream;
import io.vavr.API;
import lombok.*;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Getter
public class Account extends Aggregate<AccountId, AccountEvent> {
    private AccountId id;
    private BigDecimal balance;

    private Account(final OpenAccountCommand openAccountCommand) {
        val accountOpened = AccountOpened.builder()
                .accountId(openAccountCommand.getAccountId())
                .initialBalance(openAccountCommand.getInitialBalance())
                .build();
        addPendingEvent(accountOpened);
    }

    private Account(final EventStream<AccountEvent> eventStream) {
        apply(eventStream);
    }

    public static Account open(final OpenAccountCommand openAccountCommand) {
        return new Account(openAccountCommand);
    }

    public static Account fromEventStream(final EventStream<AccountEvent> eventStream) {
        return new Account(eventStream);
    }

    public Account credit(final CreditAccountCommand creditAccountCommand) {
        checkArgument(isPositive(creditAccountCommand.getAmount()), "Negative amount cannot be credited.");
        val accountCredited = AccountCredited.builder()
                .accountId(creditAccountCommand.getAccountId())
                .transferId(creditAccountCommand.getTransferId())
                .amount(creditAccountCommand.getAmount())
                .build();
        addPendingEvent(accountCredited);

        return this;
    }

    public Account debit(final DebitAccountCommand debitAccountCommand) {
        checkArgument(isPositive(debitAccountCommand.getAmount()), "Negative amount cannot be debited.");
        checkArgument(isLessThanOrEqualToBalance(debitAccountCommand.getAmount()), "Account has insufficient balance.");
        val accountDebited = AccountDebited.builder()
                .accountId(debitAccountCommand.getAccountId())
                .transferId(debitAccountCommand.getTransferId())
                .amount(debitAccountCommand.getAmount())
                .build();
        addPendingEvent(accountDebited);

        return this;
    }

    public Account refund(final RefundCommand refundCommand) {
        checkArgument(isPositive(refundCommand.getAmount()), "Negative amount cannot be refunded.");
        val fundsReturned = FundsReturned.builder()
                .accountId(refundCommand.getAccountId())
                .transferId(refundCommand.getTransferId())
                .amount(refundCommand.getAmount())
                .build();
        addPendingEvent(fundsReturned);

        return this;
    }

    @Override
    protected void apply(final AccountEvent event) {
        Match(event).of(
                API.Case(API.$(instanceOf(AccountOpened.class)), this::opened),
                API.Case(API.$(instanceOf(AccountCredited.class)), this::credited),
                API.Case(API.$(instanceOf(AccountDebited.class)), this::debited),
                API.Case(API.$(instanceOf(FundsReturned.class)), this::fundsReturned));
    }

    private Account opened(final AccountOpened accountOpened) {
        id = accountOpened.getAccountId();
        balance = accountOpened.getInitialBalance();
        return this;
    }

    private Account credited(final AccountCredited accountCredited) {
        balance = balance.add(accountCredited.getAmount());
        return this;
    }

    private Account debited(final AccountDebited accountDebited) {
        balance = balance.subtract(accountDebited.getAmount());
        return this;
    }

    private Account fundsReturned(final FundsReturned fundsReturned) {
        balance = balance.add(fundsReturned.getAmount());
        return this;
    }

    private boolean isPositive(@NonNull final BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isLessThanOrEqualToBalance(@NonNull final BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }
}
