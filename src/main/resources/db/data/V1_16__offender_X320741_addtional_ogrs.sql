Insert into OGRS_ASSESSMENT (
OGRS_ASSESSMENT_ID,
ASSESSMENT_DATE,
EVENT_ID,
OGRS2_SCORE,
SOFT_DELETED,
OGRS3_SCORE_1,
PARTITION_AREA_ID,
OGRS3_SCORE_2,
ROW_VERSION,TRAINING_SESSION_ID,
CREATED_BY_USER_ID,
CREATED_DATETIME,
LAST_UPDATED_USER_ID,
LAST_UPDATED_DATETIME)
values
 (2500119030,
 to_date('11-DEC-20','DD-MON-RR'),
 2500297061,
 null,
 0, -- SOFT_DELETED
 24,
 0,
 27, -- OGRS Score
 1,
 null,
 2500040507,
 to_date('11-DEC-20','DD-MON-RR'),
 2500040507,
 to_date('11-DEC-20','DD-MON-RR'));
