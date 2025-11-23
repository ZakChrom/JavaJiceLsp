#!/bin/bash
set -xe

# javac --add-exports jdk.compiler/com.sun.source.util=ALL-UNNAMED --add-exports jdk.compiler/com.sun.source.tree=ALL-UNNAMED src/Main.java -d out
./jice build
cd .jice/output
for jar in ../cache/*.jar; do
    unzip -q $jar -x "META-INF/*"
done
cd ../..
jar cfe main.jar dev.calion.Main -C .jice/output .

