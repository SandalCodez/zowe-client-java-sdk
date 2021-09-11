/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package zosfiles;

import core.ZOSConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import rest.Response;
import rest.ZoweRequest;
import rest.ZoweRequestFactory;
import rest.ZoweRequestType;
import utility.Util;
import utility.UtilDataset;
import utility.UtilRest;
import zosfiles.input.CreateParams;

import java.util.HashMap;

/**
 * ZosDsn class that provides CRUD operations on Datasets
 *
 * @author Leonid Baranov
 * @version 1.0
 */
public class ZosDsn {

    private static final Logger LOG = LogManager.getLogger(ZosDsn.class);

    private final ZOSConnection connection;

    /**
     * ZosDsn Constructor
     *
     * @param connection connection information, see ZOSConnection object
     * @author Leonid Baranov
     */
    public ZosDsn(ZOSConnection connection) {
        this.connection = connection;
    }

    /**
     * Replaces the content of an existing sequential data set with new content.
     *
     * @param dataSetName sequential dataset (e.g. 'DATASET.LIB')
     * @param content     new content
     * @throws Exception error processing request
     * @return http response object
     * @author Leonid Baranov
     */
    public Response writeDsn(String dataSetName, String content) throws Exception {
        Util.checkConnection(connection);
        Util.checkNullParameter(content == null, "content is null");
        Util.checkNullParameter(dataSetName == null, "dataSetName is null");
        Util.checkIllegalParameter(dataSetName.isEmpty(), "dataSetName not specified");
        UtilDataset.checkDatasetName(dataSetName, true);

        String url = "https://" + connection.getHost() + ":" + connection.getZosmfPort() + ZosFilesConstants.RESOURCE +
                ZosFilesConstants.RES_DS_FILES + "/" + Util.encodeURIComponent(dataSetName);

        LOG.debug(url);

        ZoweRequest request = ZoweRequestFactory.buildRequest(connection, url, content,
                ZoweRequestType.VerbType.PUT_TEXT);
        Response response = request.executeHttpRequest();

        try {
            UtilRest.checkHttpErrors(response);
        } catch (Exception e) {
            UtilDataset.checkHttpErrors(e.getMessage(), dataSetName, "write");
        }

        return response;
    }

    /**
     * Replaces the content of a member of a partitioned data set (PDS or PDSE) with new content.
     * A new dataset member will be created if the specified dataset member does not exists.
     *
     * @param dataSetName dataset name of where the member is located (e.g. 'DATASET.LIB')
     * @param member      name of member to add new content
     * @param content     new content
     * @throws Exception error processing request
     * @return http response object
     * @author Frank Giordano
     */
    public Response writeDsnMember(String dataSetName, String member, String content) throws Exception {
        Util.checkConnection(connection);
        Util.checkNullParameter(content == null, "content is null");
        Util.checkNullParameter(dataSetName == null, "dataSetName is null");
        Util.checkIllegalParameter(dataSetName.isEmpty(), "dataSetName not specified");
        Util.checkNullParameter(member == null, "member is null");
        Util.checkIllegalParameter(member.isEmpty(), "member not specified");
        UtilDataset.checkDatasetName(dataSetName, true);

        var dataSetMember = Util.encodeURIComponent(dataSetName) + "(" + Util.encodeURIComponent(member) + ")";

        String url = "https://" + connection.getHost() + ":" + connection.getZosmfPort() + ZosFilesConstants.RESOURCE +
                ZosFilesConstants.RES_DS_FILES + "/" + dataSetMember;

        LOG.debug(url);

        ZoweRequest request = ZoweRequestFactory.buildRequest(connection, url, content,
                ZoweRequestType.VerbType.PUT_TEXT);
        Response response = request.executeHttpRequest();

        try {
            UtilRest.checkHttpErrors(response);
        } catch (Exception e) {
            UtilDataset.checkHttpErrors(e.getMessage(), dataSetName, "write");
        }

        return response;
    }

    /**
     * Delete a dataset
     *
     * @param dataSetName name of a dataset (e.g. 'DATASET.LIB')
     * @throws Exception error processing request
     * @return http response object
     * @author Leonid Baranov
     */
    public Response deleteDsn(String dataSetName) throws Exception {
        Util.checkConnection(connection);
        Util.checkNullParameter(dataSetName == null, "dataSetName is null");
        Util.checkIllegalParameter(dataSetName.isEmpty(), "dataSetName not specified");
        UtilDataset.checkDatasetName(dataSetName, true);

        String url = "https://" + connection.getHost() + ":" + connection.getZosmfPort() + ZosFilesConstants.RESOURCE +
                ZosFilesConstants.RES_DS_FILES + "/" + Util.encodeURIComponent(dataSetName);

        LOG.debug(url);

        ZoweRequest request = ZoweRequestFactory.buildRequest(connection, url, null,
                ZoweRequestType.VerbType.DELETE_JSON);
        Response response = request.executeHttpRequest();

        try {
            UtilRest.checkHttpErrors(response);
        } catch (Exception e) {
            UtilDataset.checkHttpErrors(e.getMessage(), dataSetName, "delete");
        }

        return response;
    }

