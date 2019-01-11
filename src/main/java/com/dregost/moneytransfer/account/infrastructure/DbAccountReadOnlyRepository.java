package com.dregost.moneytransfer.account.infrastructure;

import com.google.inject.Inject;
import com.dregost.moneytransfer.account.model.*;
import com.dregost.moneytransfer.account.model.event.AccountEvent;
import com.dregost.moneytransfer.common.event.*;
import lombok.val;

import java.util.Optional;

public class DbAccountReadOnlyRepository implements AccountReadOnlyRepository {
    private final EventStore<AccountEvent> eventStore;

    @Inject
    public DbAccountReadOnlyRepository(final EventStore<AccountEvent> eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public Optional<Account> findById(final AccountId id) {
        val accountEventEventStream = eventStore.loadStream(id);
        return Optional.of(accountEventEventStream)
                .filter(EventStream::isNotEmpty)
                .map(Account::fromEventStream);
    }
}
