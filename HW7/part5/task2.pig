records = LOAD '/ml-1m/users.dat' using PigStorage(':');
a = FOREACH records GENERATE $2 as gender;
grp = group a by gender;
c = FOREACH grp GENERATE group, COUNT(a.gender);
dump c;
