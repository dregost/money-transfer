package com.dregost.moneytransfer.transfer.model;

import com.google.common.eventbus.*;
import com.google.inject.*;
import com.dregost.moneytransfer.account.model.event.*;
import com.dregost.moneytransfer.common.event.EventStore;
import com.dregost.moneytransfer.transfer.model.event.TransferEvent;
import lombok.*;

import java.util.function.*;

@Singleton
public class TransferService {
    private final TransferReadOnlyRepository transferRepository;
    private final EventStore<TransferEvent> eventStore;

    @Inject
    public TransferService(final EventBus eventBus,
                           final TransferReadOnlyRepository transferRepository, EventStore<TransferEvent> eventStore) {
        this.transferRepository = transferRepository;
        this.eventStore = eventStore;
        eventBus.register(this);
    }

    @Subscribe
    public void handleAccountDebited(final AccountDebited accountDebited) {
        transferRepository.findById(accountDebited.getTransferId())
                .ifPresent(transfer -> doAndPublishEvents(transfer, Transfer::markAsDebited));
    }

    @Subscribe
    public void handleAccountDebitFailed(final AccountDebitFailed accountDebitFailed) {
        markAsFailed(accountDebitFailed.getTransferId());
    }

    @Subscribe
    public void handleAccountCredited(final AccountCredited accountCredited) {
        transferRepository.findById(accountCredited.getTransferId())
                .ifPresent(transfer -> doAndPublishEvents(transfer, Transfer::markAsCredited));
    }

    @Subscribe
    public void handleAccountCreditFailed(final AccountCreditFailed accountCreditFailed) {
        markAsFailed(accountCreditFailed.getTransferId());
    }

    private void markAsFailed(final TransferId transferId) {
        transferRepository.findById(transferId)
                .ifPresent(transfer -> doAndPublishEvents(transfer, Transfer::markAsFailed));
    }

    private void doAndPublishEvents(final Transfer transfer, final UnaryOperator<Transfer> transferUnaryOperator) {
        val updatedTransfer = transferUnaryOperator.apply(transfer);
        eventStore.append(updatedTransfer);
    }
}
