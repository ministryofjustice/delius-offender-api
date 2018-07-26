CREATE TABLE COURT
(
	COURT_ID NUMBER NOT NULL
		CONSTRAINT XPKCOURT
			PRIMARY KEY,
	CODE CHAR(6) NOT NULL,
	SELECTABLE CHAR NOT NULL
		CONSTRAINT YES_OR_NO
			CHECK (SELECTABLE IN ('N','Y')),
	COURT_NAME VARCHAR2(80),
	TELEPHONE_NUMBER VARCHAR2(35),
	FAX VARCHAR2(35),
	BUILDING_NAME VARCHAR2(35),
	STREET VARCHAR2(35),
	LOCALITY VARCHAR2(35),
	TOWN VARCHAR2(35),
	COUNTY VARCHAR2(35),
	POSTCODE VARCHAR2(8),
	COUNTRY VARCHAR2(16),
	ROW_VERSION NUMBER DEFAULT 0 NOT NULL,
	COURT_TYPE_ID NUMBER NOT NULL
		REFERENCES R_STANDARD_REFERENCE_LIST,
	CREATED_DATETIME DATE NOT NULL,
	CREATED_BY_USER_ID NUMBER NOT NULL,
	LAST_UPDATED_DATETIME DATE NOT NULL,
	LAST_UPDATED_USER_ID NUMBER NOT NULL,
	TRAINING_SESSION_ID NUMBER,
	PROBATION_AREA_ID NUMBER NOT NULL
		REFERENCES PROBATION_AREA,
	SECURE_EMAIL_ADDRESS VARCHAR2(255),
	CONSTRAINT XAK1COURT
		UNIQUE (CODE, TRAINING_SESSION_ID, COURT_ID)
);

CREATE TABLE COURT_APPEARANCE
(
	COURT_APPEARANCE_ID NUMBER NOT NULL
		CONSTRAINT XPKCOURT_APPEARANCE
			PRIMARY KEY,
	APPEARANCE_DATE DATE NOT NULL,
	CROWN_COURT_CALENDAR_NUMBER VARCHAR2(20),
	BAIL_CONDITIONS VARCHAR2(256),
	COURT_NOTES CLOB,
	EVENT_ID NUMBER NOT NULL
		REFERENCES EVENT,
	TEAM_ID NUMBER
		REFERENCES TEAM,
	STAFF_ID NUMBER
		REFERENCES STAFF,
	SOFT_DELETED NUMBER NOT NULL
		CONSTRAINT TRUE_OR_FALSE26
			CHECK (SOFT_DELETED IN (0, 1)),
	PARTITION_AREA_ID NUMBER NOT NULL
		REFERENCES PARTITION_AREA,
	COURT_ID NUMBER NOT NULL
		REFERENCES COURT,
	ROW_VERSION NUMBER DEFAULT 0 NOT NULL,
	APPEARANCE_TYPE_ID NUMBER NOT NULL
		REFERENCES R_STANDARD_REFERENCE_LIST,
	PLEA_ID NUMBER
		REFERENCES R_STANDARD_REFERENCE_LIST,
	OUTCOME_ID NUMBER
		REFERENCES R_STANDARD_REFERENCE_LIST,
	REMAND_STATUS_ID NUMBER
		REFERENCES R_STANDARD_REFERENCE_LIST,
	CREATED_BY_USER_ID NUMBER NOT NULL,
	CREATED_DATETIME DATE NOT NULL,
	LAST_UPDATED_USER_ID NUMBER NOT NULL,
	LAST_UPDATED_DATETIME DATE NOT NULL,
	TRAINING_SESSION_ID NUMBER,
	OFFENDER_ID NUMBER NOT NULL
		REFERENCES OFFENDER,
	ORGANISATIONS T_ARR_ORGANISATIONS
);
