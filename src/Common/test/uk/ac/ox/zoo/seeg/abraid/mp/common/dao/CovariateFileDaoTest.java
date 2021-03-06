package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateValueBin;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the CovariateFileDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateFileDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private CovariateFileDao covariateFileDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Test
    public void getAllReturnsAll() {
        assertThat(covariateFileDao.getAll()).hasSize(22);
    }

    @Test
    public void getCovariateFilesByDiseaseGroupReturnsCorrectSet() {
        assertThat(covariateFileDao.getCovariateFilesByDiseaseGroup(diseaseGroupDao.getById(87))).hasSize(8);
    }

    @Test
    public void getCovariateFilesByDiseaseGroupExcludesHidden() {
        CovariateFile file = covariateFileDao.getById(1);
        file.setHide(true);
        covariateFileDao.save(file);
        assertThat(covariateFileDao.getCovariateFilesByDiseaseGroup(diseaseGroupDao.getById(87))).hasSize(7);
    }

    @Test
    public void saveAndReload() {
        // Arrange
        CovariateFile covariateFile = new CovariateFile("NAME", true, true, "INFO");
        Collection<DiseaseGroup> enabledDiseaseGroups = Arrays.asList(diseaseGroupDao.getById(87), diseaseGroupDao.getById(60));
        Collection<CovariateValueBin> bins = Arrays.asList(new CovariateValueBin(covariateFile, 0, 5, 10), new CovariateValueBin(covariateFile, 5, 10, 1), new CovariateValueBin(covariateFile, 10, 10, 10));
        List<CovariateSubFile> subFiles = Arrays.asList(new CovariateSubFile(covariateFile, "QUALIFIER", "FILE"));
        covariateFile.setFiles(subFiles);
        covariateFile.setEnabledDiseaseGroups(enabledDiseaseGroups);
        covariateFile.setCovariateValueHistogramData(bins);

        // Act
        covariateFileDao.save(covariateFile);

        // Assert
        Integer id = covariateFile.getId();
        flushAndClear();
        covariateFile = covariateFileDao.getById(id);
        assertThat(covariateFile.getName()).isEqualTo("NAME");
        assertThat(covariateFile.getInfo()).isEqualTo("INFO");
        assertThat(covariateFile.getFiles()).hasSize(1);
        CovariateSubFile subfile = covariateFile.getFiles().get(0);
        assertThat(subfile.getFile()).isEqualTo("FILE");
        assertThat(subfile.getQualifier()).isEqualTo("QUALIFIER");
        assertThat(covariateFile.getHide()).isEqualTo(true);
        assertThat(covariateFile.getEnabledDiseaseGroups()).containsAll(enabledDiseaseGroups);
        assertThat(covariateFile.getEnabledDiseaseGroups()).hasSameSizeAs(enabledDiseaseGroups);
        assertThat(covariateFile.getCovariateValueHistogramData()).hasSameSizeAs(bins);
    }
}
