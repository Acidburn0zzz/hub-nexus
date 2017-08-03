/*
 * hub-nexus
 *
 * 	Copyright (C) 2017 Black Duck Software, Inc.
 * 	http://www.blackducksoftware.com/
 *
 * 	Licensed to the Apache Software Foundation (ASF) under one
 * 	or more contributor license agreements. See the NOTICE file
 * 	distributed with this work for additional information
 * 	regarding copyright ownership. The ASF licenses this file
 * 	to you under the Apache License, Version 2.0 (the
 * 	"License"); you may not use this file except in compliance
 * 	with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing,
 * 	software distributed under the License is distributed on an
 * 	"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * 	KIND, either express or implied. See the License for the
 * 	specific language governing permissions and limitations
 * 	under the License.
 */
package com.blackducksoftware.integration.hub.nexus.application;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.EagerSingleton;
import org.sonatype.nexus.plugin.PluginIdentity;

@Named
@EagerSingleton
public class HubNexusPlugin extends PluginIdentity {

    public static final String ID_PREFIX = "hub-nexus";

    public static final String GROUP_ID = "com.blackducksoftware.integrations";

    public static final String ARTIFACT_ID = ID_PREFIX;

    public static final String REST_PREFIX = "/" + ID_PREFIX;

    public static final String PERMISSION_PREFIX_LOGGERS = "blackducksoftware:logconfig:";

    public static final String PERMISSION_PREFIX_LOG = "blackducksoftware:logs:";

    @Inject
    public HubNexusPlugin() throws Exception {
        super(GROUP_ID, ARTIFACT_ID);
        this.log.info("Hub nexus plugin started");
    }

}
