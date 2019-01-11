package com.dregost.moneytransfer.account.model.event;

import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.transfer.model.TransferId;
import lombok.*;

@Value
@Builder
public class AccountCreditFailed implements AccountEvent {
    @NonNull
    private AccountId accountId;
    @NonNull
    private TransferId transferId;
    @NonNull
    private String description;
}
