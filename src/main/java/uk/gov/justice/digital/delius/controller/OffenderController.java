package uk.gov.justice.digital.delius.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.digital.delius.data.api.OffenderDetail;
import uk.gov.justice.digital.delius.exception.JwtTokenMissingException;
import uk.gov.justice.digital.delius.jwt.Jwt;
import uk.gov.justice.digital.delius.jwt.JwtValidation;
import uk.gov.justice.digital.delius.service.OffenderService;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${base.url:/delius}")
@Log
public class OffenderController {

    private final OffenderService offenderService;

    private final Jwt jwt;

    @Autowired
    public OffenderController(OffenderService offenderService, Jwt jwt) {
        this.offenderService = offenderService;
        this.jwt = jwt;
    }

    @RequestMapping(value = "/offenders/{offenderId}", method = RequestMethod.GET)
    @JwtValidation
    public ResponseEntity<OffenderDetail> getOffender(final @RequestHeader HttpHeaders httpHeaders,
                                                      final @PathVariable("offenderId") Long offenderId) {
        return offenderService.getOffender(offenderId, oracleUserFrom(httpHeaders)).map(
                offenderDetail -> new ResponseEntity<>(offenderDetail, OK)
        ).orElse(notFound());
    }

    private String oracleUserFrom(HttpHeaders httpHeaders) {
        Claims jwtClaims = jwt.parseAuthorizationHeader(httpHeaders.get("Authorization").get(0)).get();
        return (String) jwtClaims.get("oracleUser");
    }

    private ResponseEntity<OffenderDetail> notFound() {
        return new ResponseEntity<>(NOT_FOUND);
    }

    @ExceptionHandler(JwtTokenMissingException.class)
    public ResponseEntity<String> missingJwt(JwtTokenMissingException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<String> badJwt(MalformedJwtException e) {
        return new ResponseEntity<>("Bad Token.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> expiredJwt(ExpiredJwtException e) {
        return new ResponseEntity<>("Expired Token.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> notMine(SignatureException e) {
        return new ResponseEntity<>("Invalid signature.", HttpStatus.UNAUTHORIZED);
    }

}
