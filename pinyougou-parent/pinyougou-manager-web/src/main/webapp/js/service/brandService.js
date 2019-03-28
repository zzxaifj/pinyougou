	//MVC 之Service层
	app.service('brandService',function($http){
		this.findAll=function(){
			return $http.get('../brand/findAll.do');
		}
		
		this.findPage=function(){
			return $http.get('../brand/findPage.do?page='+page+'&rows='+rows);
		}
		
		this.saveOrUpdate=function(brand){
			return $http.post('../brand/saveOrUpdate.do',brand);
		}
		
		this.findOne=function(id){
			return $http.get('../brand/findOne.do?id='+id);
		}
		
		this.deleteList=function(ids){
			return $http.get('../brand/delete.do?ids='+ids);
		}
		
		this.search=function(page,rows,searchEntity){
			return $http.post('../brand/search.do?page='+page
					+'&rows='+rows,searchEntity)
		}
	});