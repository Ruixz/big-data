db.nyse.mapReduce(
		function(){
			emit(this.stock_symbol, {count: 1, sum: this.stock_price_high})
		},
		function(key, values){
			reduceVal = { count: 0, sum: 0};
			for(var i=0; i<values.length; i++){
				reduceVal.count += values[i].count;
				reduceVal.sum += values[i].sum;
			}
			return reduceVal;
		},
		{
			out: { merge: "nyseAverageHigh"},
			finalize: function(key, reduceVal){
				reduceVal.avg = reduceVal.sum/reduceVal.count;
				return reduceVal;
			}
		}
	)