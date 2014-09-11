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

blmaster.controller('PopoverController',function($scope, $http){
	$scope.getDetails = function(pid) {
		$http.get('/get-rating-details/'+pid).success( function(data, status) {
			$scope.prdetails = data;
		});
	}
});

var permalink = $("#permalink_second").val();

String.prototype.replaceAll = function(s,r){ return this.split(s).join(r) }

blmaster.controller('BlogController',function($scope, $http){
	$scope.checkPermalink = function($event) {
		if($scope.permalink != undefined) {
			if($scope.permalink.length == 0) {
				$scope.permalink_new = permalink;
			}
			else {
				$scope.permalink_new = angular.lowercase($scope.permalink.replace(/\s+/g, "-"));
			}
		}
	};
	
	$scope.checkBeforeKeypress = function(event) {
		var regex = new RegExp("^[a-zA-Z0-9\ \]+$");
	    var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
	    if (!regex.test(key)) {
	       event.preventDefault();
	       return false;
	    }
	};
	
	$scope.checkAvailibility = function( _permalink ) {
		if( _permalink == undefined) {
			_permalink = permalink;
		}
		$http.get('/check-permalink-availability/' + _permalink).success( function(data, status) {
			$('.permalink-error').remove();
			$('#permalink-div').after('<p class="permalink-error text-success" style="font-size: 14px; padding:0px;">Yes, This permalink is available!</p>');
		}).error( function(data, status) {
			$('.permalink-error').remove();
			$('#permalink-div').after('<p class="permalink-error text-danger" style="font-size: 14px; padding:0px;">Sorry, This permalink is already taken. Please choose another one!</p>');
		});
	};
});

blmaster.controller('SpamTableController',function($scope, $http, $filter, $timeout, $q, ngTableParams){

	$scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10,          // count per page
        sorting: {
            name: 'asc'     // initial sorting
        }
    }, {
        total: 0, // length of data
        getData: function($defer, params) {
        	$http.get('/get-spams?page=' + params.page() +"&rows=" + params.count()).success( function(data, status) {
        		
        		$timeout(function() {
                     // update table params
                     params.total(data.total);
                     // set new data
                     
                     $defer.resolve(params.sorting() ?
                             $filter('orderBy')(data.comments, params.orderBy()) :
                                 data.comments);
                     //$scope.commentList = data.comments;
                 }, 500);
    		}).error( function(data, status) {
    			
    		});
        }
    });
	
	$scope.showDetails = function( comment, $index ){
		$scope.index = $index;
		$scope.commentVM = comment;
	};
	
	$scope.noSpam = function( comment_id, $index ){
		$http.put('/not-a-spam', {'comment_id' : comment_id}).success( function(data,status) {
			$scope.tableParams.data.splice($index,1);
		});
	};
	
	$scope.submitSpam = function( comment , $index ){
		$http.put('/submit-spam', {'comment_id' : comment.id, 'post_id' : comment.post_id}).success( function(data,status) {
			$scope.tableParams.data.splice($index,1);
		});
	};
});

blmaster.controller('BlogSpamTableController',function($scope, $http, $filter, $timeout, $q, ngTableParams){

	$scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10,          // count per page
        sorting: {
            name: 'asc'     // initial sorting
        }
    }, {
        total: 0, // length of data
        getData: function($defer, params) {
        	$http.get('/get-blog-spams?page=' + params.page() +"&rows=" + params.count()).success( function(data, status) {
        		
        		$timeout(function() {
                     // update table params
                     params.total(data.total);
                     // set new data
                     
                     $defer.resolve(params.sorting() ?
                             $filter('orderBy')(data.comments, params.orderBy()) :
                                 data.comments);
                     //$scope.commentList = data.comments;
                 }, 500);
    		}).error( function(data, status) {
    			
    		});
        }
    });
	
	$scope.showDetails = function( comment, $index ){
		$scope.index = $index;
		$scope.commentVM = comment;
	};
	
	$scope.noSpam = function( comment_id, $index ){
		$http.put('/blog/not-a-spam', {'comment_id' : comment_id}).success( function(data,status) {
			$scope.tableParams.data.splice($index,1);
		});
	};
	
	$scope.submitSpam = function( comment , $index ){
		$http.put('/blog/submit-spam', {'comment_id' : comment.id, 'post_id' : comment.post_id}).success( function(data,status) {
			$scope.tableParams.data.splice($index,1);
		});
	};
});

blmaster.controller('ProductSpamTableController',function($scope, $http, $filter, $timeout, $q, ngTableParams){

	$scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10,          // count per page
        sorting: {
            name: 'asc'     // initial sorting
        }
    }, {
        total: 0, // length of data
        getData: function($defer, params) {
        	$http.get('/get-product-spams?page=' + params.page() +"&rows=" + params.count()).success( function(data, status) {
        		
        		$timeout(function() {
                     // update table params
                     params.total(data.total);
                     // set new data
                     
                     $defer.resolve(params.sorting() ?
                             $filter('orderBy')(data.comments, params.orderBy()) :
                                 data.comments);
                     //$scope.commentList = data.comments;
                 }, 500);
    		}).error( function(data, status) {
    			
    		});
        }
    });
	
	$scope.showDetails = function( comment, $index ){
		$scope.index = $index;
		$scope.commentVM = comment;
	};
	
	$scope.noSpam = function( comment_id, $index ){
		$http.put('/product/not-a-spam', {'comment_id' : comment_id}).success( function(data,status) {
			$scope.tableParams.data.splice($index,1);
		});
	};
	
	$scope.submitSpam = function( comment , $index ){
		$http.put('/product/submit-spam', {'comment_id' : comment.id, 'post_id' : comment.post_id}).success( function(data,status) {
			$scope.tableParams.data.splice($index,1);
		});
	};
});
// For Admin
blmaster.controller('ManageTagsController',function($scope, $http){
	$scope.addLabel = function(tags){
		alert(tags);
		$http.put('/tags/add', {'tags':tags}).success( function() {
			alert("Success");
		})
	}
});

