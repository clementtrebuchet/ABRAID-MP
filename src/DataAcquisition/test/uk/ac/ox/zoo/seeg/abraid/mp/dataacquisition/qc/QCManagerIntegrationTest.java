package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.Main;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Performs integration tests for the QCManager class.
 *
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = Main.APPLICATION_CONTEXT_LOCATION)
@Transactional
public class QCManagerIntegrationTest {
    @Autowired
    private QCManager qcManager;

    @Test
    public void stage1NotRunWhenLocationPrecisionIsCountryAndStage2PassesAndStage3Passes() {
        // Arrange
        long japanId = 156L;
        Location location = new Location("Japan", 138.47861, 36.09854, LocationPrecision.COUNTRY, japanId);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(3);
        assertThat(location.getAdminUnitQC()).isNull();
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: location not an ADMIN1 or ADMIN2. QC " +
                "stage 2 passed: location already within land. QC stage 3 passed: location already within HealthMap " +
                "country.");
    }

    @Test
    public void stage1NotRunWhenLocationPrecisionIsPreciseAndStage2Fails() {
        // Arrange
        Location location = new Location("Somewhere in the North Sea", 3.524163, 56.051420, LocationPrecision.PRECISE);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(1);
        assertThat(location.getAdminUnitQC()).isNull();
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: location not an ADMIN1 or ADMIN2. QC stage " +
                "2 failed: location too distant from land (closest point is (4.916593,53.291621) at distance " +
                "320.061km).");
    }

    @Test
    public void passesStage1AndStage2AndStage3() {
        // Arrange
        long mexicoId = 14L;
        Location location = new Location("Estado de México, Mexico", -99.4922, 19.3318, LocationPrecision.ADMIN1,
                mexicoId);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(3);
        assertThat(location.getAdminUnitQC()).isNotNull();
        assertThat(location.getAdminUnitQC().getGaulCode()).isEqualTo(1006355);
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: closest distance is 10.92% of the square " +
                "root of the area. QC stage 2 passed: location already within land. QC stage 3 passed: location " +
                "already within HealthMap country.");
    }

    @Test
    public void failsStage1() {
        // Arrange
        long vietnamId = 152L;
        Location location = new Location("Huyện Cai Lậy, Tiền Giang, Vietnam", 108.69807, 7.90055,
                LocationPrecision.ADMIN2, vietnamId);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(0);
        assertThat(location.getAdminUnitQC()).isNull();
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 failed: closest distance is 2841.01% of the square " +
                "root of the area (GAUL code 1002305: \"Con Dao\").");
    }

    @Test
    public void passesStage1ButFailsStage2() {
        // Arrange
        long indonesiaId = 184L;
        Location location = new Location("Central Sulawesi, Indonesia", 121, -1, LocationPrecision.ADMIN1, indonesiaId);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(1);
        assertThat(location.getAdminUnitQC()).isNotNull();
        assertThat(location.getAdminUnitQC().getGaulCode()).isEqualTo(1013690);
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: closest distance is 9.01% of the square " +
                "root of the area. QC stage 2 failed: location too distant from land (closest point is " +
                "(121.208213,-1.166690) at distance 29.610km).");
    }

    @Test
    public void passesStage1AndStage2ButFailsStage3() {
        // Arrange
        long usId = 106L;
        Location location = new Location("Door County, Wisconsin, United States", -87.3001, 44.91666,
                LocationPrecision.ADMIN2, usId);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(2);
        assertThat(location.getAdminUnitQC()).isNotNull();
        assertThat(location.getAdminUnitQC().getGaulCode()).isEqualTo(31738);
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: closest distance is 8.76% of the square " +
                "root of the area. QC stage 2 passed: location already within land. QC stage 3 failed: location " +
                "too distant from HealthMap country (closest point is (-87.344992,44.814352) at distance 11.910km).");
    }

    @Test
    public void passesStage3IfNoHealthMapCountrySpecified() {
        // Arrange
        Location location = new Location("Door County, Wisconsin, United States", -87.3001, 44.91666,
                LocationPrecision.ADMIN2);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(3);
        assertThat(location.getAdminUnitQC()).isNotNull();
        assertThat(location.getAdminUnitQC().getGaulCode()).isEqualTo(31738);
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: closest distance is 8.76% of the square " +
                "root of the area. QC stage 2 passed: location already within land. QC stage 3 passed: no country " +
                "geometries associated with this location.");
    }

    @Test
    public void passesStage3IfHealthMapCountryHasNoGeometries() {
        // Arrange
        long maldivesId = 143;
        Location location = new Location("Maldives", 73.46564, 5.84270, LocationPrecision.COUNTRY, maldivesId);

        // Act
        int passedQCStage = qcManager.performQC(location);

        // Assert
        assertThat(passedQCStage).isEqualTo(3);
        assertThat(location.getAdminUnitQC()).isNull();
        assertThat(location.getQcMessage()).isEqualTo("QC stage 1 passed: location not an ADMIN1 or ADMIN2. QC " +
                "stage 2 passed: location (73.465640,5.842700) snapped to land (distance 0.820km). QC stage 3 " +
                "passed: no country geometries associated with this location.");
    }
}