#!/usr/bin/env bash
mvn -DskipTests clean spring-javaformat:apply  compile spring-boot:process-aot
mkdir -p blog-output
rm -rf blog-output/images
asciidoctor README.adoc -o output.html
asciidoctor-pdf README.adoc -o output.pdf
cp -r images blog-output
mv output.html blog-output