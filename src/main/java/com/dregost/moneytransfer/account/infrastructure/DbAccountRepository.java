package com.dregost.moneytransfer.account.infrastructure;

import com.google.inject.*;
import com.dregost.moneytransfer.account.read.*;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.generated.tables.Account;
import org.jooq.generated.tables.records.AccountRecord;

import java.util.Optional;

@Singleton
public class DbAccountRepository implements AccountRepository {
    private final DSLContext dsl;

    @Inject
    public DbAccountRepository(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public AccountResponse save(final AccountResponse entity) {
        val account = findRecord(entity.getId())
                .orElseGet(() -> dsl.newRecord(Account.ACCOUNT));
        account.setId(entity.getId());
        account.setBalance(entity.getBalance());
        account.store();
        return entity;
    }

    @Override
    public Optional<AccountResponse> findById(final String id) {
        return findRecord(id)
                .map(record -> AccountResponse.builder()
                        .id(record.getId())
                        .balance(record.getBalance())
                        .build());
    }

    private Optional<AccountRecord> findRecord(final String id) {
        return Optional.ofNullable(dsl.selectFrom(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.eq(id))
                .fetchOne());
    }
}
