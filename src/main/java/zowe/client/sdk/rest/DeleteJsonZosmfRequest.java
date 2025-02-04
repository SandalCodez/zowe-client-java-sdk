/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package zowe.client.sdk.rest;

import kong.unirest.core.*;
import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.utility.EncodeUtils;
import zowe.client.sdk.utility.ValidateUtils;

/**
 * Http delete operation with Json content type
 *
 * @author Frank Giordano
 * @version 2.0
 */
public class DeleteJsonZosmfRequest extends ZosmfRequest {

    /**
     * Optional Cookie object
     */
    private Cookie cookie;

    /**
     * DeleteJsonZosmfRequest constructor
     *
     * @param connection connection information, see ZosConnection object
     * @author Frank Giordano
     */
    public DeleteJsonZosmfRequest(final ZosConnection connection) {
        super(connection);
    }

    /**
     * Perform the http rest request
     *
     * @return Response object
     * @throws ZosmfRequestException request error state
     * @author Frank Giordano
     */
    @Override
    public Response executeRequest() throws ZosmfRequestException {
        ValidateUtils.checkNullParameter(url == null, "url is null");
        HttpResponse<JsonNode> reply;
        try {
            reply = cookie != null ? Unirest.delete(url).cookie(cookie).headers(headers).asJson() :
                    Unirest.get(url).headers(headers).asJson();
        } catch (UnirestException e) {
            throw new ZosmfRequestException(e.getMessage(), e);
        }
        return buildResponse(reply);
    }

    /**
     * Method to set the body information for the http request which is not used for this request.
     *
     * @param body object value
     * @author Frank Giordano
     */
    @Override
    public void setBody(Object body) {
        throw new IllegalStateException("setting body for this request is invalid");
    }

    /**
     * Set the standard headers for the http request
     *
     * @author Frank Giordano
     */
    @Override
    public void setStandardHeaders() {
        headers.put("Authorization", "Basic " + EncodeUtils.encodeAuthComponent(connection));
        headers.put("Content-Type", "application/json");
        headers.put(X_CSRF_ZOSMF_HEADER_KEY, X_CSRF_ZOSMF_HEADER_VALUE);
    }

    /**
     * Set a cookie for this request. This is optional for most requests and not needed.
     *
     * @param cookie object
     * @author Frank Giordano
     */
    @Override
    public void setCookie(final Cookie cookie) {
        this.cookie = cookie;
    }

}
