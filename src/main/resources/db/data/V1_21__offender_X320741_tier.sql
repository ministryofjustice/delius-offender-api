Insert into R_STANDARD_REFERENCE_LIST (STANDARD_REFERENCE_LIST_ID,CODE_VALUE,CODE_DESCRIPTION,SELECTABLE,CREATED_BY_USER_ID,CREATED_DATETIME,LAST_UPDATED_USER_ID,LAST_UPDATED_DATETIME,REFERENCE_DATA_MASTER_ID,ROW_VERSION,TRAINING_SESSION_ID,SPG_INTEREST,SPG_OVERRIDE)
values (2500032292,'UA2','A-2','Y',2500040507,to_date('16-OCT-19','DD-MON-RR'),2500040507,to_date('16-OCT-19','DD-MON-RR'),69,1,null,1,0);

Insert into R_STANDARD_REFERENCE_LIST (STANDARD_REFERENCE_LIST_ID,CODE_VALUE,CODE_DESCRIPTION,SELECTABLE,CREATED_BY_USER_ID,CREATED_DATETIME,LAST_UPDATED_USER_ID,LAST_UPDATED_DATETIME,REFERENCE_DATA_MASTER_ID,ROW_VERSION,TRAINING_SESSION_ID,SPG_INTEREST,SPG_OVERRIDE)
values (2500032294,'UB1','B-1','Y',2500040507,to_date('16-OCT-19','DD-MON-RR'),2500040507,to_date('16-OCT-19','DD-MON-RR'),69,1,null,1,0);

Insert into R_STANDARD_REFERENCE_LIST (STANDARD_REFERENCE_LIST_ID,CODE_VALUE,CODE_DESCRIPTION,SELECTABLE,CREATED_BY_USER_ID,CREATED_DATETIME,LAST_UPDATED_USER_ID,LAST_UPDATED_DATETIME,REFERENCE_DATA_MASTER_ID,ROW_VERSION,TRAINING_SESSION_ID,SPG_INTEREST,SPG_OVERRIDE)
values (2500032293,'ATS','???','Y',2500040507,to_date('16-OCT-19','DD-MON-RR'),2500040507,to_date('16-OCT-19','DD-MON-RR'),70,1,null,1,0);

update MANAGEMENT_TIER set TIER_ID=2500032292, TIER_CHANGE_REASON_ID=2500032293 where OFFENDER_ID=2500343964