/*$(function() {
	$("[data-sticky_column]").stick_in_parent({
	      parent: "[data-sticky_parent]",
	      offset_top:100
	    });
});*/

$(document).ready(function(){
	/*$(".readmore").readmore({
		speed: 75,
	    maxHeight: 500,
	    beforeToggle : function(trigger, element, expanded) {
	    	return false;
	    },
		afterToggle: function(trigger, element, expanded) {
			return false;
		}
	});*/
	
	$(document).on('click', '.readmore-js-toggle', function() {
		$(this).prev().css("height", "650px");
		$(this).prev().niceScroll({styler:"fb",cursorcolor:"#000"});
		$(this).removeClass('readmore-js-toggle').addClass('readmore-js-close').text('Close');
	});
	
	$(document).on('click', '.readmore-js-close', function() {
		$(this).prev().css("height", "500px");
		$(this).removeClass('readmore-js-close').addClass('readmore-js-toggle').text('Read More');
		console.log($(this).prev());
		$(this).prev().scrollTop(0);
		$(this).prev().getNiceScroll().remove();
	});
	
});

function setupSticky(){
	trigerStick();
	$("[data-sticky_column]").stick_in_parent({
	      parent: "[data-sticky_parent]",
	      inner_scrolling:true,
	      offset_top:150
	    });
}

$( window ).resize(function() {
	trigerStick();
});

function trigerStick(){
	$(document.body).trigger("sticky_kit:recalc");
}