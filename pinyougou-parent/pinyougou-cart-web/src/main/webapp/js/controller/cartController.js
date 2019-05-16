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
	
	//查询所有的收货地址列表
	$scope.findAddressList = function(){
		cartService.findAddressList().success(function(res){
			$scope.addressList=res;
			for(var i=0;i<$scope.addressList.length;i++){
				if($scope.addressList[i].isDefault == '1'){
					$scope.address=$scope.addressList[i];
					break;
				}
			}
		});
	}
	//用于存储选中的地址
	$scope.selectAddress=function(address){
		$scope.address=address;
	}
	//查看是否选中此地址
	$scope.isSelectAddress=function(address){
		if($scope.address == address){
			return true;
		}else{
			return false;
		}
	}
	
	//选择付款方式
	$scope.order={paymentType:'1'};
	//选择支付方式
	$scope.selectPayType=function(type){
		$scope.order.paymentType=type;
	}
	
	//提交订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;//地址
		$scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人
		cartService.submitOrder($scope.order).success(function(rep){
			if(rep.success){
				//页面跳转
				if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
					location.href="pay.html";
				}else{
					location.href="paysuccess.html";
				}
			}else{
				alert(rep.message);	//也可以跳转到提示页面				
			}
		})
	}
});