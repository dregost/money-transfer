package com.dregost.moneytransfer.common.model;

import com.dregost.moneytransfer.common.event.EventStream;
import lombok.Getter;

import java.util.*;

@Getter
public abstract class Aggregate<ID extends Id, EVENT extends Event> {
    private final List<EVENT> pendingEvents;

    protected Aggregate() {
        this.pendingEvents = new ArrayList<>();
    }

    public abstract ID getId();

    public void markEventsAsCommitted() {
        pendingEvents.clear();
    }

    protected void apply(final EventStream<EVENT> eventStream) {
        eventStream.forEach(this::apply);
    }

    protected void addPendingEvent(final EVENT event) {
        pendingEvents.add(event);
        apply(event);
    }

    protected abstract void apply(final EVENT event);
}
