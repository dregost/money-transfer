package com.dregost.moneytransfer.account.infrastructure;

import com.google.inject.Inject;
import com.dregost.moneytransfer.account.application.AccountApplicationService;
import com.dregost.moneytransfer.common.infrastructure.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import spark.*;

@Slf4j
public class AccountResource implements Resource {
    private final ResourceHelper resourceHelper;
    private final AccountApplicationService accountApplicationService;

    @Inject
    public AccountResource(final ResourceHelper resourceHelper, final AccountApplicationService accountApplicationService) {
        this.resourceHelper = resourceHelper;
        this.accountApplicationService = accountApplicationService;
    }

    @Override
    public void registerResources(final Service service) {
        service.post("account", this::openAccount);
        service.get("account/:id", this::getDetails);
    }

    private String openAccount(final Request request, final Response response) {
        val openAccountRequest = resourceHelper.extractBody(request, OpenAccountRequest.class);
        val openedAccount = accountApplicationService.openAccount(openAccountRequest.getInitialBalance());
        return resourceHelper.prepareResponse(response, 201, openedAccount);
    }

    private String getDetails(final Request request, final Response response) {
        val accountDetails = accountApplicationService.getDetails(request.params("id"));
        if(accountDetails.isPresent()) {
            response.status(200);
            return resourceHelper.prepareResponse(response, 200, accountDetails.get());
        } else {
            return resourceHelper.prepareNotFoundResponse(response);
        }
    }
}
