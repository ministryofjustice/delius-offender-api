CREATE TABLE R_AD_RQMNT_TYPE_MAIN_CATEGORY
(
  AD_RQMNT_TYPE_MAIN_CATEGORY_ID NUMBER        NOT NULL
    PRIMARY KEY,
  CODE                           VARCHAR2(20)  NOT NULL,
  DESCRIPTION                    VARCHAR2(200) NOT NULL
);

CREATE TABLE R_RQMNT_TYPE_MAIN_CATEGORY
(
  RQMNT_TYPE_MAIN_CATEGORY_ID NUMBER        NOT NULL
    PRIMARY KEY,
  CODE                        VARCHAR2(20)  NOT NULL,
  DESCRIPTION                 VARCHAR2(200) NOT NULL
);

CREATE TABLE EVENT
(
  EVENT_ID  NUMBER NOT NULL
    PRIMARY KEY,
  IN_BREACH NUMBER NOT NULL
);

CREATE TABLE R_NSI_TYPE
(
  NSI_TYPE_ID NUMBER        NOT NULL
    PRIMARY KEY,
  CODE        VARCHAR2(20)  NOT NULL,
  DESCRIPTION VARCHAR2(200) NOT NULL
);

CREATE TABLE RQMNT
(
  RQMNT_ID                       NUMBER NOT NULL
    PRIMARY KEY,
  START_DATE                     DATE   NOT NULL,
  RQMNT_NOTES                    CLOB,
  COMMENCEMENT_DATE              DATE,
  TERMINATION_DATE               DATE,
  EXPECTED_START_DATE            DATE,
  EXPECTED_END_DATE              DATE,
  RQMNT_TYPE_SUB_CATEGORY_ID     NUMBER
    REFERENCES R_STANDARD_REFERENCE_LIST,
  AD_RQMNT_TYPE_SUB_CATEGORY_ID  NUMBER
    REFERENCES R_STANDARD_REFERENCE_LIST,
  RQMNT_TYPE_MAIN_CATEGORY_ID    NUMBER
    REFERENCES R_RQMNT_TYPE_MAIN_CATEGORY,
  AD_RQMNT_TYPE_MAIN_CATEGORY_ID NUMBER
    REFERENCES R_AD_RQMNT_TYPE_MAIN_CATEGORY,
  OFFENDER_ID                    NUMBER NOT NULL
    REFERENCES OFFENDER
);


CREATE TABLE NSI
(
  NSI_ID          NUMBER NOT NULL
    PRIMARY KEY,
  OFFENDER_ID     NUMBER NOT NULL
    REFERENCES OFFENDER,
  EVENT_ID        NUMBER
    REFERENCES EVENT,
  NSI_TYPE_ID     NUMBER NOT NULL
    REFERENCES R_NSI_TYPE,
  NSI_SUB_TYPE_ID NUMBER
    REFERENCES R_STANDARD_REFERENCE_LIST,
  NOTES           CLOB,
  RQMNT_ID        NUMBER
    REFERENCES RQMNT
);

CREATE TABLE R_LIC_COND_TYPE_MAIN_CAT
(
  LIC_COND_TYPE_MAIN_CAT_ID NUMBER        NOT NULL
    PRIMARY KEY,
  CODE                      VARCHAR2(100) NOT NULL,
  DESCRIPTION               VARCHAR2(200)
);

CREATE TABLE R_CONTACT_OUTCOME_TYPE
(
  CONTACT_OUTCOME_TYPE_ID NUMBER       NOT NULL
    PRIMARY KEY,
  CODE                    VARCHAR2(10) NOT NULL,
  DESCRIPTION             VARCHAR2(50) NOT NULL
);

CREATE TABLE R_CONTACT_TYPE
(
  CONTACT_TYPE_ID   NUMBER         NOT NULL
    PRIMARY KEY,
  CODE              VARCHAR2(10)   NOT NULL,
  DESCRIPTION       VARCHAR2(4000) NOT NULL,
  SHORT_DESCRIPTION VARCHAR2(500)
);


