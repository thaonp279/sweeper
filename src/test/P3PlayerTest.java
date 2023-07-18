import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
class P3PlayerTest {

    Game game;
    P3Player player;

    @BeforeEach
    void setUp() {
        game = new Game(World.TEST1);
        player = new P3Player(game, false);
    }

    @Test
    void existKMines() {
        // k = 0
        Coord c1 = new Coord(0,0);
        Coord c2 = new Coord(0,1);
        Coord[] cells = new Coord[] {c1, c2};
        int k = 0;
        int[][] formula = player.existKMines(k, cells);
        int[] clause = new int[] {-c1.hashCode(), -c2.hashCode()};
        assertEquals(1, formula.length);
        assertArrayEquals(clause, formula[0]);

        // k = cells.length
        int k2 = 2;
        int[][] formula2 = player.existKMines(k2, cells);
        int[] clause2 = new int[] {c1.hashCode(), c2.hashCode()};
        assertEquals(1, formula2.length);
        assertArrayEquals(clause2, formula2[0]);

        // k < cells.length
        int k3 = 1;
        int[][] formula3 = player.existKMines(k3, cells);
        int[] firstSafe = new int[] {-c1.hashCode(), c2.hashCode()};
        int[] firstMine = new int[] {c1.hashCode(), -c2.hashCode()};
        assertEquals(2, formula3.length);
        assertArrayEquals(new int[][] {firstSafe, firstMine}, formula3);
    }

}