package main;

/**
 * Game contains the World and keeps track of player stats.
 * A game is won when all safe (non-mine) cells are probed, lost when player hit a mine or gives up.
 */
public class Game {

    private World world;
    private char[][] startMap;
    private int safeCount = 0;
    private int coveredCount = 0;
    private boolean hitMine = false;
    private boolean gaveUp = false;

    public Game(World world) {
        this.world = world;
        initializeGame();
    }

    /**
     * Initialize start map by covering all cells with ? except blocked cells
     */
    private void initializeGame() {
        int n = world.getSize();
        char[][] map = world.getMap();
        startMap = new char[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (map[row][col] != 'b') {
                    coveredCount++;
                    if (map[row][col] != 'm') { // initialize number of safe cells
                        safeCount++;
                    }
                    startMap[row][col] = '?';
                } else {
                    startMap[row][col] = 'b';
                }
            }
        }
    }

    public char[][] getStartMap() {
        return startMap;
    }

    public char probe(Coord coord) {
        return probe(coord.getX(), coord.getY());
    }

    /**
     * Uncover a cell and update the count of safe cells and covered cells remaining.
     * @param x coordinate to be probed
     * @param y coordinate to be probed
     * @return the character underlying the cell
     */
    public char probe(int x, int y) {
        char val = world.probe(x, y);
        if (val == 'm') {
            coveredCount--;
            hitMine = true;
            val = '-';
        } else if (val != 'b') {
            coveredCount--;
            safeCount--;
        }
        return val;
    }

    public void markMine() {
        coveredCount--;
    }

    public void giveUp() {
        gaveUp = true;
    }

    /**
     * @return true if remaining safe cell count is 0 else false
     */
    public boolean hasWon() {
        return safeCount == 0;
    }

    public boolean hasLost() {
        return hitMine;
    }

    public boolean hasEnded() {
        return hasWon() || hasLost() || gaveUp;
    }

    public void printResult() {
        if (hasWon()) {
            System.out.println("Result: Agent alive: all solved");
        } else if (hasLost()) {
            System.out.println("Result: Agent dead: found mine");
        } else if (gaveUp) {
            System.out.println("Result: Agent not terminated");
        }
    }

    public World getWorld() {
        return world;
    }

    public int getSafeCount() {
        return safeCount;
    }

    public int getCoveredCount() {
        return coveredCount;
    }

    public int getSize() {
        return world.getSize();
    }

    /**
     * Valid coordinate (x, y) if positive and does not exceed the border of the World
     * @param x
     * @param y
     * @return
     */
    public boolean isValidXY(int x, int y) {
        if (x < 0 || x > getSize() - 1) {
            return false;
        } else if (y < 0 || y > getSize() - 1) {
            return false;
        }
        return true;
    }
    public boolean isValidCoord(Coord coord) {
        return isValidXY(coord.getX(), coord.getY());
    }
}
