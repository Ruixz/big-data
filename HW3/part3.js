map1 = function(){
	emit(this.ip, 1)
}

reduce1 = function(key, vals){
	for(var i=0, sum=0; i < vals.length; i++){
		sum += vals[i];
	}
	return sum;
}

db.accessLog.mapReduce(map1, reduce1, {out: "ipAccessTimes"})
