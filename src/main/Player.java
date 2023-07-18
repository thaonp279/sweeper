package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public abstract class Player {

    protected Game game;
    protected char[][] view;
    protected boolean verbose;

    public Player(Game game, boolean verbose) {
        this.game = game;
        view = game.getStartMap();
        this.verbose = verbose;
    }

    public abstract void play();

    /**
     * Probe a cell at given coord.
     * If the cell has a hint of 0, probe all of its neighbors and so on.
     * @param cell
     * @return boolean whether the cell is probed
     */
    public boolean probe(Coord cell) {
        if (getViewOn(cell) == '?') {
            char c = game.probe(cell);
            updateView(cell, c);
            if (c == '0') {
                for (Coord n: getCoveredNeighbors(cell)) {
                    probe(n);
                }
            }
            return true;
        }

        return false;
    }

    public boolean probe(int x, int y) {
        return probe(new Coord(x, y));
    }

    /**
     * Probing cells that are guaranteed to be safe: top left and middle cells
     */
    public void initialProbes() {
        printInitialView();
        int n = game.getSize();
        Coord[] initialProbes = new Coord[] {new Coord(0,0), new Coord(n/2, n/2)};
        for (Coord c : initialProbes) {
            if (probe(c)) {
                printSteps();
            }
        }
    }

    public boolean markMine(Coord cell) {
        if (getViewOn(cell) == '?') {
            game.markMine();
            char mineChar = '*';
            updateView(cell, mineChar);
            return true;
        }
        return false;
    }

    /**
     * Get all neighbors in 8 directions with the type that matches given regex
     * @param coord the cell whose neighbors are returned
     * @param regex regular expression to match the type of cell
     * @return all valid neighbors of the coord with the matched type
     */
    protected Coord[] getNeighbors(Coord coord, String regex) {
        return Arrays.stream(coord.getNeighbors())
                .filter(n -> game.isValidCoord(n))
                .filter(n -> Pattern.matches(regex, String.valueOf(getViewOn(n))))
                .toArray(Coord[]::new);
    }

    public Coord[] getNeighbors(Coord coord) {
        return getNeighbors(coord, "."); // neighbors of all types
    }

    public Coord[] getCoveredNeighbors(Coord coord) {
        String coveredChar = "\\?";
        return getNeighbors(coord, coveredChar);
    }

    public Coord[] getHintNeighbors(Coord coord) {
        return getNeighbors(coord, "\\d"); // neighbors that have been probed and contains a number hint
    }

    public Coord[] getMarkedNeighbors(Coord coord) {
        return getNeighbors(coord, "\\*"); // neighbors marked as mine
    }

    /**
     * in the order from left to right, top to bottom,
     * @return cells that have not been probed and are not blocked cells
     */
    public Coord[] getAllCoveredCells() {
        Coord[] covered = new Coord[game.getCoveredCount()];
        int index = 0;
        int n = game.getSize();
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                Coord coord = new Coord(x, y);
                if (getViewOn(coord) == '?') {
                    covered[index] = coord;
                    index++;
                }
            }
        }
        return covered;
    }

    /**
     * @return coords of all hints in the neighborhood of a covered cell
     */
    public Coord[] getActiveHints() {
        ArrayList<Coord> activeHints = new ArrayList<>();
        for(Coord covered : getAllCoveredCells()) {
            for (Coord hint : getHintNeighbors(covered)) {
                activeHints.add(hint);
            }
        }
        return activeHints.stream().distinct().toArray(Coord[]::new);
    }

    public char getViewOn(Coord coord) {
        return view[coord.getY()][coord.getX()];
    }

    private void updateView(Coord coord, char c) {
        view[coord.getY()][coord.getX()] = c;
    }

    public char[][] getView() {
        return view;
    }

    public void printInitialView() {
        if (verbose) {
            A2main.printBoard(game.getStartMap());
        }
    }

    public void printFinalView() {
        System.out.println("Final map");
        A2main.printBoard(view);
        game.printResult();

    }

    public void printSteps() {
        if (game.hasWon()) {
            for (Coord mine : getAllCoveredCells()) { // mark remaining cells as mine
                markMine(mine);
            }
        }
        if (game.hasEnded()) {
            printFinalView();
        } else if (verbose) {
            A2main.printBoard(view);
        }

    }


}
