package main;

import org.apache.commons.lang3.ArrayUtils;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.Arrays;

/**
 * P3Player encodes its KBU in the form of DNF and checks the model satisfiability using the LogicNG library.
 * The 4 levels of information in the KBU are turned into String before feeding in the sat test as follows:
 * 1. KBU comprises a conjunction of hint formula
 * --- (hintFormula & hintFormula)
 * 2. Each hint formula carries a disjunction of clauses.
 * --- (clause | clause)
 * 3. Each clause is encoded as a conjunction of literals.
 * --- (literal & literal)
 * 4. Each literal is encoded by the cell's hashcode and the sign.
 * --- literal = sign * cell.hasCode()
 * --- sign = 1 if mine else -1
 *
 */
public class P3Player extends LogicalPlayer {

    public P3Player(Game game, boolean verbose) {
        super(game, verbose);
    }

    /**
     * Test satisfiability of the conjunction of the KBU and the information about the cell.
     * If sign is -1, test satisfiability for KBU and cell is danger.
     * If sign is 1, test satisfiability for KBU and cell is safe.
     * @param cell Coord to query
     * @param sign -1 for danger test, 1 for safe test
     * @return true for satisfiable, false for unsatisfiable
     */
    @Override
    protected boolean satTest(Coord cell, int sign) {
        String modifier = "";
        if (sign == -1) {
            modifier = "~";
        }
        String statement = stringifyKBU() + "&" + modifier + cell.hashCode();
        FormulaFactory f = new FormulaFactory();
        PropositionalParser p = new PropositionalParser(f);
        try {
            Formula formula = p.parse(statement);
            SATSolver miniSat = MiniSat.miniSat(f);
            miniSat.add(formula);
            Tristate result = miniSat.sat();
            if (result == Tristate.FALSE) {
                return true;
            }
        } catch (ParserException e) {
            System.out.println(e);
        }
        return false;
    }

    /**
     * existKMines creates a hint formula that expresses there exists k mines among the given cells.
     * @param k number of mines
     * @param cells Coord that potentially has mines
     * @return a hint formula that comprises all possible scenarios where k mines exist in the given cells
     */
    public int[][] existKMines(int k, Coord[] cells) {
        int[] prefix = new int[] {};
        return existKMines(k, cells, prefix);
    }

    /**
     * existKMines breaks down the k problem into 2 scenarios recursively.
     * 1. The first cell has no mine, there exists k mines in the remaining cells.
     * 2. The first cell has mine, there exists k-1 mines in the remaining cells.
     *
     * The algorithm stops when:
     * 1. k = 0: all cells have no mines
     * 2. k = the count of cells: all cells have mines
     *
     * @param k number of mines
     * @param cells Coord that potentially has mines
     * @param prefix literals passed down by the recursive caller
     *               or the precondition for the current existKMines call
     * @return a hint formula that comprises all possible scenarios where k mines exist in the given cells
     */
    private int[][] existKMines(int k, Coord[] cells, int[] prefix) {
        if (k == 0 | k == cells.length) {
            int sign;
            if (k == 0) { // all cells are not mines
                sign = -1;
            } else { // all cells are mines
                sign = 1;
            }
            int[] clause = groupLiterals(prefix, transformToLiterals(cells, sign));
            return new int[][] {clause};
        }

        Coord first = cells[0];

        // array without the first cell for recursion
        int startIndex = 1;
        int endIndex = cells.length;
        Coord[] withoutFirst = Arrays.copyOfRange(cells, startIndex, endIndex);

        // signs for the first cell when added to the prefix
        int safeSign = -1;
        int mineSign = 1;

        // 2 scenarios where first is safe vs first is mine
        int[][] firstSafe = existKMines(k, withoutFirst, groupLiterals(prefix, first, safeSign));
        int[][] firstMine = existKMines(k-1, withoutFirst, groupLiterals(prefix, first, mineSign));

        return groupClauses(firstSafe, firstMine);
    }

    /**
     * Turn an array of Coord objects into an array of literals.
     * Each coord is encoded by its hashcode.
     * If the coords are mines, the literals are positive (sign = 1).
     * Else the literals negative (sign = -1)
     * @param cells
     * @param sign
     * @return array of literals
     */
    private int[] transformToLiterals(Coord[] cells, int sign) {
        return Arrays.stream(cells).mapToInt(c -> sign * c.hashCode()).toArray();
    }

    /**
     * Transform a given Coord object and its sign into a literal and group with the exisiting literals.
     * @param literals group of literals to be merged with
     * @param toAdd Coord object to be transformed and grouped
     * @param sign 1 for mine else -1
     * @return array of literals
     */
    private int[] groupLiterals(int[] literals, Coord toAdd, int sign) {
        return ArrayUtils.addAll(literals, sign * toAdd.hashCode());
    }

    /**
     * Merge 2 groups of literals into 1.
     * @param l1 first group of literals
     * @param l2 second group of literals
     * @return a clause that comprises all literals in both groups
     */
    private int[] groupLiterals(int[] l1, int[] l2) {
        return ArrayUtils.addAll(l1, l2);
    }

    /**
     * Merge 2 group of clauses into 1
     * @param clauses1 first group of clauses
     * @param clauses2 second group of clauses
     * @return a hint clause comprises all clauses in both groups
     */
    private int[][] groupClauses(int[][] clauses1, int[][] clauses2) {
        return ArrayUtils.addAll(clauses1, clauses2);
    }

    private String stringifyKBU() {
        String sKBU = "";
        String operator = "";
        for (int[][] hintFormula: KBU) {
            sKBU = sKBU + operator + stringifyHintFormula(hintFormula);
            operator = "&";
        }
        return sKBU;
    }

    private String stringifyHintFormula(int[][] hintFormula) {
        String hintStr = "";
        String operator = "";
        for (int[] c :  hintFormula) {
            hintStr = hintStr + operator + stringifyClause(c);
            operator = "|";
        }
        return "(" + hintStr + ")";
    }


    private String stringifyClause(int[] clause) {
        String clauseStr = "";
        String operator = "";
        for (int literal : clause) {
            String sign = "";
            if (literal < 0) {
                sign = "~";
            }
            String literStr = sign + Math.abs(literal);
            clauseStr = clauseStr + operator + literStr;
            operator = "&";
        }
        return "(" + clauseStr + ")";
    }

}
