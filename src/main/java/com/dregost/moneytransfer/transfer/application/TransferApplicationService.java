package com.dregost.moneytransfer.transfer.application;

import com.google.inject.Inject;
import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.common.event.EventStore;
import com.dregost.moneytransfer.common.IdGenerator;
import com.dregost.moneytransfer.transfer.infrastructure.CreateTransferRequest;
import com.dregost.moneytransfer.transfer.model.*;
import com.dregost.moneytransfer.transfer.model.command.CreateTransferCommand;
import com.dregost.moneytransfer.transfer.model.event.TransferEvent;
import com.dregost.moneytransfer.transfer.read.*;
import lombok.val;

import java.util.Optional;

public class TransferApplicationService {
    private final IdGenerator idGenerator;
    private final EventStore<TransferEvent> eventStore;
    private final TransferRepository transferRepository;

    @Inject
    public TransferApplicationService(final IdGenerator idGenerator,
                                      final EventStore<TransferEvent> eventStore,
                                      final TransferRepository transferRepository) {
        this.idGenerator = idGenerator;
        this.eventStore = eventStore;
        this.transferRepository = transferRepository;
    }

    public TransferResponse createTransfer(final CreateTransferRequest createTransferRequest) {
        val createTransferCommand = CreateTransferCommand.builder()
                .transferId(idGenerator.generateId(TransferId::of))
                .fromAccountId(AccountId.of(createTransferRequest.getFromAccountId()))
                .toAccountId(AccountId.of(createTransferRequest.getToAccountId()))
                .amount(createTransferRequest.getAmount())
                .build();

        val transfer = Transfer.create(createTransferCommand);

        eventStore.append(transfer);
        return TransferResponse.builder()
                .id(transfer.getId().getValue())
                .fromAccountId(transfer.getFromAccountId().getValue())
                .toAccountId(transfer.getToAccountId().getValue())
                .amount(transfer.getAmount())
                .status(transfer.getStatus())
                .build();
    }

    public Optional<TransferResponse> getDetails(final String id){
        return transferRepository.findById(id);
    }
}
