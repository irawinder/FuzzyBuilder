Follow these steps to create an executable version of 
FuzzyBuilder for Windows. (MAC deployment TBD)

Requirements:

 - Have repository set up as Eclipse Java Project
 - Download launch4j onto a windows machine

- Step 1: 

 Navigate to the "fuzzybuilder-javafx/build/" folder and run 
 the ANT script “build.xml” to build an executable jar file 
 with data and libraries. (Right click on "build.xml" in the 
 Eclipse project explorer, select "Run As > Ant Build") The 
 results are copied to "fuzzybuilder-javafx/build/dist/". 
 Check the console to confirm contents were built correctly.

Step 2:

 Copy the contents of the "dist/" folder to a windows machine 
 with launch4j application installed. Open the file 
 "launch4j.xml", located in the "dist/install/windows" folder, 
 using the launch4j application. 

Step 3: 

 Updated any configuration details (such as directory 
 names), and build the EXE wrapper. It should show up in the 
 "dist/" folder.

Step 4: 

 Click on the EXE file to test running your 
 application!

