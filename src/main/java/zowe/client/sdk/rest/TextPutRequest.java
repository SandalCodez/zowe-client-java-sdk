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

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zowe.client.sdk.core.ZOSConnection;
import zowe.client.sdk.rest.type.ZoweRequestType;
import zowe.client.sdk.utility.EncodeUtils;
import zowe.client.sdk.utility.RestUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Http put operation with text content type
 *
 * @author Frank Giordano
 * @version 1.0
 */
public class TextPutRequest extends ZoweRequest {

    private static final Logger LOG = LoggerFactory.getLogger(TextPutRequest.class);
    private HttpPut request;

    /**
     * TextPutRequest constructor.
     *
     * @param connection connection information, see ZOSConnection object
     * @author Frank Giordano
     */
    public TextPutRequest(ZOSConnection connection) {
        super(connection, ZoweRequestType.PUT_JSON);
    }

    /**
     * Execute the formulated http request
     *
     * @author Frank Giordano
     */
    @Override
    public Response executeRequest() throws Exception {
        LOG.debug("TextPutRequest::executeRequest");
        return executeTextRequest(request);
    }

    /**
     * Set the standard headers for the http request
     *
     * @author Frank Giordano
     */
    @Override
    public void setStandardHeaders() {
        request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + EncodeUtils.getAuthEncoding(connection));
        request.setHeader("Content-Type", "text/plain; charset=UTF-8");
        request.setHeader(X_CSRF_ZOSMF_HEADER_KEY, X_CSRF_ZOSMF_HEADER_VALUE);
    }

    /**
     * Set any headers needed for the http request
     *
     * @param headers headers to add to the request
     * @author Frank Giordano
     */
    @Override
    public void setHeaders(Map<String, String> headers) {
        headers.forEach((key, value) -> request.setHeader(key, value));
    }

    /**
     * Initialize the http request object with an url value
     *
     * @param url rest url end point
     * @throws Exception error setting the http request
     * @author Frank Giordano
     */
    @Override
    public void setRequest(String url) throws Exception {
        throw new Exception("request requires url and body values");
    }

    /**
     * Initialize the http request object with an url and body values
     *
     * @param url  rest url end point
     * @param body data to be sent with request
     * @throws Exception error setting the http request
     * @author Frank Giordano
     */
    @Override
    public void setRequest(String url, String body) throws Exception {
        if (RestUtils.isUrlNotValid(url)) {
            throw new Exception("url is invalid");
        }
        request = new HttpPut(Optional.ofNullable(url).orElseThrow(() -> new Exception("url not specified")));
        request.setEntity(new StringEntity(Optional.ofNullable(body).orElse("")));
        setup();
    }

}
