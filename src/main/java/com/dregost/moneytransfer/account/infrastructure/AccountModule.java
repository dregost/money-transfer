package com.dregost.moneytransfer.account.infrastructure;

import com.google.inject.*;
import com.dregost.moneytransfer.account.model.*;
import com.dregost.moneytransfer.account.model.event.AccountEvent;
import com.dregost.moneytransfer.account.read.*;
import com.dregost.moneytransfer.common.event.EventStore;

public class AccountModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AccountReadOnlyRepository.class).to(DbAccountReadOnlyRepository.class);
        bind(new TypeLiteral<EventStore<AccountEvent>>() {
        }).to(AccountEventStore.class);
        bind(AccountRepository.class).to(DbAccountRepository.class);
        bind(AccountService.class).asEagerSingleton();
        bind(ReadAccountService.class).asEagerSingleton();
        requireBinding(AccountResource.class);
    }
}
