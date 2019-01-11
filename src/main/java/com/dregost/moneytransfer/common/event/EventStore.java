package com.dregost.moneytransfer.common.event;

import com.dregost.moneytransfer.common.model.*;

public interface EventStore<EVENT extends Event> {
    <ID extends Id> void append(final Aggregate<ID, EVENT> aggregate);

    EventStream<EVENT> loadStream(final Id aggregateId);
}
