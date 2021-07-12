/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package zostso;

import core.ZOSConnection;
import org.apache.http.client.methods.HttpPut;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rest.IZoweRequest;
import rest.JsonRequest;
import rest.Response;
import utility.Util;
import utility.UtilTso;
import zostso.input.SendTsoParms;
import zostso.zosmf.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SendTso {

    private static final Logger LOG = LogManager.getLogger(SendTso.class);

    public static SendResponse sendDataToTSOCollect(ZOSConnection connection, String servletKey, String data) throws Exception {
        Util.checkNullParameter(servletKey == null, "servletKey is null");
        Util.checkNullParameter(data == null, "data is null");
        Util.checkStateParameter(servletKey.isEmpty(), "servletKey not specified");
        Util.checkStateParameter(data.isEmpty(), "data not specified");

        ZosmfTsoResponse putResponse = SendTso.sendDataToTSOCommon(connection, new SendTsoParms(servletKey, data));

        if (!putResponse.getMsgData().isPresent()) {
            // TODO
        }
        CollectedResponses responses = SendTso.getAllResponses(connection, putResponse);
        return SendTso.createResponse(responses);
    }

    public static ZosmfTsoResponse sendDataToTSOCommon(ZOSConnection connection, SendTsoParms commandParms) throws Exception {
        Util.checkConnection(connection);
        Util.checkNullParameter(commandParms == null, "sendTsoParms is null");
        Util.checkStateParameter(commandParms.getData().isEmpty(), "sendTsoParms data not specified");
        Util.checkStateParameter(commandParms.getSevletKey().isEmpty(), "sendTsoParms sevletKey not specified");

        String url = "https://" + connection.getHost() + ":" + connection.getPort() + TsoConstants.RESOURCE + "/" +
                TsoConstants.RES_START_TSO + "/" + commandParms.getSevletKey() + TsoConstants.RES_DONT_READ_REPLY;
        LOG.info("SendTso::sendDataToTSOCommon - url {}", url);

        TsoResponseMessage tsoResponseMessage = new TsoResponseMessage(Optional.of("0100"),
                Optional.ofNullable(commandParms.getData()));
        String jobObj = getTsoResponseSendMessage(tsoResponseMessage);
        IZoweRequest request = new JsonRequest(connection, new HttpPut(url), Optional.of(jobObj));

        Response response = request.httpPut();

        return UtilTso.getZosmfTsoResponse(response);
    }

    private static String getTsoResponseSendMessage(TsoResponseMessage tsoResponseMessage) throws Exception {
        String message = "{\"TSO RESPONSE\":{\"VERSION\":\"" + tsoResponseMessage.getVersion().orElseThrow(Exception::new)
                + "\",\"DATA\":\"" + tsoResponseMessage.getData().orElseThrow(Exception::new) + "\"}}";
        LOG.info("SendTo::getTsoResponseSendMessage - message {}", message);
        return message;
    }

    private static CollectedResponses getAllResponses(ZOSConnection connection, ZosmfTsoResponse tso) throws Exception {
        boolean done = false;
        StringBuilder messages = new StringBuilder();
        List<ZosmfTsoResponse> tsos = new ArrayList<>();
        tsos.add(tso);
        while (!done) {
            if (tso.getTsoData().isPresent()) {
                for (TsoMessages tsoDatum : tso.getTsoData().get()) {
                    if (tsoDatum.getTsoMessage().isPresent()) {
                        TsoMessage tsoMsg = tsoDatum.getTsoMessage().orElseThrow(Exception::new);
                        String data = tsoMsg.getData().orElseThrow(Exception::new);
                        messages.append(data + "\n");
                    } else if (tsoDatum.getTsoPrompt().isPresent()) {
                        if (messages.length() > 0) {
                            done = true;
                        }
                    }
                    // TODO
                }
            }
            if (!done) {
                tso = SendTso.getDataFromTSO(connection, tso.getServletKey().orElseThrow(Exception::new));
                tsos.add(tso);
            }
        }
        return new CollectedResponses(tsos, messages.toString());
    }

    private static ZosmfTsoResponse getDataFromTSO(ZOSConnection connection, String servletKey) throws Exception {
        Util.checkConnection(connection);

        String url = "https://" + connection.getHost() + ":" + connection.getPort() +
                TsoConstants.RESOURCE + "/" + TsoConstants.RES_START_TSO + "/" + servletKey;
        LOG.info("SendTso::getDataFromTSO - url {}", url);

        IZoweRequest request = new JsonRequest(connection, new HttpPut(url), Optional.empty());
        Response response = request.httpPut();

        return UtilTso.getZosmfTsoResponse(response);
    }

    private static SendResponse createResponse(CollectedResponses responses) {
        return new SendResponse(true, responses.getTsos().get(), responses.getMessages().get());
    }

}
