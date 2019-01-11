package com.dregost.moneytransfer.account.read;

import lombok.*;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class AccountResponse {
    @NonNull
    private String id;
    @NonNull
    private BigDecimal balance;

    public AccountResponse credit(final BigDecimal amount){
        return toBuilder()
                .balance(getBalance().add(amount))
                .build();
    }

    public AccountResponse debit(final BigDecimal amount){
        return toBuilder()
                .balance(getBalance().subtract(amount))
                .build();
    }
}
