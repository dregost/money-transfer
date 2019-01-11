package com.dregost.moneytransfer.transfer.model;

import com.dregost.moneytransfer.account.model.AccountId;
import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class TransferDetails {
    private final TransferId transferId;
    private final AccountId fromAccountId;
    private final AccountId toAccountId;
    private final BigDecimal amount;
}
