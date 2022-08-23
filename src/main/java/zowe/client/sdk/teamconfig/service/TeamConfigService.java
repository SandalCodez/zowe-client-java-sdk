/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package zowe.client.sdk.teamconfig.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zowe.client.sdk.teamconfig.keytar.KeyTarConfig;
import zowe.client.sdk.teamconfig.model.ConfigContainer;
import zowe.client.sdk.teamconfig.model.Partition;
import zowe.client.sdk.teamconfig.model.Profile;
import zowe.client.sdk.teamconfig.types.SectionType;
import zowe.client.sdk.teamconfig.utility.TeamConfigUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TeamConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(TeamConfigService.class);

    public ConfigContainer getTeamConfig(KeyTarConfig config) throws Exception {
        JSONParser parser = new JSONParser();
        Object obj;
        try {
            obj = parser.parse(new FileReader(config.getLocation()));
        } catch (IOException | ParseException e) {
            throw new Exception("Error reading zowe global team configuration file");
        }
        return parseJson((JSONObject) obj);
    }

    private ConfigContainer parseJson(JSONObject jsonObj) throws Exception {
        String schema = null;
        Boolean autoStore = null;
        List<Profile> profiles = new ArrayList<>();
        Map<String, String> defaults = new HashMap<>();
        List<Partition> partitions = new ArrayList<>();

        Set<String> jsonSectionKeys = jsonObj.keySet();
        for (final String keySectionVal : jsonSectionKeys) {
            if (SectionType.$SCHEMA.getValue().equals(keySectionVal)) {
                schema = (String) jsonObj.get(SectionType.$SCHEMA.getValue());
            } else if (SectionType.PROFILES.getValue().equals(keySectionVal)) {
                JSONObject jsonProfileObj = (JSONObject) jsonObj.get(SectionType.PROFILES.getValue());
                Set<String> jsonProfileKeys = jsonProfileObj.keySet();
                for (String profileKeyVal : jsonProfileKeys) {
                    final JSONObject profileTypeJsonObj = (JSONObject) jsonProfileObj.get(profileKeyVal);
                    Set<String> isEmbeddedKeyProfile = profileTypeJsonObj.keySet();
                    if (isPartition(isEmbeddedKeyProfile)) {
                        partitions.add(getPartition(profileKeyVal, profileTypeJsonObj));
                    } else {
                        profiles.add(new Profile((String) profileTypeJsonObj.get("type"),
                                (JSONObject) profileTypeJsonObj.get("properties"),
                                (JSONArray) profileTypeJsonObj.get("secure")));
                    }
                }
            } else if (SectionType.DEFAULTS.getValue().equals(keySectionVal)) {
                JSONObject keyValues = (JSONObject) jsonObj.get(SectionType.DEFAULTS.getValue());
                for (final Object defaultKeyVal : keyValues.keySet()) {
                    String key = (String) defaultKeyVal;
                    String value = (String) keyValues.get(key);
                    defaults.put(key, value);
                }
            } else if (SectionType.AUTOSTORE.getValue().equals(keySectionVal)) {
                autoStore = (Boolean) jsonObj.get(SectionType.AUTOSTORE.getValue());
            }
        }

        return new ConfigContainer(partitions, schema, profiles, defaults, autoStore);
    }

    private Partition getPartition(String name, JSONObject jsonObject) {
        Set<String> keyObjs = jsonObject.keySet();
        List<Profile> profiles = new ArrayList<>();
        Map<String, String> properties = new HashMap<>();
        LOG.debug("Partition found name {} containing {}:", name, jsonObject);
        for (Object keyObj : keyObjs) {
            final var keyVal = (String) keyObj;
            if (SectionType.PROFILES.getValue().equals(keyVal)) {
                final JSONObject jsonProfileObj = (JSONObject) jsonObject.get(SectionType.PROFILES.getValue());
                Set<String> jsonProfileKeys = jsonProfileObj.keySet();
                for (String profileKeyVal : jsonProfileKeys) {
                    final JSONObject profileTypeJsonObj = (JSONObject) jsonProfileObj.get(profileKeyVal);
                    profiles.add(new Profile((String) profileTypeJsonObj.get("type"),
                            (JSONObject) profileTypeJsonObj.get("properties"),
                            (JSONArray) profileTypeJsonObj.get("secure")));
                }
            } else if ("properties".equalsIgnoreCase(keyVal)) {
                properties = TeamConfigUtils.parseJsonPropsObj((JSONObject) jsonObject.get(keyVal));
            }
        }
        return new Partition(name, properties, profiles);
    }

    private boolean isPartition(Set<String> profileKeyObj) throws Exception {
        Iterator<String> itr = profileKeyObj.iterator();
        if (itr.hasNext()) {
            String keyVal = itr.next();
            return SectionType.PROFILES.getValue().equals(keyVal);
        } else {
            throw new Exception("Profile type detail missing in profile section.");
        }
    }

}
