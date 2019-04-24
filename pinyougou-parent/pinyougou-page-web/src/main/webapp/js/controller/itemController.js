app.controller('itemController' ,function($scope){
	$scope.add=function(num){
		$scope.num+=num;
		if($scope.num <= 1){
			$scope.num = 1;
		}
	}
	
	$scope.specificationItems={};//记录用户选择的规格
	//用户选择规格
	$scope.selectSpecification=function(key,value){
		$scope.specificationItems[key]=value;
		searchSku();
	}
	
	//判断某规格选项是否被用户选中
	$scope.isSelected=function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}
	}
	
	//加载第一条 item
	$scope.loadSku=function(){
		$scope.sku= skuList[0];
		//深复制spec中的数据
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
	}

	//添加到购物车
	$scope.addToCar = function(){
		alert('skuid:'+$scope.sku.id);	
	}
	
	//查询 SKU
	searchSku = function(){
		for(var i=0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				return $scope.sku=skuList[i];
			}
		}
	}
	
	//匹配两个对象
	matchObject = function(map1,map2){
		for(var key in map1){
			if(map1[key] != map2[key]){
				return false;
			}
		}
		
		for(var key in map2){
			if(map2[key] != map1[key]){
				return false;
			}
		}
		
		return true;
	}
});