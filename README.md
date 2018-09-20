# LearningTetris

## Overview
This is a Java Application that performs training of a genetic algorithm for Tetris. Once trained, the algorithm may be executed on the Tetris board.

The basic Tetris game layout was taken from the following Stanford assignment: http://cslibrary.stanford.edu/112/. It was then adapted to include custom genetic algorithm logic.

Concretely, the board is rated for several features when evaluating whether a piece should be placed: number of cleared rows, the maximum height of the board, the average height of the board, and the number of holes in the board.

When evaluating how 'good' a certain board arrangement is, the features mentioned above are calculated using a weighted sum. The task of the genetic algorithm is to determine the optimal weights when calculating the 'goodness' of the board arrangement.

Optimizing this aspect allows the algorithm to determine the best way to evaluate a given board arrrangement, and this information can be used to pick the best piece and orientation to place at any given moment.

## Future Optimizations
This can be improved by adding new features for the GA to work on, and ultimately performing more training. The Genetic Algorithm could be replaced with a Neural Network which would find it's own features to optimize based on a cost function.
