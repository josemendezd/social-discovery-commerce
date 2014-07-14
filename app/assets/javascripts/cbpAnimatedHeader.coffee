###
cbpAnimatedHeader.js v1.0.0
http://www.codrops.com

Licensed under the MIT license.
http://www.opensource.org/licenses/mit-license.php

Copyright 2013, Codrops
http://www.codrops.com
###
$(document).ready ->
  try
    cbpAnimatedHeader = (->
      init = ->
        window.addEventListener "scroll", ((event) ->
          unless didScroll
            didScroll = true
            setTimeout scrollPage, 250
        ), false
      scrollPage = ->
        sy = scrollY()
        if sy >= changeHeaderOn
          classie.add header, "cbp-af-header-shrink"
        else
          classie.remove header, "cbp-af-header-shrink"
        didScroll = false
      scrollY = ->
        window.pageYOffset or docElem.scrollTop
      docElem = document.documentElement
      header = document.querySelector(".navbar")
      didScroll = false
      changeHeaderOn = 100
      init()
    )()
