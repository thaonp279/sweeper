import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    P0Player player;
    Game game;


    @BeforeEach
    void setUp() {
        game = new Game(World.SMALL1);
        player = new P0Player(game, true);
        A2main.printBoard(game.getWorld().getMap());
    }


    /**
     *      0 1 2 3 4
     *     - - - - -
     *  0| 0 0 1 b b
     *  1| 0 b 2 m 2
     *  2| 0 0 2 m 2
     *  3| 0 b 2 2 2
     *  4| 0 b 1 m 1
     */
    @Test
    void probe() {
        // test  expanding 0 cells
        int safeCount = game.getSafeCount();
        player.probe(0,0);
        safeCount -= 11;
        assertEquals(safeCount, game.getSafeCount());
        A2main.printBoard(player.getView());

        // test probing 1 cell again
        boolean updateAgain = player.probe(2,2);
        assertFalse(updateAgain);
        assertEquals(safeCount, game.getSafeCount());

    }

    @Test
    void markMine() {
        // mark covered cell
        Coord covered = new Coord(1, 0);
        player.markMine(covered);
        assertEquals('*', player.getViewOn(covered));

        // mark uncovered cell
        Coord c = new Coord(0,0);

        player.probe(c);
        assertEquals('0', player.getViewOn(c));

        boolean marked = player.markMine(c);
        assertFalse(marked);
        assertEquals('0', player.getViewOn(c));

        // mark blocked cell
        Coord b = new Coord(3, 0);
        boolean markedB = player.markMine(b);
        assertFalse(markedB);
        assertEquals('b', player.getViewOn(b));
    }


    @Test
    void getCoveredNeighbors() {
        player.probe(new Coord(0, 0));
        // unobstructed
        Coord[] neighbors = player.getCoveredNeighbors(new Coord(2, 3));
        assertEquals(4, neighbors.length);
        printNeighborhood(neighbors);

        // blocked by block cell
        Coord[] n2 = player.getCoveredNeighbors(new Coord(2, 0));
        assertEquals(1, n2.length);
        printNeighborhood(n2);

        // all uncovered
        Coord[] n3 = player.getCoveredNeighbors(new Coord(0,0));
        assertEquals(0, n3.length);
        printNeighborhood(n3);
    }

    @Test
    void getHintNeighbors() {
        player.probe(new Coord(0, 0));
        A2main.printBoard(player.getView());
        Coord[] neighbors = player.getHintNeighbors(new Coord(3, 1));
        assertEquals(3, neighbors.length);
        printNeighborhood(neighbors);

        Coord[] noNeighbors = player.getHintNeighbors(new Coord(4, 1));
        assertEquals(0, noNeighbors.length);
        printNeighborhood(noNeighbors);
    }

    @Test
    void getActiveHints() {
        // all covered
        Coord[] hints = player.getActiveHints();
        assertEquals(0, hints.length);

        player.initialProbes();
        assertEquals(4, player.getActiveHints().length);
    }

    void printNeighborhood(Coord[] neighbors) {
        for (Coord c: neighbors) {
            System.out.print(c);
        }
    }
}