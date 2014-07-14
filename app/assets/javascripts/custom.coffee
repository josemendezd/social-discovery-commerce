
#Boozology Extensions by Kislay
(($) ->
  $.fn.cat = (e, t) ->
    t = (if typeof t isnt "undefined" then t else 14)
    if e.length > t
      e.substring(0, t - 1) + "..."
    else
      e

  $.fn.askforloginorsignup = ->
    $("#signinupModel").modal show: true

  $.fn.setresponse = (e) ->
    
    #params-> ispost(false|optional),failreq(false|optional),issync(true|optional)
    #,failans(false|optional),argdata(""|optional),successnode("answer"|optional),failnode("answer"|optional)
    t = this
    t.Issuccess = true
    t.ispost = (if typeof e.ispost isnt "undefined" then e.ispost else false)
    t.failreq = (if typeof e.failreq isnt "undefined" then e.failreq else false)
    t.issync = (if typeof e.issync isnt "undefined" then e.issync else true)
    t.retresp = (if typeof e.failans isnt "undefined" then e.failans else false)
    t.argdata = (if typeof e.argdata isnt "undefined" then e.argdata else "")
    $.ajaxSetup async: false  if t.issync
    n = undefined
    if t.ispost
      n = $.post(e.url, t.argdata, "json")
    else
      n = $.getJSON(e.url, t.argdata)
    n.done (n) ->
      t.retresp = n[(if typeof e.successnode isnt "undefined" then e.successnode else "answer")]

    n.fail (n) ->
      t.Issuccess = false
      t.retresp = n[(if typeof e.failnode isnt "undefined" then e.failnode else "answer")]  if t.failreq

    $.ajaxSetup async: true  if t.issync
    if t.failreq
      t
    else
      t.retresp

  $.fn.apld = ($trg) ->
    $trg.prepend "<img style=\"margin-right:5px;\" src=\"/assets/img/sl.gif\"/>"  if $trg.has("img").length is 0

  $.fn.msgbox = (c, t) ->
    p = (if typeof t isnt "undefined" then true else false)
    $.colorbox
      opacity: 0
      html: c

    if p
      setTimeout (->
        $.colorbox.close()
      ), t

  $.fn.cboxresize = (w, h) ->
    if typeof w isnt "undefined" and typeof h isnt "undefined"
      $.colorbox.resize
        innerWidth: w
        innerHeight: h

    else
      $.colorbox.resize()

  $.fn.getdate = (date) ->
    d = new Date(date.replace(" ", "T"))
    ndate =
      date: d.getDate() + "-" + (d.getMonth() + 1) + "-" + d.getFullYear()
      time: d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds()

    ndate

  $.fn.shortnumber = (number) ->
    number
) jQuery
loaderdiv = (e, t) ->
  r = ->
    n.divbox.hide()
    n.state = false
    n.busy = false
    n.loadertext.text "Loading..."
  i = ->
    n.busy = false
    setTimeout (->
      if not n.busy and n.state isnt false
        n.loadertext.text "Load Time Over..."
        setTimeout (->
          n.switchoff()
        ), 3e3
      else
        if n.state is true
          n.watchstate()
        else
    ), 1e4
  s = ->
    n.loadertext.text "Loading..."
    n.divbox.show()
    n.state = true
    n.busy = false
    n.watchstate()
  o = ->
    n.loadertext.text "Almost Loaded"
    setTimeout (->
      n.switchoff()
    ), 500
  u = ->
    n.divbox.hide()
    n.state = false
    n.busy = false
  a = ->
    n.busy = true
  n = this
  n.divbox = $(e)
  n.loadertext = $(t)
  n.state = true
  n.busy = true
  n.initdiv = r
  n.display = s
  n.switchoff = u
  n.stillbusy = a
  n.watchstate = i
  n.loadcomplete = o

#loaderdiv complete