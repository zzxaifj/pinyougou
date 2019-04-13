app.service('uploadService',function($http){
	this.upload=function(){
		
		//FormDate 类在文件上传的时候使用 二进制类型
		var formData = new FormData();
		//追加 file:files[0]  第一个表示文件上传框的 id files[0]表示取第一个
		formData.append('file',file.files[0]);
		
		return $http({
			url:'../upload.do',
			method:'post',
			data:formData,
			//请求默认是 application/json  文件格式不能定义type
			headers:{'Content-Type':undefined},
			transformRequest: angular.identity
		});
	}
});