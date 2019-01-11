package com.dregost.moneytransfer.common;

import com.dregost.moneytransfer.common.model.Id;
import lombok.val;

import java.util.UUID;
import java.util.function.Function;

public class IdGenerator {
    public <T extends Id> T generateId(final Function<String, T> idConverter) {
        val uuid = UUID.randomUUID().toString();
        return idConverter.apply(uuid);
    }
}
