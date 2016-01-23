package Solutions.Hard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MaxBlackSquare {

    private int MaxBlackSquareSize;

    public int GetMaxBlackSquareSize(List<Boolean> matrix) {
        int width = (int) Math.sqrt(matrix.size());
        if (width != matrix.size() / width || width < 1) {
            return 0;
        }
        List<Cross> crosses = GetBlackLineLengthToAllDirections(matrix, width);
        for (Cross[] diagonal : EnumerateDiagonals(crosses, width)) {
            FindMaxBlackSquareOnDiagonal(diagonal);
        }
        return MaxBlackSquareSize;
    }

    private List<Cross> GetBlackLineLengthToAllDirections(List<Boolean> matrix, int width) {
        MaxBlackSquareSize = 0;
        List<Cross> crosses = IntStream.range(0, width * width).mapToObj(x -> new Cross(x))
                .collect(Collectors.toList());
        int last = width - 1;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < width; ++j) {
                int forwardIndex = i * width + j;
                crosses.get(forwardIndex).DiagonalPosition = Math.min(i, j);
                crosses.get(forwardIndex).Top = matrix.get(forwardIndex)
                    ? (i > 0 ? crosses.get(forwardIndex - width).Top : -1) + 1
                    : -1;
                crosses.get(forwardIndex).Left = matrix.get(forwardIndex)
                    ? (j > 0 ? crosses.get(forwardIndex - 1).Left : -1) + 1
                    : -1;
                int backwardIndex = (last - i) * width + (last - j);
                crosses.get(backwardIndex).Right = matrix.get(backwardIndex)
                    ? (j > 0 ? crosses.get(backwardIndex + 1).Right : -1) + 1
                    : -1;
                crosses.get(backwardIndex).Bottom = matrix.get(backwardIndex)
                    ? (i > 0 ? crosses.get(backwardIndex + width).Bottom : -1) + 1
                    : -1;
                if (matrix.get(i * width + j) && MaxBlackSquareSize < 1) {
                    MaxBlackSquareSize = 1;
                }
            }
        }
        return crosses;
    }

    private List<Cross[]> EnumerateDiagonals(List<Cross> crosses, int width) {
        ArrayList<Cross[]> diagonales = new ArrayList<Cross[]>(width * 2 - 1);
        diagonales.add(DiagonalElements(crosses, width, 0));
        for (int d = 1; d + MaxBlackSquareSize < width; ++d) {
            diagonales.add(DiagonalElements(crosses, width, d));
            diagonales.add(DiagonalElements(crosses, width, -d));
        }
        return diagonales;
    }

    private Cross[] DiagonalElements(List<Cross> crosses, int width, int diagonalIndex) {
        Cross[] diagonal = new Cross[width - Math.abs(diagonalIndex)];
        int startRow = diagonalIndex >= 0 ? 0 : Math.abs(diagonalIndex);
        int startColumn = diagonalIndex < 0 ? 0 : diagonalIndex;
        for (int i = 0; Math.abs(diagonalIndex) + i < width; i++) {
            diagonal[i] = crosses.get((startRow + i) * width + (startColumn + i));
        }
        return diagonal;
    }

    private void FindMaxBlackSquareOnDiagonal(Cross[] points) {
        TreeSet<Cross> starts = new TreeSet<Cross>(PositionsComparer);
        TreeSet<Cross> ends = new TreeSet<Cross>(RightBottomEndComparer);
        for (Cross point : points) {
            while (!ends.isEmpty()
                    && (ends.first().DiagonalPosition + ends.first().RightBottomMin()) < point.DiagonalPosition) {
                starts.remove(ends.first());
                ends.remove(ends.first());
            }
            if (point.TopLeftMin() > 0 && !starts.isEmpty()) {
                Cross farthest = starts.ceiling(point.CloneAsTopLeft());
                if (farthest != null) {
                    int squareSize = point.DiagonalPosition - farthest.DiagonalPosition + 1;
                    if (MaxBlackSquareSize < squareSize) {
                        MaxBlackSquareSize = squareSize;
                    }
                }
            }
            if (point.RightBottomMin() > 0) {
                starts.add(point);
                ends.add(point);
            }
        }
    }

    private class Cross {
        public int DiagonalPosition, Top, Left, Right, Bottom;

        public Cross(int diagonalPosition) {
            this.DiagonalPosition = diagonalPosition;
        }

        public Cross CloneAsTopLeft() {
            return new Cross(DiagonalPosition - TopLeftMin());
        }

        public int TopLeftMin() {
            return Math.min(Top, Left);
        }

        public int RightBottomMin() {
            return Math.min(Right, Bottom);
        }
    }

    private static Comparator<Cross> PositionsComparer = new Comparator<Cross>() {
        public int compare(Cross x, Cross y) {
            return x.DiagonalPosition - y.DiagonalPosition;
        }
    };

    private static Comparator<Cross> RightBottomEndComparer = new Comparator<Cross>() {
        public int compare(Cross x, Cross y) {
            return (x.DiagonalPosition + x.RightBottomMin())
                 - (y.DiagonalPosition + y.RightBottomMin());
        }
    };

    public static void main(String[] args) {
        List<Boolean> matrix = IntStream.of(
                1, 1, 1, 1, 1, 1, 1, 
                1, 0, 0, 0, 0, 0, 0, 
                1, 1, 1, 1, 1, 1, 1, 
                1, 0, 0, 1, 1, 1, 1, 
                1, 0, 0, 1, 1, 1, 1, 
                1, 1, 1, 1, 1, 1, 1, 
                1, 1, 1, 1, 0, 1, 1)
                .mapToObj(x -> x == 0 ? Boolean.FALSE : Boolean.TRUE).collect(Collectors.toList());
        MaxBlackSquare solver = new MaxBlackSquare();
        int maxBlackSquareSize = solver.GetMaxBlackSquareSize(matrix);
        System.out.println(maxBlackSquareSize);
    }
}
