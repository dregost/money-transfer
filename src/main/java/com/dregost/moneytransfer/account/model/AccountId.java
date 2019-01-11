package com.dregost.moneytransfer.account.model;

import com.dregost.moneytransfer.common.model.Id;
import lombok.Value;

@Value(staticConstructor = "of")
public class AccountId implements Id {
    private final String value;
}
