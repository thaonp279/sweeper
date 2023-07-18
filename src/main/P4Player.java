package main;

import org.apache.commons.lang3.ArrayUtils;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.Arrays;

/**
 * P4Player encodes its KBU in the form of CNF and checks the model satisfiability using the SAT4J library.
 * The 4 levels of information are encoded in the KBU as follows:
 * 1. KBU comprises a conjunction of hint formula
 * --- (hintFormula /\ hintFormula)
 * 2. Each hint formula carries a conjunction of clauses.
 * --- (clause /\ clause)
 * 3. Each clause is encoded as a disjunction of literals.
 * --- (literal \/ literal)
 * 4. Each literal is encoded by the cell's hashcode and the sign.
 * --- literal = sign * cell.hasCode()
 * --- sign = 1 if mine else -1
 */
public class P4Player extends LogicalPlayer {

    public P4Player(Game game, boolean verbose) {
        super(game, verbose);
    }

    @Override
    protected boolean satTest(Coord cell, int sign) {
        final int MAXVAR = 1000000;
        final int NBCLAUSES = 500000;
        ISolver solver = SolverFactory.newDefault();
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);

        try {
            for (int[][] hintFormula : KBU) {
                for (int[] clause: hintFormula) {
                    solver.addClause(new VecInt(clause));
                }
            }

            // add query clause
            int[] query = new int[]{sign * cell.hashCode()};
            VecInt v = new VecInt(query);
            solver.addClause(v);

            IProblem problem = solver;
            if (!problem.isSatisfiable()) {
                return true;
            }
        } catch (ContradictionException c) {
            return true;
        } catch (TimeoutException e) {
            System.out.println(e);
            System.out.println(e.getCause());
        }
        return false;
    }

    /**
     * existKMines creates a hint formula that expresses there exists k mines among the given cells.
     * existKMines contains 2 parts: atMostKMines and atLeastKMines
     * @param k number of mines
     * @param cells Coord that potentially has mines
     * @return a hint formula that comprises all possible scenarios where k mines exist in the given cells
     */
    public int[][] existKMines(int k, Coord[] cells) {
        return ArrayUtils.addAll(atMostKMines(k, cells), atLeastKMines(k, cells));
    }

    /**
     * atMostK creates a collection of clauses that expresses at most k cells have a certain status (safe or mine).
     * The collection of clauses are all subsets of size k+1 of cells where at least 1 doesn't have the status.
     * Clause = (notSign * literal \/ notSign * literal ...)
     *
     * atMostK breaks down the k problem into 2 scenarios recursively.
     * 1. The subset includes the first cell
     * 2. The subset doesn't include the first cell
     *
     * The algorithm stops when:
     * 1. k = -1: a subset of size (-1 + 1 = 0) is invalid, return all the encoded cells in the subset so far
     * 2. k = the count of cells: hint doesn't give any new information
     * 3. k = the count of cells - 1: the subset is all the cells
     *
     * @param k maximum number of mines
     * @param cells Coord that potentially has mines
     * @param prefix a clause that encodes the subset so far
     * @param notSign -1 for atmostKMines, 1 for atMostKSafe
     * @return a hint formula
     */
    private int[][] atMostK(int k, Coord[] cells, int[] prefix, int notSign) {
        int count = cells.length;
        if (k == -1) {
            return new int[][] {prefix};
        } else if (k == count) { // learn nothing new
            return new int[][] {};
        } else if (k == count-1) { // all cells make a subset
            int[] clause = Arrays.stream(cells).mapToInt(c -> notSign * c.hashCode()).toArray();
            int[][] clauses = new int[][] {ArrayUtils.addAll(prefix, clause)};
            return clauses;
        }
        Coord first = cells[0];

        // array without first cell for recursion
        int startIndex = 1;
        int endIndex = cells.length;
        Coord[] withoutFirst = Arrays.copyOfRange(cells, startIndex, endIndex);

        // 2 scenarios first is not and is in the subset
        int[][] firstNot = atMostK(k, withoutFirst, prefix, notSign);
        int[][] firstIs = atMostK(k-1, withoutFirst, ArrayUtils.addAll(prefix, notSign * first.hashCode()), notSign);
        return ArrayUtils.addAll(firstNot, firstIs);
    }

    /**
     * atMostKMines's recursion starter with empty prefix
     * @param k maximum number of mines
     * @param cells Coord that potentially has mines
     * @return a hint formula that expresses there are at most k mines among given cells
     */
    public int[][] atMostKMines(int k, Coord[] cells) {
        int[] prefix = new int[]{};
        int sign = -1;
        return atMostK(k, cells, prefix, sign);
    }

    /**
     * At least k mines is at most cell count - k safe
     * @param k minimum number of mines
     * @param cells Coord in consideration
     * @return a hint formula
     */
    public int[][] atLeastKMines(int k, Coord[] cells) {
        int[] prefix = new int[]{};
        int sign = 1;
        int kSafe = cells.length - k;
        return atMostK(kSafe, cells, prefix, sign);
    }

}
