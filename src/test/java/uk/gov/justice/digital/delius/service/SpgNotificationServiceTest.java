package uk.gov.justice.digital.delius.service;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.justice.digital.delius.jpa.standard.entity.*;
import uk.gov.justice.digital.delius.jpa.standard.repository.*;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpgNotificationServiceTest {
    @Mock
    private BusinessInteractionRepository businessInteractionRepository;
    @Mock
    private BusinessInteractionXmlMapRepository businessInteractionXmlMapRepository;
    @Mock
    private StandardReferenceRepository standardReferenceRepository;
    @Mock
    private ProbationAreaRepository probationAreaRepository;
    @Mock
    private SpgNotificationRepository spgNotificationRepository;
    @Mock
    private SpgNotificationHelperRepository spgNotificationHelperRepository;

    @Captor
    private ArgumentCaptor<List<SpgNotification>> spgNotificationsCaptor;

    private SpgNotificationService spgNotificationService;


    @Before
    public void before() {
        when(businessInteractionRepository.findByBusinessInteractionCode(any())).thenAnswer(
                params -> Optional.of(
                        BusinessInteraction
                                .builder()
                                .businessInteractionCode(params.getArguments()[0].toString()) // echo back the code looked for
                                .build()));
        when(businessInteractionXmlMapRepository.findByBusinessInteractionId(any())).thenReturn(Optional.of(BusinessInteractionXmlMap
                .builder()
                .dataUpdateMode("I")
                .build()));
        when(standardReferenceRepository.findByCodeAndCodeSetName(any(), any())).thenReturn(Optional.of(StandardReference
                .builder()
                .standardReferenceListId(88L)
                .build()));
        when(probationAreaRepository.findByCode(any())).thenReturn(Optional.of(ProbationArea.builder().build()));
        when(spgNotificationHelperRepository.getInterestedCRCs(any())).thenReturn(ImmutableList.of());
        when(spgNotificationHelperRepository.getNextControlSequence(any())).thenReturn(1L);

        spgNotificationService = new SpgNotificationService(businessInteractionRepository, businessInteractionXmlMapRepository, standardReferenceRepository, probationAreaRepository, spgNotificationRepository, spgNotificationHelperRepository);
    }

    @Test
    public void whenNoInterestedCRCsThenNothingIsSaved() {
        when(spgNotificationHelperRepository.getInterestedCRCs(any())).thenReturn(ImmutableList.of());

        spgNotificationService.notifyNewCourtCaseCreated(Event
                .builder()
                .offenderId(99L)
                .courtAppearances(ImmutableList.of(
                        CourtAppearance.builder().build(),
                        CourtAppearance.builder().build()
                        ))
                .build());

        verify(spgNotificationHelperRepository, atLeastOnce()).getInterestedCRCs("99");
        verify(spgNotificationRepository, atLeastOnce()).save(spgNotificationsCaptor.capture());

        spgNotificationsCaptor.getAllValues().forEach(notificationsSaved -> assertThat(notificationsSaved).isEmpty());
    }

    @Test
    public void withOneInterestedCBCOffenderUpdateNotificationIsSaved() {
        when(spgNotificationHelperRepository.getInterestedCRCs(any())).thenReturn(ImmutableList.of(ProbationArea
                .builder()
                .code("AA")
                .build()));

        spgNotificationService.notifyNewCourtCaseCreated(Event
                .builder()
                .offenderId(99L)
                .courtAppearances(ImmutableList.of(
                        CourtAppearance.builder().build(),
                        CourtAppearance.builder().build()
                ))
                .build());

        verify(spgNotificationRepository, atLeastOnce()).save(spgNotificationsCaptor.capture());

        assertThat(findFor(SpgNotificationService.NotificationEvents.UPDATE_OFFENDER.getNotificationCode())).hasSize(1);
    }

    @Test
    public void withManyInterestedCRCsOffenderUpdateNotificationIsSavedForEachProbationArea() {
        when(spgNotificationHelperRepository.getInterestedCRCs(any())).thenReturn(ImmutableList.of(
                ProbationArea
                        .builder()
                        .code("AA")
                        .build(),
                ProbationArea
                        .builder()
                        .code("AB")
                        .build()));

        spgNotificationService.notifyNewCourtCaseCreated(Event
                .builder()
                .offenderId(99L)
                .courtAppearances(ImmutableList.of(
                        CourtAppearance.builder().build(),
                        CourtAppearance.builder().build()
                ))
                .build());

        verify(spgNotificationRepository, atLeastOnce()).save(spgNotificationsCaptor.capture());

        assertThat(findFor(SpgNotificationService.NotificationEvents.UPDATE_OFFENDER.getNotificationCode())).hasSize(2);
        assertThat(findFor(SpgNotificationService.NotificationEvents.UPDATE_OFFENDER.getNotificationCode()).stream().anyMatch(notification -> notification.getReceiverIdentity().getCode().equals("AA"))).isTrue();
        assertThat(findFor(SpgNotificationService.NotificationEvents.UPDATE_OFFENDER.getNotificationCode()).stream().anyMatch(notification -> notification.getReceiverIdentity().getCode().equals("AB"))).isTrue();
    }


    @Test
    public void withOneInterestedCRCInsertEventNotificationIsSaved() {
        when(spgNotificationHelperRepository.getInterestedCRCs(any())).thenReturn(ImmutableList.of(ProbationArea
                .builder()
                .code("AA")
                .build()));

        spgNotificationService.notifyNewCourtCaseCreated(Event
                .builder()
                .offenderId(99L)
                .courtAppearances(ImmutableList.of(
                        CourtAppearance.builder().build(),
                        CourtAppearance.builder().build()
                ))
                .build());

        verify(spgNotificationRepository, atLeastOnce()).save(spgNotificationsCaptor.capture());

        assertThat(findFor(SpgNotificationService.NotificationEvents.INSERT_EVENT.getNotificationCode())).hasSize(1);
    }

    @Test
    public void withManyInterestedCRCsInsertEventNotificationIsSavedForEachProbationArea() {
        when(spgNotificationHelperRepository.getInterestedCRCs(any())).thenReturn(ImmutableList.of(
                ProbationArea
                    .builder()
                    .code("AA")
                    .build(),
                ProbationArea
                    .builder()
                    .code("AB")
                    .build()));

        spgNotificationService.notifyNewCourtCaseCreated(Event
                .builder()
                .offenderId(99L)
                .courtAppearances(ImmutableList.of(
                        CourtAppearance.builder().build(),
                        CourtAppearance.builder().build()
                ))
                .build());

        verify(spgNotificationRepository, atLeastOnce()).save(spgNotificationsCaptor.capture());

        assertThat(findFor(SpgNotificationService.NotificationEvents.INSERT_EVENT.getNotificationCode())).hasSize(2);
        assertThat(findFor(SpgNotificationService.NotificationEvents.INSERT_EVENT.getNotificationCode()).stream().anyMatch(notification -> notification.getReceiverIdentity().getCode().equals("AA"))).isTrue();
        assertThat(findFor(SpgNotificationService.NotificationEvents.INSERT_EVENT.getNotificationCode()).stream().anyMatch(notification -> notification.getReceiverIdentity().getCode().equals("AB"))).isTrue();
    }

    @Test
    public void withOneInterestedCRCInsertCourtAppearanceNotificationIsSavedForEachCourtAppearance() {
        when(spgNotificationHelperRepository.getInterestedCRCs(any())).thenReturn(ImmutableList.of(ProbationArea
                .builder()
                .code("AA")
                .build()));

        spgNotificationService.notifyNewCourtCaseCreated(Event
                .builder()
                .offenderId(99L)
                .courtAppearances(ImmutableList.of(
                        CourtAppearance.builder().build(),
                        CourtAppearance.builder().build()
                ))
                .build());

        verify(spgNotificationRepository, atLeastOnce()).save(spgNotificationsCaptor.capture());

        assertThat(findFor(SpgNotificationService.NotificationEvents.INSERT_COURT_APPEARANCE.getNotificationCode())).hasSize(2);
    }

    @Test
    public void withManyInterestedCRCsInsertCourtAppearanceNotificationIsSavedForEachProbationAreaAndForEachCourtAppearance() {
        when(spgNotificationHelperRepository.getInterestedCRCs(any())).thenReturn(ImmutableList.of(
                ProbationArea
                        .builder()
                        .code("AA")
                        .build(),
                ProbationArea
                        .builder()
                        .code("AB")
                        .build()));

        spgNotificationService.notifyNewCourtCaseCreated(Event
                .builder()
                .offenderId(99L)
                .courtAppearances(ImmutableList.of(
                        CourtAppearance.builder().courtAppearanceId(20L).build(),
                        CourtAppearance.builder().courtAppearanceId(21L).build()
                ))
                .build());

        verify(spgNotificationRepository, atLeastOnce()).save(spgNotificationsCaptor.capture());

        assertThat(findFor(SpgNotificationService.NotificationEvents.INSERT_COURT_APPEARANCE.getNotificationCode())).hasSize(4);

        assertThat(findFor(SpgNotificationService.NotificationEvents.INSERT_COURT_APPEARANCE.getNotificationCode()).stream()
                .anyMatch(notification ->
                        notification.getReceiverIdentity().getCode().equals("AA") && notification.getUniqueId() == 20L)).isTrue();
        assertThat(findFor(SpgNotificationService.NotificationEvents.INSERT_COURT_APPEARANCE.getNotificationCode())
                .stream().anyMatch(notification ->
                        notification.getReceiverIdentity().getCode().equals("AA") && notification.getUniqueId() == 21L)).isTrue();
        assertThat(findFor(SpgNotificationService.NotificationEvents.INSERT_COURT_APPEARANCE.getNotificationCode())
                .stream().anyMatch(notification ->
                        notification.getReceiverIdentity().getCode().equals("AB") && notification.getUniqueId() == 20L)).isTrue();
        assertThat(findFor(SpgNotificationService.NotificationEvents.INSERT_COURT_APPEARANCE.getNotificationCode())
                .stream().anyMatch(notification ->
                        notification.getReceiverIdentity().getCode().equals("AB") && notification.getUniqueId() == 21L)).isTrue();
    }


    private List<SpgNotification> findFor(String businessInteraction) {
        return spgNotificationsCaptor
                .getAllValues()
                .stream()
                .flatMap(notificationSet -> notificationSet.stream().filter(notification -> notification.getBusinessInteraction().getBusinessInteractionCode().equals(businessInteraction)))
                .collect(toList());

    }
}