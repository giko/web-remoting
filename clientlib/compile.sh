#!/bin/bash
set -e
set -x
if [ ! -f compiler.jar ]; then
    wget http://dl.google.com/closure-compiler/compiler-latest.zip -O compiler.zip
    unzip compiler.zip
    rm -f compiler.zip
fi

if [ $1 == "u" ]; then
  #rm jquery.js sweetalert.js flipclock.js flipclock.css sweetalert.css socketio.js timer
  wget https://raw.githubusercontent.com/bfattori/timersjs/master/timers.js -O timers.js
  wget http://code.jquery.com/jquery-2.1.3.min.js -O jquery.js
  wget https://raw.githubusercontent.com/Automattic/socket.io-client/master/socket.io.js -O socketio.js
  wget https://raw.githubusercontent.com/t4t5/sweetalert/master/lib/sweet-alert.js -O sweetalert.js
  wget https://cdnjs.cloudflare.com/ajax/libs/sweetalert/0.3.3/sweet-alert.min.css -O sweetalert.css
  wget https://raw.githubusercontent.com/objectivehtml/FlipClock/master/compiled/flipclock.js -O flipclock.js
  wget https://raw.githubusercontent.com/objectivehtml/FlipClock/master/compiled/flipclock.css -O flipclock.css
fi

echo '(function() {' > alljs.js
cat jquery.js sweetalert.js timers.js socketio.js flipclock.js client.js >> alljs.js
echo '})();' >> alljs.js
java -jar compiler.jar alljs.js --js_output_file compiled.js --compilation_level WHITESPACE_ONLY --output_wrapper "var remoterpc = {properties: {username: 'unnamed',project: 'hrb'}};%output%"
rm alljs.js
