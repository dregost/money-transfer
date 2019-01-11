package com.dregost.moneytransfer.transfer.model;

import com.dregost.moneytransfer.common.model.Id;
import lombok.Value;

@Value(staticConstructor = "of")
public class TransferId implements Id {
    private String value;
}
