package uk.gov.justice.digital.delius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.digital.delius.data.api.InstitutionalReport;
import uk.gov.justice.digital.delius.jpa.standard.repository.InstitutionalReportRepository;
import uk.gov.justice.digital.delius.transformers.InstitutionalReportTransformer;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static uk.gov.justice.digital.delius.transformers.TypesTransformer.convertToBoolean;

@Service
public class InstitutionalReportService {

    private final InstitutionalReportRepository institutionalReportRepository;
    private final InstitutionalReportTransformer institutionalReportTransformer;
    private final OffenceService offenceService;
    private final CourtAppearanceService courtAppearanceService;

    @Autowired
    public InstitutionalReportService(InstitutionalReportRepository institutionalReportRepository,
                                      InstitutionalReportTransformer institutionalAppearanceTransformer,
                                      OffenceService offenceService,
                                      CourtAppearanceService courtAppearanceService) {

        this.institutionalReportRepository = institutionalReportRepository;
        this.institutionalReportTransformer = institutionalAppearanceTransformer;
        this.offenceService = offenceService;
        this.courtAppearanceService = courtAppearanceService;
    }

    public List<InstitutionalReport> institutionalReportsFor(Long offenderId) {

        List<uk.gov.justice.digital.delius.jpa.standard.entity.InstitutionalReport> institutionalReports =
            institutionalReportRepository.findByOffenderId(offenderId);

        return institutionalReports
            .stream()
            .filter(this::notDeleted)
            .map(institutionalReportTransformer::institutionalReportOf)
            .map(this::updateConvictionWithOffences)
            .map(this::updateSentenceWithOutcomeDescription)
            .collect(toList());
    }

    public Optional<InstitutionalReport> institutionalReportFor(Long offenderId, Long institutionalReportId) {

        Optional<uk.gov.justice.digital.delius.jpa.standard.entity.InstitutionalReport> maybeInstitutionalReport =
            institutionalReportRepository.findByOffenderIdAndInstitutionalReportId(offenderId, institutionalReportId);

        return maybeInstitutionalReport
                .filter(this::notDeleted)
            .map(institutionalReportTransformer::institutionalReportOf)
            .map(this::updateConvictionWithOffences)
            .map(this::updateSentenceWithOutcomeDescription);
    }

    private InstitutionalReport updateSentenceWithOutcomeDescription(InstitutionalReport institutionalReport) {
        return Optional.ofNullable(institutionalReport.getSentence())
            .map(ignored -> institutionalReport.toBuilder()
                .sentence(institutionalReport.getSentence().toBuilder()
                    .description(courtAppearanceService.findCourtAppearanceByEventId(institutionalReport.getConviction().getConvictionId())
                        .map(courtAppearance -> courtAppearance.getOutcome().getDescription())
                        .orElse("")
                    ).build())
                .build())
            .orElseGet(() -> institutionalReport);
    }

    private InstitutionalReport updateConvictionWithOffences(InstitutionalReport institutionalReport) {

        return Optional.ofNullable(institutionalReport.getConviction())
            .map(ignored -> institutionalReport.toBuilder()
                .conviction(
                    institutionalReport.getConviction().toBuilder()
                        .offences(offenceService.eventOffencesFor(institutionalReport.getConviction().getConvictionId()))
                        .build())
                .build())
            .orElseGet(() -> institutionalReport);
    }

    private boolean notDeleted(uk.gov.justice.digital.delius.jpa.standard.entity.InstitutionalReport institutionalReport) {
        return !convertToBoolean(institutionalReport.getSoftDeleted());
    }

}
