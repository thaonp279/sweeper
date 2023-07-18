package main;

/**
 * P1Player probes cell from left to right, top to bottom
 */
public class P1Player extends Player {

    public P1Player(Game game, boolean verbose) {
        super(game, verbose);
    }

    @Override
    public void play() {
        printInitialView();
        for (Coord c : getAllCoveredCells()) { // getAllCoveredCells returns left, right, top bottom order
            if (probe(c)) { // probed a cell that has not been probed before and not a blocked cell
                printSteps();
                if (game.hasEnded()) {
                    break;
                }
            }
        }
    }

    /**
     * Override to not automatically mark mine cells if won
     */
    @Override
    public void printSteps() {
        if (game.hasEnded()) {
            printFinalView();
        } else if (verbose) {
            A2main.printBoard(view);
        }
    }

}
