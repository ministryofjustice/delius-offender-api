package uk.gov.justice.digital.delius.controller.secure;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.digital.delius.controller.NotFoundException;
import uk.gov.justice.digital.delius.controller.advice.ErrorResponse;
import uk.gov.justice.digital.delius.data.api.DocumentLink;
import uk.gov.justice.digital.delius.data.api.ManagedOffender;
import uk.gov.justice.digital.delius.data.api.ResponsibleOfficer;
import uk.gov.justice.digital.delius.data.api.StaffDetails;
import uk.gov.justice.digital.delius.jpa.dao.OffenderDelta;
import uk.gov.justice.digital.delius.service.StaffService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Api(tags = "Staff (Secure)", authorizations = {@Authorization("ROLE_COMMUNITY")})
@RequestMapping(value = "secure", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@RestController
@PreAuthorize("hasRole('ROLE_COMMUNITY')")
public class StaffResource {

    private final StaffService staffService;

    @ApiOperation(value = "Return list of of currently managed offenders for one responsible officer (RO)", notes = "Accepts a Delius staff officer code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = ManagedOffender.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid request", response = ErrorResponse.class),
            @ApiResponse(code = 401, message = "Unauthorised", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Unrecoverable error whilst processing request.", response = ErrorResponse.class)})
    @GetMapping(path = "/staff/staffCode/{staffCode}/managedOffenders")
    public List<ManagedOffender> getOffendersForResponsibleOfficer(
            @ApiParam(name = "staffCode", value = "Delius officer code of the responsible officer", example = "SH0001", required = true) @NotNull @PathVariable(value = "staffCode") final String staffCode,
            @ApiParam(name = "current", value = "Current only", example = "false") @RequestParam(name = "current", required = false, defaultValue = "false") final boolean current) {
        return staffService.getManagedOffendersByStaffCode(staffCode, current)
                .orElseThrow(() -> new NotFoundException(String.format("Staff member with code %s", staffCode)));
    }

    @ApiOperation(value = "Return details of a staff member including option user details", notes = "Accepts a Delius staff officer code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = StaffDetails.class),
            @ApiResponse(code = 400, message = "Invalid request", response = ErrorResponse.class),
            @ApiResponse(code = 401, message = "Unauthorised", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Unrecoverable error whilst processing request.", response = ErrorResponse.class)})
    @GetMapping(path = "/staff/staffCode/{staffCode}")
    public StaffDetails getStaffDetails(
            @ApiParam(name = "staffCode", value = "Delius officer code", example = "SH0001", required = true)
            @NotNull
            @PathVariable(value = "staffCode") final String staffCode) {
        log.info("getStaffDetails called with {}", staffCode);
        return staffService.getStaffDetails(staffCode)
                .orElseThrow(() -> new NotFoundException(String.format("Staff member with code %s", staffCode)));
    }

    @ApiOperation(value = "Return details of a staff member including user details", notes = "Accepts a Delius staff username")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = StaffDetails.class),
            @ApiResponse(code = 400, message = "Invalid request", response = ErrorResponse.class),
            @ApiResponse(code = 401, message = "Unauthorised", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Unrecoverable error whilst processing request.", response = ErrorResponse.class)})
    @GetMapping(path = "/staff/username/{username}")
    public StaffDetails getStaffDetailsForUsername(
            @ApiParam(name = "username", value = "Delius username", example = "SheliaHancockNPS", required = true)
            @NotNull
            @PathVariable(value = "username") final String username) {
        log.info("getStaffDetailsByUsername called with {}", username);
        return staffService.getStaffDetailsByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("Staff member with username %s", username)));
    }

    @ApiOperation(value = "Returns a list of user details for supplied usernames - POST version to allow large user lists.", notes = "user details for supplied usernames", nickname = "getUserDetailsList")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = StaffDetails.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid request", response = ErrorResponse.class),
            @ApiResponse(code = 401, message = "Unauthorised", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Unrecoverable error whilst processing request.", response = ErrorResponse.class)})
    @PostMapping(path="/staff/list", consumes = "application/json")
    public ResponseEntity<List<StaffDetails>> getUserDetailsList(final @RequestBody Set<String> usernames){
        return new ResponseEntity<>(staffService.getStaffDetailsByUsernames(usernames), OK);
    }
}
