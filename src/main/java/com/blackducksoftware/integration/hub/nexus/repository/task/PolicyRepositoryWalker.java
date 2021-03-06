/*
 * hub-nexus
 *
 * 	Copyright (C) 2018 Black Duck Software, Inc.
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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.sonatype.nexus.proxy.item.StorageCollectionItem;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.item.uid.IsHiddenAttribute;
import org.sonatype.nexus.proxy.walker.AbstractWalkerProcessor;
import org.sonatype.nexus.proxy.walker.WalkerContext;
import org.sonatype.sisu.goodies.common.Loggers;
import org.sonatype.sisu.goodies.eventbus.EventBus;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.nexus.application.HubServiceHelper;
import com.blackducksoftware.integration.hub.nexus.event.HubPolicyCheckEvent;
import com.blackducksoftware.integration.hub.nexus.util.ItemAttributesHelper;

public class PolicyRepositoryWalker extends AbstractWalkerProcessor {
    private final Logger logger = Loggers.getLogger(getClass());
    private final EventBus eventBus;
    private final ItemAttributesHelper itemAttributesHelper;
    private final Map<String, String> taskParameters;
    private final HubServiceHelper hubServiceHelper;

    public PolicyRepositoryWalker(final EventBus eventBus, final ItemAttributesHelper itemAttributesHelper, final Map<String, String> taskParameters, final HubServiceHelper hubServiceHelper) {
        this.itemAttributesHelper = itemAttributesHelper;
        this.eventBus = eventBus;
        this.taskParameters = taskParameters;
        this.hubServiceHelper = hubServiceHelper;
    }

    @Override
    public void processItem(final WalkerContext context, final StorageItem item) throws Exception {
        try {
            if (item instanceof StorageCollectionItem) {
                return; // directory found
            }
            if (item.getRepositoryItemUid().getBooleanAttributeValue(IsHiddenAttribute.class)) {
                return;
            }

            if (StringUtils.isNotBlank(item.getRemoteUrl())) {
                logger.info("Item came from a proxied repository, skipping: {}", item);
                return;
            }

            final long scanResult = itemAttributesHelper.getScanResult(item);
            if (scanResult == ItemAttributesHelper.SCAN_STATUS_SUCCESS) {
                logger.info("Begin Policy check for item {}", item);
                final ProjectVersionView projectVersionView = getProjectVersion(item);
                final HubPolicyCheckEvent event = new HubPolicyCheckEvent(item.getRepositoryItemUid().getRepository(), item, taskParameters, context.getResourceStoreRequest(), projectVersionView);
                eventBus.post(event);
            }
        } catch (final Exception ex) {
            logger.error("Error occurred in walker processor for repository: ", ex);
        }
    }

    private ProjectVersionView getProjectVersion(final StorageItem item) throws IntegrationException {
        final String url = itemAttributesHelper.getApiUrl(item);
        final ProjectVersionView projectVersionView = hubServiceHelper.getProjectVersionRequestService().getItem(url, ProjectVersionView.class);
        return projectVersionView;
    }
}
