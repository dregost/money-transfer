package com.dregost.moneytransfer.account.model.event;

import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.common.model.Event;

public interface AccountEvent extends Event {
    AccountId getAccountId();
}
