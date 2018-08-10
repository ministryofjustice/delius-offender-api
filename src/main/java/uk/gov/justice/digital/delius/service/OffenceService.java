package uk.gov.justice.digital.delius.service;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.digital.delius.data.api.Offence;
import uk.gov.justice.digital.delius.jpa.standard.repository.AdditionalOffenceRepository;
import uk.gov.justice.digital.delius.jpa.standard.repository.MainOffenceRepository;
import uk.gov.justice.digital.delius.transformers.AdditionalOffenceTransformer;
import uk.gov.justice.digital.delius.transformers.MainOffenceTransformer;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OffenceService {

    private final MainOffenceRepository mainOffenceRepository;
    private final AdditionalOffenceRepository additionalOffenceRepository;
    private final MainOffenceTransformer mainOffenceTransformer;
    private final AdditionalOffenceTransformer additionalOffenceTransformer;

    @Autowired
    public OffenceService(MainOffenceRepository mainOffenceRepository, AdditionalOffenceRepository additionalOffenceRepository,
                          MainOffenceTransformer mainOffenceTransformer, AdditionalOffenceTransformer additionalOffenceTransformer) {
        this.mainOffenceRepository = mainOffenceRepository;
        this.additionalOffenceRepository = additionalOffenceRepository;
        this.mainOffenceTransformer = mainOffenceTransformer;
        this.additionalOffenceTransformer = additionalOffenceTransformer;
    }

    public List<Offence> offencesFor(Long offenderId) {
        List<Offence> mainOffences = mainOffenceTransformer.offencesOf(mainOffenceRepository.findByOffenderId(offenderId));
        return mainOffences.stream()
            .map(mainOffence -> {
                List<Offence> additionalOffences = additionalOffenceTransformer.offencesOf(additionalOffenceRepository.findByEventId(offenderId));
                return ImmutableList.<Offence>builder()
                    .add(mainOffence)
                    .addAll(additionalOffences)
                    .build();
            })
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
}
