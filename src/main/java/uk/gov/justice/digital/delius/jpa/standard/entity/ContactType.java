package uk.gov.justice.digital.delius.jpa.standard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "R_CONTACT_TYPE")
public class ContactType {
    @Id
    @Column(name = "CONTACT_TYPE_ID")
    private long contactTypeId;

    @Column(name = "CODE")
    private String code;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SHORT_DESCRIPTION")
    private String shortDescription;

    @Column(name = "ATTENDANCE_CONTACT")
    @Type(type = "yes_no")
    private Boolean attendanceContact;

    @Column(name = "NATIONAL_STANDARDS_CONTACT")
    private String nationalStandardsContact;

    @Column(name = "CONTACT_ALERT_FLAG")
    private String alertFlag;

    @Column(name = "SELECTABLE", nullable = false, length = 1)
    private String selectable;

    /**
     * This is used in Delius to populate the list of available contact types on the schedule future appointments feature.
     * This should be used when checking if a contact type is appropriate for a logical appointment operation e.g. booking recurring, cancelling.
     */
    @Column(name = "FUTURE_SCHEDULED_CONTACTS_FLAG", nullable = false, length = 1)
    private String scheduleFutureAppointments;

    @Column(name = "CONTACT_LOCATION_FLAG", length = 1, nullable = false)
    private String locationFlag;

    @Column(name = "CJA_ORDERS", nullable = false, length = 1)
    private String cjaOrderLevel;

    @Column(name = "LEGACY_ORDERS", nullable = false, length = 1)
    private String  legacyOrderLevel;

    @ManyToMany()
    @JoinTable(
        name = "R_CONTACT_TYPECONTACT_CATEGORY",
        joinColumns = { @JoinColumn(name = "CONTACT_TYPE_ID", nullable = false) },
        inverseJoinColumns = { @JoinColumn(name = "STANDARD_REFERENCE_LIST_ID", nullable = false) }
    )
    private List<StandardReference> contactCategories;
}
