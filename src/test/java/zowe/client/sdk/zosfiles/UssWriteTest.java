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
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import zowe.client.sdk.core.ZosConnection;
import zowe.client.sdk.rest.PutStreamZosmfRequest;
import zowe.client.sdk.rest.PutTextZosmfRequest;
import zowe.client.sdk.rest.Response;
import zowe.client.sdk.rest.exception.ZosmfRequestException;
import zowe.client.sdk.zosfiles.uss.input.WriteParams;
import zowe.client.sdk.zosfiles.uss.methods.UssWrite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class containing unit tests for UssWrite.
 *
 * @author Frank Giordano
 * @version 2.0
 */
public class UssWriteTest {

    private final ZosConnection connection = new ZosConnection("1", "1", "1", "1");
    private UssWrite ussWrite;

    @Before
    public void init() throws ZosmfRequestException {
        ussWrite = new UssWrite(connection);
    }

    @Test
    public void tstUssWriteTextSuccess() throws ZosmfRequestException {
        final PutTextZosmfRequest mockTextPutRequest = Mockito.mock(PutTextZosmfRequest.class);
        Mockito.when(mockTextPutRequest.executeRequest()).thenReturn(
                new Response(new JSONObject(), 200, "success"));
        final UssWrite ussWrite = new UssWrite(connection, mockTextPutRequest);
        final Response response = ussWrite.writeText("/xx/xx/x", "text");
        Assertions.assertEquals("{}", response.getResponsePhrase().orElse("n\\a").toString());
        Assertions.assertEquals(200, response.getStatusCode().orElse(-1));
        Assertions.assertEquals("success", response.getStatusText().orElse("n\\a"));
    }

    @Test
    public void tstUssWriteBinarySuccess() throws ZosmfRequestException {
        final PutStreamZosmfRequest mockStreamPutRequest = Mockito.mock(PutStreamZosmfRequest.class);
        Mockito.when(mockStreamPutRequest.executeRequest()).thenReturn(
                new Response(new byte[0], 200, "success"));
        final UssWrite ussWrite = new UssWrite(connection, mockStreamPutRequest);
        final Response response = ussWrite.writeBinary("/xx/xx/x", new byte[0]);
        assertTrue(response.getResponsePhrase().orElse(null) instanceof byte[]);
        Assertions.assertEquals(200, response.getStatusCode().orElse(-1));
        Assertions.assertEquals("success", response.getStatusText().orElse("n\\a"));
    }

    @Test
    public void tstUssWriteTextNullTargetPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeText(null, "content");
        } catch (NullPointerException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fileNamePath is null", errMsg);
    }

    @Test
    public void tstUssWriteTextEmptyTargetPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeText("", "content");
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fileNamePath not specified", errMsg);
    }

    @Test
    public void tstUssWriteTextEmptyTargetPathWithSpacesFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeText("    ", "content");
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fileNamePath not specified", errMsg);
    }

    @Test
    public void tstUssWriteTextInvalidTargetPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeText("name", "text");
        } catch (IllegalStateException e) {
            errMsg = e.getMessage();
        }
        assertEquals("specify valid path value", errMsg);
    }

    @Test
    public void tstUssWriteBinaryNullTargetPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeBinary(null, new byte[0]);
        } catch (NullPointerException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fileNamePath is null", errMsg);
    }

    @Test
    public void tstUssWriteBinaryEmptyTargetPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeBinary("", new byte[0]);
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fileNamePath not specified", errMsg);
    }

    @Test
    public void tstUssWriteBinaryEmptyTargetPathWithSpacesFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeBinary("   ", new byte[0]);
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fileNamePath not specified", errMsg);
    }

    @Test
    public void tstUssWriteBinaryInvalidTargetPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeBinary("name", new byte[0]);
        } catch (IllegalStateException e) {
            errMsg = e.getMessage();
        }
        assertEquals("specify valid path value", errMsg);
    }

    @Test
    public void tstUssWriteCommonNullTargetPathWithParamsFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeCommon(null, new WriteParams.Builder().build());
        } catch (NullPointerException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fileNamePath is null", errMsg);
    }

    @Test
    public void tstUssWriteCommonEmptyTargetPathWithParamsFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeCommon("", new WriteParams.Builder().build());
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fileNamePath not specified", errMsg);
    }

    @Test
    public void tstUssWriteCommonEmptyTargetPathWithSpacesWithParamsFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeCommon("  ", new WriteParams.Builder().build());
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage();
        }
        assertEquals("fileNamePath not specified", errMsg);
    }

    @Test
    public void tstUssWriteCommonNullParamsFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeCommon("/xx/xx/x", null);
        } catch (NullPointerException e) {
            errMsg = e.getMessage();
        }
        assertEquals("params is null", errMsg);
    }

    @Test
    public void tstUssWriteCommonInvalidTargetPathFailure() throws ZosmfRequestException {
        String errMsg = "";
        try {
            ussWrite.writeCommon("name", new WriteParams.Builder().build());
        } catch (IllegalStateException e) {
            errMsg = e.getMessage();
        }
        assertEquals("specify valid path value", errMsg);
    }

}
