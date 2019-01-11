package com.dregost.moneytransfer.common.infrastructure;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.dregost.moneytransfer.common.*;
import io.vavr.control.Try;
import lombok.val;
import spark.*;

public class ResourceHelper {
    private final static String CONTENT_TYPE = "application/json";
    private final Gson gson;

    @Inject
    public ResourceHelper(final Gson gson) {
        this.gson = gson;
    }

    public <T> String prepareResponse(final Response response,
                                      final int status,
                                      final T responseBody) {
        response.type(CONTENT_TYPE);
        response.status(status);
        return gson.toJson(responseBody);
    }

    public String prepareNotFoundResponse(final Response response) {
        return prepareResponse(response, 404, ErrorDto.of("Entity not found"));
    }

    public <T> T extractBody(final Request request, final Class<T> cls) {
        val body = request.body();
        return Try
                .of(() -> gson.fromJson(body, cls))
                .getOrElseThrow(InvalidPayloadException::new);
    }
}
