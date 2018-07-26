INSERT INTO USER_ (USER_ID, DISTINGUISHED_NAME, SURNAME, FORENAME, PRIVATE, ORGANISATION_ID)
VALUES (1, 'NationalUser', 'User', 'National', 0, 1);

INSERT INTO USER_ (USER_ID, DISTINGUISHED_NAME, SURNAME, FORENAME, PRIVATE, ORGANISATION_ID)
VALUES (2, 'OlIvEr.cOnNoLlY', 'Connolly', 'Oliver', 0, 1);

INSERT INTO OFFENDER (FIRST_NAME, OFFENDER_ID, CRN, SECOND_NAME, SURNAME, DATE_OF_BIRTH_DATE, PARTITION_AREA_ID, SOFT_DELETED, ROW_VERSION, GENDER_ID, SURNAME_SOUNDEX, CREATED_DATETIME, LAST_UPDATED_DATETIME, CURRENT_EXCLUSION, CREATED_BY_USER_ID, LAST_UPDATED_USER_ID, FIRST_NAME_SOUNDEX, LAST_UPDATED_USER_ID_DIVERSITY, CURRENT_DISPOSAL, CURRENT_RESTRICTION, PENDING_TRANSFER)
VALUES ('John', 1, 'CRN1', 'James', 'Smith', '1988-06-06', 1, 0, 1, 1, 'SURNAME_SOUNDEX', '2018-01-01', '2018-01-01', 0, 1, 1, 'FIRST_NAME_SOUNDEX', 1, 0, 0, 0);

INSERT INTO R_STANDARD_REFERENCE_LIST (STANDARD_REFERENCE_LIST_ID, CODE_VALUE, CODE_DESCRIPTION, SELECTABLE, CREATED_BY_USER_ID, CREATED_DATETIME, LAST_UPDATED_USER_ID, LAST_UPDATED_DATETIME, REFERENCE_DATA_MASTER_ID, ROW_VERSION)
VALUES (1, 'CODE_VALUE', 'CODE_DESCRIPTION', 'Y', 1, '2018-01-01', 1, '2018-01-01', 1, 1);

INSERT INTO PARTITION_AREA (PARTITION_AREA_ID, AREA, ROW_VERSION)
VALUES (1, 'AREA', 1);

INSERT INTO EVENT (EVENT_ID, IN_BREACH)
VALUES (1, 0);

INSERT INTO PROBATION_AREA (PROBATION_AREA_ID, CODE, DESCRIPTION, SELECTABLE, ROW_VERSION, CREATED_BY_USER_ID, CREATED_DATETIME, LAST_UPDATED_USER_ID, LAST_UPDATED_DATETIME, PRIVATE, ORGANISATION_ID, ADDRESS_ID, START_DATE, SPG_ACTIVE_ID)
VALUES (1, 'A', 'probation are description', 'Y', 1, 1, '2018-01-01', 1, '2018-01-01', 0, 1, 1, '2018-01-01', 1);

-- COURT --
INSERT INTO COURT (COURT_ID, CODE, SELECTABLE, COURT_NAME, LOCALITY, ROW_VERSION, COURT_TYPE_ID, CREATED_BY_USER_ID, CREATED_DATETIME, LAST_UPDATED_USER_ID, LAST_UPDATED_DATETIME, PROBATION_AREA_ID)
VALUES (1, 'A', 'Y', 'Old Baily', 'Local Justice Area', 1, 1, 1, '2018-01-01', 1, '2018-01-01', 1);

INSERT INTO COURT (COURT_ID, CODE, SELECTABLE, COURT_NAME, LOCALITY, ROW_VERSION, COURT_TYPE_ID, CREATED_BY_USER_ID, CREATED_DATETIME, LAST_UPDATED_USER_ID, LAST_UPDATED_DATETIME, PROBATION_AREA_ID)
VALUES (2, 'A', 'Y', 'Sheffield Crown Court', '50 West Bar', 1, 1, 1, '2018-01-01', 1, '2018-01-01', 1);

-- COURT_APPEARANCE --
INSERT INTO COURT_APPEARANCE (COURT_APPEARANCE_ID, APPEARANCE_DATE, EVENT_ID, SOFT_DELETED, PARTITION_AREA_ID, COURT_ID, ROW_VERSION, APPEARANCE_TYPE_ID, CREATED_BY_USER_ID, CREATED_DATETIME, LAST_UPDATED_USER_ID, LAST_UPDATED_DATETIME, OFFENDER_ID)
VALUES (1, '2018-07-25', 1, 0, 1, 1, 1, 1, 1, '2018-01-01', 1, '2018-01-01', 1);

INSERT INTO COURT_APPEARANCE (COURT_APPEARANCE_ID, APPEARANCE_DATE, EVENT_ID, SOFT_DELETED, PARTITION_AREA_ID, COURT_ID, ROW_VERSION, APPEARANCE_TYPE_ID, CREATED_BY_USER_ID, CREATED_DATETIME, LAST_UPDATED_USER_ID, LAST_UPDATED_DATETIME, OFFENDER_ID)
VALUES (2, '2016-06-01', 1, 0, 1, 2, 1, 1, 1, '2018-01-01', 1, '2018-01-01', 1);

INSERT INTO COURT_APPEARANCE (COURT_APPEARANCE_ID, APPEARANCE_DATE, EVENT_ID, SOFT_DELETED, PARTITION_AREA_ID, COURT_ID, ROW_VERSION, APPEARANCE_TYPE_ID, CREATED_BY_USER_ID, CREATED_DATETIME, LAST_UPDATED_USER_ID, LAST_UPDATED_DATETIME, OFFENDER_ID)
VALUES (3, '2017-02-10', 1, 0, 1, 1, 1, 1, 1, '2018-01-01', 1, '2018-01-01', 1);

