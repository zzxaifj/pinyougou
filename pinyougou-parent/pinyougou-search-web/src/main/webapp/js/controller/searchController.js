app.controller('searchController',function($scope,$location,searchService){
	//传向后台的参数
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':10,'sort':'','field':''}
	//查询框的搜索
	$scope.search=function(){
		$scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(function(response){
			$scope.resultMap=response;
			//分页参数赋值
			$scope.queryByPage();
		});
	}
	
	//当选中分类 品牌 规格时 添加到 search 对象中
	$scope.addSearchItem=function(keys,value){
		if('category'==keys || 'brand'==keys || 'price'==keys){
			$scope.searchMap[keys]=value;
		}else{
			$scope.searchMap.spec[keys]=value;
		}
		
		//提交查询
		$scope.search();
	}
	
	//当点击x 时 从search 对象中撤销
	$scope.removeSeachItem=function(keys){
		if('category'==keys || 'brand'==keys|| 'price'==keys){
			$scope.searchMap[keys]="";
		}else{
			delete $scope.searchMap.spec[keys]; //直接移除key
		}
		//提交查询
		$scope.search();
	}
	//前台输入 页面 查询
	$scope.pushPageLabe = function(pageNo){
	
			if(pageNo < 1 || pageNo > $scope.resultMap.totalPage){
				return;
			}
			$scope.searchMap.pageNo=pageNo;
			//提交查询
			$scope.search();
	}
	
	//分页查询  分页按钮
	$scope.queryByPage = function(){
		$scope.pageLabel=[];
		var firstPage=1;
		var endPage=$scope.resultMap.totalPage;
		//是否显示三个点
		$scope.firstDispalyDrop=false;
		$scope.endDispalyDrop=false;
		
		//总页数大于5也才做特殊处理
		if(endPage > 5){
			//如果页面是 123 就保持  12345
			if($scope.searchMap.pageNo-2 <= 1){
				endPage = firstPage+4;
				$scope.endDispalyDrop=true;
				
			//当页面是 最后三位时  endPage 不变
			}else if($scope.searchMap.pageNo >= endPage-2){
				firstPage = $scope.resultMap.totalPage-4;
				$scope.firstDispalyDrop=true;
			}else{
				firstPage = $scope.searchMap.pageNo-2;
				endPage = $scope.searchMap.pageNo+2;
				$scope.firstDispalyDrop=true;
				$scope.endDispalyDrop=true;
			}
		}
		for(i=firstPage;i<=endPage;i++){
			$scope.pageLabel.push(i);
		}
	}
	
	//排序请求
	$scope.sortForFeild=function(sort,field){
		$scope.searchMap.sort=sort;
		$scope.searchMap.field=field;
		//提交查询
		$scope.search();
	}
	
	//判断查询keyword中是否存在品牌关键字  存在时就隐藏品牌信息
	$scope.findKeywordsConditionBrand=function(){
		//循环品牌列表
		for(i=0;i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true;
			}
		}
		return false;
	}
	
	//主页面请求此页面 时初始化 方法 需要加载父页面的参数
	$scope.onloadFatherUrl=function(){
		$scope.searchMap.keywords = $location.search()['keywords'];
		//提交查询
		$scope.search();
	}
});