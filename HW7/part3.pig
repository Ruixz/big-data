nyse = load '/test/NYSE_daily_prices_A.csv' using PigStorage(',');
runs = FOREACH nyse GENERATE $1 as stock_symbol, $4 as stock_price_high;
grp_data = GROUP runs BY (stock_symbol);
avg_stock_high = FOREACH grp_data GENERATE AVG(runs.stock_price_high);
dump avg_stock_high;