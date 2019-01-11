package com.dregost.moneytransfer.transfer.model.event;

import com.dregost.moneytransfer.transfer.model.*;
import lombok.*;

@Value(staticConstructor = "of")
public class DebitRecorded implements TransferEvent {
    @NonNull
    private TransferDetails details;
}
