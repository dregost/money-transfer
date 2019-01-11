package com.dregost.moneytransfer.transfer.infrastructure;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@ToString
@Builder
public class CreateTransferRequest {
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
}
