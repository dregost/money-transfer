package com.dregost.moneytransfer.transfer.infrastructure;

import com.google.inject.*;
import com.dregost.moneytransfer.common.event.EventStore;
import com.dregost.moneytransfer.transfer.model.*;
import com.dregost.moneytransfer.transfer.model.event.TransferEvent;
import com.dregost.moneytransfer.transfer.read.*;

public class TransferModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<EventStore<TransferEvent>>() {
        }).to(TransferEventStore.class);
        bind(TransferReadOnlyRepository.class).to(DbTransferReadOnlyRepository.class);
        bind(TransferRepository.class).to(DbTransferRepository.class);
        bind(TransferService.class).asEagerSingleton();
        bind(ReadTransferService.class).asEagerSingleton();
        requireBinding(TransferResource.class);
    }
}
