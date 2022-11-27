/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 */
package zowe.client.sdk.zoslogs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zowe.client.sdk.core.ZOSConnection;
import zowe.client.sdk.rest.JsonGetRequest;
import zowe.client.sdk.rest.Response;
import zowe.client.sdk.rest.ZoweRequest;
import zowe.client.sdk.rest.ZoweRequestFactory;
import zowe.client.sdk.rest.type.ZoweRequestType;
import zowe.client.sdk.utility.RestUtils;
import zowe.client.sdk.utility.ValidateUtils;
import zowe.client.sdk.zoslogs.input.ZosLogParams;
import zowe.client.sdk.zoslogs.response.ZosLogItem;
import zowe.client.sdk.zoslogs.response.ZosLogReply;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Get zos log via z/OSMF restful api
 *
 * @author Frank Giordano
 * @version 1.0
 */
public class GetZosLog {

    private static final Logger LOG = LoggerFactory.getLogger(GetZosLog.class);
    public static final String RESOURCE = "/zosmf/restconsoles/v1/log?";
    private ZOSConnection connection;
    private ZoweRequest request;

    /**
     * GetZosLog constructor
     *
     * @param connection connection information, see ZOSConnection object
     * @author Frank Giordano
     */
    public GetZosLog(ZOSConnection connection) {
        ValidateUtils.checkConnection(connection);
        this.connection = connection;
    }

    /**
     * Alternative GetZosLog constructor with ZoweRequest object. This is mainly used for internal code unit testing
     * with mockito, and it is not recommended to be used by the larger community.
     *
     * @param connection connection information, see ZOSConnection object
     * @param request    any compatible ZoweRequest Interface type object
     * @throws Exception processing error
     * @author Frank Giordano
     */
    public GetZosLog(ZOSConnection connection, ZoweRequest request) throws Exception {
        ValidateUtils.checkConnection(connection);
        this.connection = connection;
        if (!(request instanceof JsonGetRequest)) {
            throw new Exception("GET_JSON request type required");
        }
        this.request = request;
    }

    /**
     * Issue a z/OSMF syslog command, returns "raw" z/OSMF response.
     *
     * @param params ZosLogParams object
     * @return ZosLogReply object with log messages/items
     * @throws Exception processing error
     * @author Frank Giordano
     */
    public ZosLogReply getZosLog(ZosLogParams params) throws Exception {
        ValidateUtils.checkNullParameter(params == null, "params is null");
        ValidateUtils.checkNullParameter(params.getStartTime() == null, "startTime is not specified");

        String url = "https://" + connection.getHost() + ":" + connection.getZosmfPort() + RESOURCE;

        final String patternStr = ".*[0-9]-.*[0-9]-.*[0-9]";
        final Pattern pattern = Pattern.compile(patternStr);
        final Matcher matcher = pattern.matcher(params.getStartTime());
        if (matcher.matches()) {
            throw new IllegalArgumentException("startTime date format is invalid");
        }

        final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse(params.getStartTime(), formatter);

        url += "time=" + zonedDateTime.toString();

        if (params.getTimeRange() != null) {
            url += "&timeRange=" + params.getTimeRange();
        }

        if (params.getDirection() != null) {
            url += "&direction=" + params.getDirection().getValue();
        }

        LOG.debug(url);

        request = ZoweRequestFactory.buildRequest(connection, ZoweRequestType.GET_JSON);
        request.setRequest(url);

        final Response response = request.executeRequest();
        if (response.isEmpty()) {
            throw new Exception("response was null");
        }

        try {
            RestUtils.checkHttpErrors(response);
        } catch (Exception e) {
            final int httpCode = response.getStatusCode().get();
            if (httpCode == 500) {
                throw new Exception(e.getMessage() + " May be missing APAR see PH35930 required for log operations.");
            }
        }
        final JSONObject results = (JSONObject) response.getResponsePhrase().orElse(null);
        if (results == null) {
            throw new Exception("response JSONObject was null");
        }

        final JSONArray jsonArray = (JSONArray) results.get("items");
        final List<ZosLogItem> zosLogItems = new ArrayList<>();
        jsonArray.forEach(item -> {
            JSONObject itemObj = (JSONObject) item;
            ZosLogItem zosLogItem = new ZosLogItem.Builder()
                    .cart(itemObj.get("cart") != null ? (String) itemObj.get("cart") : null)
                    .color(itemObj.get("color") != null ? (String) itemObj.get("color") : null)
                    .jobName(itemObj.get("jobName") != null ? (String) itemObj.get("jobName") : null)
                    .message(itemObj.get("message") != null ? (String) itemObj.get("message") : null)
                    .messageId(itemObj.get("messageId") != null ? (String) itemObj.get("messageId") : null)
                    .replyId(itemObj.get("replyId") != null ? (String) itemObj.get("replyId") : null)
                    .system(itemObj.get("system") != null ? (String) itemObj.get("system") : null)
                    .type(itemObj.get("type") != null ? (String) itemObj.get("type") : null)
                    .subType(itemObj.get("subType") != null ? (String) itemObj.get("subType") : null)
                    .time(itemObj.get("time") != null ? (String) itemObj.get("v") : null)
                    .number(itemObj.get("number") != null ? (Long) itemObj.get("number") : 0).build();
            zosLogItems.add(zosLogItem);
        });

        return new ZosLogReply(results.get("timezone") != null ? (Long) results.get("timezone") : 0,
                results.get("timeTimestamp") != null ? (Long) results.get("timeTimestamp") : 0,
                results.get("source") != null ? (String) results.get("source") : null,
                results.get("totalitems") != null ? (Long) results.get("totalitems") : null,
                zosLogItems);
    }

}