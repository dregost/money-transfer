package com.dregost.moneytransfer.transfer.read;

import com.dregost.moneytransfer.transfer.model.TransferStatus;
import lombok.*;
import lombok.experimental.Wither;

import java.math.BigDecimal;

@Value
@Builder
public class TransferResponse {
    private String id;
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    @Wither
    private TransferStatus status;
}
