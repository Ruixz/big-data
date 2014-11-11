REGISTER /home/hadoop/pig-0.13.0/contrib/piggybank/java/piggybank.jar;
DEFINE ApacheCommonLogLoader org.apache.pig.piggybank.storage.apachelog.CommonLogLoader();
DEFINE DayExtractor org.apache.pig.piggybank.evaluation.util.apachelogparser.DateExtractor('yyyy-MM-dd');

logs = LOAD '/test/access.log' USING ApacheCommonLogLoader AS (ip_address, rfc, userId, dt, request, serverstatus, returnobject, referersite, clientbrowser);
b = GROUP logs BY ip_address;
outcome = FOREACH b GENERATE COUNT(logs);
DUMP outcome;