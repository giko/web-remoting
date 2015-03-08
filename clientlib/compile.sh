#!/bin/bash
set -e
set -x
if [ ! -f compiler.jar ]; then
    wget http://dl.google.com/closure-compiler/compiler-latest.zip -O compiler.zip
    unzip compiler.zip
    rm -f compiler.zip
fi
echo '(function() {' > alljs.js
cat jquery.js sweetalert.js timer.js socketio.js flipclock.js client.js >> alljs.js
echo '})();' >> alljs.js
java -jar compiler.jar alljs.js --js_output_file compiled.js --compilation_level SIMPLE_OPTIMIZATIONS --output_wrapper "var remoterpc = {properties: {username: 'unnamed',project: 'hrb'}};%output%"
rm alljs.js