CREATE TABLE LIC_CONDITION
(
  LIC_CONDITION_ID          NUMBER NOT NULL
    PRIMARY KEY,
  START_DATE                DATE   NOT NULL,
  LIC_CONDITION_NOTES       CLOB,
  COMMENCEMENT_DATE         DATE,
  COMMENCEMENT_NOTES        CLOB,
  TERMINATION_DATE          DATE,
  TERMINATION_NOTES         CLOB,
  EXPECTED_START_DATE       DATE,
  EXPECTED_END_DATE         DATE,
  LIC_COND_TYPE_SUB_CAT_ID  NUMBER
    REFERENCES R_STANDARD_REFERENCE_LIST,
  CREATED_DATETIME          DATE   NOT NULL,
  LIC_COND_TYPE_MAIN_CAT_ID NUMBER
    REFERENCES R_LIC_COND_TYPE_MAIN_CAT,
  OFFENDER_ID               NUMBER NOT NULL
    REFERENCES OFFENDER,
  ACTIVE_FLAG               NUMBER NOT NULL
);

CREATE TABLE R_EXPLANATION
(
  EXPLANATION_ID NUMBER        NOT NULL
    PRIMARY KEY,
  CODE           VARCHAR2(10)  NOT NULL,
  DESCRIPTION    VARCHAR2(100) NOT NULL
);

CREATE TABLE PROBATION_AREA
(
  PROBATION_AREA_ID     NUMBER           NOT NULL
    PRIMARY KEY,
  CODE                  CHAR(3)          NOT NULL,
  DESCRIPTION           VARCHAR2(60)     NOT NULL,
  SELECTABLE            CHAR             NOT NULL,
  ROW_VERSION           NUMBER DEFAULT 0 NOT NULL,
  FORM_20_CODE          VARCHAR2(20),
  MIGRATED_DATE         DATE,
  HO_AREA_CODE          VARCHAR2(20),
  CREATED_BY_USER_ID    NUMBER           NOT NULL,
  CREATED_DATETIME      DATE             NOT NULL,
  LAST_UPDATED_USER_ID  NUMBER           NOT NULL,
  LAST_UPDATED_DATETIME DATE             NOT NULL,
  TRAINING_SESSION_ID   NUMBER,
  TRUST_CODE            CHAR(3),
  TRUST_DIVISION_ID     NUMBER,
  TRUST_CPA_ID          NUMBER,
  PRIVATE               NUMBER           NOT NULL,
  DIVISION_ID           NUMBER,
  ORGANISATION_ID       NUMBER           NOT NULL,
  CONTACT_NAME          VARCHAR2(200),
  ADDRESS_ID            NUMBER           NOT NULL,
  START_DATE            DATE             NOT NULL,
  END_DATE              DATE,
  SPG_ACTIVE_ID         NUMBER           NOT NULL,
  INSTITUTION_ID        NUMBER,
  ESTABLISHMENT         CHAR
);


CREATE TABLE PROVIDER_LOCATION
(
  PROVIDER_LOCATION_ID  NUMBER           NOT NULL
    PRIMARY KEY,
  CODE                  CHAR(7)          NOT NULL,
  DESCRIPTION           VARCHAR2(50)     NOT NULL,
  FAX_NUMBER            VARCHAR2(35),
  EXTERNAL_PROVIDER_ID  NUMBER           NOT NULL,
  ROW_VERSION           NUMBER DEFAULT 0 NOT NULL,
  ADDRESS_ID            NUMBER,
  START_DATE            DATE             NOT NULL,
  END_DATE              DATE,
  NOTES                 CLOB,
  CONTACT_NAME          VARCHAR2(107),
  CREATED_DATETIME      DATE             NOT NULL,
  CREATED_BY_USER_ID    NUMBER           NOT NULL,
  LAST_UPDATED_DATETIME DATE             NOT NULL,
  LAST_UPDATED_USER_ID  NUMBER           NOT NULL,
  TRAINING_SESSION_ID   NUMBER,
  PROBATION_AREA_ID     NUMBER
    REFERENCES PROBATION_AREA
);

