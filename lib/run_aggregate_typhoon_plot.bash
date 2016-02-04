#!/bin/bash

# Set the bam_file_name
bam_file_name="/data/collaborator_data/aligned_experiments/CB2/CB2.bam"

# Set the feature_file_name
feature_file_name="/data/data2/jab112/2015_belsky_dissertation/chapter2/abf1_analysis/feature_files/"
feature_file_name="${feature_file_name}abf1_chip_seq_dm290_macs_motif_midpoint_feature_file.csv"

# Set the output_file_name
output_file_name="/data/data2/jab112/2015_belsky_dissertation/chapter2/abf1_analysis/output_files/henikoff_N20_abf1_dm290_macs_midpoint_motif_agg_typhoon_xwin_500_yheight_250.csv"

# Set the parameters
xwin=500
yheight=250

# Set the parameters
program="AggregateTyphoonPlot"

# Set the jar file path
jar_file_path="/data/illumina_pipeline/scripts/java_scripts/jar_files/two-dim-plot.jar"

# Execute the script
java -jar $jar_file_path $program $bam_file_name $feature_file_name $output_file_name $xwin $yheight
