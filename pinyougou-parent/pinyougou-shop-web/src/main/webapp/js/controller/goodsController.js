 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	$scope.add=function(){
		$scope.entity.tbGoodsDesc.introduction=editor.html();
		goodsService.add($scope.entity).success(
			function(response){
				if(response.success){
					//重新查询 
		        	alert(response.message);
		        	$scope.entity={};
		        	editor.html("");
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	//文件上传   上传按钮
	$scope.upload=function(){
		uploadService.upload().success(function(response){
			if(response.success){
				$scope.image_entity.url=response.message;
			}else{
				alert(response.messge);
			}
		});
	}
	
	//保存图片
	$scope.entity={tbGoodsDesc:{itemImages:[],specificationItems:[]}};
	$scope.add_image_entity=function(){
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
	}
	
	$scope.remove=function(index){
		$scope.entity.tbGoodsDesc.itemImages.splice(index,1);
	}
	
	//下拉一级分录列表的实现
	$scope.findItemCatEntity=function(){
		itemCatService.findByParentId(0).success(function(response){
			$scope.itemCatEntity1=response;
		});
	}
	
	//下拉二级分录列表的实现
	$scope.$watch('entity.tbGoods.category1Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCatEntity2=response;
			$scope.itemCatEntity3=[];
			$scope.entity.tbGoods.typeTemplateId="";
			$scope.brandList=[];
		});
	});
	//下拉三级分录列表的实现
	$scope.$watch('entity.tbGoods.category2Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCatEntity3=response;
			$scope.entity.tbGoods.typeTemplateId="";
			$scope.brandList=[];
		});
	});
	//三级分录所属的模板ID
	$scope.$watch('entity.tbGoods.category3Id',function(newValue,oldValue){
		itemCatService.findOne(newValue).success(function(response){
			$scope.entity.tbGoods.typeTemplateId=response.typeId;
			$scope.brandList=[];
		});
	});
	
	//通过模板ID 获取品牌列表
	$scope.$watch('entity.tbGoods.typeTemplateId',function(newValue,oldValue){
		typeTemplateService.findOne(newValue).success(function(response){
			//品牌列表
			$scope.brandList = JSON.parse(response.brandIds);
			$scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
		});
		
		typeTemplateService.findSpecList(newValue).success(function(response){
			$scope.specList=response;
		});
	});
	
	//选择规格信息，进行组装
	$scope.updateSpecAttribute=function($event,key,value){
		var obj = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems,"attributeName",key);
			if(obj != null){
				if($event.target.checked){
					obj.attributeValue.push(value);
				}else{
					obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);
					if(obj.attributeValue.length == 0){
						$scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(obj),1);
					}
				}
			}else{
				$scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":key,"attributeValue":[value]});
			}
	}
	
	$scope.createItemList=function(){
		//初始化
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];
		
		//将规格的list进行遍历
		var items = $scope.entity.tbGoodsDesc.specificationItems;
		
		for(var i=0;i<items.length;i++){
			$scope.entity.itemList=addCloum($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
	}
	//将规格中的值,按照value 新增List
	addCloum=function(list,conlumName,conlumValues){
		var newList=[];
		for(var i=0;i<list.length;i++){
			var oldList=list[i]; 
			for(var j=0;j<conlumValues.length;j++){
				var	newRow = JSON.parse(JSON.stringify(oldList));//深克隆
				newRow.spec[conlumName]=conlumValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
});	
