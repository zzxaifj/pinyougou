var app = angular.module('pinyougou',[]);

//过滤器

app.filter('trustHtml',['$sce',function($sce){
	return function(data){
		return $sce.trustAsHtml(data);
	}
}]);