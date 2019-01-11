package com.dregost.moneytransfer.common.infrastructure;

import spark.Service;

public interface Resource {
    void registerResources(final Service service);
}
