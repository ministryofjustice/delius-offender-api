package uk.gov.justice.digital.delius.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.digital.delius.controller.NotFoundException;
import uk.gov.justice.digital.delius.data.api.ConvictionRequirements;
import uk.gov.justice.digital.delius.data.api.Requirement;
import uk.gov.justice.digital.delius.jpa.standard.entity.Disposal;
import uk.gov.justice.digital.delius.jpa.standard.entity.Event;
import uk.gov.justice.digital.delius.jpa.standard.entity.Offender;
import uk.gov.justice.digital.delius.jpa.standard.repository.EventRepository;
import uk.gov.justice.digital.delius.jpa.standard.repository.OffenderRepository;
import uk.gov.justice.digital.delius.transformers.RequirementTransformer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequirementServiceTest {
    private static final String CONVICTION_ID = "CONVICTION_ID";
    private static final String CRN = "CRN";
    public static final Long OFFENDER_ID = 12345678L;

    @Mock
    private RequirementTransformer transformer;
    @Mock
    private OffenderRepository offenderRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private Offender offender;
    @Mock
    private Event event;
    @Mock
    private Disposal disposal;
    @Mock
    private Requirement requirement;
    @Mock
    private uk.gov.justice.digital.delius.jpa.standard.entity.Requirement requirementEntity;

    private List<Requirement> expectedRequirements;

    private RequirementService requirementService;

    @Before
    public void setUp() {
        requirementService = new RequirementService(offenderRepository, eventRepository, transformer);
        expectedRequirements = Collections.singletonList(requirement);

        when(offenderRepository.findByCrn(CRN)).thenReturn(Optional.of(offender));
        when(offender.getOffenderId()).thenReturn(OFFENDER_ID);
        when(eventRepository.findByOffenderId(OFFENDER_ID)).thenReturn(Collections.singletonList(event));
        when(event.getDisposal()).thenReturn(disposal);
        when(disposal.getRequirements()).thenReturn(Collections.singletonList(requirementEntity));
        when(transformer.requirementOf(requirementEntity)).thenReturn(requirement);
    }

    @Test
    public void whenGetRequirementsByConvictionId_thenReturnRequirements() {
        ConvictionRequirements requirements = requirementService.getRequirementsByConvictionId(CRN, CONVICTION_ID);
        assertThat(requirements.getRequirements()).isEqualTo(expectedRequirements);
    }

    @Test
    public void givenOffenderDoesNotExist_whenGetRequirementsByConvictionId_thenThrowException() {
        when(offenderRepository.findByCrn(CRN)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> requirementService.getRequirementsByConvictionId(CRN, CONVICTION_ID))
            .withMessage("Offender with CRN 'CRN' not found");

    }
}