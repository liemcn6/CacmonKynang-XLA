
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel
        implements ActionListener {

    private final int BoardWidth = 10;
    private final int BoardHeight = 20;

    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private JLabel statusbar;
//    Tọa độ khối tâm của curPiece
    private int curX = 0;
    private int curY = 0;
//    Khối đang rơi
    private Shape curPiece;
//    Bảng chứa các khối đã rơi xong
    private Tetrominoes[][] board;

    public Board(Tetris parent) {
        initBoard(parent);
    }

    private void initBoard(Tetris parent) {
//        Kích hoạt sử dụng keyboard
        setFocusable(true);
        curPiece = new Shape();
//        Mỗi 1000ms thực hiện actionPerformed method
        timer = new Timer(300, this);
        timer.start();
//        Lấy statusbar từ tetris jframe
        statusbar = parent.getStatusBar();
//        Khởi tạo bảng với kích thước đã cho
        board = new Tetrominoes[BoardWidth][BoardHeight];
        clearBoard();
//        Lắng nghe sự kiện bàn phím
        addKeyListener(new TAdapter());
    }
    
    /*
        Tự động được gọi sau khoảng thời gian set cho timer
        Kiểm tra khối đang rơi đã rơi xong
        Yes -> Tạo khối mới
        No -> Rơi thêm 1 hàng
    */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown(curY-1);
        }
    }

//     Kích thước khối vuông theo pixel
    private int squareWidth() {
        return (int) getSize().getWidth() / BoardWidth;
    }

//     Kích thước khối vuông theo pixel
    private int squareHeight() {
        return (int) getSize().getHeight() / BoardHeight;
    }

//    Toạ độ của khối theo trục tọa độ x: trái->phải, y: dưới -> trên
    private Tetrominoes shapeAt(int x, int y) {
        return board[x][y];
    }

//    Bắt đầu game
    public void start() {
        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();
        newPiece();
        timer.start();
    }

//    Tạm dừng game
    private void pause() {
        isPaused = !isPaused;

        if (isPaused) {
//            Dừng timer -> Dừng gọi method actionPerformed
            timer.stop();
            statusbar.setText("Paused");
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
    }

//    Vẽ bảng
    private void doDrawing(Graphics g) {
//      Size cua board
        Dimension size = this.getSize();

//        Vẽ đường giới hạn trên của bảng
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();
        g.drawLine(0, boardTop, getSize().width, boardTop);
        
//        Vẽ các khối đã rơi xong
        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, i);

                if (shape != Tetrominoes.NoShape) {
                    drawSquare(g, j * squareWidth(),
                            boardTop + (BoardHeight - i - 1) * squareHeight(), shape);
                }
            }
        }

//        Vẽ khối đang rơi
        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
//              Tọa độ các khối vuông trong bảng
                int x = curX + curPiece.getX(i);
                int y = curY + curPiece.getY(i);
//                Vẽ khối vuông: tọa độ/kích thước -> pixel
                drawSquare(g, x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(), curPiece.getShape());
            }
        }
    }

//    Tự động được gọi khi có sự thay đổi
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

//    Rơi xuống hết mức
    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            --newY;
            oneLineDown(newY);
        }
    }

//    Rơi 1 hàng
    private void oneLineDown(int newY) {
        if (!tryMove(curPiece, curX, newY)) {
            pieceDropped();
            return;
        }
    }

//    Gán các khối trong bảng là noshape
    private void clearBoard() {
        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                board[j][i] = Tetrominoes.NoShape;
            }
        }
    }

//    Xử lý khi khối đã rơi xong    
    private void pieceDropped() {
//        Lưu tọa độ của khối
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.getX(i);
            int y = curY + curPiece.getY(i);
            board[x][y] = curPiece.getShape();
        }

//        Kiểm tra full line
        removeFullLines();
    }

//    Tạo khối mới với shape ngẫu nhiên
    private void newPiece() {
        curPiece.setRandomShape();
        
//        Khối tâm nằm giữa bảng theo chiều ngang
        curX = BoardWidth / 2;
//       Khối tâm cách boardTop bằng số khối phía trên khối tâm
        curY = BoardHeight - 1 - curPiece.maxY();
        
//        Di chuyển xuống failed -> Gameover
        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("Game over");
        }
    }

//    Di chuyển khối
    private boolean tryMove(Shape newPiece, int newX, int newY) {
        
//        Lặp lấy tọa độ 4 khối vuông tạo thành khối chính
        for (int i = 0; i < 4; ++i) {

            int x = newX + newPiece.getX(i);
            int y = newY + newPiece.getY(i);

            if (x < 0 || x >= BoardWidth || y < 0) {
                return false;
            }

//            Khối mới chỉ được nằm đè lên các khối noshape
            if (shapeAt(x, y) != Tetrominoes.NoShape) {
                return false;
            }
        }

//        Gán tạo độ mới
        curPiece = newPiece;
        curX = newX;
        curY = newY;

//        Vẽ lại bảng
        repaint();

        return true;
    }

//    Kiểm tra các hàng có thể xóa
    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = 0; i < BoardHeight; i++) {
            boolean lineIsFull = true;

//            Duyệt toàn bộ hàng
            for (int j = 0; j < BoardWidth; j++) {
//                Có khối noshape -> Line chưa full
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
//                Dịch các hàng phía trên hàng full xuống 1 hàng
                for (int k = i; k < BoardHeight - 1; k++) {
                    for (int j = 0; j < BoardWidth; j++) {
                        board[j][k] = shapeAt(j, k + 1);
                    }
                }
            }
        }
        
        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
//            Cập nhật điểm bằng số hàng đã xóa
            statusbar.setText(String.valueOf(numLinesRemoved));
            
//            Reset khối hiện tại về noshape
            curPiece.setShape(Tetrominoes.NoShape);
        }
        
        isFallingFinished = true;
    }

    
    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {

        Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102),
            new Color(102, 204, 102), new Color(102, 102, 204),
            new Color(204, 204, 102), new Color(204, 102, 204),
            new Color(102, 204, 204), new Color(218, 170, 0)
        };

//      Lấy màu của khối theo stt trong enum
        Color color = colors[shape.ordinal()];

//       Tô màu
        g.setColor(color);
        g.fillRect(x, y, squareWidth() - 2, squareHeight() - 2);
    }

//    Bắt sự kiện nhấn bàn phím
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (isPaused) {
                return;
            }

            switch (keycode) {

                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;

                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;

                case KeyEvent.VK_DOWN:
                    tryMove(curPiece.rotateRight(), curX, curY);
                    break;

                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    break;

                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
            }
        }
    }
}
