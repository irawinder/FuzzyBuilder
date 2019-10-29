# Fuzzy Builder
FuzzyBuilder is an application for generating "fuzzy" resolution development scenarios on a configurable parcel.

![Fuzzy Builder by Ira Winder](screenshots/Screen%20Shot%202019-07-23%20at%201.36.52%20PM.png "Fuzzy Builder by Ira Winder")

## How to Use the Processing IDE for files in /Processing

1. Make sure you have installed the latest version of [Java](https://www.java.com/verify/)
2. Download [Processing 3](https://processing.org/download/)
3. Clone or download the repository to your computer
4. Open and run "Processing/Main/Main.pde" with Processing 3
 
## How to use the Eclipse IDE for files in /Eclipse

1. This software runs on version 13 of Java with the following external libraries:
..*javafx-sdk-13
..*processing-3.5.3

2. Set up GUI_FX.java in Eclipse. In the "Run Configurations" menu, under the "Arguments" tab, insert the following code into VM arguments. Replace `/path/to/javafx-sdk-13` with the actual path to your JavaFX download. Also be sure to *uncheck* the box that says "Use the -XstartOnFirstThread argument when launching with SWT.
```
--module-path "/path/to/javafx-sdk-13/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web
```

3. No special arguments needed to run GUI_Processing, as long as you have pointed to processing's core.jar
