/*$(function() {
	$("[data-sticky_column]").stick_in_parent({
	      parent: "[data-sticky_parent]",
	      offset_top:100
	    });
});*/

$(document).ready(function(){
	$(".readmore").readmore({
		speed: 75,
		  maxHeight: 500,
		  afterToggle: function(trigger, element, expanded) {
			  setTimeout(trigerStick,1500);
			  }
			});
	setTimeout(setupSticky,2000);
	
});

function setupSticky(){
	$("[data-sticky_column]").stick_in_parent({
	      parent: "[data-sticky_parent]",
	      offset_top:-50
	    });
}

$( window ).resize(function() {
	trigerStick();
});

function trigerStick(){
	$(document.body).trigger("sticky_kit:recalc");
}