/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package zowe.client.sdk.zosfiles;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.rest.PutJsonZosmfRequest;
import zowe.client.sdk.rest.Response;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.zosfiles.uss.methods.UssMove;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class containing unit tests for UssMove.
 *
 * @author James Kostrewski
 * @version 2.0
 */
public class UssMoveTest {

    private final ZosConnection connection = new ZosConnection("1", "1", "1", "1");
    private PutJsonZosmfRequest mockJsonPutRequest;
    private UssMove ussMove;

    @Before
    public void init() throws ZosmfRequestException {
        mockJsonPutRequest = Mockito.mock(PutJsonZosmfRequest.class);
        Mockito.when(mockJsonPutRequest.executeRequest()).thenReturn(
                new Response(new JSONObject(), 200, "success"));
        ussMove = new UssMove(connection);
    }

    @Test
    public void tstUssMoveSuccess() throws ZosmfRequestException {
        final UssMove ussMove = new UssMove(connection, mockJsonPutRequest);
        final Response response = ussMove.move("/xxx/xx/xx", "/xxx/xx/xx");
        assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        assertEquals(200, response.getStatusCode().orElse(-1));
        assertEquals("success", response.getStatusText().orElse("n\\a"));
    }

    @Test
    public void tstUssMoveOverwriteSuccess() throws ZosmfRequestException {
        final UssMove ussMove = new UssMove(connection, mockJsonPutRequest);
        final Response response = ussMove.move("/xxx/xx/xx", "/xxx/xx/xx", true);
        assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        assertEquals(200, response.getStatusCode().orElse(-1));
        assertEquals("success", response.getStatusText().orElse("n\\a"));
    }

    @Test
    public void tstUssMoveNullFromPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussMove.move(null, "/xxx/xx/xx");
        } catch (NullPointerException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fromPath is null", errMsg);
    }

    @Test
    public void tstUssMoveEmptyFromPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussMove.move("", "/xxx/xx/xx");
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fromPath not specified", errMsg);
    }

    @Test
    public void tstUssMoveEmptyFromPathWithSpacesFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussMove.move("   ", "/xxx/xx/xx");
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fromPath not specified", errMsg);
    }

    @Test
    public void tstUssMoveNullTargetPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussMove.move("/xxx/xx/xx", null);
        } catch (NullPointerException e) {
            errMsg = e.getMessage();
        }
        assertEquals("targetPath is null", errMsg);
    }

    @Test
    public void tstUssMoveEmptyTargetPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussMove.move("/xxx/xx/xx", "");
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage();
        }
        assertEquals("targetPath not specified", errMsg);
    }

    @Test
    public void tstUssMoveEmptyTargetPathWithSpacesFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussMove.move("/xxx/xx/xx", "   ");
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage();
        }
        assertEquals("targetPath not specified", errMsg);
    }

    @Test
    public void tstUssMoveInvalidTargetPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussMove.move("/xxx/xx/xx", "name");
        } catch (IllegalStateException e) {
            errMsg = e.getMessage();
        }
        assertEquals("specify valid path value", errMsg);
    }

    @Test
    public void tstUssMoveInvalidFromPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussMove.move("name", "/xxx/xx/xx");
        } catch (IllegalStateException e) {
            errMsg = e.getMessage();
        }
        assertEquals("specify valid path value", errMsg);
    }

    @Test
    public void tstUssMoveInvalidTargetPathWithOverWriteFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussMove.move("/xxx/xx/xx", "name", true);
        } catch (IllegalStateException e) {
            errMsg = e.getMessage();
        }
        assertEquals("specify valid path value", errMsg);
    }

    @Test
    public void tstUssMoveInvalidFromPathWithOverWriteFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussMove.move("name", "/xxx/xx/xx", false);
        } catch (IllegalStateException e) {
            errMsg = e.getMessage();
        }
        assertEquals("specify valid path value", errMsg);
    }

}
