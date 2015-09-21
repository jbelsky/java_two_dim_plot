#!/bin/bash

# Enter the java_project_dir
java_project_dir="/data/illumina_pipeline/scripts/GitHub/java_two_dim_plot/"

# Enter the package name
package_name="twodimplot"

# Enter the java_file
java_file_name='"*.java"'
java_file=$(find ${java_project_dir}src/${package_name}/ -type f -name "*.java")

# Enter the new jar file
jar_file_name="java-two-dim-plot"

# Set the external jars
jbfunctions_jar="/data/illumina_pipeline/scripts/java_scripts/jar_files/jbfunctions.jar"
sam_jar="/data/illumina_pipeline/scripts/java_scripts/jar_files/sam-1.67.jar"

# Update the class files
javac -verbose \
-cp ${jbfunctions_jar}:${sam_jar} \
-sourcepath ${java_project_dir}src/ \
-d ${java_project_dir}bin/ \
${java_file[@]}

# Create the jar
jar -cvmf \
${java_project_dir}src/twodimplot/java-two-dim-plot.manifest \
/data/illumina_pipeline/scripts/java_scripts/jar_files/${jar_file_name}.jar \
-C ${java_project_dir}bin/ \
${package_name}/ #\

# Add the source files to the jar
jar -vuf \
/data/illumina_pipeline/scripts/java_scripts/jar_files/${jar_file_name}.jar \
-C ${java_project_dir}src/ ${package_name}/
