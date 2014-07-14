(function(e,a,g,h,f,c,b,d){var i,el,len;if(!(f=e.jQuery)||g>f.fn.jquery||h(f)){c=a.createElement("script");c.type="text/javascript";c.src="https://ajax.googleapis.com/ajax/libs/jquery/"+g+"/jquery.min.js";c.onload=c.onreadystatechange=function(){if(!b&&(!(d=this.readyState)||d=="loaded"||d=="complete")){h((f=e.jQuery).noConflict(true),b=1);f(c).remove();}};i=0;len=a.documentElement.childNodes.length;while(--len){el=a.documentElement.childNodes[i];if(typeof el.tagName!=='undefined'&&el.tagName.toLowerCase()==='head'){a.documentElement.childNodes[i].appendChild(c);break;}i++;}}})(window,document,"1.8.3",function($,L){
  (function(f){f.fn.drag=function(b,a,d){var e=typeof b=="string"?b:"",k=f.isFunction(b)?b:f.isFunction(a)?a:null;if(e.indexOf("drag")!==0)e="drag"+e;d=(b==k?a:d)||{};return k?this.bind(e,d,k):this.trigger(e)};var i=f.event,h=i.special,c=h.drag={defaults:{which:1,distance:0,not:":input",handle:null,relative:false,drop:true,click:false},datakey:"dragdata",livekey:"livedrag",add:function(b){var a=f.data(this,c.datakey),d=b.data||{};a.related+=1;if(!a.live&&b.selector){a.live=true;i.add(this,"draginit."+ c.livekey,c.delegate)}f.each(c.defaults,function(e){if(d[e]!==undefined)a[e]=d[e]})},remove:function(){f.data(this,c.datakey).related-=1},setup:function(){if(!f.data(this,c.datakey)){var b=f.extend({related:0},c.defaults);f.data(this,c.datakey,b);i.add(this,"mousedown",c.init,b);this.attachEvent&&this.attachEvent("ondragstart",c.dontstart)}},teardown:function(){if(!f.data(this,c.datakey).related){f.removeData(this,c.datakey);i.remove(this,"mousedown",c.init);i.remove(this,"draginit",c.delegate);c.textselect(true); this.detachEvent&&this.detachEvent("ondragstart",c.dontstart)}},init:function(b){var a=b.data,d;if(!(a.which>0&&b.which!=a.which))if(!f(b.target).is(a.not))if(!(a.handle&&!f(b.target).closest(a.handle,b.currentTarget).length)){a.propagates=1;a.interactions=[c.interaction(this,a)];a.target=b.target;a.pageX=b.pageX;a.pageY=b.pageY;a.dragging=null;d=c.hijack(b,"draginit",a);if(a.propagates){if((d=c.flatten(d))&&d.length){a.interactions=[];f.each(d,function(){a.interactions.push(c.interaction(this,a))})}a.propagates= a.interactions.length;a.drop!==false&&h.drop&&h.drop.handler(b,a);c.textselect(false);i.add(document,"mousemove mouseup",c.handler,a);return false}}},interaction:function(b,a){return{drag:b,callback:new c.callback,droppable:[],offset:f(b)[a.relative?"position":"offset"]()||{top:0,left:0}}},handler:function(b){var a=b.data;switch(b.type){case !a.dragging&&"mousemove":if(Math.pow(b.pageX-a.pageX,2)+Math.pow(b.pageY-a.pageY,2)<Math.pow(a.distance,2))break;b.target=a.target;c.hijack(b,"dragstart",a); if(a.propagates)a.dragging=true;case "mousemove":if(a.dragging){c.hijack(b,"drag",a);if(a.propagates){a.drop!==false&&h.drop&&h.drop.handler(b,a);break}b.type="mouseup"}case "mouseup":i.remove(document,"mousemove mouseup",c.handler);if(a.dragging){a.drop!==false&&h.drop&&h.drop.handler(b,a);c.hijack(b,"dragend",a)}c.textselect(true);if(a.click===false&&a.dragging){$.event.triggered=true;setTimeout(function(){$.event.triggered=false},20);a.dragging=false}break}},delegate:function(b){var a= [],d,e=f.data(this,"events")||{};f.each(e.live||[],function(k,j){if(j.preType.indexOf("drag")===0)if(d=f(b.target).closest(j.selector,b.currentTarget)[0]){i.add(d,j.origType+"."+c.livekey,j.origHandler,j.data);f.inArray(d,a)<0&&a.push(d)}});if(!a.length)return false;return f(a).bind("dragend."+c.livekey,function(){i.remove(this,"."+c.livekey)})},hijack:function(b,a,d,e,k){if(d){var j={event:b.originalEvent,type:b.type},n=a.indexOf("drop")?"drag":"drop",l,o=e||0,g,m;e=!isNaN(e)?e:d.interactions.length; b.type=a;b.originalEvent=null;d.results=[];do if(g=d.interactions[o])if(!(a!=="dragend"&&g.cancelled)){m=c.properties(b,d,g);g.results=[];f(k||g[n]||d.droppable).each(function(q,p){l=(m.target=p)?i.handle.call(p,b,m):null;if(l===false){if(n=="drag"){g.cancelled=true;d.propagates-=1}if(a=="drop")g[n][q]=null}else if(a=="dropinit")g.droppable.push(c.element(l)||p);if(a=="dragstart")g.proxy=f(c.element(l)||g.drag)[0];g.results.push(l);delete b.result;if(a!=="dropinit")return l});d.results[o]=c.flatten(g.results); if(a=="dropinit")g.droppable=c.flatten(g.droppable);a=="dragstart"&&!g.cancelled&&m.update()}while(++o<e);b.type=j.type;b.originalEvent=j.event;return c.flatten(d.results)}},properties:function(b,a,d){var e=d.callback;e.drag=d.drag;e.proxy=d.proxy||d.drag;e.startX=a.pageX;e.startY=a.pageY;e.deltaX=b.pageX-a.pageX;e.deltaY=b.pageY-a.pageY;e.originalX=d.offset.left;e.originalY=d.offset.top;e.offsetX=b.pageX-(a.pageX-e.originalX);e.offsetY=b.pageY-(a.pageY-e.originalY);e.drop=c.flatten((d.drop||[]).slice()); e.available=c.flatten((d.droppable||[]).slice());return e},element:function(b){if(b&&(b.jquery||b.nodeType==1))return b},flatten:function(b){return f.map(b,function(a){return a&&a.jquery?f.makeArray(a):a&&a.length?c.flatten(a):a})},textselect:function(b){f(document)[b?"unbind":"bind"]("selectstart",c.dontstart).attr("unselectable",b?"off":"on").css("MozUserSelect",b?"":"none")},dontstart:function(){return false},callback:function(){}};c.callback.prototype={update:function(){h.drop&&this.available.length&& f.each(this.available,function(b){h.drop.locate(this,b)})}};h.draginit=h.dragstart=h.dragend=c})($);

  /*
   * The Bookmarklet. No longer the scavenger.
   */
  var sv_bookmarklet = {
    /*
     * This is the array of images from scanning.
     */
    images: [],

    /*
     * This becomes the image you select.
     */
    image: false,


    /*
     * Start everythign up.
     */
    init: function(key, user_id){
      var self = this;
      /*
       * Setup the instance variables
       */
      this.user_id  = user_id.length ? user_id : SV.USER_ID;
      this.key = key.length ? key : SV.PUBLIC_KEY;
      this.container = $('<div id="sv_bookmarklet">').appendTo(document.body);

      /*
       * Bind a few events
       */
      $('#sv_bookmarklet').on('click', '.sv_close', function(){
        self.close();
      });

      /*
       * Start loading!
       */
      this.scan();
    },

    /*
     * When you select a yellow lined image, we will build your box for you.
     */
    select: function($img){
      this.image = $img[0].src;
      this.openPopup($img);
      this.close();
    },

    /*
     * This looks through all of the images and checks to see if we can
     * grab them. It basically loads the images again and checks their
     * size so that we dont get folled by manually sized 1x1 px.
     */
    scan: function(){
      var $images = $('img'),
          imageCount = $images.length,
          self = this,
          allImagesChecked = function(){
            // Clear the timeout
            clearTimeout(window.sv_scan_timeout);

            if(self.images.length){
              self.addImageBorders();
            } else {
              self.buildBox(function(){
                self.showSorry();
              });
            }
          },
          imageChecked = function(){
            if(!--imageCount){
              allImagesChecked();
            }
          };

      // This is a manual timeout if for some reason some of the images
      // never load.
      window.sv_scan_timeout = setTimeout(function(){
        allImagesChecked();
      }, 5000);

      if(!imageCount){
        allImagesChecked();
      } else {
        $images.each(function(k, v){
          var image = v,
              imageCheck = $('<img>');

          if(v.offsetHeight < 100 || v.offsetWidth < 100){
            imageChecked();
          } else {
            (function(image){
              imageCheck
                .attr('src', image.src)
                .load(function(){
                  var width, height, left, top,
                      overlay = $('<div>');

                  if(this.width >= 100 && this.height >= 100){
                    self.images.push(image);
                  }

                  imageChecked();
                });
            })(image);
          }
        });
      }
    },

    /*
     * This function gives the images that distinct yellow border that is a
     * Svpply trademark. That way, you can easily see what products we can grab.
     */
    addImageBorders: function(){
      var self = this;
      $.each(this.images, function(k, image){
        var $img = $(image),
            offset = $img.offset(),
            overlay = $('<div class="sv_overlay">'),
            insetText = $('<div class="sv_overlay_text">'),
            textIndent = parseInt($img.css('text-indent').split('px')[0], 10) || 0,
            lineHeight = parseInt($img.css('line-height').split('px')[0], 10) || 0;

        insetText.text('Click to Add');

        overlay.css({
          top: offset.top-14,
          left: (offset.left-14)+(-textIndent),
          width: $img.outerWidth(false)+12,
          height: $img.outerHeight(false)+12,
          opacity: 0
        })
        .click(function(e){
          e.stopImmediatePropagation();
          e.preventDefault();
          self.clean.call(this);
          self.select($img);
        })
        .append($img.clone().css({ display: 'none' }))
        .append(insetText)
        .appendTo(self.container)
        .animate({ opacity: 1 }, 1000);
      });
    },

    serialize: function(obj, sep){
      var s = "",
          sep = sep || '&';
      for (var key in obj) {
          if (s != "") s += sep;
          s += key + "=" + obj[key];
      }
      return s;
    },

    get_price: function($img){
      var jQuery = $;

      var currencies = [
            'EUR','EURO','€',
            'GBP','£',
            'JPY','¥',
            'CAD','C$',
            'AUD','A$',
            'USD','$' // this needs to be last
          ],
          cur_sym_to_abbrv_map = {
            '€':'EUR',
            '£':'GBP',
            '¥':'JPY',
            'C$':'CAD',
            'A$':'AUD',
            '$':'USD'
          },
          currency_string = currencies.join('|').replace(/\$/g,'\\$'),
          number_string   = "\\d+(?:(?:\\.|,)\\d\\d)?",
          money_string    = "\\s*?(?:"+currency_string+")?\\s*"+number_string+"\\s*(?:"+currency_string+")?\\s*?",
          currency_regex  = new RegExp(currency_string),
          number_regex    = new RegExp(number_string),
          money_regex     = new RegExp(money_string),
          exclude_tags    = ['SCRIPT','NOSCRIPT','DEL','STRIKE'],
          find_with_regex = function(ancestor, regex_string) {
            var n, text,
                elements = [],
                regex_loose = new RegExp(regex_string, "i"),
                regex_exact = new RegExp("^"+regex_string+"$", "i");

            walk(ancestor);
            return elements;

            function walk(element){
              if( // exclude if:
                !element.childNodes // it has no text
                || exclude_tags.indexOf(element.tagName) != -1 // it's a tag we don't care about
                || element.style.getPropertyValue('text-decoration') == 'line-through' // it a line through the text
                || (window.getComputedStyle(element).width <= 0 && window.getComputedStyle(element).height <= 0) // it's not visible on the page
                ){
                return false;
              } else {
                var text = jQuery(element).text();
                if(!regex_loose.exec(text)){
                  return false;
                } else {
                  var n = element.childNodes.length;
                  if(n === 1 && element.childNodes[0].nodeType === 3){
                    elements.push(element);
                    return true;
                  } else if(regex_exact.exec(text)){
                    elements.push(element);
                    return true;
                  } else {
                    var state = false;
                    for(var i = 0; i < n; i++){
                      var child = element.childNodes[i];
                      if(child.nodeType === 1){
                        state = walk(child) || state ? true : false;
                      }
                    }
                    if(!state){
                      elements.push(element);
                      return true;
                    } else {
                      return state;
                    }
                  }
                }
              }
            }
          },
          find_font_size = function(element){
            var n, size, max = 0;

            return walk(element);

            function walk(element){
              size = parseInt($(element).css('font-size'), 10);
              max = Math.max(size, max);
              if(element.childNodes){
                var n = element.childNodes.length;
                for(var i = 0; i < n; i++){
                  var child = element.childNodes[i];
                  if(child.nodeType === 1){
                    max = walk(child);
                  }
                }
              }
              return max;
            }
          };

      jQuery.expr[':'].contains_numbers = function(obj, index, meta, stack){
        return number_regex.exec(jQuery(obj).text() || '') != null;
      };

      var amount = null,
          currency = null,
          max_amount = 1000,
          body_font_size = parseInt(jQuery('body').css('font-size'), 10),
          node = false,
          distances = [],
          prices = [],
          $prices = [],
          shared_dom_prices = [],
          img_offset = $img.offset(),
          img_center = {top: img_offset.top+$img.height()/2, left: img_offset.left+$img.width()/2};

      // Check for prices that are within a DOM element shared by the img
      node = find_with_regex($img.closest(":contains_numbers"))[0];
      if(node){
        $prices = jQuery(node, money_string);
        $prices.each(function(){
          var text = jQuery.trim(money_regex.exec(jQuery(this).text())[0]);
          shared_dom_prices.push(text);
        });
      }

      // Weighting - add points for the following:
      node = window.document.body;
      $prices = jQuery(find_with_regex(node, money_string));
      $prices.each(function(){
        var $this     = jQuery(this),
            weight    = 0,
            font_size = find_font_size($this[0]),
            offset    = $this.offset(),
            center    = {top: offset.top+$this.height()/2, left: offset.left+$this.width()/2},
            diff_top  = img_center.top - center.top,
            diff_left = img_center.left - center.left,
            distance  = Math.sqrt(diff_top*diff_top+diff_left*diff_left),
            text      = jQuery.trim(money_regex.exec($this.text().toUpperCase())[0]);

        // Is near the image
        if(shared_dom_prices.indexOf(text) > -1) weight+=6;
        // Is less than the max amount
        if(number_regex.exec(text) < max_amount) weight++;
        // Has a currency unit
        if(currency_regex.exec(text)) weight+=200;
        // Has a . or ,
        if(/(\.|,)\d\d/.exec(text)) weight++;
        // Is bold
        if($this.css('font-weight') == 'bold') weight++;
        // Has strike through
        if($this.css('text-decoration') == 'line-through') weight-=20;
        // Has a larger font
        weight += (font_size - body_font_size) * 20;
        // Is near the image
        weight += Math.round(100 * (1000 - Math.min(distance, 1000))/1000);

        prices.push({
          el: this,
          text: text,
          weight: weight
        });
      });
      prices.sort(function(a,b){ return b.weight-a.weight; });

      if(prices[0]) amount = number_regex.exec(prices[0].text);

      if(amount){
        var i=0, cur;
        amount = amount[0];
        while(!currency){
          cur = currencies[i];
          if(prices[0].text.toUpperCase().substr(0, cur.length).toUpperCase() == cur || prices[0].text.substr(cur.length*-1).toUpperCase() == cur || i == currencies.length-1){
            currency = cur_sym_to_abbrv_map[cur] || cur;
          }
          i++;
        }
      }

      // Debug code:
      // jQuery(".sv_weight").remove();
      // for(var i = 0; i < prices.length; i++){
      //   var offset = jQuery(prices[i].el).offset(),
      //       $el = jQuery("<div>").text(
      //           // prices[i].weight
      //           find_font_size(prices[i].el)
      //         ).addClass("sv_weight").css({
      //         background:"black",
      //         color:"yellow",
      //         position:"absolute",
      //         top: offset.top,
      //         'z-index': 99999,
      //         padding: 5
      //       });

      //   jQuery('body').append($el);
      //   $el.css({left: offset.left-$el.outerWidth()-5});
      // }

      return({
        amount: amount,
        currency: currency
      });

    },

    openPopup: function($img){
      var price, params, window_params, url;

      price = this.get_price($img);

      params = {
        key: this.key,
        image: encodeURIComponent(this.image),
        price_value: price.amount? parseFloat(price.amount).toFixed(2) : '',
        price_currency: price.currency,
        title: encodeURIComponent(document.title),
        url: encodeURIComponent(location.href)
      };
      url = 'https://svpply.com/b/create?' + this.serialize(params);

      window_params = {
         width: 550,
         height: 415,
         scrollbars: 'no',
         toolbar: 'no',
         location: 'no',
         directories: 'no',
         status: 'no',
         menubar: 'no',
         resizable: 'no'
      };
      window_params['left'] = window.screenLeft + (window.innerWidth - window_params.width) * .5;
      window_params['top'] = window.screenTop + (window.innerHeight - window_params.height) * .5;
      window_params = this.serialize(window_params, ',');

      try {
        window.open(url, '_blank', window_params);
      } catch(err) {
        window.showModalDialog(url, '_blank', window_params);
      }
    },

    /*
     * This builds what many consider "the bookmarklet". They don't consider the
     * image scanning that we do to avoid grabbing small images, etc etc.
     */
    buildBox: function(fn){

      var $selectionBox = $('<div class="sv_select_box">'),
          $selectionContent = $('<div class="sv_select_content">'),
          $handle = $('<div class="sv_drag_handle">'),
          $logo = $('<img>', { src: 'https://svpply.com/assets/image/svpply_white.png' }),
          $close = $('<img>', { src: 'https://svpply.com/assets/image/bookmarklet_close.png', 'class': 'sv_close' }),
          scrollTop = $(document.body).scrollTop(),
          self = this,
          fn = fn || function(){};

      // Position the black box in the middle of the screen
      $selectionBox.css({
        top: ($(window).height()/2)+scrollTop-220,
        left: ($(window).width()/2)-275,
        opacity: 0
      }).append($selectionContent);

      // Build the initial selection box, which is the black box
      $selectionBox
        .append($handle, $close, $selectionContent)
        .appendTo(this.container)
        .animate({ opacity: 1 });

      $selectionContent.append($logo);

      // Make the handle dragable.
      $selectionBox.drag(function(ev, dd){
        $(this).css({
           top: dd.offsetY,
           left: dd.offsetX
        });
      })
      .drag("start", function(){
        $(this).animate({ opacity: .8 });
      })
      .drag("end", function(){
        $(this).animate({ opacity: 1 });
      });

      fn();
    },

    showSorry: function(msg1, msg2){
      if(!msg1){
          var msg1 = 'We had trouble finding any images large enough to save.',
              msg2 = 'Try visiting a page with larger product images or email help@svpply.com if you continue to have trouble.';
      }
      var $selectionContent = $('.sv_select_content', this.container),
          $message = $('<div class="sv_message">')
            .html('<span class="sv_message_yellow">' + msg1 + '</span>' +
                  '<span class="sv_message_grey">' + msg2 + '</span>').css({margin: 5});

      $('#sv_bookmarklet_loader', document.body).animate({ opacity: 0 }, function(){
        $(this).remove();
      });

      $selectionContent.append($message);
    },

    showError: function(){
      var $selectionContent = $('.sv_select_content', this.container),
          $message = $('<div class="sv_message">')
            .html('<span class="sv_message_yellow sv_large">Uh oh. You\'ve found a problem. Would you mind reporting it?.</span>' +
                  '<span class="sv_message_grey">Try reloading the page or email help@svpply.com if you continue to have trouble adding this product.');

      $('#sv_bookmarklet_loader', document.body).animate({ opacity: 0 }, function(){
        $(this).remove();
      });

      $selectionContent.append($message);
    },

    close: function(){
      window.sv_bookmarklet_running = false;

      $($('#sv_bookmarklet')).off('click', '.sv_category_values');
      $($('#sv_bookmarklet')).off('click', '.sv_close');

      this.container.animate({ opacity: 0 }, function(){
        $(this).remove();
      });
      $('#sv_bookmarklet_loader').animate({ opacity: 0 }, function(){
        $(this).remove();
      });
    },

    clean: function(fn){
      var fn = fn || function() {},
          self = this;

      $('.sv_overlay', self.container)
        .not(this)
        .animate({ opacity: 'hide' }, function(){
          var self = this;
          // We set a timeout because sometimes the render jitters
          // if we try and delete right when the animation is finished.
          setTimeout(function(){ $(self).remove(); fn(); }, 1000);
        });
    },

    remove: function(){
      this.container.remove();
    }

  };

  /*
   * If the bookmarklet has already been started (aka, someone double clicks
   * the bookmarklet button, then we don't want to start another scan process.
   */
  if(typeof window.sv_bookmarklet_running === 'undefined' || window.sv_bookmarklet_running === false){
    var $sv = $('<img src="https://svpply.com/assets/image/svpply_icon_large.jpg">').css({ width: 60 }),
        $exit = $('<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAC8AAAAvCAMAAABE+WOeAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAGBQTFRF////AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAhlxAAAAFlJREFUeNrslLENACAMw/wJ/3/JDKqgnmAIW1MLRWmB4Q7hw7/jsYCtN4G7f4y9VaKXD41eIdLPn2OnlHHzlbq8X/qX+cj85Xzl/sj9lDX+Aea/Cv8PPwUYADZZBxQtE/3DAAAAAElFTkSuQmCC">').css({ width: 60 });
    // Set the window variables so we know if we need to load stuff again
    window.sv_bookmarklet_running = true;

    // Show the "loading" icon, so people know the bookmarklet is working
    $('<div id="sv_bookmarklet_loader">')
      .append($sv)
      .css({
        opacity: 0
      }).appendTo('body').animate({ opacity: 1 }, 500)
      .hover(function(){
        $($sv, $(this)).replaceWith($exit);
      }, function(){
        $($exit, $(this)).replaceWith($sv);
      })
      .click(function(){
        $(this).animate({ opacity: 0 }, function(){
          window.sv_bookmarklet_running = false;
          $(this).remove();
        });
        sv_bookmarklet.clean();
        sv_bookmarklet.remove();
      });


    /*
     * If we haven't run the bookmarklet on this page, then load our
     * external style sheets.
     */
    if(!window.sv_bookmarklet_ran){
      var css_href = 'https://svpply.com/assets/css/bookmarklet_v2.css?v=2'
      if(document.createStyleSheet){
        document.createStyleSheet(css_href);
      } else {
        $('<link>', {
          href: css_href,
          rel: 'stylesheet',
          type: 'text/css'
        }).appendTo('head');
      }
      window.sv_bookmarklet_ran = true;
    }

    // check if this domain is blacklisted
    if(window.location.protocol == 'https:'){
      if(typeof sv_bookmarklet.blacklisted == 'undefined'){
        $.ajax({
          url: '//svpply.com/bookmarklet/check_blacklist/' + window.location.host,
          method: 'GET',
          dataType: 'jsonp',
          success: function(d){
            sv_bookmarklet.blacklisted = d.blacklisted;
            if(d.blacklisted == true){
                sv_bookmarklet.buildBox(function(){
                  sv_bookmarklet.showSorry("We're sorry", "You can't post to Svpply from " + window.location.host + " because they don't sell any products. If you've found a product you admire, please post it from the retailer's website so other people can easily find it . For more information, visit our <a href='//svpply.com/guidelines' style='color: white;' >community guidelines</a>.</p>");
                });
               sv_bookmarklet.clean();
            }
          }
        });
      } else if (sv_bookmarklet.blacklisted == true){
          sv_bookmarklet.buildBox(function(){
            sv_bookmarklet.showSorry("We're sorry", "You can't post to Svpply from " + window.location.host + " because they don't sell any products. If you've found a product you admire, please post it from the retailer's website so other people can easily find it . For more information, visit our <a href='//svpply.com/guidelines' style='color: white;' >community guidelines</a>.</p>");
          });
         sv_bookmarklet.clean();
      }
    }

    sv_bookmarklet.init('svpk_f37aa4740465ca757fd86167d134017f', '531157');
    sv_bookmarklet.api_loaded = true;

  }
});
