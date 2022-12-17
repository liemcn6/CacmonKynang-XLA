
import java.util.Random;

public class Shape {

    private Tetrominoes pieceShape;
    private int coords[][];
    private int[][][] coordsTable = {
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}},
            {{0, -1}, {0, 0}, {1, 0}, {1, 1}},
            {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},
            {{0, -1}, {0, 0}, {0, 1}, {0, 2}},
            {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
            {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
            {{1, -1}, {0, -1}, {0, 0}, {0, 1}},
            {{-1, -1}, {0, -1}, {0, 0}, {0, 1}}
        };

    public Shape() {
//        Tọa độ 4 khối của shape
        coords = new int[4][2];
        setShape(Tetrominoes.NoShape);
    }

    public void setShape(Tetrominoes shape) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; ++j) {
//                shape.ordinal() - số thứ tự của shape trong enum
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }

        pieceShape = shape;
    }

//    Sua toa do x
    private void setX(int row, int x) {
        coords[row][0] = x;
    }

//    Sua toa do y
    private void setY(int row, int y) {
        coords[row][1] = y;
    }

//    Lay toa do x
    public int getX(int row) {
        return coords[row][0];
    }

//    Lay toa do y
    public int getY(int row) {
        return coords[row][1];
    }

    public Tetrominoes getShape() {
        return pieceShape;
    }

//    random 1 trong 7 shape != noshape
    public void setRandomShape() {
        Random r = new Random();
        
//        %7 => Lay so du 0->6 + 1
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Tetrominoes[] values = Tetrominoes.values(); 
        setShape(values[x]);
    }

    public int maxY() {
        int m = coords[0][1];

        for (int i = 1; i < 4; i++) {
            m = Math.max(m, coords[i][1]);
        }

        return m;
    }
    
    public Shape rotateRight() {
//        hvuong -> khong can xoay
        if (pieceShape == Tetrominoes.SquareShape) {
            return this;
        }

        Shape result = new Shape();
        result.pieceShape = pieceShape;

//        Quay 4 khối tậm (0,0) góc a = -90
        for (int i = 0; i < 4; ++i) {
            result.setX(i, getY(i));
            result.setY(i, -getX(i));
        }

        return result;
    }

    public Shape rotateLeft() {
        if (pieceShape == Tetrominoes.SquareShape) {
            return this;
        }

        Shape result = new Shape();
        result.pieceShape = pieceShape;

//        Quay 4 khối tậm (0,0) góc a = 90
        for (int i = 0; i < 4; ++i) {
            result.setX(i, -getY(i));
            result.setY(i, getX(i));
        }

        return result;
    }
}
