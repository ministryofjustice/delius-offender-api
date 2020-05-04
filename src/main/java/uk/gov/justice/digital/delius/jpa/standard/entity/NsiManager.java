package uk.gov.justice.digital.delius.jpa.standard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Getter
@Entity
@Table(name = "NSI_MANAGER")
@NoArgsConstructor
@AllArgsConstructor
public class NsiManager {

    @Id
    @Column(name = "NSI_MANAGER_ID")
    private Long nsiManagerId;

    @ManyToOne
    @JoinColumn(name = "NSI_ID")
    private Nsi nsi;

    @JoinColumn(name = "PROBATION_AREA_ID")
    @OneToOne
    private ProbationArea probationArea;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;
}
