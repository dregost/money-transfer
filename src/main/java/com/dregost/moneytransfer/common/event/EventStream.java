package com.dregost.moneytransfer.common.event;

import com.dregost.moneytransfer.common.model.Event;
import lombok.Value;

import java.util.List;
import java.util.function.Consumer;

@Value(staticConstructor = "of")
public class EventStream<EVENT extends Event> {
    private List<EVENT> events;

    public void forEach(final Consumer<EVENT> consumer) {
        events.forEach(consumer);
    }

    public boolean isEmpty(){
        return events.isEmpty();
    }

    public boolean isNotEmpty(){
        return !isEmpty();
    }
}
