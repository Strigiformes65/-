import java.util.Random;

/**
 * 方块的父类
 * 属性：4个方格
 * 方法：左右下
 */

public class Tetromino {
    public Cell[] cells = new Cell[4];
    //编写旋转状态
    public State[] states;
    //编写旋转次数
    public int count = 1000;
    //顺时针旋转四方格方法
    public void rotateRight(){
        if(states.length == 0){
            return;
        }
        //旋转次数+1
        count++;
        State s = states[count % cells.length];
        Cell cell = cells[0];
        int row = cell.getRow();
        int col = cell.getCol();
        cells[1].setRow(row + s.row1);
        cells[1].setCol(col + s.col1);
        cells[1].setRow(row + s.row2);
        cells[1].setCol(col + s.col2);
        cells[1].setRow(row + s.row3);
        cells[1].setCol(col + s.col3);
    }
    //逆时针旋转
    public void rotateLeft(){
        //旋转次数-1
        count--;
        State s = states[count % cells.length];
        Cell cell = cells[0];
        int row = cell.getRow();
        int col = cell.getCol();
        cells[1].setRow(row + s.row1);
        cells[1].setCol(col + s.col1);
        cells[1].setRow(row + s.row2);
        cells[1].setCol(col + s.col2);
        cells[1].setRow(row + s.row3);
        cells[1].setCol(col + s.col3);
    }


    public void moveLeft(){
        for(Cell cell : cells){
            cell.left();
        }
    }
    public void moveRight(){
        for(Cell cell : cells){
            cell.right();
        }
    }
    public void softDrop(){
        for(Cell cell : cells){
            cell.drop();
        }
    }


    public static Tetromino random(){
         int num = (int)(Math.random() * 7);
         Tetromino tertromino = null;
         switch (num){
             case 0:
                 tertromino = new I();
                 break;
             case 1:
                 tertromino = new T();
                 break;
             case 2:
                 tertromino = new J();
                 break;
             case 3:
                 tertromino = new L();
                 break;
             case 4:
                 tertromino = new O();
                 break;
             case 5:
                 tertromino = new S();
                 break;
             case 6:
                 tertromino = new Z();
                 break;
         }
         return tertromino;
    }
}

/**
 * 四方格旋转状态内部类
 */
class State{
    //四方格各元素相对位置
    int row0,col0,row1,col1,row2,col2,row3,col3;

    public State() {
    }

    public State(int row0, int col0, int row1, int col1, int row2, int col2, int row3, int col3) {
        this.row0 = row0;
        this.col0 = col0;
        this.row1 = row1;
        this.col1 = col1;
        this.row2 = row2;
        this.col2 = col2;
        this.row3 = row3;
        this.col3 = col3;
    }

    public int getRow0() {
        return row0;
    }

    public void setRow0(int row0) {
        this.row0 = row0;
    }

    public int getCol0() {
        return col0;
    }

    public void setCol0(int col0) {
        this.col0 = col0;
    }

    public int getRow1() {
        return row1;
    }

    public void setRow1(int row1) {
        this.row1 = row1;
    }

    public int getCol1() {
        return col1;
    }

    public void setCol1(int col1) {
        this.col1 = col1;
    }

    public int getRow2() {
        return row2;
    }

    public void setRow2(int row2) {
        this.row2 = row2;
    }

    public int getCol2() {
        return col2;
    }

    public void setCol2(int col2) {
        this.col2 = col2;
    }

    public int getRow3() {
        return row3;
    }

    public void setRow3(int row3) {
        this.row3 = row3;
    }

    public int getCol3() {
        return col3;
    }

    public void setCol3(int col3) {
        this.col3 = col3;
    }
}