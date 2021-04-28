package uk.gov.justice.digital.delius.service;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import uk.gov.justice.digital.delius.config.DeliusIntegrationContextConfig;
import uk.gov.justice.digital.delius.config.DeliusIntegrationContextConfig.IntegrationContext;
import uk.gov.justice.digital.delius.controller.BadRequestException;
import uk.gov.justice.digital.delius.data.api.AppointmentCreateRequest;
import uk.gov.justice.digital.delius.data.api.ContextlessAppointmentCreateRequest;
import uk.gov.justice.digital.delius.data.api.Requirement;
import uk.gov.justice.digital.delius.data.api.deliusapi.ContactDto;
import uk.gov.justice.digital.delius.data.api.deliusapi.NewContact;
import uk.gov.justice.digital.delius.jpa.filters.AppointmentFilter;
import uk.gov.justice.digital.delius.jpa.standard.entity.Contact;
import uk.gov.justice.digital.delius.jpa.standard.entity.ContactType;
import uk.gov.justice.digital.delius.jpa.standard.entity.ContactType.ContactTypeBuilder;
import uk.gov.justice.digital.delius.jpa.standard.repository.ContactRepository;
import uk.gov.justice.digital.delius.jpa.standard.repository.ContactTypeRepository;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.digital.delius.utils.DateConverter.toLondonLocalDate;
import static uk.gov.justice.digital.delius.utils.DateConverter.toLondonLocalTime;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {
    private static final String PROVIDER_CODE = "CRS";
    private static final String STAFF_CODE = "CRSUATU";
    private static final String TEAM_CODE = "CRSUAT";
    private static final String RAR_TYPE_CODE = "F";
    private static final String CRSAPT_CONTACT_TYPE = "CRSAPT";
    private static final String CONTEXT = "commissioned-rehabilitation-services";

    @Mock
    private ContactRepository contactRepository;
    @Mock
    private RequirementService requirementService;
    @Mock
    private DeliusApiClient deliusApiClient;
    @Mock
    private ContactTypeRepository contactTypeRepository;

    @Captor
    private ArgumentCaptor<Specification<Contact>> specificationArgumentCaptor;
    @Captor
    private ArgumentCaptor<Sort> sortArgumentCaptor;
    private AppointmentService service;

    @BeforeEach
    public void before(){
        final var integrationContextConfig = new DeliusIntegrationContextConfig();
        IntegrationContext integrationContext = new IntegrationContext();
        integrationContextConfig.getIntegrationContexts().put(CONTEXT, integrationContext);
        integrationContext.setProviderCode(PROVIDER_CODE);
        integrationContext.setStaffCode(STAFF_CODE);
        integrationContext.setTeamCode(TEAM_CODE);
        integrationContext.setRequirementRehabilitationActivityType(RAR_TYPE_CODE);
        integrationContext.getContactMapping().setAppointmentContactType(CRSAPT_CONTACT_TYPE);

        service = new AppointmentService(contactTypeRepository, contactRepository, requirementService, deliusApiClient, integrationContextConfig);
    }

    @Test
    public void appointmentsSortedByContactDateDescending() {
        when(contactRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(ImmutableList.of());
        service.appointmentsFor(1L, AppointmentFilter.builder().build());

        verify(contactRepository).findAll(specificationArgumentCaptor.capture(), sortArgumentCaptor.capture());

        assertThat(sortArgumentCaptor.getValue().getOrderFor("contactDate")).isNotNull();
        assertThat(sortArgumentCaptor.getValue().getOrderFor("contactDate").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    public void createsAppointment() {
        // Given
        final var startTime = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        final var endTime = startTime.plusHours(1);

        havingContactType(true, builder -> builder.attendanceContact("Y"));

        final var deliusNewContactRequest = aDeliusNewContactRequest(startTime, endTime);
        final var createdContact = ContactDto.builder().id(3L).build();
        when(deliusApiClient.createNewContract(deliusNewContactRequest)).thenReturn(createdContact);

        // When
        final var appointmentCreateRequest = aAppointmentCreateRequest(startTime, endTime);
        final var response = service.createAppointment("X007", 1L, appointmentCreateRequest);

        // Then
        assertThat(response.getAppointmentId()).isEqualTo(3L);
        verifyNoInteractions(requirementService);
    }

    @Test
    public void failsToCreateAppointmentWithMissingContactType() {
        // Given
        final var startTime = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        final var endTime = startTime.plusHours(1);

        havingContactType(false, builder -> builder);

        // When
        final var appointmentCreateRequest = aAppointmentCreateRequest(startTime, endTime);
        assertThrows(BadRequestException.class,
            () -> service.createAppointment("X007", 1L, appointmentCreateRequest),
            "contact type 'X007' does not exist");
    }

    @Test
    public void failsToCreateAppointmentWithNonAppointmentContactType() {
        // Given
        final var startTime = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        final var endTime = startTime.plusHours(1);

        havingContactType(true, builder -> builder.attendanceContact("N"));

        // When
        final var appointmentCreateRequest = aAppointmentCreateRequest(startTime, endTime);
        assertThrows(BadRequestException.class,
            () -> service.createAppointment("X007", 1L, appointmentCreateRequest),
            "contact type 'X007' is not an appointment type");
    }

    @Test
    public void createsAppointmentUsingContextlessClientRequest() {
        // Given
        final var requirement = Requirement.builder().requirementId(99L).build();
        when(requirementService.getRequirement("X007", 1L, RAR_TYPE_CODE)).thenReturn(requirement);

        final var startTime = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        final var endTime = startTime.plusHours(1);

        havingContactType(true, builder -> builder.attendanceContact("Y"));

        final var deliusNewContactRequest = aDeliusNewContactRequest(startTime, endTime);
        final var createdContact = ContactDto.builder().id(3L).build();
        when(deliusApiClient.createNewContract(deliusNewContactRequest)).thenReturn(createdContact);

        // When
        final var appointmentCreateRequest = aContextlessAppointmentCreateRequest(startTime, endTime);
        final var response = service.createAppointment("X007", 1L, "commissioned-rehabilitation-services", appointmentCreateRequest);

        // Then
        assertThat(response.getAppointmentId()).isEqualTo(3L);
    }

    private void havingContactType(boolean having, UnaryOperator<ContactTypeBuilder> builderOperator) {
        final var contactType = builderOperator.apply(ContactType.builder()).build();
        final Optional<ContactType> result = having ? Optional.of(contactType) : Optional.empty();
        when(contactTypeRepository.findByCode(CRSAPT_CONTACT_TYPE)).thenReturn(result);
    }

    private NewContact aDeliusNewContactRequest(OffsetDateTime startTime, OffsetDateTime endTime) {
        return NewContact.builder()
            .offenderCrn("X007")
            .type(CRSAPT_CONTACT_TYPE)
            .outcome(null)
            .provider(PROVIDER_CODE)
            .team(TEAM_CODE)
            .staff(STAFF_CODE)
            .officeLocation("CRSSHEF")
            .date(toLondonLocalDate(startTime))
            .startTime(toLondonLocalTime(startTime))
            .endTime(toLondonLocalTime(endTime))
            .alert(null)
            .sensitive(null)
            .notes("/url")
            .description(null)
            .eventId(1L)
            .requirementId(99L)
            .build();
    }

    private AppointmentCreateRequest aAppointmentCreateRequest(OffsetDateTime startTime, OffsetDateTime endTime) {
        return AppointmentCreateRequest.builder()
            .requirementId(99L)
            .contactType(CRSAPT_CONTACT_TYPE)
            .appointmentStart(startTime)
            .appointmentEnd(endTime)
            .officeLocationCode("CRSSHEF")
            .notes("/url")
            .providerCode("CRS")
            .staffCode("CRSUATU")
            .teamCode("CRSUAT")
            .build();
    }

    private ContextlessAppointmentCreateRequest aContextlessAppointmentCreateRequest(OffsetDateTime startTime, OffsetDateTime endTime) {
        return ContextlessAppointmentCreateRequest.builder()
            .appointmentStart(startTime)
            .appointmentEnd(endTime)
            .officeLocationCode("CRSSHEF")
            .notes("/url")
            .build();
    }
}
