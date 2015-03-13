"use strict";
/**
 * Created by giko on 3/2/15.
 */
$('body').append('<div id="countdown-wrapper" style="position: absolute;left:1px;bottom:10px;z-index: 1000;"> <h1>Перезагрузка через</h1> <div class="countdown-clock"></div> </div>');
$('body').append('<link type="text/css" rel="stylesheet" href="https://rpc.ventra.ru/static/sweetalert.css">');
$('body').append('<link type="text/css" rel="stylesheet" href="https://rpc.ventra.ru/static/flipclock.css">');
/** @const */
var clock = $('.countdown-clock').FlipClock({
    countdown: true,
    clockFace: 'MinuteCounter',
    callbacks: {
        stop: function () {
            $('#countdown-wrapper').hide();
            swal({title: 'Сервер будет перезагружен', text: 'Просьба сохранить все рабочие данные', type: 'error'})
        }
    }
});
$('#countdown-wrapper').hide();
var mouseX = 0;
var mouseY = 0;
var oldMouseMove = document.onmousemove;

function readMouseMove(e) {
    mouseX = e.clientX;
    mouseY = e.clientY;
    //oldMouseMove(e);
}
document.onmousemove = readMouseMove;

var isActive = true;

window.onfocus = function () {
    isActive = true;
};

window.onblur = function () {
    isActive = false;
};

/** @const */
var base = window.location.href.substr(0, window.location.href.lastIndexOf('/'));
/** @const */
var socket = io('https://rpc.ventra.ru/');
socket.on('message', function (data) {
    swal({title: data.title, text: data.message, type: data.type});
});
socket.on('connect', function () {
    socket.emit('client');
    infoBroadcaster.restart();
});
socket.on('disconnect', function () {
    broadcaster.cancel();
    infoBroadcaster.cancel();
});
socket.on('reload', function () {
    location.reload();
});
socket.on('pingDom', function (time) {
    updateAttributes(document.body);
    socket.emit('pongDom', {html: document.documentElement.outerHTML, time: time});
});
socket.on('countdown', function (data) {
    clock.setTime(data);
    clock.start();
    $('#countdown-wrapper').show();
    swal({
        title: 'Сервер будет перезагружен',
        text: 'Просьба сохранить все рабочие данные',
        type: 'warning',
        timer: 5000
    })
});

var MutationObserver = (function () {
    var prefixes = ['WebKit', 'Moz', 'O', 'Ms', ''];
    for (var i = 0; i < prefixes.length; i++) {
        if (prefixes[i] + 'MutationObserver' in window) {
            return window[prefixes[i] + 'MutationObserver'];
        }
    }
    return false;
}());

// Create an observer instance
var observer;

if (MutationObserver) {
    observer = new MutationObserver(function (mutations) {
        mutations.forEach(function (mutation) {
            var newNodes = mutation.addedNodes; // DOM NodeList
            for (var index = 0; index < newNodes.length; ++index) {
                var newNode = newNodes[index];
                if (newNode.nodeType === 1) {
                    newNode.addEventListener("scroll", function (event) {
                        var node = event.target;
                        node.setAttribute("scrollTop", node.scrollTop);
                        node.setAttribute("scrollLeft", node.scrollLeft);
                    });
                    newNode.addEventListener("change", function (event) {
                        var node = event.target;
                        node.setAttribute("value", node.value);
                    });
                    updateAttributes(newNode);
                }
            }
        });
    });
    // Configuration of the observer:
    var config = {
        attributes: true,
        childList: true,
        characterData: true,
        subtree: true
    };
}


function updateAttributes(node) {
    for (var index = 0; index < node.childNodes.length; ++index) {
        if (node.childNodes[index].nodeType === 1) {
            updateAttributes(node.childNodes[index]);
        }
    }

    node.setAttribute("scrollTop", node.scrollTop);
    node.setAttribute("scrollLeft", node.scrollLeft);
    node.setAttribute("absWidth", node.scrollWidth);
    node.setAttribute("absHeight", node.scrollHeight);
    node.setAttribute("value", node.value);
}
window.onresize = function () {
    updateAttributes(document.body);
};
/** @const */
var broadcaster = TimersJS.repeater(800, function (delta) {
    if (!MutationObserver) {
        updateAttributes(document.body);
    }

    socket.emit('screendata', document.documentElement.outerHTML);
});
broadcaster.cancel();

var infoBroadcaster = TimersJS.repeater(400, function () {
    if (remoterpc.properties['username'] === '' || remoterpc.properties['username'] === 'unnamed') {
        if (remoterpc.properties['project'] === 'hrb') {
            remoterpc.properties['username'] = $('div.aboxx span.leafnormaltext').text();
        }

        if (remoterpc.properties['project'] === 'hrb52') {
            remoterpc.properties['username'] = $('div.leaf_toolbar.leaf_headbar div.leaf_text span.rcoveredtext, #\\.\\.Button\\.\\.gotoMyData > span').text();
        }
    }

    socket.emit('userinfo', {
        x: mouseX,
        y: mouseY,
        location: base,
        isActive: isActive,
        name: remoterpc.properties['username']
    });
});
infoBroadcaster.cancel();

socket.on('startbroadcast', function () {
    if (false) {
        updateAttributes(document.body);
        observer.observe(document.body, config);
    }
    ;
    updateAttributes(document.body);

    broadcaster.restart();
});

socket.on('stopbroadcast', function () {
    broadcaster.cancel();
});
