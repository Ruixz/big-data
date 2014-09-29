db.nyse.mapReduce(
		function(){
			emit(this.stock_symbol, this.stock_price_high)
		},
		function(key, values){
			for(var i=0, sum=0; i<values.length; i++){
				sum += values[i];
			}
			i += 1;
			return sum/i;
		},
		{out: "nyseAverageHigh"}
	)