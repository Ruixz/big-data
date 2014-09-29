db.accessLog.mapReduce(
		function(){
			emit(this.http_code, 1)
		},
		function(key, values){
			for(var i=0, sum=0; i<values.length; i++){
				sum += values[i];
			}
			return sum;
		},
		{out: "httpStatusTimes"}
	)