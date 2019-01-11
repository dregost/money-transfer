package com.dregost.moneytransfer.common.model;

import java.util.Optional;

public interface ReadOnlyRepository<T, ID> {
    Optional<T> findById(final ID id);
}
