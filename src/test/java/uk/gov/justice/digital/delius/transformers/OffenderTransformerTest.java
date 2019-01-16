package uk.gov.justice.digital.delius.transformers;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import uk.gov.justice.digital.delius.jpa.standard.entity.OffenderManager;
import uk.gov.justice.digital.delius.jpa.standard.entity.ProbationArea;
import uk.gov.justice.digital.delius.jpa.standard.entity.StandardReference;

import static org.assertj.core.api.Assertions.assertThat;

public class OffenderTransformerTest {
    private OffenderTransformer offenderTransformer = new OffenderTransformer(new ContactTransformer());

    @Test
    public void offenderManagerAllocationReasonMappedFromAllocationReasonInOffenderTransfer() {
        assertThat(offenderTransformer.offenderManagersOf(ImmutableList.of(aOffenderManager())).get(0).getAllocationReason())
                .isNotNull()
                .hasFieldOrPropertyWithValue("code", "1984")
                .hasFieldOrPropertyWithValue("description", "Reallocation - Inactive Offender");

    }

    @Test
    public void offenderManagerAllocationReasonNullWhenOffenderTransferAbsent() {
        assertThat(offenderTransformer.offenderManagersOf(
                ImmutableList.of(
                        aOffenderManager()
                                .toBuilder()
                                .allocationReason(null)
                                .build()))
                .get(0).getAllocationReason())
                .isNull();

    }

    private OffenderManager aOffenderManager() {
        return OffenderManager
                .builder()
                .allocationReason(
                        StandardReference
                                .builder()
                                .codeDescription("Reallocation - Inactive Offender")
                                .codeValue("1984")
                                .build()
                )
                .probationArea(ProbationArea.builder().build())
                .build();
    }

}