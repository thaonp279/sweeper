import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class P4PlayerTest {

    Game game;
    P4Player player;
    @BeforeEach
    void setUp() {
        game = new Game(World.TEST1);
        player = new P4Player(game, true);
    }


    @Test
    void atMostKMines() {
        // k = -1
        Coord c1 = new Coord(0,0);
        Coord c2 = new Coord(0,1);
        Coord[] cells = new Coord[] {c1, c2};
        int k = -1;
        int[][] formula = player.atMostKMines(k, cells);
        int[] prefix = new int[] {};
        assertArrayEquals(new int[][] {prefix}, formula);
        // k = cells.length
        int k2 = 2;
        int[][] formula2 = player.atMostKMines(k2, cells);
        assertArrayEquals(new int[][] {}, formula2);
        // k = cells.length -1
        int k3 = 1;
        int[][] formula3 = player.atMostKMines(k3, cells);
        int[] clause = new int[] {-c1.hashCode(), -c2.hashCode()};
        assertArrayEquals(new int[][] {clause}, formula3);
        // k < cells.length -1
        int k4 = 0;
        int[][] formula4 = player.atMostKMines(k4, cells);
        int[] firstNot = new int[] {-c2.hashCode()};
        int[] firstIs = new int[] {-c1.hashCode()};
        assertArrayEquals(new int[][] {firstNot, firstIs}, formula4);

    }
}