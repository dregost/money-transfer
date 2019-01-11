package com.dregost.moneytransfer.account.infrastructure;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.inject.*;
import com.dregost.moneytransfer.account.model.event.AccountEvent;
import com.dregost.moneytransfer.common.infrastructure.AbstractEventStore;
import com.dregost.moneytransfer.common.model.Id;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.AccountEventRecord;

import java.util.List;

@Singleton
public class AccountEventStore extends AbstractEventStore<AccountEvent, AccountEventRecord> {
    private final DSLContext dsl;
    private final Gson gson;

    @Inject
    public AccountEventStore(final EventBus eventBus, final DSLContext dsl, final Gson gson) {
        super(eventBus);
        this.dsl = dsl;
        this.gson = gson;
    }

    protected <ID extends Id> AccountEventRecord makeEntity(final ID aggregateId, final AccountEvent event) {
        val entity = dsl.newRecord(org.jooq.generated.tables.AccountEvent.ACCOUNT_EVENT);
        entity.setAggregateId(aggregateId.getValue());
        entity.setEvent(gson.toJson(event));
        return entity;
    }

    protected List<AccountEvent> findEvents(final Id aggregateId) {
        return dsl.selectFrom(org.jooq.generated.tables.AccountEvent.ACCOUNT_EVENT)
                .where(org.jooq.generated.tables.AccountEvent.ACCOUNT_EVENT.AGGREGATE_ID.eq(aggregateId.getValue()))
                .fetch()
                .map(record -> gson.fromJson(record.getEvent(), AccountEvent.class));
    }
}
