package com.dregost.moneytransfer.common.infrastructure;

import com.google.common.eventbus.EventBus;
import com.google.gson.*;
import com.google.inject.AbstractModule;
import com.dregost.moneytransfer.account.model.event.AccountEvent;
import com.dregost.moneytransfer.transfer.model.event.TransferEvent;
import lombok.*;
import org.jooq.DSLContext;

@AllArgsConstructor(staticName = "of")
public class CommonModule extends AbstractModule {
    private final EventBus eventBus;
    private final DSLContext dsl;

    @Override
    protected void configure() {
        val gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(AccountEvent.class, PolymorphicTypeAdapter.<AccountEvent>of(new Gson()));
        gsonBuilder.registerTypeHierarchyAdapter(TransferEvent.class, PolymorphicTypeAdapter.<TransferEvent>of(new Gson()));
        val gson = gsonBuilder.create();
        bind(Gson.class).toInstance(gson);
        bind(EventBus.class).toInstance(eventBus);
        bind(DSLContext.class).toInstance(dsl);
    }
}
