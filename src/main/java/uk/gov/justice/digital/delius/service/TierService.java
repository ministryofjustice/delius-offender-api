package uk.gov.justice.digital.delius.service;

import com.microsoft.applicationinsights.TelemetryClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.digital.delius.controller.NotFoundException;
import uk.gov.justice.digital.delius.jpa.standard.entity.ManagementTier;
import uk.gov.justice.digital.delius.jpa.standard.entity.ManagementTierId;
import uk.gov.justice.digital.delius.jpa.standard.entity.Offender;
import uk.gov.justice.digital.delius.jpa.standard.entity.StandardReference;
import uk.gov.justice.digital.delius.jpa.standard.repository.ManagementTierRepository;
import uk.gov.justice.digital.delius.jpa.standard.repository.OffenderRepository;
import uk.gov.justice.digital.delius.jpa.standard.repository.StandardReferenceRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TierService {

    private final ManagementTierRepository managementTierRepository;;
    private final TelemetryClient telemetryClient;
    private final OffenderRepository offenderRepository;
    private final ReferenceDataService referenceDataService;


    @Transactional
    public void updateTier(String crn, String tier) {
        final var telemetryProperties = Map.of("crn", crn, "tier", tier);
        final var offenderId = getOffender(crn, telemetryProperties);

        writeTierUpdate(tier, telemetryProperties, offenderId);

        telemetryClient.trackEvent("TierUpdateSuccess", telemetryProperties, null);
    }

    private Long getOffender(String crn, Map<String, String> telemetryProperties) {
        return offenderRepository.findByCrn(crn).map(Offender::getOffenderId).orElseThrow(() -> logAndThrow(telemetryProperties, "TierUpdateFailureOffenderNotFound", String.format("Offender with CRN %s not found", crn)));
    }

    private void writeTierUpdate(String tier, Map<String, String> telemetryProperties, Long offenderId) {
        final var updatedTier = referenceDataService.getTier(tierWithUPrefix(tier)).orElseThrow(() -> logAndThrow(telemetryProperties, "TierUpdateFailureTierNotFound", String.format("Tier %s not found", tier)));

        final var changeReason = referenceDataService.getAtsTierChangeReason().orElseThrow(() -> logAndThrow(telemetryProperties, "TierUpdateFailureTierChangeReasonNotFound", "Tier change reason ATS not found"));

        ManagementTier newTier = ManagementTier
            .builder()
            .id(ManagementTierId
                .builder()
                .offenderId(offenderId)
                .tier(updatedTier)
                .dateChanged(LocalDateTime.now())
                .build())
            .tierChangeReason(changeReason)
            .build();

        managementTierRepository.save(newTier);
    }

    private String tierWithUPrefix(String tier) {
        return String.format("U%s", tier);
    }

    private NotFoundException logAndThrow(Map<String, String> telemetryProperties, String event, String exceptionReason) {
        telemetryClient.trackEvent(event, telemetryProperties, null);
        return new NotFoundException(exceptionReason);
    }
}
