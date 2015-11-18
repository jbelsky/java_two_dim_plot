#!/bin/bash

# Enter the java_project_dir
java_project_dir="/mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/GitHub/java_two_dim_plot"

# Set the jar_manifest
jar_manifest="${java_project_dir}/two-dim-plot.manifest"

# Enter the package name
package_name="twodimplot"

# Enter the new jar file
jar_file_name="two-dim-plot"

# Set the external jars
genomics_functions_jar="/opt/jar_files/genomics-functions.jar"
sam_jar="/opt/jar_files/sam-1.67.jar"






# Get the java class files
java_class_file=($(find ${java_project_dir}/bin -type f -name "*.class"))

# Clear the bin directory of any lingering .class files
if [ ${#java_class_file[@]} -gt 0 ]
then

	rm -v ${java_class_file[@]}

fi



# Get the java *.java files
java_src_file=($(find ${java_project_dir}/src/${package_name} -type f -name "*.java"))




# Update the class files
javac -verbose \
-cp ${sam_jar}:${genomics_functions_jar} \
-sourcepath ${java_project_dir}/src/${package_name} \
-d ${java_project_dir}/bin \
${java_src_file[@]}

# Get the Java classes
java_class_file=($(find ${java_project_dir}/bin/${package_name} -name "*.class"))
java_base_name=($(basename -a ${java_class_file[@]}))

# Create the java_class_str
java_class_str=""

for(( i=0; i<${#java_base_name[@]}; i++ ))
do
	java_class_str="$java_class_str -C ${java_project_dir}/bin ${package_name}/${java_base_name[$i]}"
done

echo -e "The java_class_str is\n\t${java_class_str}"

# Create the jar
jar -cvmf $jar_manifest \
/mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/java_scripts/lib/${jar_file_name}.jar \
$java_class_str
