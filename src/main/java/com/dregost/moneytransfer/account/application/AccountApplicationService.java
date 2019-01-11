package com.dregost.moneytransfer.account.application;

import com.google.inject.Inject;
import com.dregost.moneytransfer.account.model.*;
import com.dregost.moneytransfer.account.model.command.OpenAccountCommand;
import com.dregost.moneytransfer.account.model.event.AccountEvent;
import com.dregost.moneytransfer.account.read.*;
import com.dregost.moneytransfer.common.IdGenerator;
import com.dregost.moneytransfer.common.event.EventStore;
import lombok.val;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountApplicationService {
    private final IdGenerator idGenerator;
    private final EventStore<AccountEvent> eventStore;
    private final AccountRepository accountResponseRepository;

    @Inject
    public AccountApplicationService(final IdGenerator idGenerator,
                                     final EventStore<AccountEvent> eventStore,
                                     final AccountRepository accountResponseRepository) {
        this.idGenerator = idGenerator;
        this.eventStore = eventStore;
        this.accountResponseRepository = accountResponseRepository;
    }

    public AccountResponse openAccount(final BigDecimal initialBalance) {
        val id = idGenerator.generateId(AccountId::of);
        val openAccountCommand = OpenAccountCommand.builder()
                .accountId(id)
                .initialBalance(initialBalance)
                .build();

        val account = Account.open(openAccountCommand);

        eventStore.append(account);
        return toResponse(account);
    }

    public Optional<AccountResponse> getDetails(final String id) {
        return accountResponseRepository.findById(id);
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId().getValue())
                .balance(account.getBalance())
                .build();
    }
}
