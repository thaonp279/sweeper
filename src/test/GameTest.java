import main.Game;
import main.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.CharBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    Game game;

    @BeforeEach
    void setUp() {
        game = new Game(World.TEST1);
    }

    @Test
    void getStartMap() {
        for (World w : World.values()) {
            Game g = new Game(w);
            int trueCount = getTrueCount(w);

            assertNotEquals(g.getStartMap(), w.getMap()); // different map references
            assertEquals(trueCount, g.getSafeCount()); // check safe cell counts
            int n = w.getSize();
            char[][] map = w.getMap();
            char[][] startMap = g.getStartMap();
            for (int row = 0; row < n; row++) {
                for (int col = 0; col < n; col++) {
                    char trueVal = map[row][col];
                    char startVal = startMap[row][col];

                    if (trueVal == 'b') {
                        assertEquals(trueVal, startVal);
                    } else {
                        assertEquals('?', startVal);
                    }
                }
            }
        }

    }

    private int getTrueCount(World w) {
        int trueCount = (int) Arrays.stream(w.getMap())
                .map(CharBuffer::wrap)
                .flatMapToInt(CharBuffer::chars)
                .filter(c -> c != 'b' && c != 'm')
                .count();
        return trueCount;
    }

    @Test
    void probe() {
        int safeCount = game.getSafeCount();
        char c1 = game.probe(2, 1);
        // probe blocked cell
        assertEquals('b', c1);
        assertEquals(safeCount, game.getSafeCount());

        // probe uncovered safe cell
        char c2 = game.probe(1, 1);
        safeCount--;
        assertEquals('3', c2);
        assertEquals(safeCount, game.getSafeCount());

        // probe mine
        char c3 = game.probe(1, 2);
        assertEquals('-', c3);
        assertEquals(safeCount, game.getSafeCount());
        assertTrue(game.hasLost());
        assertTrue(game.hasEnded());

    }

    @Test
    void hasWon() {
        int n = game.getSize();
        World w = game.getWorld();

        // probe all safe cells
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (w.probe(row, col) != 'm') {
                    game.probe(row, col);
                }
            }
        }

        assertTrue(game.hasWon());
        assertTrue(game.hasEnded());
    }


    @Test
    void isValidCoord() {
        int n = game.getSize();
        assertTrue(game.isValidXY(0,0));
        assertTrue(game.isValidXY(n-1,n-1));
        assertFalse(game.isValidXY(0, n));
        assertFalse(game.isValidXY(n, 0));
        assertFalse(game.isValidXY(-1, 0));
        assertFalse(game.isValidXY(0, -2));
    }
}