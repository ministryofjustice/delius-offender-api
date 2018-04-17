package uk.gov.justice.digital.delius.data.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Institution {
    private Long institutionId;
    private Boolean isEstablishment;
    private String code;
    private String description;
    private String institutionName;
    private KeyValue establishmentType;
    private Boolean isPrivate;
}
