app.controller('searchController',function($scope,searchService){
	//传向后台的参数
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{}}
	//查询框的搜索
	$scope.search=function(){
		searchService.search($scope.searchMap).success(function(response){
			$scope.resultMap=response;
		});
	}
	
	//当选中分类 品牌 规格时 添加到 search 对象中
	$scope.addSearchItem=function(keys,value){
		if('category'==keys || 'brand'==keys){
			$scope.searchMap[keys]=value;
		}else{
			$scope.searchMap.spec[keys]=value;
		}
		
		//提交查询
		$scope.search();
	}
	
	//当点击x 时 从search 对象中撤销
	$scope.removeSeachItem=function(keys){
		if('category'==keys || 'brand'==keys){
			$scope.searchMap[keys]="";
		}else{
			delete $scope.searchMap.spec[keys]; //直接移除key
		}
		//提交查询
		$scope.search();
	}
});