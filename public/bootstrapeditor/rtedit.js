

function selectedImage(){
	 var files = $("#fileupload")[0].files;
     $("#numfile").text(files.length);
        /* for (i=0;i<numFiles;i++){
             fileSize = parseInt(e.currentTarget.files[i].size, 10)/1024;
             filesize = Math.round(fileSize);
             $('<li />').text(e.currentTarget.files[i].name).appendTo($('#output'));
             $('<span />').addClass('filesize').text('(' + filesize + 'kb)').appendTo($('#output li:last'));
         }*/
}


function selectedVideo(){
	 var files = $("#fileupload2")[0].files;
     $("#numfile2").text(files.length);
}

var uploadedFiles = [];

function getFiles(){
	$("#fileupload").click();
	var files = $('#fileupload').fileupload({
	    sequentialUploads: true,
	    autoUpload:false,
	    change: function (e, data) {
	        $.each(data.files, function (index, file) {
	        	uploadedFiles.push(file);
	        });
	        $("#numfile").text(uploadedFiles.length);
	    }
	});
}

var formData;
function addImagesBeforeSubmit() {
	formData = new FormData($("#blogForm")[0]);
	
	ldiv.loadertext.text("Saving...");
	ldiv.display();
	
	$.each(uploadedFiles, function(i, file) {
		formData.append('fileUpload[]', file, file.name);
	});
	console.log(formData);
	
	var request = new XMLHttpRequest();
	request.open("POST", "/blog/postnew", true);
	request.onreadystatechange = function() {//Call a function when the state changes.
	    if(request.readyState == 4 && request.status == 200) {
	    	ldiv.switchoff();
	    	window.location.replace("/blog/page/" + this.response);
	    }
	}
	request.send(formData);
}

function getVideos(){
	$("#fileupload2").click();
}
