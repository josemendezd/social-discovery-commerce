#
#    BigVideo - The jQuery Plugin for Big Background Video (and Images)
#	by John Polacek (@johnpolacek)
#	
#	Dual licensed under MIT and GPL.
#
#    Dependencies: jQuery, jQuery UI (Slider), Video.js, ImagesLoaded
#
(($) ->
  $.BigVideo = (options) ->
    
    # If you want to use a single mp4 source, set as true
    
    # If you are doing a playlist, the video won't play the first time
    # on a touchscreen unless the play event is attached to a user click
    updateSize = ->
      windowW = $(window).width()
      windowH = $(window).height()
      windowAspect = windowW / windowH
      if windowAspect < mediaAspect
        
        # taller
        if currMediaType is "video"
          player.width(windowH * mediaAspect).height windowH
          $(vidEl).css("top", 0).css("left", -(windowH * mediaAspect - windowW) / 2).css "height", windowH
          $(vidEl + "_html5_api").css "width", windowH * mediaAspect
          $(vidEl + "_flash_api").css("width", windowH * mediaAspect).css "height", windowH
        else
          
          # is image
          $("#big-video-image").css
            width: "auto"
            height: windowH
            top: 0
            left: -(windowH * mediaAspect - windowW) / 2

      else
        
        # wider
        if currMediaType is "video"
          player.width(windowW).height windowW / mediaAspect
          $(vidEl).css("top", -(windowW / mediaAspect - windowH) / 2).css("left", 0).css "height", windowW / mediaAspect
          $(vidEl + "_html5_api").css "width", "100%"
          $(vidEl + "_flash_api").css("width", windowW).css "height", windowW / mediaAspect
        else
          
          # is image
          $("#big-video-image").css
            width: windowW
            height: "auto"
            top: -(windowW / mediaAspect - windowH) / 2
            left: 0

    initPlayControl = ->
      
      # create video controller
      markup = "<div id=\"big-video-control-container\">"
      markup += "<div id=\"big-video-control\">"
      markup += "<a href=\"#\" id=\"big-video-control-play\"></a>"
      markup += "<div id=\"big-video-control-middle\">"
      markup += "<div id=\"big-video-control-bar\">"
      markup += "<div id=\"big-video-control-bound-left\"></div>"
      markup += "<div id=\"big-video-control-progress\"></div>"
      markup += "<div id=\"big-video-control-track\"></div>"
      markup += "<div id=\"big-video-control-bound-right\"></div>"
      markup += "</div>"
      markup += "</div>"
      markup += "<div id=\"big-video-control-timer\"></div>"
      markup += "</div>"
      markup += "</div>"
      settings.container.append markup
      
      # hide until playVideo
      $("#big-video-control-container").css "display", "none"
      
      # add events
      $("#big-video-control-track").slider
        animate: true
        step: 0.01
        slide: (e, ui) ->
          isSeeking = true
          $("#big-video-control-progress").css "width", (ui.value - 0.16) + "%"
          player.currentTime (ui.value / 100) * player.duration()

        stop: (e, ui) ->
          isSeeking = false
          player.currentTime (ui.value / 100) * player.duration()

      $("#big-video-control-bar").click (e) ->
        player.currentTime (e.offsetX / $(this).width()) * player.duration()

      $("#big-video-control-play").click (e) ->
        e.preventDefault()
        playControl "toggle"

      player.on "timeupdate", ->
        if not isSeeking and (player.currentTime() / player.duration())
          currTime = player.currentTime()
          minutes = Math.floor(currTime / 60)
          seconds = Math.floor(currTime) - (60 * minutes)
          seconds = "0" + seconds  if seconds < 10
          progress = player.currentTime() / player.duration() * 100
          $("#big-video-control-track").slider "value", progress
          $("#big-video-control-progress").css "width", (progress - 0.16) + "%"
          $("#big-video-control-timer").text minutes + ":" + seconds + "/" + vidDur

    playControl = (a) ->
      action = a or "toggle"
      action = (if isPlaying then "pause" else "play")  if action is "toggle"
      if action is "pause"
        player.pause()
        $("#big-video-control-play").css "background-position", "-16px"
        isPlaying = false
      else if action is "play"
        player.play()
        $("#big-video-control-play").css "background-position", "0"
        isPlaying = true
    setUpAutoPlay = ->
      player.play()
      settings.container.off "click", setUpAutoPlay
    nextMedia = ->
      currMediaIndex++
      currMediaIndex = 0  if currMediaIndex is playlist.length
      playVideo playlist[currMediaIndex]
    playVideo = (source) ->
      
      # clear image
      $(vidEl).css "display", "block"
      currMediaType = "video"
      player.src source
      isPlaying = true
      if isAmbient
        $("#big-video-control-container").css "display", "none"
        player.ready ->
          player.volume 0

        doLoop = true
      else
        $("#big-video-control-container").css "display", "block"
        player.ready ->
          player.volume defaultVolume

        doLoop = false
      $("#big-video-image").css "display", "none"
      $(vidEl).css "display", "block"
    showPoster = (source) ->
      
      # remove old image
      $("#big-video-image").remove()
      
      # hide video
      player.pause()
      $(vidEl).css "display", "none"
      $("#big-video-control-container").css "display", "none"
      
      # show image
      currMediaType = "image"
      bgImage = $("<img id=\"big-video-image\" src=" + source + " />")
      wrap.append bgImage
      $("#big-video-image").imagesLoaded ->
        mediaAspect = $("#big-video-image").width() / $("#big-video-image").height()
        updateSize()

    defaults =
      useFlashForFirefox: true
      forceAutoplay: false
      controls: true
      doLoop: false
      container: $("body")

    BigVideo = this
    player = undefined
    vidEl = "#big-video-vid"
    wrap = $("<div id=\"big-video-wrap\"></div>")
    video = $("")
    mediaAspect = 16 / 9
    vidDur = 0
    defaultVolume = 0.8
    isInitialized = false
    isSeeking = false
    isPlaying = false
    isQueued = false
    isAmbient = false
    playlist = []
    currMediaIndex = undefined
    currMediaType = undefined
    settings = $.extend({}, defaults, options)
    BigVideo.init = ->
      unless isInitialized
        
        # create player
        settings.container.prepend wrap
        autoPlayString = (if settings.forceAutoplay then "autoplay" else "")
        player = $("<video id=\"" + vidEl.substr(1) + "\" class=\"video-js vjs-default-skin\" preload=\"auto\" data-setup=\"{}\" " + autoPlayString + " webkit-playsinline></video>")
        player.css "position", "absolute"
        wrap.append player
        videoTechOrder = ["html5", "flash"]
        
        # If only using mp4s and on firefox, use flash fallback
        ua = navigator.userAgent.toLowerCase()
        isFirefox = ua.indexOf("firefox") isnt -1
        videoTechOrder = ["flash", "html5"]  if settings.useFlashForFirefox and (isFirefox)
        player = videojs(vidEl.substr(1),
          controls: false
          autoplay: true
          preload: "auto"
          techOrder: videoTechOrder
        )
        
        # add controls
        initPlayControl()  if settings.controls
        
        # set initial state
        updateSize()
        isInitialized = true
        isPlaying = false
        $("body").on "click", setUpAutoPlay  if settings.forceAutoplay
        $("#big-video-vid_flash_api").attr("scale", "noborder").attr("width", "100%").attr "height", "100%"
        
        # set events
        $(window).resize ->
          updateSize()

        player.on "loadedmetadata", (data) ->
          if document.getElementById("big-video-vid_flash_api")
            
            # use flash callback to get mediaAspect ratio
            mediaAspect = document.getElementById("big-video-vid_flash_api").vjs_getProperty("videoWidth") / document.getElementById("big-video-vid_flash_api").vjs_getProperty("videoHeight")
          else
            
            # use html5 player to get mediaAspect
            mediaAspect = $("#big-video-vid_html5_api").prop("videoWidth") / $("#big-video-vid_html5_api").prop("videoHeight")
          updateSize()
          dur = Math.round(player.duration())
          durMinutes = Math.floor(dur / 60)
          durSeconds = dur - durMinutes * 60
          durSeconds = "0" + durSeconds  if durSeconds < 10
          vidDur = durMinutes + ":" + durSeconds

        player.on "ended", ->
          if settings.doLoop
            player.currentTime 0
            player.play()
          nextMedia()  if isQueued


    BigVideo.show = (source, options) ->
      options = {}  if options is `undefined`
      isAmbient = options.ambient is true
      settings.doLoop = true  if isAmbient or options.doLoop
      if typeof (source) is "string"
        ext = source.substring(source.lastIndexOf(".") + 1)
        if ext is "jpg" or ext is "gif" or ext is "png"
          showPoster source
        else
          source = options.altSource  if options.altSource and navigator.userAgent.toLowerCase().indexOf("firefox") > -1
          playVideo source
          isQueued = false
      else
        playlist = source
        currMediaIndex = 0
        playVideo playlist[currMediaIndex]
        isQueued = true

    
    # Expose Video.js player
    BigVideo.getPlayer = ->
      player

    
    # Expose BigVideoJS player actions (like 'play', 'pause' and so on)
    BigVideo.triggerPlayer = (action) ->
      playControl action
) jQuery