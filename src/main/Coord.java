package main;

import java.util.Objects;

/**
 * Coord contains the x and y coordinate for a cell.
 */
public class Coord {

    private final int x;
    private final int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * @return neighbors on 8 sides from left to right, top to bottom
     */
    public Coord[] getNeighbors() {
        Coord[] neighbors = new Coord[8];
        // left to right on each row, top row to bottom row
        neighbors[0] = new Coord(x-1, y-1);
        neighbors[1] = new Coord(x, y-1);
        neighbors[2] = new Coord(x+1, y-1);
        neighbors[3] = new Coord(x-1, y);
        neighbors[4] = new Coord(x+1, y);
        neighbors[5] = new Coord(x-1, y+1);
        neighbors[6] = new Coord(x, y+1);
        neighbors[7] = new Coord(x+1, y+1);

        return neighbors;
    }

    @Override
    public String toString() {
        return "(" + String.valueOf(x) + "," + String.valueOf(y) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return x == coord.x && y == coord.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
