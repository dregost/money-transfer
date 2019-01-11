package com.dregost.moneytransfer.common.read;

import com.dregost.moneytransfer.common.model.ReadOnlyRepository;

public interface Repository<T, ID> extends ReadOnlyRepository<T, ID> {
    T save(final T entity);
}
