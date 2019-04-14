app.service('contentService',function($http){
	this.findByContentCategory=function(id){
		return $http.get('content/findByContentCategoryId.do?id='+id);
	}
});