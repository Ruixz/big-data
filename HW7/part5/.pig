records = LOAD '/ml-1m/ratings.dat' using PigStorage(':');
c = FOREACH records GENERATE $2 as movieId, $4 as (rating:float);
d = GROUP c BY movieId;
#rate = FOREACH d GENERATE $0, flatten($1.rating);

avg = FOREACH d GENERATE FLATTEN(group), AVG(c.rating);
top25 = FOREACH c {
	result = TOP(25, 1, c);
	GENERATE FLATTEN(result);
};
