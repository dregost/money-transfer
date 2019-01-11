package com.dregost.moneytransfer.account.model.event;

import com.dregost.moneytransfer.account.model.AccountId;
import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class AccountOpened implements AccountEvent {
    @NonNull
    private AccountId accountId;
    @NonNull
    private BigDecimal initialBalance;
}
