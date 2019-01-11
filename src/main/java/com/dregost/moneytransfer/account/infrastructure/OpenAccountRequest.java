package com.dregost.moneytransfer.account.infrastructure;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class OpenAccountRequest {
    private BigDecimal initialBalance;
}
