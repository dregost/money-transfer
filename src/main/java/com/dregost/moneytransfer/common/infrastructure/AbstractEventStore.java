package com.dregost.moneytransfer.common.infrastructure;

import com.google.common.eventbus.EventBus;
import com.dregost.moneytransfer.common.event.*;
import com.dregost.moneytransfer.common.model.*;
import lombok.*;
import org.jooq.UpdatableRecord;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEventStore<EVENT extends Event, RECORD extends UpdatableRecord<?>> implements EventStore<EVENT> {
    private final EventBus eventBus;

    @Override
    public <ID extends Id> void append(final Aggregate<ID, EVENT> aggregate) {
        val pendingEvents = aggregate.getPendingEvents();
        val aggregateId = aggregate.getId();
        pendingEvents.forEach(event -> {
            val entity = makeEntity(aggregateId, event);
            entity.store();
        });
        pendingEvents.forEach(eventBus::post);
        aggregate.markEventsAsCommitted();
    }

    @Override
    public EventStream<EVENT> loadStream(final Id aggregateId) {
        val events = findEvents(aggregateId);
        return EventStream.of(events);
    }

    protected abstract <ID extends Id> RECORD makeEntity(final ID aggregateId, final EVENT event);

    protected abstract List<EVENT> findEvents(final Id aggregateId);
}
