# Obscured Sweeper Solver
In Obscured Sweeper, player probes a square map containing covered cells and blocked cells. A covered cell can be a mine or a safe cell.

Once probed, a cell reveals the number of mines in the 8 cells immediately adjacent to it.

The logical agent solves Obscured Sweeper by maintaining a knowledge base in Conjunctive normal form (CNF) or Disjunctive normal form (DNF) and making inferences from knowledge base.

## Compiling & Running
```
cd src
./playSweeper.sh [agent name] [verbose]
search name: P1 | P2 | P3 | P4
```
## Running stacscheck tests
```
cd a2-submit
stacscheck Tests
```
## Running JUnit tests
```
cd src
./testSweeper.sh
```
## UML Architecture
<img width="561" alt="Screen Shot 2023-07-18 at 12 03 03" src="https://github.com/thaonp279/sweeper/assets/77321721/374ceb18-1f78-4353-bfe6-aef9a4b85899">
