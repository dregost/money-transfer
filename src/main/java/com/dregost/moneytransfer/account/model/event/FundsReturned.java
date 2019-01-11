package com.dregost.moneytransfer.account.model.event;

import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.transfer.model.TransferId;
import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class FundsReturned implements AccountEvent{
    @NonNull
    private AccountId accountId;
    @NonNull
    private TransferId transferId;
    @NonNull
    private BigDecimal amount;
}
