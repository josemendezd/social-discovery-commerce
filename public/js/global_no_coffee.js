function globalfunctions() {
    $.ajaxSetup({
      cache: false
    });
    $("html, body").animate({
      scrollTop: 0
    }, 10);
    $.each($("form"), function(i, f) {
      return f.reset();
    });
    $("#footerfeedbackform").find(".loaderspan").hide();
    $("#footerfeedbackform").submit(function(event) {
      var $form, $formcontent, $mes, $url, Errormsgs, formname, reply, retnresp;
      event.preventDefault();
      formname = this.id;
      $form = $(this);
      $formcontent = $form.serialize();
      $url = $form.attr("action");
      $mes = $form.find(".loaderspan");
      $mes.show();
      if (!$mes.hasClass("bgloadericoleft")) {
        $mes.addClass("bgloadericoleft");
      }
      $mes.html("<label>loading.....</label>");
      retnresp = $().setresponse({
        ispost: true,
        url: $url,
        argdata: $formcontent,
        failans: false,
        failreq: true,
        failnode: "responseText"
      });
      if (retnresp.Issuccess === false) {
        reply = $.parseJSON(retnresp.retresp);
        Errormsgs = "";
        $.each(reply, function(eron, errmsg) {
          return Errormsgs += eron + " - " + errmsg + ".";
        });
        $mes.html($().cat("<label style=\"color:red;float:right;\">Failed!!" + Errormsgs + "</label>", 100));
      } else {
        $mes.html("<label style=\"color:green\">" + retnresp.retresp + "</label>");
        setTimeout((function() {
          return $form.hide(2000);
        }), 5000);
      }
      if ($mes.hasClass("bgloadericoleft")) {
        return $mes.removeClass("bgloadericoleft");
      }
    });
    $(".fromiframe").colorbox({
      iframe: true,
      scrolling: false
    });
    $(function() {
      return $(".nav.navbar-nav.navbar-right a[href=\"" + location.href.replace(window.location.protocol + "//" + location.host, "") + "\"]").parent().addClass("active");
    });
    return $("#btn-scrollup").click(function() {
      $("html, body").animate({
        scrollTop: 0
      }, 600);
      return false;
    });
  };
  
function globalswitch(obj) {
	    if (obj.st === "papen" && !(typeof postregistering !== "undefined" && postregistering !== null)) {
	      location.href = obj.rurl;
	    }
	    if (obj.st === "neelog") {
	      $("form[id^='Formsign']").submit(function(event) {
	        var $form, Errorhead, Errortail, eerbrd, erlc, formcontent, formname, posting, redirect_uri, returnb, rspbd, url;
	        event.preventDefault();
	        Errorhead = "<div class=\"alert alert-block alert-danger\"><b>Errors:</b><ul>";
	        Errortail = "</ul></div>";
	        erlc = window.location.href;
	        redirect_uri = erlc.replace("#signin", "").replace("#signup", "");
	        formname = this.id;
	        $form = $(this);
	        formcontent = $("#" + formname).serialize() + "&redirecturi=" + encodeURIComponent(redirect_uri);
	        url = $form.attr("action");
	        $("#dispsignerror").empty();
	        rspbd = $form.parent();
	        eerbrd = $form.find("#dispsignerror");
	        returnb = false;
	        if (formname === "Formsignup") {
	          if ($form.find("input[name=\"password\"]").val().length < 5) {
	            eerbrd.html(Errorhead + "<li>Password should be at least 5 characters.</li>" + Errortail);
	            returnb = true;
	          }
	          if ($form.find("input[name=\"password\"]").val() !== $form.find("input[name=\"repeatPassword\"]").val()) {
	            eerbrd.html(Errorhead + "<li>Passwords do not match.</li>" + Errortail);
	            returnb = true;
	          }
	          if (!$form.find("input[name=\"agree\"]").is(":checked")) {
	            eerbrd.html(Errorhead + "<li>Please accept agreement.</li>" + Errortail);
	            returnb = true;
	          }
	        }
	        if (formname === "Formsignin") {
	          if ($form.find("input[name=\"password\"]").val().length < 5) {
	            eerbrd.html(Errorhead + "<li>Invalid Password.</li>" + Errortail);
	            returnb = true;
	          }
	        }
	        if (returnb === true) {
	          $.colorbox.resize();
	          return;
	        }
	        eerbrd.html('<img src="' + obj.loadimg + '"></img>');
	        $.colorbox.resize();
	        posting = $.post(url, formcontent, "json");
	        posting.done(function(data) {
	          if (data.redirectto) {
	            return window.location.replace(data.redirectto);
	          }
	        });
	        return posting.fail(function(data) {
	          var Errormsgs, reply;
	          ldiv.loadcomplete();
	          Errormsgs = "";
	          reply = $.parseJSON(data.responseText);
	          $.each(reply, function(eron, errmsg) {
	            return Errormsgs += "<li>" + eron + " - " + errmsg + "</li>";
	          });
	          if (typeof Recaptcha !== "undefined" && Recaptcha) {
	            Recaptcha.reload();
	          }
	          eerbrd.html(Errorhead + Errormsgs + Errortail);
	          return $.colorbox.resize();
	        });
	      });
	    }
	    return 0;
	  };