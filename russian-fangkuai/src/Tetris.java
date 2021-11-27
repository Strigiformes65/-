import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 俄罗斯方块主类
 */

public class Tetris extends JPanel {
    //正在下落的方块
    private Tetromino currentOne = Tetromino.random();
    //将要下落的方块
    private Tetromino nextOne = Tetromino.random();
    //主区域
    private Cell[][] wall = new Cell[18][9];
    //单元格的值为48像素
    public static final int CELL_SIZE = 48;

    //分数池,消除1行得到1分....
    int[] scores_pool = {0, 1, 2, 5, 10};
    //总得分
    private int total_scores = 0;
    //消除的行数
    private int total_line = 0;
    //游戏分为三个状态：游戏中、暂停、游戏结束
    public static final int PLAYING = 0;
    public static final int PAUSE = 1;
    public static final int GAMEOVER = 2;
    //存放游戏状态的值
    private int game_state;
    //声明数组，用来显示游戏状态
    String[] show_state = {"P[pause]", "C[continue]", "R[reply]"};

    public static BufferedImage I;
    public static BufferedImage T;
    public static BufferedImage L;
    public static BufferedImage J;
    public static BufferedImage S;
    public static BufferedImage Z;
    public static BufferedImage O;
    public static BufferedImage backImage;

