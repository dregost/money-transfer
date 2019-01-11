package com.dregost.moneytransfer.account.read;

import com.google.common.eventbus.*;
import com.google.inject.*;
import com.dregost.moneytransfer.account.model.event.*;
import lombok.val;

@Singleton
public class ReadAccountService {
    private final AccountRepository accountRepository;

    @Inject
    public ReadAccountService(final EventBus eventBus, final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        eventBus.register(this);
    }

    @Subscribe
    public void opened(final AccountOpened accountOpened) {
        val accountResponse = AccountResponse.builder()
                .id(accountOpened.getAccountId().getValue())
                .balance(accountOpened.getInitialBalance())
                .build();

        accountRepository.save(accountResponse);
    }

    @Subscribe
    public void credited(final AccountCredited accountCredited) {
        accountRepository.findById(accountCredited.getAccountId().getValue())
                .ifPresent(account -> {
                    val updatedAccount = account.credit(accountCredited.getAmount());
                    accountRepository.save(updatedAccount);
                });
    }

    @Subscribe
    public void debited(final AccountDebited accountDebited) {
        accountRepository.findById(accountDebited.getAccountId().getValue())
                .ifPresent(account -> {
                    val updatedAccount = account.debit(accountDebited.getAmount());
                    accountRepository.save(updatedAccount);
                });
    }

    @Subscribe
    public void fundsReturned(final FundsReturned fundsReturned) {
        accountRepository.findById(fundsReturned.getAccountId().getValue())
                .ifPresent(account -> {
                    val updatedAccount = account.credit(fundsReturned.getAmount());
                    accountRepository.save(updatedAccount);
                });
    }
}
