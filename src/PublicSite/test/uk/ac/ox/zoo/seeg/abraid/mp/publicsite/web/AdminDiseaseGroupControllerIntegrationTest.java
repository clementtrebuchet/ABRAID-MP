package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractPublicSiteIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the AdminDiseaseGroupController class.
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:PublicSite/web/WEB-INF/abraid-servlet-beans.xml",
        "file:PublicSite/web/WEB-INF/applicationContext.xml" })
public class AdminDiseaseGroupControllerIntegrationTest extends AbstractPublicSiteIntegrationTests {
    public static final String MODELWRAPPER_URL_PREFIX = "http://username:password@localhost:8080/modelwrapper";

    @ReplaceWithMock
    @Autowired
    private WebServiceClient webServiceClient;

    @Autowired
    private ModelRunService modelRunService;

    @Autowired
    private DiseaseService diseaseService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        // Setup user
        PublicSiteUser loggedInUser = mock(PublicSiteUser.class);
        when(loggedInUser.getId()).thenReturn(1);
        AbstractAuthenticatingTests.setupCurrentUser(loggedInUser);
    }

    @Test
    public void getModelRunInformation() throws Exception {
        this.mockMvc.perform(
                get(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/modelruninformation"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"lastModelRunText\":\"never\",\"diseaseOccurrencesText\":\"total 45, occurring between 24 Feb 2014 and 27 Feb 2014\",\"hasModelBeenSuccessfullyRun\":false,\"canRunModel\":true}"));
    }

    @Test
    public void getModelRunInformationRejectsNonGETRequests() throws Exception {
        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/modelruninformation"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/modelruninformation"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/modelruninformation"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/modelruninformation"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void requestModelRun() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        setDiseaseGroupParametersToEnsureHelperReturnsOccurrences(diseaseGroupId);
        mockModelWrapperWebServiceCall();
        assertThat(modelRunService.getLastRequestedModelRun(diseaseGroupId)).isNull();

        // Act
        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/" + diseaseGroupId + "/requestmodelrun"))
                .andExpect(status().isOk());

        // Assert
        assertThat(modelRunService.getLastRequestedModelRun(diseaseGroupId)).isNotNull();
    }

    @Test
    public void requestModelRunRejectsNonPOSTRequests() throws Exception {
        this.mockMvc.perform(
                get(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/requestmodelrun"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/requestmodelrun"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/requestmodelrun"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/requestmodelrun"))
                .andExpect(status().isMethodNotAllowed());
    }

    private void setDiseaseGroupParametersToEnsureHelperReturnsOccurrences(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(27);
        diseaseGroup.setOccursInAfrica(false);
        diseaseGroup.setMinDistinctCountries(null);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    private void mockModelWrapperWebServiceCall() {
        when(webServiceClient.makePostRequestWithJSON(startsWith(MODELWRAPPER_URL_PREFIX), anyString()))
                .thenReturn("{\"modelRunName\":\"testname\"}");
    }
}