CREATE TABLE PROVIDER_EMPLOYEE
(
  PROVIDER_EMPLOYEE_ID  NUMBER           NOT NULL
    PRIMARY KEY,
  CODE                  CHAR(4)          NOT NULL,
  SURNAME               VARCHAR2(35)     NOT NULL,
  START_DATE            DATE             NOT NULL,
  FORENAME              VARCHAR2(35)     NOT NULL,
  END_DATE              DATE,
  FORENAME2             VARCHAR2(35),
  ROW_VERSION           NUMBER DEFAULT 0 NOT NULL,
  EXTERNAL_PROVIDER_ID  NUMBER           NOT NULL,
  CREATED_BY_USER_ID    NUMBER           NOT NULL,
  CREATED_DATETIME      DATE             NOT NULL,
  LAST_UPDATED_USER_ID  NUMBER           NOT NULL,
  LAST_UPDATED_DATETIME DATE             NOT NULL,
  TRAINING_SESSION_ID   NUMBER,
  PROBATION_AREA_ID     NUMBER
    REFERENCES PROBATION_AREA
);

CREATE TABLE STAFF
(
  STAFF_ID              NUMBER           NOT NULL
    PRIMARY KEY,
  START_DATE            DATE             NOT NULL,
  SURNAME               VARCHAR2(35)     NOT NULL,
  END_DATE              DATE,
  FORENAME              VARCHAR2(35)     NOT NULL,
  ROW_VERSION           NUMBER DEFAULT 0 NOT NULL,
  FORENAME2             VARCHAR2(35),
  STAFF_GRADE_ID        NUMBER,
  TITLE_ID              NUMBER
    REFERENCES R_STANDARD_REFERENCE_LIST,
  OFFICER_CODE          CHAR(7),
  CREATED_BY_USER_ID    NUMBER           NOT NULL,
  LAST_UPDATED_USER_ID  NUMBER           NOT NULL,
  CREATED_DATETIME      DATE             NOT NULL,
  LAST_UPDATED_DATETIME DATE             NOT NULL,
  TRAINING_SESSION_ID   NUMBER,
  PRIVATE               NUMBER           NOT NULL,
  SC_PROVIDER_ID        NUMBER,
  PROBATION_AREA_ID     NUMBER           NOT NULL
    REFERENCES PROBATION_AREA
);

CREATE TABLE TEAM
(
  TEAM_ID                NUMBER           NOT NULL
    PRIMARY KEY,
  CODE                   CHAR(6)          NOT NULL,
  DESCRIPTION            VARCHAR2(50)     NOT NULL,
  DISTRICT_ID            NUMBER           NOT NULL,
  LOCAL_DELIVERY_UNIT_ID NUMBER           NOT NULL,
  TELEPHONE              VARCHAR2(35),
  UNPAID_WORK_TEAM       CHAR             NOT NULL,
  ROW_VERSION            NUMBER DEFAULT 0 NOT NULL,
  FAX_NUMBER             VARCHAR2(35),
  CONTACT_NAME           VARCHAR2(200),
  START_DATE             DATE             NOT NULL,
  END_DATE               DATE,
  CREATED_DATETIME       DATE             NOT NULL,
  CREATED_BY_USER_ID     NUMBER           NOT NULL,
  LAST_UPDATED_DATETIME  DATE             NOT NULL,
  LAST_UPDATED_USER_ID   NUMBER           NOT NULL,
  TRAINING_SESSION_ID    NUMBER,
  PROBATION_AREA_ID      NUMBER           NOT NULL
    REFERENCES PROBATION_AREA,
  PRIVATE                NUMBER           NOT NULL,
  SC_PROVIDER_ID         NUMBER
);

CREATE TABLE PROVIDER_TEAM
(
  PROVIDER_TEAM_ID      NUMBER           NOT NULL
    PRIMARY KEY,
  CODE                  CHAR(3)          NOT NULL,
  NAME                  VARCHAR2(50)     NOT NULL,
  EXTERNAL_PROVIDER_ID  NUMBER           NOT NULL,
  START_DATE            DATE,
  END_DATE              DATE,
  UNPAID_WORK           CHAR,
  ROW_VERSION           NUMBER DEFAULT 0 NOT NULL,
  CREATED_DATETIME      DATE             NOT NULL,
  CREATED_BY_USER_ID    NUMBER           NOT NULL,
  LAST_UPDATED_DATETIME DATE             NOT NULL,
  LAST_UPDATED_USER_ID  NUMBER           NOT NULL,
  TRAINING_SESSION_ID   NUMBER,
  PROBATION_AREA_ID     NUMBER           NOT NULL
    REFERENCES PROBATION_AREA
);

