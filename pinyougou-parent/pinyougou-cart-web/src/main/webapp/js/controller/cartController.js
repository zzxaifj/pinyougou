app.controller('cartController',function($scope,cartService){
	$scope.findCartList = function(){
		cartService.findCartList().success(function(resp){
			$scope.cartList=resp;
			//统计总个数与总金额
			$scope.totalValue=cartService.sum($scope.cartList);
		});
	}
	
	//添加商品到购物车
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
			function(response){
				if(response.success){
					$scope.findCartList();//刷新列表
				}else{
					alert(response.message);//弹出错误提示
				}				
			}
		);
	}
	
});