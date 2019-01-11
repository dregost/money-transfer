package com.dregost.moneytransfer.transfer.infrastructure;

import com.google.inject.Inject;
import com.dregost.moneytransfer.common.event.*;
import com.dregost.moneytransfer.transfer.model.*;
import com.dregost.moneytransfer.transfer.model.event.TransferEvent;
import lombok.val;

import java.util.Optional;

public class DbTransferReadOnlyRepository implements TransferReadOnlyRepository {
    private final EventStore<TransferEvent> transferEventStore;

    @Inject
    public DbTransferReadOnlyRepository(final EventStore<TransferEvent> transferEventStore) {
        this.transferEventStore = transferEventStore;
    }

    @Override
    public Optional<Transfer> findById(final TransferId id) {
        val eventStream = transferEventStore.loadStream(id);
        return Optional.of(eventStream)
                .filter(EventStream::isNotEmpty)
                .map(Transfer::fromEventStream);
    }
}
