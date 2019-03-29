	<!-- $scope 与body进行交互 $http 与后台交互-->
	app.controller('brandController',function($scope,$controller,$http,brandService){
		//继承 baseController
		$controller('baseController',{$scope:$scope});
		
		$scope.findAll = function(){
			brandService.findAll().success(function(response){
				$scope.brandList=response;
			});
		}
		
		//查询后端代码
		$scope.findPage=function(page,rows){
			brandService.findPage().success(function(response){
    			$scope.paginationConf.totalItems = response.total;
    			$scope.brandList = response.rows;
    		});
		}
		
		//新增/修改品牌列表
		$scope.add=function(){
			brandService.saveOrUpdate($scope.brand).success(function(response){
				if(response.success){
					$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			});
		}
		
		//通过 id 获取品牌数据 
		$scope.findOne=function(id){
			brandService.findOne(id).success(function(response){
				$scope.brand=response;
			});
		}
		
		$scope.dele=function(){
			brandService.deleteList($scope.selectIds).success(function(response){
    			if(response.success){
    				$scope.reloadList();//重新加载
    			}else{
    				alert(response.messsage);
    			}
    		});
		}
		
		//页面刷新
		$scope.refresh = function(){
			$scope.reloadList();//重新加载
		}
		
		//页面模糊查询 
		$scope.searchEntity={};
		$scope.search = function(page,rows){
			brandService.search(page,rows,$scope.searchEntity).success(function(response){
				$scope.paginationConf.totalItems = response.total;
    			$scope.brandList = response.rows;
			});
		}
	});