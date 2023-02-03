#!/usr/bin/env bash
mvn -DskipTests clean spring-javaformat:apply  compile spring-boot:process-aot
mkdir -p blog-output && mkdir -p images
# cp -r blog-output/images/* images
asciidoctor README.adoc -o output.html
asciidoctor README.adoc --backend docbook -o output.docbook
pandoc -f docbook -t docx  ./output.docbook -o output.docx
asciidoctor-pdf README.adoc -o output.pdf
cp -r images blog-output
mv output.pdf blog-output
mv output.html blog-output
mv output.docx blog-output
mv output.docbook blog-output
