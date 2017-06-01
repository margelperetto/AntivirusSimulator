# Antivirus Simulator
This is the most dangerous project in the world, run at your own risk.
## What is this shit?
A software that monitors a folder and deletes files as defined in the rules you have added.
## How this works?
The program will read all the files and check if their name matches any of the rules. If it hits, the file will be deleted.

* What is a file path?  

Example file | File path | File name
-------------|-----------|----------
C:\User\foo\test.txt | C:\User\foo | test.txt

* Rule Types: What will be used to be matched
    ```
    FILE_NAME, PATH, ALL
    ```
* Match Types: As will be matched
    ```
    EQUAL_TO, STARTS_WITH, ENDS_WITH, CONTAINS
    ```
* Match Sentence: With what will be matched
    ```
    Can be a file name, folder name, file extension, etc
    ```
* Who to define a rule? Examples:
  
    FILE_NAME|EQUAL_TO|test.txt
    
    FILE_NAME|ENDS_WITH|.txt
    
    PATH|STARTS_WITH|C:\tests\tmp
    
    ALL|ENDS_WITH|tmp\my_files\test.txt
    
## How to use it?
There are two ways:
1. **Graphical interface**: You just choose a folder and add rules;
![alt text](https://github.com/margelperetto/AntivirusSimulator/blob/master/screenshot.JPG "ScreenShot")
1. **Command Line**:
```
$ java -jar AntiVirusSimulator-0.0.1-SNAPSHOT.jar [folder_path] [rule1] [rule2] [rule3] ...
```
## Command line examples:
Delete all files named exactly "test.txt" from folder "C:\User\test\my_folder"
```
$ java -jar AntiVirusSimulator-0.0.1-SNAPSHOT.jar C:\User\test\my_folder FILE_NAME|EQUAL_TO|test.txt
```
Delete all files that have the name ending with ".txt" from "C:\my_folder"
```
$ java -jar AntiVirusSimulator-0.0.1-SNAPSHOT.jar C:\my_folder FILE_NAME|ENDS_WITH|.txt
```
Delete all files from folder "C:\my_folder\test\tmp" (subfolders will not be deleted, just files)
```
$ java -jar AntiVirusSimulator-0.0.1-SNAPSHOT.jar C:\my_folder\test\tmp PATH|EQUAL_TO|C:\my_folder\test\tmp
```
Monitoring from folder "C:\my_folder" and delete all files named "test.txt" in folders named "tmp"
```
$ java -jar AntiVirusSimulator-0.0.1-SNAPSHOT.jar C:\my_folder ALL|ENDS_WITH|\tmp\test.txt
```
Adding two rules
```
$ java -jar AntiVirusSimulator-0.0.1-SNAPSHOT.jar C:\my_folder FILE_NAME|ENDS_WITH|.bkp PATH|EQUAL_TO|C:\my_folder\tmp
```

## Compile and run with commands:
```
$ git clone https://github.com/margelperetto/AntivirusSimulator.git
$ cd AntivirusSimulator
$ mvn clean package
$ java -jar target/AntiVirusSimulator-0.0.1-SNAPSHOT.jar
```
