package uk.gov.justice.digital.delius.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.digital.delius.data.api.Nsi;
import uk.gov.justice.digital.delius.jpa.standard.entity.Event;
import uk.gov.justice.digital.delius.jpa.standard.repository.NsiRepository;
import uk.gov.justice.digital.delius.transformers.NsiTransformer;

@ExtendWith(MockitoExtension.class)
public class NsiServiceTest {

    private static final Long OFFENDER_ID = 123L;
    private static final Long EVENT_ID = 124L;
    private static final Event EVENT = Event.builder().softDeleted(0L).build();

    @Mock
    private NsiRepository nsiRepository;

    @Mock
    private NsiTransformer nsiTransformer;

    @InjectMocks
    private NsiService nsiService;

    @DisplayName("All NSIs are fetched and returned, having been transformed")
    @Test
    void whenFetchNsisNoneFilterByCodeOrSoftDeleted() {

        final uk.gov.justice.digital.delius.jpa.standard.entity.Nsi nsiEntity = buildNsi(EVENT, "BRE");
        final Nsi nsi = mock(Nsi.class);
        when(nsiRepository.findByEventIdAndOffenderId(EVENT_ID, OFFENDER_ID)).thenReturn(singletonList(nsiEntity));
        when(nsiTransformer.nsiOf(nsiEntity)).thenReturn(nsi);

        final List<Nsi> nsis = nsiService.getNsiByCodes(OFFENDER_ID, EVENT_ID, Set.of("BRE", "APCUS"));

        assertThat(nsis).hasSize(1);
        assertThat(nsis).contains(nsi);

        verify(nsiTransformer).nsiOf(nsiEntity);
        verify(nsiRepository).findByEventIdAndOffenderId(EVENT_ID, OFFENDER_ID);
        verifyNoMoreInteractions(nsiRepository, nsiTransformer);
    }

    @DisplayName("All NSIs filtered out because the code doesn't match on any of those fetched")
    @Test
    void whenFetchNsisAllFilterByCode() {

        final uk.gov.justice.digital.delius.jpa.standard.entity.Nsi nsiEntity1 = buildNsi(EVENT, "SPG");
        final uk.gov.justice.digital.delius.jpa.standard.entity.Nsi nsiEntity2 = buildNsi(EVENT, "SPX");
        when(nsiRepository.findByEventIdAndOffenderId(EVENT_ID, OFFENDER_ID)).thenReturn(asList(nsiEntity1, nsiEntity2));

        final List<Nsi> nsis = nsiService.getNsiByCodes(OFFENDER_ID, EVENT_ID, Set.of("BRE", "BRZ"));

        assertThat(nsis).hasSize(0);

        verify(nsiRepository).findByEventIdAndOffenderId(EVENT_ID, OFFENDER_ID);
        verifyNoMoreInteractions(nsiRepository, nsiTransformer);
    }

    @DisplayName("All NSIs filtered out because they are soft deleted, despite match on code")
    @Test
    void whenFetchNsisAllFilterBySoftDeleted() {

        final Event deletedEvent = Event.builder().softDeleted(1L).build();
        final uk.gov.justice.digital.delius.jpa.standard.entity.Nsi nsiEntity = buildNsi(deletedEvent, "BRE");
        when(nsiRepository.findByEventIdAndOffenderId(EVENT_ID, OFFENDER_ID)).thenReturn(singletonList(nsiEntity));

        final List<Nsi> nsis = nsiService.getNsiByCodes(OFFENDER_ID, EVENT_ID, Set.of("BRE"));

        assertThat(nsis).hasSize(0);

        verify(nsiRepository).findByEventIdAndOffenderId(EVENT_ID, OFFENDER_ID);
        verifyNoMoreInteractions(nsiRepository, nsiTransformer);
    }

    @DisplayName("Repo returns empty list, so does the service")
    @Test
    void whenFetchNsisRepoReturnsEmptyList() {

        when(nsiRepository.findByEventIdAndOffenderId(EVENT_ID, OFFENDER_ID)).thenReturn(Collections.emptyList());

        final List<Nsi> nsis = nsiService.getNsiByCodes(OFFENDER_ID, EVENT_ID, Set.of("BRE"));

        assertThat(nsis).hasSize(0);

        verify(nsiRepository).findByEventIdAndOffenderId(EVENT_ID, OFFENDER_ID);
        verifyNoMoreInteractions(nsiRepository, nsiTransformer);
    }

    public static uk.gov.justice.digital.delius.jpa.standard.entity.Nsi buildNsi(final Event event, final String typeCode) {
        return uk.gov.justice.digital.delius.jpa.standard.entity.Nsi.builder()
                .nsiType(uk.gov.justice.digital.delius.jpa.standard.entity.NsiType.builder()
                    .code(typeCode)
                    .description("Some description")
                    .build())
                .event(event)
            .build();
    }

}