    /**
     * Delete a dataset member
     *
     * @param dataSetName name of a dataset (e.g. 'DATASET.LIB')
     * @param member      name of member to delete
     * @throws Exception error processing request
     * @return http response object
     * @author Frank Giordano
     */
    public Response deleteDsnMember(String dataSetName, String member) throws Exception {
        Util.checkConnection(connection);
        Util.checkNullParameter(dataSetName == null, "dataSetName is null");
        Util.checkIllegalParameter(dataSetName.isEmpty(), "dataSetName not specified");
        Util.checkNullParameter(member == null, "member is null");
        Util.checkIllegalParameter(member.isEmpty(), "member not specified");
        UtilDataset.checkDatasetName(dataSetName, true);

        var dataSetMember = Util.encodeURIComponent(dataSetName) + "(" + Util.encodeURIComponent(member) + ")";

        String url = "https://" + connection.getHost() + ":" + connection.getZosmfPort() + ZosFilesConstants.RESOURCE +
                ZosFilesConstants.RES_DS_FILES + "/" + dataSetMember;

        LOG.debug(url);

        ZoweRequest request = ZoweRequestFactory.buildRequest(connection, url, null,
                ZoweRequestType.VerbType.DELETE_JSON);
        Response response = request.executeHttpRequest();

        try {
            UtilRest.checkHttpErrors(response);
        } catch (Exception e) {
            UtilDataset.checkHttpErrors(e.getMessage(), dataSetMember, "delete");
        }

        return response;
    }

    /**
     * Creates a new dataset with specified parameters
     *
     * @param dataSetName name of a dataset to create (e.g. 'DATASET.LIB')
     * @param params      create dataset parameters, see CreateParams object
     * @throws Exception error processing request
     * @return http response object
     * @author Leonid Baranov
     */
    public Response createDsn(String dataSetName, CreateParams params) throws Exception {
        Util.checkConnection(connection);
        Util.checkNullParameter(params == null, "params is null");
        Util.checkNullParameter(dataSetName == null, "dataSetName is null");
        Util.checkIllegalParameter(dataSetName.isEmpty(), "dataSetName not specified");
        UtilDataset.checkDatasetName(dataSetName, true);

        String url = "https://" + connection.getHost() + ":" + connection.getZosmfPort() + ZosFilesConstants.RESOURCE +
                ZosFilesConstants.RES_DS_FILES + "/" + Util.encodeURIComponent(dataSetName);

        LOG.debug(url);

        String body = buildBody(params);

        ZoweRequest request = ZoweRequestFactory.buildRequest(connection, url, body,
                ZoweRequestType.VerbType.POST_JSON);

        Response response = request.executeHttpRequest();

        try {
            UtilRest.checkHttpErrors(response);
        } catch (Exception e) {
            UtilDataset.checkHttpErrors(e.getMessage(), dataSetName, "create");
        }

        return response;
    }

    /**
     * Create the http body request
     *
     * @param params CreateParams parameters
     * @return body string value for http request
     * @author Leonid Baranov
     */
    private static String buildBody(CreateParams params) {
        var jsonMap = new HashMap<String, Object>();
        params.getVolser().ifPresent(v -> jsonMap.put("volser", v));
        params.getUnit().ifPresent(v -> jsonMap.put("unit", v));
        params.getDsorg().ifPresent(v -> jsonMap.put("dsorg", v));
        params.getAlcunit().ifPresent(v -> jsonMap.put("alcunit", v));
        params.getPrimary().ifPresent(v -> jsonMap.put("primary", v));
        params.getSecondary().ifPresent(v -> jsonMap.put("secondary", v));
        params.getDirblk().ifPresent(v -> jsonMap.put("dirblk", v));
        params.getAvgblk().ifPresent(v -> jsonMap.put("avgblk", v));
        params.getRecfm().ifPresent(v -> jsonMap.put("recfm", v));
        params.getBlksize().ifPresent(v -> jsonMap.put("blksize", v));
        params.getLrecl().ifPresent(v -> jsonMap.put("lrecl", v));
        params.getStorclass().ifPresent(v -> jsonMap.put("storclass", v));
        params.getStorclass().ifPresent(v -> jsonMap.put("mgntclass", v));
        params.getMgntclass().ifPresent(v -> jsonMap.put("mgntclass", v));
        params.getDataclass().ifPresent(v -> jsonMap.put("dataclass", v));
        params.getDsntype().ifPresent(v -> jsonMap.put("dsntype", v));

        var jsonRequestBody = new JSONObject(jsonMap);
        LOG.debug(jsonRequestBody);
        return jsonRequestBody.toString();
    }

}
