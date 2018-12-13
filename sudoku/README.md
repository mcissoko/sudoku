# sudoku
Sudoku game

## Requirements
- Java 1.8
- Maven 3

## chechout
- git clone https://github.com/mcissoko/sudoku.git

## move to modules
- cd sudoku/sudoku

## build core api
- cd sudoku-api
- mvn clean install

### build IHM module based on JavaFx
- cd ../sudoku-ihm
- mvn clean jfx:native 


## run the windows executable file
- cd target/jfx/native/sudoku-ihm-1.0.0-SNAPSHOT/
- sudoku-ihm-1.0.0-SNAPSHOT.exe
