records = LOAD '/ml-1m/ratings.dat' using PigStorage(':');
c = FOREACH records GENERATE $2 as movieId, $4 as (rating:float);
d = GROUP c BY movieId;
avg = FOREACH d GENERATE FLATTEN(group), AVG(c.rating);
topResults = FOREACH avg {
    result = TOP(25, 1, d); 
    GENERATE FLATTEN(result);
}
