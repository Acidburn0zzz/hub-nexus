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
package com.blackducksoftware.integration.hub.nexus.repository.task.walker;

import java.util.concurrent.ExecutorService;

import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.walker.WalkerContext;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.nexus.application.HubServiceHelper;
import com.blackducksoftware.integration.hub.nexus.event.HubPolicyCheckEvent;
import com.blackducksoftware.integration.hub.nexus.event.handler.HubEventHandler;
import com.blackducksoftware.integration.hub.nexus.event.handler.HubPolicyCheckEventHandler;
import com.blackducksoftware.integration.hub.nexus.util.ItemAttributesHelper;
import com.blackducksoftware.integration.hub.nexus.util.ScanAttributesHelper;

public class PolicyRepositoryWalker extends RepositoryWalkerProcessor<HubPolicyCheckEvent> {
    private final ItemAttributesHelper itemAttributesHelper;
    private final HubServiceHelper hubServiceHelper;
    private final ScanAttributesHelper scanAttributesHelper;

    public PolicyRepositoryWalker(final ExecutorService executorService, final ItemAttributesHelper itemAttributesHelper, final ScanAttributesHelper scanAttributesHelper,
            final HubServiceHelper hubServiceHelper) {
        super(executorService);
        this.scanAttributesHelper = scanAttributesHelper;
        this.itemAttributesHelper = itemAttributesHelper;
        this.hubServiceHelper = hubServiceHelper;
    }

    @Override
    public HubEventHandler<HubPolicyCheckEvent> getHubEventHandler(final WalkerContext context, final StorageItem item) throws IntegrationException {
        final HubPolicyCheckEvent event = createEvent(context, item);
        final HubPolicyCheckEventHandler hubPolicyCheckEventHandler = new HubPolicyCheckEventHandler(itemAttributesHelper, event, hubServiceHelper);

        return hubPolicyCheckEventHandler;
    }

    public HubPolicyCheckEvent createEvent(final WalkerContext context, final StorageItem item) throws IntegrationException {
        final ProjectVersionView projectVersionView = getProjectVersion(item);
        final HubPolicyCheckEvent event = new HubPolicyCheckEvent(item.getRepositoryItemUid().getRepository(), item, scanAttributesHelper.getScanAttributes(), context.getResourceStoreRequest(), projectVersionView);
        return event;
    }

    private ProjectVersionView getProjectVersion(final StorageItem item) throws IntegrationException {
        final String url = itemAttributesHelper.getApiUrl(item);
        final ProjectVersionView projectVersionView = hubServiceHelper.getProjectVersionRequestService().getItem(url, ProjectVersionView.class);
        return projectVersionView;
    }

}
