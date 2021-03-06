package uk.gov.justice.digital.delius.controller.secure;

import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.justice.digital.delius.FlywayRestoreExtension;
import uk.gov.justice.digital.delius.data.api.CommunityOrPrisonOffenderManager;
import uk.gov.justice.digital.delius.data.api.Custody;
import uk.gov.justice.digital.delius.data.api.UpdateCustody;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ExtendWith(FlywayRestoreExtension.class)
public class CustodyUpdateAPITest extends IntegrationTestBase {
    private static final String NOMS_NUMBER = "G9542VP";
    private static final String OFFENDER_ID = "2500343964";
    private static final String PRISON_BOOKING_NUMBER = "V74111";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @SpyBean
    private TelemetryClient telemetryClient;

    @BeforeEach
    public void setup() {
        super.setup();
        //noinspection SqlWithoutWhere
        jdbcTemplate.execute("DELETE FROM CONTACT");
    }

    @Test
    public void mustHaveUpdateRole() {
        final var token = createJwt("ROLE_COMMUNITY");

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(createUpdateCustody("MDI"))
                .when()
                .put(format("offenders/nomsNumber/%s/custody/bookingNumber/%s",  NOMS_NUMBER, PRISON_BOOKING_NUMBER))
                .then()
                .statusCode(403);
    }

    @Test
    public void updatePrisonInstitution() {
        final var token = createJwt("ROLE_COMMUNITY_CUSTODY_UPDATE", "ROLE_COMMUNITY");

        final var custody = given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(createUpdateCustody("MDI"))
                .when()
                .put(format("offenders/nomsNumber/%s/custody/bookingNumber/%s", NOMS_NUMBER, PRISON_BOOKING_NUMBER))
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(Custody.class);

        assertThat(custody.getInstitution().getNomsPrisonInstitutionCode()).isEqualTo("MDI");
        verify(telemetryClient).trackEvent(eq("P2PTransferPrisonUpdated"), any(), isNull());

        //custody record should have been updated
        final var custodyRecord = jdbcTemplate.query(
                "SELECT * from CUSTODY where PRISONER_NUMBER = ?",
                List.of(PRISON_BOOKING_NUMBER).toArray(),
                new ColumnMapRowMapper()).get(0);
        assertThat(toLocalDateTime(custodyRecord.get("LAST_UPDATED_DATETIME"))).isCloseTo(LocalDateTime.now(), within(10, ChronoUnit.SECONDS));
        assertThat(toLocalDateTime(custodyRecord.get("LOCATION_CHANGE_DATE")).toLocalDate()).isEqualTo(LocalDate.now());

        // at least one custody history record should be inserted
        final var latestCustodyHistoryRecord = jdbcTemplate.query(
                "SELECT * from CUSTODY_HISTORY where OFFENDER_ID = ?",
                List.of(OFFENDER_ID).toArray(),
                new ColumnMapRowMapper()).stream().max(Comparator.comparing(record -> toLocalDateTime(record.get("HISTORICAL_DATE")))).orElseThrow();
        assertThat(toLocalDateTime(latestCustodyHistoryRecord.get("HISTORICAL_DATE")).toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(latestCustodyHistoryRecord.get("DETAIL")).isEqualTo("Moorland (HMP & YOI)");


        // should have allocated a new "unallocated POM" at the prison probation area
        final var offenderManagers = given()
                .auth().oauth2(token)
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .get("/offenders/nomsNumber/G9542VP/allOffenderManagers")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(CommunityOrPrisonOffenderManager[].class);

        assertThat(offenderManagers).hasSize(2);

        final var prisonOffenderManager = Stream.of(offenderManagers).filter(CommunityOrPrisonOffenderManager::getIsPrisonOffenderManager).findAny().orElseThrow();

        assertThat(prisonOffenderManager.getIsUnallocated()).isTrue();
        assertThat(prisonOffenderManager.getProbationArea().getInstitution().getNomsPrisonInstitutionCode()).isEqualTo("MDI");
        assertThat(prisonOffenderManager.getStaffCode()).isEqualTo("MDIALLU");


        final var contact = jdbcTemplate.query(
                "SELECT * from CONTACT where OFFENDER_ID = ?",
                List.of(OFFENDER_ID).toArray(),
                new ColumnMapRowMapper())
                .stream()
                .filter(record -> toLocalDateTime(record.get("CONTACT_DATE")).toLocalDate().equals(LocalDate.now()))
                .filter(record -> record.get("EVENT_ID") != null)
                .findFirst()
                .orElseThrow();

        assertThat(contact.get("NOTES").toString()).contains("Custodial Establishment: Moorland (HMP & YOI)");

    }

    private String createUpdateCustody(String prisonCode) {
        return writeValueAsString(UpdateCustody
                .builder()
                .nomsPrisonInstitutionCode(prisonCode)
                .build());
    }
    private LocalDateTime toLocalDateTime(Object columnValue) {
        return ((Timestamp)columnValue).toLocalDateTime();
    }


}
