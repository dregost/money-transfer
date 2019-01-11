package com.dregost.moneytransfer.transfer.infrastructure;

import com.google.inject.Inject;
import com.dregost.moneytransfer.common.infrastructure.Resource;
import com.dregost.moneytransfer.common.infrastructure.ResourceHelper;
import com.dregost.moneytransfer.transfer.application.TransferApplicationService;
import lombok.val;
import spark.*;

public class TransferResource implements Resource {
    private final ResourceHelper resourceHelper;
    private final TransferApplicationService transferApplicationService;

    @Inject
    public TransferResource(final ResourceHelper resourceHelper,
                            final TransferApplicationService transferApplicationService) {
        this.resourceHelper = resourceHelper;
        this.transferApplicationService = transferApplicationService;
    }

    @Override
    public void registerResources(final Service service) {
        service.post("transfer", this::createTransfer);
        service.get("transfer/:id", this::getDetails);
    }

    private String createTransfer(final Request request, final Response response) {
        val createTransferRequest = resourceHelper.extractBody(request, CreateTransferRequest.class);
        val createdTransfer = transferApplicationService.createTransfer(createTransferRequest);

        return resourceHelper.prepareResponse(response, 202, createdTransfer);
    }

    private String getDetails(final Request request, final Response response) {
        val transferDetails = transferApplicationService.getDetails(request.params("id"));
        if(transferDetails.isPresent()) {
            response.status(200);
            return resourceHelper.prepareResponse(response, 200, transferDetails.get());
        } else {
            return resourceHelper.prepareNotFoundResponse(response);
        }
    }
}
