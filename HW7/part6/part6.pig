REGISTER 'myUDF.jar';
records = LOAD '/ml-1m/users.dat' using com.udf.pig.MyLoader();
a = FOREACH records GENERATE $2 as gender;
grp = group a by gender;
c = FOREACH grp GENERATE group, COUNT(a.gender);
dump c;