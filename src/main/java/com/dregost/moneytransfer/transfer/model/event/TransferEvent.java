package com.dregost.moneytransfer.transfer.model.event;

import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.common.model.Event;
import com.dregost.moneytransfer.transfer.model.*;

import java.math.BigDecimal;

public interface TransferEvent extends Event {
    TransferDetails getDetails();

    default TransferId getTransferId() {
        return getDetails().getTransferId();
    }

    default AccountId getFromAccountId() {
        return getDetails().getFromAccountId();
    }

    default AccountId getToAccountId() {
        return getDetails().getToAccountId();
    }

    default BigDecimal getAmount() {
        return getDetails().getAmount();
    }
}
