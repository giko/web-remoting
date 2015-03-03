/**
 * TimersJS
 *
 * @fileoverview A collection of timer objects.
 *
 * Copyright (c) 2013 Brett Fattori (bfattori@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
(function () {

    var global = this, timerPool = [], callbacksPool = [];

    function addTimerToPool(timer) {
        timerPool.push(timer);
        return timerPool.length - 1;
    }

    function removeTimerFromPool(timer) {
        timerPool.splice(timer.id, 1);
    }


    var Timer = function (interval, callback) {
        var prototyping = false;
        if (typeof interval === "undefined")
            prototyping = true;

        this._systemTimerReference = null;
        this._interval = interval;
        this._callback = callback;
        this._running = false;
        this._paused = false;
        this._systemTimerFunction = null;
        this._canBeKilled = true;
        this._state = {};

        if (!prototyping)
            this.id = addTimerToPool(this);

        if (typeof arguments[2] === "undefined")
            this.restart();

        return this;
    };

    Timer.prototype.state = function(key, value) {
        if (typeof key === "object")
            this._state = key;
        else if (typeof key === "string" && typeof value === "undefined")
            return this._state[key];
        else if (typeof key === "string" && typeof value !== "undefined")
            this._state[key] = value;
        else
            return this._state;
    };

    Timer.prototype.kill = function () {
        if (!this._canBeKilled)
            return this;

        // The engine needs to remove this timer
        TimersJS.cleanupCallback(this._systemTimerFunction);
        this.cancel();
        removeTimerFromPool(this);
        this._systemTimerReference = null;
        return null;
    };

    Timer.prototype.systemTimer = function (timer) {
        if (typeof timer !== "undefined") {
            this._systemTimerReference = timer;
        }
        return this._systemTimerReference;
    };

    Timer.prototype.isRunning = function () {
        return this._running;
    };

    Timer.prototype.cancel = function () {
        global.clearTimeout(this.systemTimer());
        this._systemTimerReference = null;
        this._running = false;
        return this;
    };

    Timer.prototype.pause = function () {
        this.cancel();
        this._paused = true;
        return this;
    };

    Timer.prototype.restart = function () {
        this.cancel();
        this.systemTimer(global.setTimeout(this.callback(), this.interval()));
        this._running = true;
        this._paused = false;
        return this;
    };

    Timer.prototype.killable = function(state) {
        if (typeof state !== "undefined")
            this._canBeKilled = state;

        return this._canBeKilled;
    };

    Timer.prototype.callback = function (callback) {
        if (typeof callback !== "undefined") {
            this._callback = callback;
            this._systemTimerFunction = null;
            if (this.isRunning()) {
                this.restart();
            }
        } else {
            if (this._systemTimerFunction === null) {
                this._systemTimerFunction = function () {
                    var aC = arguments.callee, now = Date.now(), delta = now - aC.lastTime;
                    aC.lastTime = now;
                    if (aC.timerCallback)
                        aC.timerCallback.call(aC.timer, delta, now);
                };
                this._systemTimerFunction.timerCallback = this._callback;
                this._systemTimerFunction.timer = this;
                this._systemTimerFunction.lastTime = Date.now();
            }
        }
        return this._systemTimerFunction;
    };

    Timer.prototype.interval = function (interval) {
        if (typeof interval !== "undefined") {
            this.cancel();
            this._interval = interval;
        }
        return this._interval;
    };


    // ### SUBCLASSES ------------------------------------------------------------------

    var RepeaterTimer = function (interval, callback) {
        var internalCallback = function(delta, now) {
            var aC = arguments.callee;
            if (aC.timerCallback)
                aC.timerCallback.call(this, delta, now);

            this.restart();
        };
        internalCallback.timerCallback = callback;

        Timer.call(this, interval, internalCallback);
    };
    RepeaterTimer.prototype = new Timer();
    RepeaterTimer.base = Timer.prototype;

    var MultiTimer = function (interval, callback, repetitions, completionCallback) {

        var internalCallback = function (delta, now) {
            var aC = arguments.callee;
            if (aC.repetitions-- > 0) {
                aC.callbackFunction.call(this, aC.totalRepetitions, delta, now);
                aC.totalRepetitions++;
                this.restart();
            } else {
                if (aC.completionCallback) {
                    aC.completionCallback.call(this, delta, now);
                }
                this.kill();
                global.TimersJS.cleanupCallback(aC);
            }
        };
        internalCallback.callbackFunction = callback;
        internalCallback.completionCallback = completionCallback;
        internalCallback.repetitions = repetitions;
        internalCallback.totalRepetitions = 0;

        Timer.call(this, interval, internalCallback);
    };
    MultiTimer.prototype = new Timer();
    MultiTimer.base = Timer.prototype;

    var OneShotTimer = function (interval, callback) {

        var innerCallback = function (delta, now) {
            if (arguments.callee.callbackFunction) {
                arguments.callee.callbackFunction.call(this, delta, now);
                this.kill();
                global.TimersJS.cleanupCallback(arguments.callee);
            }
        };
        innerCallback.callbackFunction = callback;

        Timer.call(this, interval, innerCallback);
    };
    OneShotTimer.prototype = new Timer();
    OneShotTimer.base = Timer.prototype;

    OneShotTimer.prototype.restart = function () {
        if (!this._paused && this._running) {
            return;
        }

        OneShotTimer.base.restart.call(this);
    };

    var TriggerTimer = function (interval, callback, triggerInterval, triggerCallback) {

        var completionCallback = function (delta, now) {
            var aC = arguments.callee;
            aC.interval.kill();
            aC.intervalCompletionCallback.call(this, delta, now);
            global.TimersJS.cleanupCallback(aC);
        };

        // Create an Interval internally
        completionCallback.interval = new RepeaterTimer(triggerInterval, triggerCallback);
        completionCallback.intervalCompletionCallback = callback;

        OneShotTimer.call(this, interval, completionCallback);
    };
    TriggerTimer.prototype = new OneShotTimer();
    TriggerTimer.base = OneShotTimer.prototype;

    /*
     *      PUBLIC API --------------------------------------------------------------------------
     */

    global.TimersJS = {
        cleanupCallback: function(cb) {
            callbacksPool.push(cb);
        },

        poolSize: function() {
            // Subtract the class inheritance objects
            return timerPool.length;
        },

        pauseAllTimers: function() {
            for (var i = 0; i < timerPool.length; i++)
                timerPool[i].pause();
        },

        restartAllTimers: function() {
            for (var i = 0; i < timerPool.length; i++)
                timerPool[i].restart();
        },

        cancelAllTimers: function() {
            for (var i = 0; i < timerPool.length; i++)
                timerPool[i].cancel();
        },

        killAllTimers: function() {
            var liveTimers = [];
            while (timerPool.length > 0) {
                var timer = timerPool.shift();
                if (!timer.killable())
                    liveTimers.push(timer);
                else
                    timer.kill();
            }
            timerPool = liveTimers;
        },

        // TIMERS ---------------------------------------------------------------------------

        timer: function(interval, callback) {
            return new Timer(interval, callback);
        },

        repeater: function(interval, callback) {
            return new RepeaterTimer(interval, callback);
        },

        multi: function(interval, repetitions, callback, completionCallback) {
            return new MultiTimer(interval, callback, repetitions, completionCallback);
        },

        oneShot: function(interval, callback) {
            return new OneShotTimer(interval, callback);
        },

        trigger: function(interval, callback, triggerRate, triggerCallback) {
            return new TriggerTimer(interval, callback, triggerRate, triggerCallback);
        }
    };

    global.setInterval(function() {
        while (callbacksPool.length > 0) {
            callbacksPool[0] = null;
            callbacksPool.shift();
        }
    }, 500);
})();