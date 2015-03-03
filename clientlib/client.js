/**
 * Created by giko on 3/2/15.
 */
$('body').append('<div id="countdown-wrapper" style="position: absolute;left:1px;bottom:10px;z-index: 1000;"> <h1>Перезагрузка через</h1> <div class="countdown-clock"></div> </div>');
$('body').append('<link type="text/css" rel="stylesheet" href="https://rpc.ventra.ru/static/sweetalert.css">');
$('body').append('<link type="text/css" rel="stylesheet" href="https://rpc.ventra.ru/static/flipclock.css">');

/** @const */
var clock = $('.countdown-clock').FlipClock({
    countdown:true,
    clockFace: 'MinuteCounter',
    callbacks: {stop: function(){
        $('#countdown-wrapper').hide();
        swal({title:'Сервер будет перезагружен', text: 'Просьба сохранить все рабочие данные',  type: 'error'})
    }}
});
$('#countdown-wrapper').hide();
var mouseX = 0;
var mouseY = 0;
oldMouseMove = document.onmousemove;

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
});
socket.on('reload', function () {
    location.reload();
});
socket.on('countdown', function (data) {
    clock.setTime(data);
    clock.start();
    $('#countdown-wrapper').show();
    swal({title:'Сервер будет перезагружен', text: 'Просьба сохранить все рабочие данные',  type: 'warning', timer: 5000})
});
/** @const */
var broadcaster = TimersJS.repeater(300, function (delta) {
    $("input").each(function () {
        $(this).attr("value", $(this).val());
    });
    $("*").each(function () {
        $(this).attr("scrollTop", $(this).scrollTop());
        $(this).attr("scrollLeft", $(this).scrollLeft());
        $(this).attr("absWidth", $(this).width());
        $(this).attr("absHeight", $(this).height());
    });
    socket.emit('screendata', document.documentElement.outerHTML);
});
broadcaster.cancel();

socket.on('startbroadcast', function () {
    broadcaster.restart();
});

socket.on('stopbroadcast', function () {
    broadcaster.cancel();
});

window.setInterval(function () {
    if (remoterpc.properties['project'] === 'hrb') {
        remoterpc.properties['username'] = $('div.aboxx span.leafnormaltext').text();
    }

    if (remoterpc.properties['project'] === 'hrb52') {
        remoterpc.properties['username'] = $('div.leaf_toolbar div.leaf_text span.rcoveredtext').text();
    }
    
    socket.emit('userinfo', {
        x: mouseX,
        y: mouseY,
        location: base,
        isActive: isActive,
        name: remoterpc.properties['username']
    });
}, 300);