# Dots and Boxes
## Software Development Methods Exam-2024
Dots and Boxes is a table game played by 2 or more players.
The game board is a grid made of points that, if adjacent, can be connected to form a line.
The aim of the game is to close as many boxes as possible, where the boxes are considered the 4 adjacent points that can be closed by 4 lines, forming a square.
The score of the player increases by one when a box is closed, and when the board is complete the player with the highest score wins.
### Game modes
The game can be played with 3 UIs:
1. A very basic text mode, that can be entered using `text` as an argument;
2. A TUI mode, that enriches the previous one with colors, and can be entered using `tui` as an argument;
3. a GUI mode in Swing, that can be entered using `gui` as an argument.
## Syntax
To run the program, the syntax is `java -jar DotsandBoxes-1.0.jar text|tui|gui`.
## Disclaimer
The code is meant to work with Java 21 and the console UIs work without problems on Unix terminals. To make UTF-8 work on Windows Terminal, you need to enable it with:
`powershell.exe -NoExit -Command chcp 65001` before running the program.
