package com.dregost.moneytransfer.account.model;

import com.google.common.eventbus.*;
import com.google.inject.*;
import com.dregost.moneytransfer.account.model.command.*;
import com.dregost.moneytransfer.account.model.event.*;
import com.dregost.moneytransfer.common.event.EventStore;
import com.dregost.moneytransfer.transfer.model.event.*;
import io.vavr.control.Try;
import lombok.val;

@Singleton
public class AccountService {
    private final AccountReadOnlyRepository accountRepository;
    private final EventBus eventBus;
    private final EventStore<AccountEvent> eventStore;

    @Inject
    public AccountService(final AccountReadOnlyRepository accountRepository,
                          final EventBus eventBus,
                          final EventStore<AccountEvent> eventStore) {
        this.accountRepository = accountRepository;
        this.eventBus = eventBus;
        this.eventStore = eventStore;
        this.eventBus.register(this);
    }

    @Subscribe
    public void handleTransferCreated(final TransferCreated transferCreated) {
        val fromAccountId = transferCreated.getFromAccountId();
        val toAccountId = transferCreated.getToAccountId();

        val toAccount = accountRepository.findById(toAccountId);
        if(toAccount.isPresent()) {
            val fromAccount = accountRepository.findById(fromAccountId);
            fromAccount
                    .ifPresent(account -> debitAccount(account, transferCreated)
                            .onSuccess(eventStore::append)
                            .onFailure(exception -> publishAccountDebitFailed(exception.getMessage(), transferCreated)));
            if(!fromAccount.isPresent())
                publishAccountDebitFailed("The debited account has not been found.", transferCreated);
        } else {
            publishAccountDebitFailed("The credited account has not been found.", transferCreated);
        }

    }

    @Subscribe
    public void handleDebitRecorded(final DebitRecorded debitRecorded) {
        val toAccountId = debitRecorded.getToAccountId();

        val toAccount = accountRepository.findById(toAccountId);
        toAccount
                .ifPresent(account -> creditAccount(account, debitRecorded)
                        .onSuccess(eventStore::append)
                        .onFailure(exception -> publishAccountCreditFailed(exception.getMessage(), debitRecorded)));

        if(!toAccount.isPresent())
            publishAccountCreditFailed("The credited account has not been found.", debitRecorded);
    }

    @Subscribe
    public void handleRefundCreated(final RefundCreated refundCreated) {
        val fromAccountId = refundCreated.getFromAccountId();

        accountRepository.findById(fromAccountId)
                .ifPresent(account -> refund(account, refundCreated)
                        .onSuccess(eventStore::append)
                        .onFailure(exception -> publishRefundFailed(exception.getMessage(), refundCreated)));
    }

    private void publishAccountDebitFailed(final String description,
                                           final TransferCreated transferCreated) {
        eventBus.post(AccountDebitFailed.builder()
                .accountId(transferCreated.getFromAccountId())
                .transferId(transferCreated.getTransferId())
                .description(description)
                .build());
    }

    private void publishAccountCreditFailed(final String description,
                                            final DebitRecorded debitRecorded) {
        eventBus.post(AccountCreditFailed.builder()
                .accountId(debitRecorded.getToAccountId())
                .transferId(debitRecorded.getTransferId())
                .description(description)
                .build());
    }

    private void publishRefundFailed(final String description,
                                     final RefundCreated refundCreated) {
        eventBus.post(ReturnFundsFailed.builder()
                .accountId(refundCreated.getToAccountId())
                .transferId(refundCreated.getTransferId())
                .description(description)
                .build());
    }

    private Try<Account> debitAccount(final Account account,
                                      final TransferCreated transferCreated) {
        return Try.of(() -> account.debit(DebitAccountCommand.builder()
                .accountId(account.getId())
                .transferId(transferCreated.getTransferId())
                .amount(transferCreated.getAmount())
                .build()));
    }

    private Try<Account> creditAccount(final Account account,
                                       final DebitRecorded transferCreated) {
        return Try.of(() -> account.credit(CreditAccountCommand.builder()
                .accountId(account.getId())
                .transferId(transferCreated.getTransferId())
                .amount(transferCreated.getAmount())
                .build()));
    }

    private Try<Account> refund(final Account account,
                                final RefundCreated refundCreated) {
        return Try.of(() -> account.refund(RefundCommand.builder()
                .accountId(account.getId())
                .transferId(refundCreated.getTransferId())
                .amount(refundCreated.getAmount())
                .build()));
    }
}
