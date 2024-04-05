package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Line;

public class ShellView implements IGameView {

    private Board board;

    @Override
    public void init(Board gameBoard) {
        this.board=gameBoard;
    }

    @Override
    public void refresh() {
        int width = board.length();
        int height = board.height();
        System.out.println(columnNumber(width));

        System.out.println("  ┏" + "━".repeat(width * 4 - 1) + "┓");
        for (int i = 0; i < height; i++) {
            printBoardInside(i, width, height);
        }
        System.out.println("  ┗" + "━".repeat(width * 4 - 1) + "┛");
    }

    private void printBoardInside(int i, int width, int height) {
        StringBuilder sb;
        sb = new StringBuilder(i + " ┃");
        for (int j = 0; j < width; j++) {
            sb.append(" ● ");
            if (j < width - 1) { appendRowsWithHorizontalLines(j, i, sb);}
        }
        System.out.println(sb + "┃");
        if (i < height - 1) {
            sb = new StringBuilder("  ┃");
            for (int j = 0; j < width; j++) { appendRowsWithVerticalLines(j, i, sb, width);}
            System.out.println(sb + "┃");
        }
    }

    private void appendRowsWithVerticalLines(int j, int i, StringBuilder sb, int width) {
        Line searched;
        searched = new Line(j, i, j, i + 1);
        if (board.getLines().containsKey(searched.hashCode())) {
            sb.append(board.getLines().get(
                    searched.hashCode()).color().getFormat().format(" ‖ "));
        } else {
            sb.append("   ");
        }
        if (j < width - 1) {
            sb.append(" ");
        }
    }

    private void appendRowsWithHorizontalLines(int j, int i, StringBuilder sb) {
        Line searched;
        searched = new Line(j, i, j + 1, i);
        if (board.getLines().containsKey(searched.hashCode())) {
            sb.append(board.getLines().get(
                    searched.hashCode()).color().getFormat().format("="));
        } else {
            sb.append(" ");
        }
    }

    private StringBuilder columnNumber(int width) {
        StringBuilder sb;
        sb = new StringBuilder("   ");
        for (int j = 0; j < width; j++) {
            sb.append(" ").append(j).append("  ");
        }
        return sb;
    }
}
