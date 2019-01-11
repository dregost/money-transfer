package com.dregost.moneytransfer.transfer.model;

import com.dregost.moneytransfer.common.event.EventStream;
import com.dregost.moneytransfer.common.model.Aggregate;
import com.dregost.moneytransfer.transfer.model.command.CreateTransferCommand;
import com.dregost.moneytransfer.transfer.model.event.*;
import io.vavr.API;
import lombok.*;
import lombok.experimental.Delegate;

import static com.dregost.moneytransfer.transfer.model.TransferStatus.*;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

public class Transfer extends Aggregate<TransferId, TransferEvent> {
    @Delegate
    private TransferDetails transferDetails;
    @Getter
    private TransferStatus status;

    private Transfer(final CreateTransferCommand createTransferCommand) {
        val transferCreated = TransferCreated.of(TransferDetails.builder()
                .transferId(createTransferCommand.getTransferId())
                .amount(createTransferCommand.getAmount())
                .toAccountId(createTransferCommand.getToAccountId())
                .fromAccountId(createTransferCommand.getFromAccountId())
                .build());
        addPendingEvent(transferCreated);
    }

    private Transfer(final EventStream<TransferEvent> eventStream) {
        apply(eventStream);
    }

    public static Transfer create(final CreateTransferCommand createTransferCommand) {
        return new Transfer(createTransferCommand);
    }

    public static Transfer fromEventStream(final EventStream<TransferEvent> eventStream){
        return new Transfer(eventStream);
    }

    public Transfer markAsDebited() {
        val debitedRecorded = DebitRecorded.of(transferDetails);
        addPendingEvent(debitedRecorded);
        return this;
    }

    public Transfer markAsCredited() {
        val creditRecorded = CreditRecorded.of(transferDetails);
        addPendingEvent(creditRecorded);
        return this;
    }

    public Transfer markAsFailed() {
        if(status == TransferStatus.DEBITED) {
            val refundCreated = RefundCreated.of(transferDetails);
            addPendingEvent(refundCreated);
        } else {
            val transferFailed = TransferFailed.of(transferDetails);
            addPendingEvent(transferFailed);
        }
        return this;
    }

    private Transfer created(final TransferCreated transferCreated) {
        transferDetails = transferCreated.getDetails();
        status = PENDING;
        return this;
    }

    private Transfer debitRecorded() {
        status = DEBITED;
        return this;
    }

    private Transfer creditRecorded() {
        status = COMPLETED;
        return this;
    }

    private Transfer transferFailed() {
        status = FAILED;
        return this;
    }

    private Transfer refundCreated() {
        status = FAILED;
        return this;
    }

    @Override
    public TransferId getId() {
        return transferDetails.getTransferId();
    }

    @Override
    protected void apply(final TransferEvent event) {
        Match(event).of(
                API.Case(API.$(instanceOf(TransferCreated.class)), this::created),
                API.Case(API.$(instanceOf(DebitRecorded.class)), this::debitRecorded),
                API.Case(API.$(instanceOf(CreditRecorded.class)), this::creditRecorded),
                API.Case(API.$(instanceOf(TransferFailed.class)), this::transferFailed),
                API.Case(API.$(instanceOf(RefundCreated.class)), this::refundCreated));
    }
}
