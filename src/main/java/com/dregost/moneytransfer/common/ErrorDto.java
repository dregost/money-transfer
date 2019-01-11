package com.dregost.moneytransfer.common;

import lombok.Value;

@Value(staticConstructor = "of")
public class ErrorDto {
    private String error;
}
