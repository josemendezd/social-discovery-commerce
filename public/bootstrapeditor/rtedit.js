


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

function getFiles(){
	$("#fileupload").click();
}

function getVideos(){
	$("#fileupload2").click();
}


