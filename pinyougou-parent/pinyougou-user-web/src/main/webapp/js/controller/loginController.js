app.controller('loginController',function($scope,loginService){
	$scope.showName=function(){
		loginService.showName().success(function(resp){
			$scope.loginName = resp.loginName;
		});
	}
});