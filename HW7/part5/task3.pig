records = LOAD '/ml-1m/ratings.dat' using PigStorage(':');
a = FOREACH records GENERATE $0 as userId, $2 as movieId;
b = group a by movieId;
c = filter b by COUNT(a)>1;
d = foreach c generate group;
