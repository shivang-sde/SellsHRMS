-- Drop the old month/year/org unique key
ALTER TABLE tbl_pay_run 
DROP INDEX UKnt58yi50g8kgbx948h6hpx3qa;

-- Add new unique key based on cycle boundaries
ALTER TABLE tbl_pay_run 
ADD CONSTRAINT uk_org_cycle UNIQUE (organisation_id, start_date, end_date);
