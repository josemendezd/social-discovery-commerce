globalfunctions = ->
  $.ajaxSetup cache: false
  $("html, body").animate
    scrollTop: 0
  , 10
  $.each $("form"), (i, f) ->
    f.reset()

  $("#footerfeedbackform").find(".loaderspan").hide()
  $("#footerfeedbackform").submit (event) ->
    event.preventDefault()
    formname = @id
    $form = $(this)
    $formcontent = $form.serialize()
    $url = $form.attr("action")
    $mes = $form.find(".loaderspan")
    #$captcha = $form.find("#recaptcha_challenge")
    #if $captcha
    # if $captcha.children().length <= 0
    #  $captcha.html(bl_recaptcha_html)
    #  false
    $mes.show()
    $mes.addClass "bgloadericoleft"  unless $mes.hasClass("bgloadericoleft")
    $mes.html "<label>loading.....</label>"
    retnresp = $().setresponse(
      ispost: true
      url: $url
      argdata: $formcontent
      failans: false
      failreq: true
      failnode: "responseText"
    )
    if retnresp.Issuccess is false
      reply = $.parseJSON(retnresp.retresp)
      Errormsgs = ""
      $.each reply, (eron, errmsg) ->
        Errormsgs += eron + " - " + errmsg + "."

      $mes.html $().cat("<label style=\"color:red;float:right;\">Failed!!" + Errormsgs + "</label>", 100)
    else
      $mes.html "<label style=\"color:green\">" + retnresp.retresp + "</label>"
      setTimeout (->
        $form.hide 2000
      ), 5000
    $mes.removeClass "bgloadericoleft"  if $mes.hasClass("bgloadericoleft")

  $(".fromiframe").colorbox
    iframe: true
    scrolling: false

  $ ->
    $(".nav.navbar-nav.navbar-right a[href=\"" + location.href.replace(window.location.protocol + "//" + location.host, "") + "\"]").parent().addClass "active"

  
  #$(function(){$('.navigationtabs a[href="/' + location.href.replace(window.location.protocol+"//","").split("/")[1] + '"]').parent().addClass('active');});
  $("#btn-scrollup").click ->
    $("html, body").animate
      scrollTop: 0
    , 600
    false




globalswitch = (obj) ->
  location.href = obj.rurl  if obj.st is "papen" and !postregistering?
  if obj.st is "neelog"
    $("form[id^='Formsign']").submit (event) ->
      event.preventDefault()
      Errorhead = "<div class=\"alert alert-block alert-danger\"><b>Errors:</b><ul>"
      Errortail = "</ul></div>"
      erlc = window.location.href
      redirect_uri = erlc.replace("#signin", "").replace("#signup", "")
      formname = @id
      $form = $(this)
      formcontent = $("#" + formname).serialize() + "&redirecturi=" + encodeURIComponent(redirect_uri)
      url = $form.attr("action")
      $("#dispsignerror").empty()
      rspbd = $form.parent()
      eerbrd = $form.find("#dispsignerror")
      returnb = false
      
      #alert(formname+rspbd.id+eerbrd.id);
      if formname is "Formsignup"
        if $form.find("input[name=\"password\"]").val().length < 5
          eerbrd.html Errorhead + "<li>Password should be at least 5 characters.</li>" + Errortail
          returnb = true
        if $form.find("input[name=\"password\"]").val() isnt $form.find("input[name=\"repeatPassword\"]").val()
          eerbrd.html Errorhead + "<li>Passwords do not match.</li>" + Errortail
          returnb = true
        unless $form.find("input[name=\"agree\"]").is(":checked")
          eerbrd.html Errorhead + "<li>Please accept agreement.</li>" + Errortail
          returnb = true
      if formname is "Formsignin"
        if $form.find("input[name=\"password\"]").val().length < 5
          eerbrd.html Errorhead + "<li>Invalid Password.</li>" + Errortail
          returnb = true
      if returnb is true
        $.colorbox.resize()
        return
      #ldiv.display()
      eerbrd.html '<img src="'+obj.loadimg+'"></img>'
      $.colorbox.resize()
      posting = $.post(url, formcontent, "json")
      posting.done (data) ->
        #ldiv.loadcomplete()
        
        # data.redirect contains the string URL to redirect to
        window.location.replace data.redirectto  if data.redirectto

      
      #var erlc=window.location.href;
      #window.location.replace(erlc.replace('#signin','').replace('#signup',''));
      posting.fail (data) ->
        ldiv.loadcomplete()
        Errormsgs = ""
        reply = $.parseJSON(data.responseText)
        $.each reply, (eron, errmsg) ->
          Errormsgs += "<li>" + eron + " - " + errmsg + "</li>"

        Recaptcha.reload()  if typeof Recaptcha isnt "undefined" and Recaptcha
        eerbrd.html Errorhead + Errormsgs + Errortail
        $.colorbox.resize()


  0
