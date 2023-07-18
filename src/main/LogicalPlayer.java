package main;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

/**
 * A LogicalPlayer keeps a knowledge base of dangers in the game (KBU).
 * Every time a hint about the number of mines in its neighborhood is uncovered, LogicalPlayer updates its KBU.
 * It chooses its next probe target by querying whether a cell is safe against its KBU.
 * The query is done using a satisfiability test.
 *
 * LogicalPlayer's KBU contains 4 levels of information where the above contains the below:
 * 1. KBU: the entirety of knowledge about world of the LogicalPlayer
 * --- KBU = a collection of Hint Formulas
 * 2. Hint Formula: logical formulas about a neighborhood surrounding a hint
 * --- Hint Formula = a collection of clauses
 * 3. Clause: a possible scenario or a condition given the hint
 * --- Clause = a collection of literals
 * 4. Literal: information on whether a cell is a mine or not.
 *
 * The implemented class must implement:
 * 1. existKMines: encoding k mines exist among an array of given cells
 * 2. satTest: satisfiability test used for checking entailSafe
 */
public abstract class LogicalPlayer extends Player {

    /**
     * KBU uses a 3-D int array to express the 3 levels within itself.
     * int[ hintFormula ][ clause ][ literal ]
     */
    protected int[][][] KBU = new int[][][]{};
    public LogicalPlayer(Game game, boolean verbose) {
        super(game, verbose);
    }

    /**
     * First starts the game by probing the cells that are guaranteed to be safe.
     * Initialize the KBU based on hints disclosed by the probes.
     * Infer next moves until (1) the game ends with a victory or loss OR (2) no more inference can be made.
     */
    @Override
    public void play() {
        initialProbes();
        initializeKBU();
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
     * Infer next moves by checking whether each covered cell is safe according to KBU.
     * If confirmed to be safe, the cell is probed and its hint is added to the KBU.
     * @return whether a probe has been made
     */
    private boolean inferMoves() {
        boolean changed = false;
        ArrayList<Coord> newHints = new ArrayList<>();
        for (Coord c : getAllCoveredCells()) {
            if (game.hasEnded()) {
                break;
            }
            if (entailSafe(c)) {
                probe(c);
                newHints.add(c);
                printSteps();
                changed = true;
            } else if (entailMine(c)) {
                markMine(c);
                printSteps();
            }
        }

        addToKBU(newHints.toArray(Coord[]::new));
        return changed;
    }

    /**
     * Check entailment of safety by testing the satisfiability of the KBU and the cell as a mine.
     * @param cell Coord object of the cell under safety check
     * @return boolean true if KBU entails the safety of the cell, false if not entails
     */
    private boolean entailSafe(Coord cell) {
        int mineSign = 1;
        return satTest(cell, mineSign);
    }

    /**
     * Check entailment of danger by testing the satisfiability of the KBU and the cell as a safe cell.
     * @param cell Coord object of the cell under danger check
     * @return boolean true if KBU entails the danger of the cell, false if not entails
     */
    private boolean entailMine(Coord cell) {
        int safeSign = -1;
        return satTest(cell, safeSign);
    }

    /**
     * Add new rules inferred by the given hints to the KBU.
     * @param hints Coord object of the cell that contains the number of mines in its neighborhood.
     */
    private void addToKBU(Coord[] hints) {
        for (Coord hint: hints) {
            Coord[] neighbors = getCoveredNeighbors(hint);
            int k = Character.getNumericValue(getViewOn(hint));
            int markCount = getMarkedNeighbors(hint).length;
            int[][] newRules = existKMines(k-markCount, neighbors);
            addHintFormulaToKBU(newRules);
        }
    }

    /**
     * Initialize the KBU with rules about the covered cells after initialProbes
     */
    private void initializeKBU() {
        addToKBU(getActiveHints());
    }

    private void addHintFormulaToKBU(int[][] hintFormula) {
        if (hintFormula.length != 0 && hintFormula[0].length != 0) {
            KBU = ArrayUtils.addAll(KBU, new int[][][]{hintFormula});
        }
    }

    public abstract int[][] existKMines(int i, Coord[] neighbors);
    protected abstract boolean satTest(Coord cell, int sign);
}