    static {
            try {
                I = ImageIO.read(new File("images/I.png"));
                T = ImageIO.read(new File("images/T.png"));
                L = ImageIO.read(new File("images/L.png"));
                J = ImageIO.read(new File("images/J.png"));
                S = ImageIO.read(new File("images/S.png"));
                Z = ImageIO.read(new File("images/Z.png"));
                O = ImageIO.read(new File("images/O.png"));
                backImage = ImageIO.read(new File("images/background.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(backImage, 0, 0, null);
        //加入偏移值
        g.translate(22, 15);
        //绘制游戏主区域
        paintWall(g);
        //绘制正在下落的方块
        paintCurrentOne(g);
        //绘制将要下落的四方格
        paintNextOne(g);
        //绘制游戏得分
        paintScores(g);
        //显示游戏当前状态
        paintState(g);
    }
    //判断游戏是否结束
    public boolean isGameOver(){
        Cell[] cells = nextOne.cells;
        for(Cell cell : cells){
            int row = cell.getRow();
            int col = cell.getCol();
            if(wall[row][col] != null){
                return true;
            }
        }
        return false;
    }
    //判断当前行是否满
    public boolean isFullLine(int row){
        Cell[] cells = wall[row];
        for(Cell cell : cells){
            if(cell == null){
                return false;
            }
        }
        return true;
    }
    //消除行方法
    public void destoryLine(){
        //统计当前消除的行数
        int line = 0;
        Cell[] cells = currentOne.cells;
        for(Cell cell : cells){
            int row = cell.getRow();
            //判断当前行是否已满
            if(isFullLine(row)){
                line++;
                for(int i = row ; i > 0; i --){
                    System.arraycopy(wall[i-1], 0, wall[i], 0, wall[0].length);
                }
                wall[0] = new Cell[9];
            }
        }

        //将分数加入到总分中
        total_scores += scores_pool[line];
        total_line += line;
    }
    //判断四方格能否下落
    public boolean canDrop(){
        Cell[] cells = currentOne.cells;
        for(Cell cell : cells){
            int row = cell.getRow();
            int col = cell.getCol();
            //判断是否到底部
            if(row == wall.length - 1){
                return false;
            } else if (wall[row+1][col] != null){
                return false;
            }
        }
        return true;
    }

    /**
     * 首先判断能否下落，如果能下落就下移，否则将四方格嵌入墙中，
     * 同时判断是否消行，并且还要判断游戏是否结束，如果没有结束，
     * 则继续生成四方格
     *
     */
    //按键依次下落一个
    public void sortDropAction(){
        //判断是否下落
        if(canDrop()){
            currentOne.softDrop();
        } else {
            //将四方格嵌入强中
            LandToWall();
            //判断是否消除行
            destoryLine();
            //判断是否游戏结束
            if(isGameOver()){
                game_state = GAMEOVER;
            } else {
                //当游戏没有结束是，继续生成新的四方格
                currentOne = nextOne;
                nextOne = Tetromino.random();
            }
        }
    }

    //四方格嵌入到墙中
    private void LandToWall() {
        Cell[] cells = currentOne.cells;
        for(Cell cell : cells){
            int row = cell.getRow();
            int col = cell.getCol();
            wall[row][col] = cell;
        }
    }
    //瞬时下落
    public void handDropAction(){
        while(true){
            if(canDrop()){
                currentOne.softDrop();
            } else {
                break;
            }
        }
        //将四方格嵌入强中
        LandToWall();
        //判断是否消除行
        destoryLine();
        //判断是否游戏结束
        if(isGameOver()){
            game_state = GAMEOVER;
        } else {
            //当游戏没有结束是，继续生成新的四方格
            currentOne = nextOne;
            nextOne = Tetromino.random();
        }
    }
    //创建顺时针旋转
    public void rotateRightAction(){
        currentOne.rotateRight();
        //判断是否越界或重合
        if(outOfBounds() || same()){
            currentOne.rotateLeft();
        }
    }
    //按一次四方格左移一次
    public void moveLeftAction(){
        currentOne.moveLeft();
        if(outOfBounds() || same()){
            currentOne.moveRight();
        }
    }
    //按一次四方格右移一次
    public void moveRightAction(){
        currentOne.moveRight();
        if(outOfBounds() || same()){
            currentOne.moveLeft();
        }
    }

    private void paintState(Graphics g) {
        if(game_state == 0){
            g.drawString(show_state[0], 500, 660);
        } else if (game_state == 1){
            g.drawString(show_state[1], 500, 660);
        } else {
            g.drawString(show_state[2], 500, 660);
            g.setColor(Color.yellow);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
            g.drawString("GAMEOVER", 30, 400);
        }
    }

    private void paintScores(Graphics g) {
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        g.drawString("Scores:" + total_scores, 500, 248);
        g.drawString("Lines:" + total_line, 500, 430);
    }

    private void paintNextOne(Graphics g) {
        Cell[] cells = nextOne.cells;
        for(Cell cell : cells){
            int x = cell.getCol() * CELL_SIZE + 370;
            int y = cell.getRow() * CELL_SIZE + 25;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }

    private void paintCurrentOne(Graphics g) {
        Cell[] cells = currentOne.cells;
        for(Cell cell : cells){
            int x = cell.getCol() * CELL_SIZE;
            int y = cell.getRow() * CELL_SIZE;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }

    private void paintWall(Graphics g) {
        for(int i = 0; i < wall.length; i++){
            for(int j = 0; j < wall[i].length; j++){
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                Cell cell = wall[i][j];
                //如果存在则加入方块，不存在则绘制空格
                if(cell == null){
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                } else {
                    g.drawImage(cell.getImage(), x, y, null);
                }
            }
        }
    }

    //判断是否越界
    public boolean outOfBounds(){
        Cell[] cells = currentOne.cells;
        for(Cell cell : cells){
            int col = cell.getCol();
            int row = cell.getRow();
            if(col < 0 || col >= 9 || row < 0 || row >= 18){
                return true;
            }
        }
        return false;
    }
    //判断是否重合
    public boolean same(){
        Cell[] cells = currentOne.cells;
        for(Cell cell : cells){
            int col = cell.getCol();
            int row = cell.getRow();
            if(wall[row][col] != null){
                return true;
            }
        }
        return false;
    }
    //开启键盘监听
    public void start(){
        game_state = PLAYING;
        KeyListener l = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int code = e.getKeyCode();
                switch(code){
                    case KeyEvent.VK_DOWN:
                        sortDropAction();//向下一个
                        break;
                    case KeyEvent.VK_LEFT:
                        moveLeftAction();//左移
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRightAction();//右移
                        break;
                    case KeyEvent.VK_UP:
                        rotateRightAction();//顺时针旋转
                        break;
                    case KeyEvent.VK_SPACE:
                        handDropAction();//瞬时下落
                        break;
                    case KeyEvent.VK_P:
                        if(game_state == PLAYING){
                            game_state = PAUSE;
                        }
                        break;
                    case KeyEvent.VK_C:
                        if(game_state == PAUSE){
                            game_state = PLAYING;
                        }
                        break;
                    case KeyEvent.VK_S:
                        game_state = PLAYING;
                        wall = new Cell[18][9];
                        currentOne = Tetromino.random();
                        nextOne = Tetromino.random();
                        total_line = 0;
                        total_scores = 0;
                }
                //重新绘制
                repaint();
            }
        };

        //将俄罗斯方块窗口设为焦点
        this.addKeyListener(l);
        this.requestFocus();
        //自动下落
        while (true){
            if(game_state == PLAYING){
                try{
                    Thread.sleep(500);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                //判断是否下落
                if(canDrop()){
                    currentOne.softDrop();
                }else{
                    //嵌入强中
                    LandToWall();
                    //判断是否小航
                    destoryLine();
                    //判断是否结束
                    if(isGameOver()){
                        game_state = GAMEOVER;
                    } else {
                        currentOne = nextOne;
                        nextOne = Tetromino.random();
                    }
                }
            }
            repaint();
        }
    }


    public static void main(String[] args) {
        //创建一个窗口对象
        JFrame frame = new JFrame("俄罗斯方块");
        //创建面板
        Tetris panel = new Tetris();
        //加入面板
        frame.add(panel);


        //设置可见
        frame.setVisible(true);
        //窗口大小
        frame.setSize(800, 800);
        //窗口居中
        frame.setLocationRelativeTo(null);
        //设置窗口关闭时程序停止
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //游戏主要逻辑封装在方法中
        panel.start();
    }


}
