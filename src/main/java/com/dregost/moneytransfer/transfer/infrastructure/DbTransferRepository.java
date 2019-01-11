package com.dregost.moneytransfer.transfer.infrastructure;

import com.google.inject.*;
import com.dregost.moneytransfer.transfer.model.TransferStatus;
import com.dregost.moneytransfer.transfer.read.*;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.TransferRecord;

import java.util.Optional;

import static org.jooq.generated.tables.Transfer.TRANSFER;

@Singleton
public class DbTransferRepository implements TransferRepository {
    private final DSLContext dsl;

    @Inject
    public DbTransferRepository(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public TransferResponse save(final TransferResponse entity) {
        val transfer = findRecord(entity.getId())
                .orElseGet(() -> dsl.newRecord(TRANSFER));
        transfer.setId(entity.getId());
        transfer.setFromAccountId(entity.getFromAccountId());
        transfer.setToAccountId(entity.getToAccountId());
        transfer.setAmount(entity.getAmount());
        transfer.setStatus(entity.getStatus().name());
        transfer.store();
        return entity;
    }

    @Override
    public Optional<TransferResponse> findById(final String id) {
        return findRecord(id)
                .map(record -> TransferResponse.builder()
                        .id(record.getId())
                        .fromAccountId(record.getFromAccountId())
                        .toAccountId(record.getToAccountId())
                        .amount(record.getAmount())
                        .status(TransferStatus.valueOf(record.getStatus()))
                        .build());
    }

    private Optional<TransferRecord> findRecord(final String id) {
        return Optional.ofNullable(dsl.selectFrom(TRANSFER)
                .where(TRANSFER.ID.eq(id))
                .fetchOne());
    }
}
