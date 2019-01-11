package com.dregost.moneytransfer.account.model.command;

import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.transfer.model.TransferId;
import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class CreditAccountCommand {
    @NonNull
    private final AccountId accountId;
    @NonNull
    private final TransferId transferId;
    @NonNull
    private final BigDecimal amount;
}
