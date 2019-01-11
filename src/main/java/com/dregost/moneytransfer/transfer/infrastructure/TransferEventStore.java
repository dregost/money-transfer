package com.dregost.moneytransfer.transfer.infrastructure;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.inject.*;
import com.dregost.moneytransfer.common.infrastructure.AbstractEventStore;
import com.dregost.moneytransfer.common.model.Id;
import com.dregost.moneytransfer.transfer.model.event.TransferEvent;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.TransferEventRecord;

import java.util.List;

import static org.jooq.generated.tables.TransferEvent.TRANSFER_EVENT;

@Singleton
public class TransferEventStore extends AbstractEventStore<TransferEvent, TransferEventRecord> {
    private final DSLContext dsl;
    private final Gson gson;

    @Inject
    public TransferEventStore(final EventBus eventBus, final DSLContext dsl, final Gson gson) {
        super(eventBus);
        this.dsl = dsl;
        this.gson = gson;
    }

    protected <ID extends Id> TransferEventRecord makeEntity(final ID aggregateId, final TransferEvent event) {
        val entity = dsl.newRecord(TRANSFER_EVENT);
        entity.setAggregateId(aggregateId.getValue());
        entity.setEvent(gson.toJson(event));
        return entity;
    }

    protected List<TransferEvent> findEvents(final Id aggregateId) {
        return dsl.selectFrom(TRANSFER_EVENT)
                .where(TRANSFER_EVENT.AGGREGATE_ID.eq(aggregateId.getValue()))
                .fetch()
                .map(record -> gson.fromJson(record.getEvent(), TransferEvent.class));
    }
}
