 //控制层 
app.controller('userController' ,function($scope,$controller,userService){	
	
	//注册
    $scope.reg = function(){
    	if($scope.passworld != $scope.entity.passworld){
    		alert("两次密码输入不一致请重新输入");
    		$scope.passworld = "";
    		return ;
    	}
    	userService.add($scope.entity,$scope.code).success(function(resp){
    		alert(resp.message);
    	});
    }
    
    //发送验证码
    $scope.sendCode = function(){
    	userService.sendCode($scope.entity.phone).success(function(resp){
    		if(!resp.success){
    			alert(resp.message);
    		}
    	});
    }
});	
