package com.dregost.moneytransfer;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.dregost.moneytransfer.account.infrastructure.*;
import com.dregost.moneytransfer.common.*;
import com.dregost.moneytransfer.common.infrastructure.*;
import com.dregost.moneytransfer.transfer.infrastructure.*;
import lombok.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.h2.Driver;
import org.jooq.*;
import org.jooq.impl.DSL;
import spark.Service;

public class ApplicationServer {
    private final Service service;

    public ApplicationServer(final int port) {
        this.service = Service.ignite().port(port);
    }

    public void start() {
        val dataSource = prepareDataSource();
        val dsl = DSL.using(dataSource, SQLDialect.H2);

        initializeSchema(dsl);

        val eventBus = new EventBus("money-transfer");
        val injector = Guice.createInjector(CommonModule.of(eventBus, dsl), new AccountModule(), new TransferModule());

        val accountResource = injector.getInstance(AccountResource.class);
        val transferResource = injector.getInstance(TransferResource.class);
        val resourceHelper = injector.getInstance(ResourceHelper.class);

        accountResource.registerResources(service);
        transferResource.registerResources(service);

        service.exception(InvalidPayloadException.class, (exception, request, response) -> resourceHelper.prepareResponse(response, 400, ErrorDto.of("Invalid payload")));
        service.internalServerError((request, response) -> resourceHelper.prepareResponse(response, 500, ErrorDto.of("Unexpected error")));
    }

    public void stop() {
        service.stop();
    }

    private BasicDataSource prepareDataSource() {
        val dataSource = new BasicDataSource();

        dataSource.setDriverClassName(Driver.class.getCanonicalName());
        dataSource.setUrl("jdbc:h2:mem:money_transfer");
        dataSource.setUsername("sa");
        return dataSource;
    }

    @SneakyThrows
    private void initializeSchema(final DSLContext dsl) {
        val url = Resources.getResource("schema.sql");
        val schemaUrl = Resources.toString(url, Charsets.UTF_8);
        dsl.execute(schemaUrl);
    }
}
