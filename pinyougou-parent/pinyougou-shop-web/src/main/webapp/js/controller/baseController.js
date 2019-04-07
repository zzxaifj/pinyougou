app.controller('baseController',function($scope){
	//2、需要在table结束后添加页数显示标签tm-pagination 并定义conf（位于博客最后）
    /*3、将2中定义的conf变量，进行赋值
    	currentPage : 页码,
		totalItems : 总记录数,
		itemsPerPage : 每页显示记录数,
		perPageOptions : 需要每页显示多少记录，用户可自定义,
		onChange : 当页码发生变化时，出发事件。
    */
	//分页控件配置 
	$scope.paginationConf = {
		currentPage : 1,
		totalItems : 10,
		itemsPerPage : 10,
		perPageOptions : [ 10, 20, 30, 40, 50 ],
		onChange : function() {
			$scope.reloadList();//重新加载
		}
	};
	
	$scope.reloadList=function(){
		//$scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	}
	
	//批量删除,数组插入push,删除splice
	$scope.selectIds=[];
	//通过 $event 内置属性判断是否选中   (**************************************************)
	$scope.updateSelection = function($event,id){
		if($event.target.checked){//如果选中则添加到 ids 中
			$scope.selectIds.push(id);
		}else{
			var index = $scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index,1);//删除
		}
	}
	
	//获取对象中某一属性进行拼装
	$scope.jsonToString=function(jsonString , key){
		var json = JSON.parse(jsonString);
		var value="";
		for(i=0;i<json.length;i++){
			if(i>0){
				value+=",";
			}
			value+=json[i][key];
		}
		return value;
	}
	
	//将对象转换为list
	$scope.jsonToList=function(jsonString , key){
		var json = JSON.parse(jsonString);
		var value=[];
		for(i=0;i<json.length;i++){
			value.push(json[i][key]);
		}
		return value;
	}
	
	//在 list 中通过key 添加value 如果 key 不存在则添加 key
	$scope.searchObjectByKey=function(list,key,value){
		for(var i=0;i<list.length;i++){
			if(list[i][key] == value){
				return list[i]
			}
		}
	}
});