CREATE TABLE PARTITION_AREA
(
  PARTITION_AREA_ID   NUMBER           NOT NULL
    PRIMARY KEY,
  AREA                VARCHAR2(30)     NOT NULL,
  ROW_VERSION         NUMBER DEFAULT 0 NOT NULL,
  TRAINING_SESSION_ID NUMBER
);


CREATE TABLE CONTACT
(
  CONTACT_ID                   NUMBER           NOT NULL
    PRIMARY KEY,
  LINKED_CONTACT_ID            NUMBER
    REFERENCES CONTACT,
  CONTACT_DATE                 DATE             NOT NULL,
  OFFENDER_ID                  NUMBER           NOT NULL
    REFERENCES OFFENDER,
  CONTACT_START_TIME           DATE,
  CONTACT_END_TIME             DATE,
  RQMNT_ID                     NUMBER
    REFERENCES RQMNT,
  LIC_CONDITION_ID             NUMBER
    REFERENCES LIC_CONDITION,
  PROVIDER_LOCATION_ID         NUMBER
    REFERENCES PROVIDER_LOCATION,
  PROVIDER_EMPLOYEE_ID         NUMBER
    REFERENCES PROVIDER_EMPLOYEE,
  HOURS_CREDITED               NUMBER(10, 2),
  NOTES                        CLOB,
  VISOR_CONTACT                CHAR,
  STAFF_ID                     NUMBER
    REFERENCES STAFF,
  TEAM_ID                      NUMBER
    REFERENCES TEAM,
  SOFT_DELETED                 NUMBER           NOT NULL,
  VISOR_EXPORTED               CHAR,
  PARTITION_AREA_ID            NUMBER           NOT NULL
    REFERENCES PARTITION_AREA,
  OFFICE_LOCATION_ID           NUMBER,
  ROW_VERSION                  NUMBER DEFAULT 0 NOT NULL,
  ALERT_ACTIVE                 CHAR,
  ATTENDED                     CHAR,
  CREATED_DATETIME             DATE             NOT NULL,
  COMPLIED                     CHAR,
  SENSITIVE                    CHAR,
  LAST_UPDATED_DATETIME        DATE             NOT NULL,
  EVENT_ID                     NUMBER
    REFERENCES EVENT,
  CONTACT_TYPE_ID              NUMBER           NOT NULL
    REFERENCES R_CONTACT_TYPE,
  PROVIDER_TEAM_ID             NUMBER
    REFERENCES PROVIDER_TEAM,
  CONTACT_OUTCOME_TYPE_ID      NUMBER
    REFERENCES R_CONTACT_OUTCOME_TYPE,
  CREATED_BY_USER_ID           NUMBER           NOT NULL,
  EXPLANATION_ID               NUMBER
    REFERENCES R_EXPLANATION,
  LAST_UPDATED_USER_ID         NUMBER           NOT NULL,
  TRAINING_SESSION_ID          NUMBER,
  TRUST_PROVIDER_FLAG          NUMBER           NOT NULL,
  STAFF_EMPLOYEE_ID            NUMBER           NOT NULL,
  PROBATION_AREA_ID            NUMBER           NOT NULL
    REFERENCES PROBATION_AREA,
  TRUST_PROVIDER_TEAM_ID       NUMBER           NOT NULL,
  ENFORCEMENT                  NUMBER,
  DOCUMENT_LINKED              CHAR,
  UPLOAD_LINKED                CHAR,
  LATEST_ENFORCEMENT_ACTION_ID NUMBER,
  NSI_ID                       NUMBER
    CONSTRAINT R_895
    REFERENCES NSI,
  TABLE_NAME                   VARCHAR2(30)
    CONSTRAINT CONTACT_TABLE_69233217
    CHECK (TABLE_NAME IN
           ('APPROVED_PREMISES_REFERRAL', 'APPROVED_PREMISES_RESIDENCE', 'COURT_REPORT', 'INSTITUTIONAL_REPORT')),
  PRIMARY_KEY_ID               NUMBER(22),
  PSS_RQMNT_ID                 NUMBER(22),
  RAR_ACTIVITY                 CHAR,
  NOMIS_CASE_NOTE_ID           NUMBER
);