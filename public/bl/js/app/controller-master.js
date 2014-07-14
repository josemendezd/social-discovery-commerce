'use strict';

var blmaster = angular.module('bl-master');

blmaster.controller('CollectionImageSelectionController',function($scope, $http,$modal, $window,$location){
	
	$scope.fdata = {withId: '',ofId: ''};
	
	var modalInstance;
	$scope.collectiongallery = function() {
		$http.get('/user/collection/images')
		.success(function(data, status, headers, config){
			$scope.collections = data.images;
			
			modalInstance = $modal.open({
			      templateUrl: '/assets/bl/js/app/views/collectiongallery.html',
			      scope : $scope
			});
		});
	}
	
	$scope.close = function() {
		modalInstance.dismiss('close');
	}
	
	$scope.ok = function() {
		modalInstance.dismiss('close');
		ldiv.display();
		$http.post('/user/collection/copycoverimage/'+$scope.fdata.ofId+'/'+$scope.fdata.withId).success(function(data, status){
			ldiv.loadcomplete();
			if(status == 200) {
				if($('#colcn_coverimage').length == 0 ) {
					$(".img-profile").append("<img id='colcn_coverimage' src='" + data + "' class='img-responsive'>");
				} else {
					$("#colcn_coverimage").attr('src',data);
				}
			}
			
		});
	}
	
	
	
	
});

blmaster.controller('ApplicationController',function($scope, $http){
	
	
});
