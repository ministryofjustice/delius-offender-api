
package uk.gov.justice.digital.delius.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.digital.delius.controller.NotFoundException;
import uk.gov.justice.digital.delius.jpa.standard.entity.Custody;
import uk.gov.justice.digital.delius.jpa.standard.entity.Event;
import uk.gov.justice.digital.delius.jpa.standard.entity.Offender;
import uk.gov.justice.digital.delius.jpa.standard.repository.OffenderRepository;
import uk.gov.justice.digital.delius.transformers.*;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class OffenderServiceTest_getOffenderLatestRecall {

    private static final Long ANY_OFFENDER_ID = 123L;
    private static final Long SOME_OFFENDER_ID = 456L;
    private static final Optional<Offender> SOME_OFFENDER = Optional.of(Offender.builder().offenderId(SOME_OFFENDER_ID).build());
    private static final Optional<Offender> ANY_OFFENDER = Optional.of(Offender.builder().offenderId(ANY_OFFENDER_ID).build());
    private static final Event SOME_CUSTODIAL_EVENT = Event.builder().eventId(333L).build();
    private static final Event ANY_CUSTODIAL_EVENT = Event.builder().eventId(444L).build();

    @Mock
    private OffenderRepository mockOffenderRepository;
    @Mock
    private ConvictionService mockConvictionService;
    @Mock
    private CustodyService mockCustodyService;

    private OffenderService offenderService;

    @Before
    public void setup() {
        offenderService = new OffenderService(
                mockOffenderRepository,
                new OffenderTransformer(
                        new ContactTransformer()),
                new OffenderManagerTransformer(
                        new StaffTransformer(
                                new TeamTransformer()),
                        new TeamTransformer(),
                        new ProbationAreaTransformer(
                                new InstitutionTransformer())),
                mockConvictionService,
                mockCustodyService
        );
    }

    @Test
    public void getOffenderLatestRecall_withOffenderId_searchesForOffenderInRepository() {
        given(mockOffenderRepository.findByOffenderId(SOME_OFFENDER_ID))
                .willReturn(SOME_OFFENDER);

        offenderService.getOffenderLatestRecall(SOME_OFFENDER_ID);

        then(mockOffenderRepository).should().findByOffenderId(SOME_OFFENDER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getOffenderLatestRecall_offenderNotFound_throwsNotFound() {
        given(mockOffenderRepository.findByOffenderId(ANY_OFFENDER_ID))
                .willReturn(Optional.empty());

        offenderService.getOffenderLatestRecall(ANY_OFFENDER_ID);
    }

    @Test
    public void getOffenderLatestRecall_offenderFound_retrievesActiveCustodialEvent() {
        given(mockOffenderRepository.findByOffenderId(SOME_OFFENDER_ID)).willReturn(SOME_OFFENDER);
        given(mockConvictionService.getActiveCustodialEvent(SOME_OFFENDER_ID)).willReturn(mock(Event.class));

        offenderService.getOffenderLatestRecall(SOME_OFFENDER_ID);

        then(mockConvictionService).should().getActiveCustodialEvent(SOME_OFFENDER_ID);
    }

    @Test(expected = ConvictionService.SingleActiveCustodyConvictionNotFoundException.class)
    public void getOffenderLatestRecall_withNoCustodyRecord_propagatesNoCustodyException() {
        given(mockOffenderRepository.findByOffenderId(ANY_OFFENDER_ID)).willReturn(ANY_OFFENDER);
        given(mockConvictionService.getActiveCustodialEvent(ANY_OFFENDER_ID)).willThrow(ConvictionService.SingleActiveCustodyConvictionNotFoundException.class);

        offenderService.getOffenderLatestRecall(ANY_OFFENDER_ID);
    }

    @Test
    public void getOffenderLatestRecall_custodialEventFound_retrievesCustodyRecord() {
        given(mockOffenderRepository.findByOffenderId(ANY_OFFENDER_ID)).willReturn(ANY_OFFENDER);
        given(mockConvictionService.getActiveCustodialEvent(ANY_OFFENDER_ID)).willReturn(SOME_CUSTODIAL_EVENT);
        given(mockCustodyService.findCustodyFromCustodialEvent(SOME_CUSTODIAL_EVENT)).willReturn(mock( Custody.class));

        offenderService.getOffenderLatestRecall(ANY_OFFENDER_ID);

        then(mockCustodyService).should().findCustodyFromCustodialEvent(SOME_CUSTODIAL_EVENT);
    }

    @Test(expected = CustodyService.CustodyNotFoundException.class)
    public void getOffenderLatestRecall_noCustodyRecord_propagatesNotFoundException() {
        given(mockOffenderRepository.findByOffenderId(ANY_OFFENDER_ID)).willReturn(ANY_OFFENDER);
        given(mockConvictionService.getActiveCustodialEvent(ANY_OFFENDER_ID)).willReturn(ANY_CUSTODIAL_EVENT);
        given(mockCustodyService.findCustodyFromCustodialEvent(ANY_CUSTODIAL_EVENT)).willThrow(CustodyService.CustodyNotFoundException.class);

        offenderService.getOffenderLatestRecall(ANY_OFFENDER_ID);
    }

}
