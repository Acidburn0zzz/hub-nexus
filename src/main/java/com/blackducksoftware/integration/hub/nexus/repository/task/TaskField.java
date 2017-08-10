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
package com.blackducksoftware.integration.hub.nexus.repository.task;

public enum TaskField {

    DISTRIBUTION("blackduck.hub.project.version.distribution"),
    FILE_PATTERNS("file.pattern.match.wildcards"),
    HUB_URL("blackduck.hub.url"),
    HUB_TIMEOUT("blackduck.hub.timeout"),
    HUB_USERNAME("blackduck.hub.username"),
    HUB_PASSWORD("blackduck.hub.password"),
    HUB_PROXY_HOST("blackduck.hub.proxy.host"),
    HUB_PROXY_PORT("blackduck.hub.proxy.port"),
    HUB_PROXY_USERNAME("blackduck.hub.proxy.username"),
    HUB_PROXY_PASSWORD("blackduck.hub.proxy.password"),
    HUB_AUTO_IMPORT_CERT("blackduck.hub.auto.import.cert"),
    PHASE("blackduck.hub.project.version.phase"),
    REPOSITORY_FIELD_ID("repositoryId"),
    REPOSITORY_PATH_FIELD_ID("repositoryPath"),
    WORKING_DIRECTORY("blackduck.working.directory");

    private String parameterKey;

    private TaskField(final String parameterKey) {
        this.parameterKey = parameterKey;
    }

    public String getParameterKey() {
        return this.parameterKey;
    }
}
