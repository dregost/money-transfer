package com.dregost.moneytransfer.account.model.command;

import com.dregost.moneytransfer.account.model.AccountId;
import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class OpenAccountCommand {
    @NonNull
    private final AccountId accountId;
    @NonNull
    private final BigDecimal initialBalance;
}
