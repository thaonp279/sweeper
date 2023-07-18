package main;

/**
 * P2Player uses the single point strategy to infer whether a cell is safe or contains a mine.
 */
public class P2Player extends Player {

    public P2Player(Game game, boolean verbose) {
        super(game, verbose);
    }

    /**
     * First starts the game by probing the cells that are guaranteed to be safe.
     * Infer next moves until (1) the game ends with a victory or loss OR (2) no more inference can be made.
     */
    @Override
    public void play() {
        initialProbes();
        boolean changed = inferMoves();
        while (changed) {
            changed = inferMoves();
        }
        if (!game.hasEnded()) {
            game.giveUp();
            printFinalView();
        }
    }

    /**
     * Infer next moves by deducing whether a cell is safe or contains mine.
     * @return whether a deduction and corresponding move has been made
     */
    private boolean inferMoves() {
        boolean changed = false;
        for (Coord c : getAllCoveredCells()) {
            for (Coord hint: getHintNeighbors(c)) {
                if (deduceHint(hint, c)) {
                    changed = true;
                    printSteps();
                }
            }
        }
        return changed;
    }

    /**
     * Deduce information about a given neighbor of a hint
     * by comparing the number of mines in the hint and the number of covered neighbors
     * 1. If mines = 0, the neighbor is safe
     * 2. If mines = covered neighbors count, the neighbor contains a mine
     * 3. else no deduction can be made
     * @param hint the Coord of the cell containing the hint
     * @param coord the Coord of the cell to check whether is safe or danger.
     * @return
     */
    private boolean deduceHint(Coord hint, Coord coord) {
        boolean deduced = false;
        int minesCount = getViewOn(hint);
        int markedCount = getMarkedNeighbors(hint).length;
        int remainingMinesCount = minesCount - markedCount;
        int coveredCount = getCoveredNeighbors(hint).length;

        if (remainingMinesCount == coveredCount) {
            markMine(coord);
            deduced = true;
        } else if (remainingMinesCount == 0) {
            probe(coord);
            deduced = true;
        }
        return deduced;
    }

}
