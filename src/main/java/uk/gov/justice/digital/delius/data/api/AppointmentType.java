package uk.gov.justice.digital.delius.data.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentType {
    @NotNull
    @ApiModelProperty(name = "Contact type", example = "CHVS", position = 1)
    private String contactType;

    @NotNull
    @ApiModelProperty(name = "Description", example = "Home Visit to Case (NS)", position = 2)
    private String description;

    @ApiModelProperty(name = "Requires location", example = "REQUIRED", position = 3)
    private RequiredOptional requiresLocation;

    public enum RequiredOptional {
        /**
         * Value must be provided
         */
        REQUIRED,

        /**
         * Value may be provided
         */
        OPTIONAL,

        /**
         * Value must not be provided
         */
        NOT_REQUIRED
    }
}
