package com.dregost.moneytransfer.transfer.read;

import com.google.common.eventbus.*;
import com.google.inject.*;
import com.dregost.moneytransfer.transfer.model.TransferStatus;
import com.dregost.moneytransfer.transfer.model.event.*;
import lombok.val;

import static com.dregost.moneytransfer.transfer.model.TransferStatus.*;

@Singleton
public class ReadTransferService {
    private final TransferRepository transferRepository;

    @Inject
    public ReadTransferService(final EventBus eventBus, final TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
        eventBus.register(this);
    }

    @Subscribe
    public void created(final TransferCreated transferCreated) {
        val transfer = TransferResponse.builder()
                .id(transferCreated.getTransferId().getValue())
                .fromAccountId(transferCreated.getFromAccountId().getValue())
                .toAccountId(transferCreated.getToAccountId().getValue())
                .amount(transferCreated.getAmount())
                .status(PENDING)
                .build();

        transferRepository.save(transfer);
    }

    @Subscribe
    public void creditRecorded(final CreditRecorded creditRecorded) {
        updateStatus(creditRecorded, COMPLETED);
    }

    @Subscribe
    public void debitRecorded(final DebitRecorded debitRecorded) {
        updateStatus(debitRecorded, DEBITED);
    }

    @Subscribe
    public void refundCreated(final RefundCreated refundCreated) {
        updateStatus(refundCreated, FAILED);
    }

    @Subscribe
    public void transferFailed(final TransferFailed transferFailed) {
        updateStatus(transferFailed, FAILED);
    }

    private void updateStatus(final TransferEvent creditRecorded, final TransferStatus status) {
        transferRepository.findById(creditRecorded.getTransferId().getValue())
                .ifPresent(transfer -> {
                    val updatedTransfer = transfer.withStatus(status);
                    transferRepository.save(updatedTransfer);
                });
    }
}
