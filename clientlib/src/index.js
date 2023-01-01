import * as io from 'socket.io-client';

/**
 * Created by giko on 3/2/15.
 */
let mouseX = 0;
let mouseY = 0;
const oldMouseMove = document.onmousemove;

function readMouseMove(e) {
    mouseX = e.clientX;
    mouseY = e.clientY;
    if (oldMouseMove) {
        oldMouseMove(e);
    }
}
document.onmousemove = readMouseMove;

let isActive = true;

window.onfocus = function () {
    isActive = true;
};

window.onblur = function () {
    isActive = false;
};

const MutationObserver = (function () {
    var prefixes = ['WebKit', 'Moz', 'O', 'Ms', ''];
    for (var i = 0; i < prefixes.length; i++) {
        if (prefixes[i] + 'MutationObserver' in window) {
            return window[prefixes[i] + 'MutationObserver'];
        }
    }
    return false;
}());

// Create an observer instance
let observer;

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
    const config = {
        attributes: true,
        childList: true,
        characterData: true,
        subtree: true
    };
}


function updateAttributes(node) {
    for (let index = 0; index < node.childNodes.length; ++index) {
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

let socket;

const broadcaster = delta => {
    if (!MutationObserver) {
        updateAttributes(document.body);
    }

    socket.emit('screendata', document.documentElement.outerHTML);
};
const infoBroadcaster = () => {
    socket.emit('userinfo', {
        x: mouseX,
        y: mouseY,
        location: window.location.href,
        isActive: isActive,
    });
};

if (window.webRemotingHost) {
    init(window.webRemotingHost);
}

export function init(host) {
    socket = io(host);
    socket.on('message', function (data) {
        // swal({title: data.title, text: data.message, type: data.type});
        alert(data.message);
    });
    socket.on('connect', function () {
        socket.emit('client');
        infoBroadcasterInterval = setInterval(infoBroadcaster, 400);
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

    let broadcasterInterval;
    let infoBroadcasterInterval;

    socket.on('startbroadcast', function () {
        updateAttributes(document.body);

        broadcasterInterval = setInterval(broadcaster, 1000);
        infoBroadcasterInterval = setInterval(infoBroadcaster, 400);
    });

    socket.on('stopbroadcast', function () {
        clearInterval(broadcasterInterval);
        clearInterval(infoBroadcasterInterval);
    });
}
