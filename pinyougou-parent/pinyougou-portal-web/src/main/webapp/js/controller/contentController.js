app.controller('contentController',function($scope,contentService){
	
	//因为页面有多种广告分类列表  因此通过数组 将id 作为下标进行分裂
	$scope.contentCategoryList=[];
	$scope.findByContentCategory=function(id){
		contentService.findByContentCategory(id).success(function(response){
			$scope.contentCategoryList[id]=response;
		});
	}
	
	$scope.search=function(){
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
});