package com.dregost.moneytransfer.transfer.model.command;

import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.transfer.model.TransferId;
import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class CreateTransferCommand {
    private TransferId transferId;
    private AccountId fromAccountId;
    private AccountId toAccountId;
    private BigDecimal amount;
}
