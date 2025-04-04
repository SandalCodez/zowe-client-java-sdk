/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package zowe.client.sdk.parse.type;

/**
 * Represents json parse operation type
 *
 * @author Frank Giordano
 * @version 3.0
 */
public enum ParseType {

    DATASET,
    JOB,
    JOB_FILE,
    MEMBER,
    MVS_CONSOLE,
    PROPS,
    TSO_CONSOLE,
    TSO_STOP,
    UNIX_FILE,
    UNIX_ZFS,
    ZOS_LOG_ITEM,
    ZOS_LOG_REPLY,
    ZOSMF_SYSTEMS,
    ZOSMF_INFO

}
