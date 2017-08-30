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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.util.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.nexus.proxy.item.StorageItem;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDescription;
import com.blackducksoftware.integration.hub.model.enumeration.ProjectVersionDistributionEnum;
import com.blackducksoftware.integration.hub.model.enumeration.ProjectVersionPhaseEnum;
import com.blackducksoftware.integration.hub.model.enumeration.VersionBomPolicyStatusOverallStatusEnum;
import com.blackducksoftware.integration.hub.model.request.ProjectRequest;
import com.blackducksoftware.integration.hub.model.request.ProjectVersionRequest;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.model.view.VersionBomPolicyStatusView;
import com.blackducksoftware.integration.hub.nexus.helpers.RestConnectionTestHelper;
import com.blackducksoftware.integration.hub.nexus.helpers.TestEventLogger;
import com.blackducksoftware.integration.hub.nexus.helpers.TestProjectCreator;
import com.blackducksoftware.integration.hub.nexus.helpers.TestingPropertyKey;
import com.blackducksoftware.integration.hub.nexus.repository.task.TaskField;
import com.blackducksoftware.integration.hub.report.api.ReportData;

public class HubServiceHelperTestIT {
    private final RestConnectionTestHelper restConnection = new RestConnectionTestHelper();
    private final TestEventLogger logger = new TestEventLogger();

    private HubServiceHelper hubServiceHelper;
    private Map<String, String> params;
    private ProjectVersionView projectVersionView;
    private ProjectView projectView = null;
    private TestProjectCreator projectCreator;

    @Before
    public void initProject() throws Exception {
        params = generateParams();
        projectCreator = new TestProjectCreator(restConnection, logger);
        hubServiceHelper = new HubServiceHelper(logger, params);
        projectView = projectCreator.getProjectViewCreateIfNeeded("NexusTest", ProjectVersionDistributionEnum.EXTERNAL, ProjectVersionPhaseEnum.DEVELOPMENT, "0.0.1");
        projectVersionView = projectCreator.getProjectVersionView(projectView, "0.0.1");
    }

    @After
    public void cleanProject() throws IntegrationException {
        projectCreator.destroyProject(projectView);
    }

    private Map<String, String> generateParams() {
        final Map<String, String> newParams = new HashMap<>();

        newParams.put(TaskField.HUB_URL.getParameterKey(), restConnection.getProperty(TestingPropertyKey.TEST_HUB_SERVER_URL));
        newParams.put(TaskField.HUB_USERNAME.getParameterKey(), restConnection.getProperty(TestingPropertyKey.TEST_USERNAME));
        newParams.put(TaskField.HUB_PASSWORD.getParameterKey(), restConnection.getProperty(TestingPropertyKey.TEST_PASSWORD));
        newParams.put(TaskField.HUB_TIMEOUT.getParameterKey(), restConnection.getProperty(TestingPropertyKey.TEST_HUB_TIMEOUT));
        newParams.put(TaskField.HUB_PROXY_HOST.getParameterKey(), restConnection.getProperty(TestingPropertyKey.TEST_PROXY_HOST_BASIC));
        newParams.put(TaskField.HUB_PROXY_PORT.getParameterKey(), restConnection.getProperty(TestingPropertyKey.TEST_PROXY_PORT_BASIC));
        newParams.put(TaskField.HUB_PROXY_USERNAME.getParameterKey(), restConnection.getProperty(TestingPropertyKey.TEST_PROXY_USER_BASIC));
        newParams.put(TaskField.HUB_PROXY_PASSWORD.getParameterKey(), restConnection.getProperty(TestingPropertyKey.TEST_PROXY_PASSWORD_BASIC));
        newParams.put(TaskField.HUB_AUTO_IMPORT_CERT.getParameterKey(), restConnection.getProperty(TestingPropertyKey.TEST_AUTO_IMPORT_HTTPS_CERT));

        return newParams;
    }

    @Test
    public void checkPolicyStatusTest() throws IntegrationException {
        final PolicyStatusDescription status = hubServiceHelper.checkPolicyStatus(projectVersionView);
        final String expected = "The Hub found no components.";
        final String actual = status.getPolicyStatusMessage();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void retrieveRiskReport() throws IntegrationException {
        final ReportData reportData = hubServiceHelper.retrieveRiskReport(5000, projectVersionView, projectView);
        final String distribution = reportData.getDistribution();
        final String phase = reportData.getPhase();
        Assert.assertEquals(ProjectVersionDistributionEnum.EXTERNAL.toString(), distribution);
        Assert.assertEquals(ProjectVersionPhaseEnum.DEVELOPMENT.toString(), phase);
    }

    @Test
    public void getOverallPolicyStatusTest() throws IntegrationException {
        final VersionBomPolicyStatusView versionBomPolicyStatusView = hubServiceHelper.getOverallPolicyStatus(projectVersionView);
        final VersionBomPolicyStatusOverallStatusEnum actual = versionBomPolicyStatusView.overallStatus;
        final String expected = "NOT_IN_VIOLATION";
        Assert.assertEquals(expected, actual.toString());
    }

    @Test
    public void createProjectRequestTest() {
        final StorageItem item = mock(StorageItem.class);
        when(item.getName()).thenReturn("NexusTest");
        when(item.getParentPath()).thenReturn("/NexusTest/0.0.1");

        final ProjectRequest testProjectRequest = hubServiceHelper.createProjectRequest(ProjectVersionDistributionEnum.EXTERNAL.toString(), ProjectVersionPhaseEnum.DEVELOPMENT.toString(), item);
        Assert.assertEquals("NexusTest", testProjectRequest.getName());
    }

    @Test
    public void createProjectAndVersionTest() throws IntegrationException {
        final ProjectRequest projectRequest = new ProjectRequest("NexusTest2");
        final ProjectVersionRequest versionRequest = new ProjectVersionRequest(ProjectVersionDistributionEnum.EXTERNAL, ProjectVersionPhaseEnum.DEVELOPMENT, "0.0.2");
        projectRequest.setVersionRequest(versionRequest);
        boolean projectFound;

        try {
            hubServiceHelper.createProjectAndVersion(projectRequest);
            projectFound = true;
        } catch (final IntegrationException e) {
            projectFound = false;
        }

        Assert.assertTrue(projectFound);

        final ProjectView testProjectView = projectCreator.getProjectView("NexusTest2");
        projectCreator.destroyProject(testProjectView);
    }

    @Test
    public void installCLITest() throws IntegrationException, IOException {
        final File tempInstallLoc = new File("testCli");
        hubServiceHelper.installCLI(tempInstallLoc);
        Assert.assertTrue(tempInstallLoc.exists());
        if (tempInstallLoc.isDirectory()) {
            FileUtils.cleanDirectory(tempInstallLoc);
        }
        Files.deleteIfExists(tempInstallLoc.toPath());
    }
}
