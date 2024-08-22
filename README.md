# Dots and Boxes
## Software Development Methods Exam-2024
It is a game you can play with paper and pen between 2 or more players.
The game board is a grid made of points that, if adjacent, can be connected to form a line.
The aim of the game is to close as many boxes as possible, where the boxes are considered the 4 adjacent points that can be closed by 4 lines.
The score of the player increases by one when a box is closed, and when the board is complete the player with the highest score wins.
### Game modes
The game can be played in 3 modes:
1. a very basic text mode, that can be entered using `text` as argument while running the Main;
2. a TUI mode, that enriches the previous one with colors, that can be entered using `tui` as argument while running the Main;
3. a GUI mode, that can be entered using `gui` as argument while running the Main.
## Disclaimer
The code is meant to work with Java 21 and works without problems on Unix. To make it work on Windows, you need to enable UTF-8 in Windows Terminal:
`powershell.exe -NoExit -Command chcp 65001` .
