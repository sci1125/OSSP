import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

interface minesweeper{
    /* 맵 만들기 랜덤 지뢰만들기 눌럿을때 그림바뀌기 등등 */
    void setScreen();
    void createHeart();
    boolean isExist(int row, int col);
    int getMine(int row, int col);
    void addGrid(Component c, int x, int y, int w);
}

// 메인 화면
class TitlePanel extends JPanel{
    JPanelChange pc;
    JButton[] btn = new JButton[4];
    public TitlePanel(JPanelChange pc){
        this.pc = pc;
        setLayout(null);
        JLabel lb = new JLabel("지뢰 찾기");
        lb.setFont(new Font("맑은 고딕", Font.PLAIN, 50));
        lb.setBounds(270, 400, 320, 90);
        lb.setHorizontalAlignment(SwingConstants.CENTER);
        String path = TitlePanel.class.getResource("").getPath();
        ImageIcon im = new ImageIcon(path+"지뢰 모양"
        		+ ".png");
        JLabel imlb = new JLabel(im);
        imlb.setBounds(0, 0, 900, 500);
        String[] st = {"10 x 10", "15 x 15", "20 x 20", "custom"};
        int j=0;
        for(int i=100;i<800;i+=200){
            btn[j] = new JButton(st[j]);
            btn[j].setBounds(i, 550, 100, 30);
            btn[j].addActionListener(new MyActionListener());
            add(btn[j++]);
        }
        add(lb);
        add(imlb);
    }
    class MyActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            JButton b = (JButton)e.getSource();
            if(b.getText().equals("10 x 10")) {
            	pc.change("panel10");
            	pc.panel10.setListener();
                TimeSingleTone.getInstance().setControlThread(true);
            }                
            else if(b.getText().equals("15 x 15")) {
            	pc.change("panel15");
            	pc.panel15.setListener();
                TimeSingleTone.getInstance().setControlThread(true);
            }                
            else if (b.getText().equals("20 x 20")) {         
            	pc.change("panel20");
            	pc.panel20.setListener();
            	TimeSingleTone.getInstance().setControlThread(true);
            }                
            else {
            	// custom grid case 
            	JTextField xField = new JTextField(5);
                JTextField yField = new JTextField(5);

                JPanel myPanel = new JPanel();
                myPanel.add(new JLabel("가로 :"));
                myPanel.add(xField);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("세로 :"));
                myPanel.add(yField);

                // 가로 세로 규격을 묻는 다이얼로그
                int result = JOptionPane.showConfirmDialog(null, myPanel, 
                         "가로 세로 값을 모두 입력해주세요", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                	pc.panelCustom = new PanelCustom(pc, Integer.parseInt(yField.getText()), Integer.parseInt(xField.getText()));                	
                	pc.change("custom size");
                	pc.panelCustom.setListener();
                	TimeSingleTone.getInstance().setControlThread(true);
                }                        
            }            
        }
    }
}

// 10 x 10 지뢰찾기
class Panel10 extends JPanel implements minesweeper, GameTimeListener{   /* 각각 지뢰찾기 화면 구성 */
    JPanelChange pc;
    GridBagLayout grid;
    GridBagConstraints gbc = new GridBagConstraints();
    JButton[][] btn = new JButton[10][10];
    String[][] arr = new String[10][10];
    private JTextField textFieldPlayTime;    
    TimeSingleTone timeSingleTone;
    
    public Panel10(JPanelChange pc){   
        grid = new GridBagLayout();
        setLayout(grid);
        this.pc = pc;
        timeSingleTone = TimeSingleTone.getInstance();        
        setScreen();        
    }    
    
    public void setListener() {
    	timeSingleTone.setListener(this);
    }
    
    @Override
    public void setScreen(){    	
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx=1.0;
        gbc.weighty=1.0;

        JTextField tx = new JTextField(20);
        tx.setText("9");
        tx.setHorizontalAlignment(JLabel.CENTER);
        tx.setEditable(false);
        createHeart();
                
        timeSingleTone.setUpdateTime(0);
        
        textFieldPlayTime = new JTextField(20);
        textFieldPlayTime.setText("Play Time : 0");
        textFieldPlayTime.setHorizontalAlignment(JLabel.CENTER);
        textFieldPlayTime.setEditable(false);                                   
        
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                btn[i][j] = new JButton(String.valueOf(arr[i][j]));
                btn[i][j].setBackground(Color.PINK);
                btn[i][j].setForeground(Color.PINK);
                btn[i][j].setPreferredSize(new Dimension(10, 10));
                bntActionListener bn = new bntActionListener(btn, arr, i, j, 10);
                btn[i][j].addMouseListener(new bntMouseAdapter(tx, 10, btn));
                btn[i][j].addActionListener(bn);
                addGrid(btn[i][j], j, i+1, 1);
            }
        }
        addGrid(tx, 0, 0, 10);
        addGrid(textFieldPlayTime, 0, 100, 15);                      
    }
    
    public void createHeart() {
        Random rand = new Random();
        int mine = 9;
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++)
                arr[i][j]="0";
        }
        while (mine-- > 0) {
            int row = rand.nextInt(10);
            int col = rand.nextInt(10);
            if (arr[row][col].equals("-1"))
                mine++;
            if (arr[row][col].equals("0"))
                arr[row][col] = "-1";
        }
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                int a = getMine(i, j);
                if(arr[i][j].equals("0")&&a!=0)
                    arr[i][j] = String.valueOf(a);
            }
        }
    }
    
    public boolean isExist(int row, int col){
        if(row<0||row>=10||col<0||col>=10)
            return false;
        return arr[row][col].equals("-1");
    }
    
    public int getMine(int row, int col){
        int cnt = 0;
        if(isExist(row-1, col-1)) cnt++;
        if(isExist(row-1, col)) cnt++;
        if(isExist(row-1, col+1)) cnt++;
        if(isExist(row, col-1)) cnt++;
        if(isExist(row, col+1)) cnt++;
        if(isExist(row+1, col-1)) cnt++;
        if(isExist(row+1, col)) cnt++;
        if(isExist(row+1, col+1)) cnt++;

        return cnt;
    }
    
    public void addGrid(Component c, int x, int y, int w){
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = 1;
        grid.setConstraints(c, gbc);
        add(c);
    }
    
    static class bntMouseAdapter extends MouseAdapter{
        JButton[][] btn;
        JTextField tx;
        int k;
        int customRow, customCol;           
        TimeSingleTone timeSingleTone;
      
        public bntMouseAdapter(JTextField tx, int k, JButton[][] btn){
            this.tx = tx;
            this.k = k;
            this.customRow = k;
            this.customCol = k;
            this.btn = btn;            
            timeSingleTone = TimeSingleTone.getInstance();
        }
        
        public bntMouseAdapter(JTextField tx, int customRow, int customCol, JButton[][] btn){
            this.tx = tx;            
            this.customRow = customRow;
            this.customCol = customCol;
            this.btn = btn;
            timeSingleTone = TimeSingleTone.getInstance();
        }
        
        public void mouseClicked(MouseEvent e){
            if(e.getButton()==MouseEvent.BUTTON3){
            	JButton b = (JButton)e.getSource();
                if(!tx.getText().equals("0")&&b.isEnabled()){
                    b.setEnabled(false);
                    bntActionListener.setIcon(b, "bow.png");
                    tx.setText(String.valueOf(Integer.parseInt(tx.getText())-1));
                }
                int state=1;
                for(int i=0;i<customRow;i++){
                    for(int j=0;j<customCol;j++){
                        if(btn[i][j].isEnabled())
                           state = 0;
                    }
                }
                if(state == 1) {
                	// stop thread
                	timeSingleTone.setControlThread(false);                
                	JOptionPane.showMessageDialog(null, "Play Time : " + String.valueOf(timeSingleTone.getUpdateTime()) + "\n지뢰를 모두 찾았습니다!", "안내", JOptionPane.INFORMATION_MESSAGE);
                	timeSingleTone = null;
                }                    
            }
        }
    }
    @Override
	public void onTimeTick(int timeSecond) {		
		if (textFieldPlayTime != null) {
			textFieldPlayTime.setText("Play Time : " + String.valueOf(timeSecond));
		}				
	}
    
    static class bntActionListener implements ActionListener{
        String[][] arr;
        JButton[][] btn;
        int row, col, k;        
        TimeSingleTone timeSingleTone; //플레이 시간을 저장해주고 있는 싱글톤 패턴의 클래스
        private JPanel currentPanel;
        
        public bntActionListener(JButton[][] btn, String[][] arr, int row, int col, int k){
            this.btn = btn;
            this.arr = arr;
            this.row = row;
            this.col = col;
            this.k = k;            
            
            // 싱글톤 초기화
            timeSingleTone = TimeSingleTone.getInstance();
        }
        
        public void setCurrentPanel(JPanel panel) {
        	currentPanel = panel;
        }
            
        public void findAction(int row, int col, int k){
            if(row<0||row>=k||col<0||col>=k||arr[row][col].equals("-2")||arr[row][col].equals("-1"))
                return;
            if(arr[row][col].equals("0")){
                btn[row][col].setEnabled(false);
                setIcon(btn[row][col], btn[row][col].getText()+".png");
                arr[row][col] = "-2";
                btn[row][col].setText("");
                findAction(row-1, col-1, k);
                findAction(row-1, col, k);
                findAction(row-1, col+1, k);
                findAction(row, col-1, k);
                findAction(row, col+1, k);
                findAction(row+1, col-1, k);
                findAction(row+1, col, k);
                findAction(row+1, col+1, k);
            }
            else{
                btn[row][col].setEnabled(false);
                setIcon(btn[row][col], arr[row][col]+".png");
                arr[row][col] = "-2";
            }
        }
        
        private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
            Image img = icon.getImage();
            Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        }

        public static void setIcon(JButton b, String pa){
            String path = TitlePanel.class.getResource("").getPath();
            b.setText("");
            ImageIcon icon = new ImageIcon(path+pa);
            b.setIcon(resizeIcon(icon, b.getWidth(), b.getHeight()));
            b.setDisabledIcon(resizeIcon(icon, b.getWidth(), b.getHeight()));
            b.setContentAreaFilled(false);
        }

        public void actionPerformed(ActionEvent e){
            JButton b = (JButton)e.getSource();
            b.setEnabled(false);
            if(e.getActionCommand().equals("-1")){
                setIcon(b, "지뢰.png");
                
               // 타임 스레드 종료
               timeSingleTone.setControlThread(false);               
                
                // 싱글톤 클래스로부터 저장되어있던 시간 값 가져와서 다이얼로그에 표시
                int result = JOptionPane.showConfirmDialog(null, "Play Time : " + String.valueOf(timeSingleTone.getUpdateTime()) + "\n다시하시겠습니까?", "안내", JOptionPane.YES_NO_OPTION);
                if(result!=0)  {
                	timeSingleTone.setUpdateTime(0);
                	timeSingleTone = null;
                    System.exit(0);
                }                	
                else {
                	timeSingleTone.setUpdateTime(0);
                	timeSingleTone = null;
                	JComponent comp = (JComponent) e.getSource();
                	Window win = SwingUtilities.getWindowAncestor(comp);
                	win.dispose();
                    new heartsweeper();
                }                	
            }
            else if(e.getActionCommand().equals("0")) findAction(row, col, k);
            else{
                switch (e.getActionCommand()){
                    case "1": setIcon(b, "1.png");break;
                    case "2": setIcon(b, "2.png");break;
                    case "3": setIcon(b, "3.png");break;
                    case "4": setIcon(b, "4.png");break;
                    case "5": setIcon(b, "5.png");break;
                    case "6": setIcon(b, "6.png");break;
                    case "7": setIcon(b, "7.png");break;
                    case "8": setIcon(b, "8.png");break;
                }
            }
            int state=1;
            for(int i=0;i<k;i++){
                for(int j=0;j<k;j++){
                    if(btn[i][j].isEnabled())
                        state = 0;
                }
            }
            if(state == 1) {
            	timeSingleTone.setControlThread(false);
            	JOptionPane.showMessageDialog(null, "Play Time : " + String.valueOf(timeSingleTone.getUpdateTime()) + "\n지뢰를 모두 찾았습니다!", "안내", JOptionPane.INFORMATION_MESSAGE);
            	timeSingleTone.setUpdateTime(0);
            	timeSingleTone = null;
            	JComponent comp = (JComponent) e.getSource();
          	    Window win = SwingUtilities.getWindowAncestor(comp);
          	    win.dispose();
                new heartsweeper();
            }                
        }
    }
	
}

// 15 x 15 지뢰찾기
class Panel15 extends JPanel implements minesweeper, GameTimeListener{
    JPanelChange pc;
    GridBagLayout grid;
    GridBagConstraints gbc = new GridBagConstraints();
    JButton[][] btn = new JButton[15][15];
    String[][] arr = new String[15][15];
    private TimeSingleTone timeSingleTone;
    JTextField textFieldPlayTime;    
    
    public Panel15(JPanelChange pc){
        grid = new GridBagLayout();
        setLayout(grid);
        this.pc = pc;
        // 싱글톤 객체 초기화
        timeSingleTone = TimeSingleTone.getInstance();        
        setScreen();        
    }
    
    public void setListener() {
    	timeSingleTone.setListener(this);
    }
   
    
    @Override
    public void setScreen(){
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx=1.0;
        gbc.weighty=1.0;

        JTextField tx = new JTextField(20);
        tx.setText("35");
        tx.setHorizontalAlignment(JLabel.CENTER);
        tx.setEditable(false);
        createHeart();
               
        timeSingleTone.setUpdateTime(0);

        textFieldPlayTime = new JTextField(20);
        textFieldPlayTime.setText("Play Time : 0");
        textFieldPlayTime.setHorizontalAlignment(JLabel.CENTER);
        textFieldPlayTime.setEditable(false);                
       
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                btn[i][j] = new JButton(String.valueOf(arr[i][j]));
                btn[i][j].setBackground(Color.PINK);
                btn[i][j].setForeground(Color.PINK);
                btn[i][j].setPreferredSize(new Dimension(10, 10));
                Panel10.bntActionListener bn = new Panel10.bntActionListener(btn, arr, i, j, 15);
                btn[i][j].addActionListener(bn);
                btn[i][j].addMouseListener(new Panel10.bntMouseAdapter(tx, 15, btn));
                addGrid(btn[i][j], j, i+1, 1);
            }
        }
        addGrid(tx, 0, 0, 15);
        addGrid(textFieldPlayTime, 0, 100, 15);                     
    }
    
    public void createHeart() {
        Random rand = new Random();
        int mine = 35;
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++)
                arr[i][j]="0";
        }
        while (mine-- > 0) {
            int row = rand.nextInt(15);
            int col = rand.nextInt(15);
            if (arr[row][col].equals("-1"))
                mine++;
            if (arr[row][col].equals("0"))
                arr[row][col] = "-1";
        }
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                int a = getMine(i, j);
                if(arr[i][j].equals("0")&&a!=0)
                    arr[i][j] = String.valueOf(a);
            }
        }
    }
    
    public boolean isExist(int row, int col){
        if(row<0||row>=15||col<0||col>=15)
            return false;
        return arr[row][col].equals("-1");
    }
    
    public int getMine(int row, int col){
        int cnt = 0;
        if(isExist(row-1, col-1)) cnt++;
        if(isExist(row-1, col)) cnt++;
        if(isExist(row-1, col+1)) cnt++;
        if(isExist(row, col-1)) cnt++;
        if(isExist(row, col+1)) cnt++;
        if(isExist(row+1, col-1)) cnt++;
        if(isExist(row+1, col)) cnt++;
        if(isExist(row+1, col+1)) cnt++;

        return cnt;
    }
    
    public void addGrid(Component c, int x, int y, int w){
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = 1;
        grid.setConstraints(c, gbc);
        add(c);
    }

    @Override
	public void onTimeTick(int timeSecond) {
		if (textFieldPlayTime != null) {
			textFieldPlayTime.setText("Play Time : " + String.valueOf(timeSecond));
		}				
	}
}

//20 x 20 지뢰찾기
class Panel20 extends JPanel implements minesweeper, GameTimeListener{
    JPanelChange pc;
    GridBagLayout grid;
    GridBagConstraints gbc = new GridBagConstraints();
    JButton[][] btn = new JButton[20][20];
    String[][] arr = new String[20][20];
    TimeSingleTone timeSingleTone;
    JTextField textFieldPlayTime;
    
    public Panel20(JPanelChange pc){
        grid = new GridBagLayout();
        setLayout(grid);
        this.pc = pc;
        timeSingleTone = TimeSingleTone.getInstance();        
        setScreen();
    }
    
    public void setListener() {
    	timeSingleTone.setListener(this);
    }
    
    @Override
    public void setScreen(){
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx=1.0;
        gbc.weighty=1.0;

        JTextField tx = new JTextField(20);
        tx.setText("45");
        tx.setHorizontalAlignment(JLabel.CENTER);
        tx.setEditable(false);
        createHeart();
                
        timeSingleTone.setUpdateTime(0);
        
        textFieldPlayTime = new JTextField(20);
        textFieldPlayTime.setText("Play Time : 0");
        textFieldPlayTime.setHorizontalAlignment(JLabel.CENTER);
        textFieldPlayTime.setEditable(false);                
        
        for(int i=0;i<20;i++){
            for(int j=0;j<20;j++){
                btn[i][j] = new JButton(String.valueOf(arr[i][j]));
                btn[i][j].setBackground(Color.PINK);
                btn[i][j].setForeground(Color.PINK);
                btn[i][j].setPreferredSize(new Dimension(10, 10));
                Panel10.bntActionListener bn = new Panel10.bntActionListener(btn, arr, i, j, 20);
                btn[i][j].addActionListener(bn);
                btn[i][j].addMouseListener(new Panel10.bntMouseAdapter(tx, 20, btn));
                addGrid(btn[i][j], j, i+1, 1);
            }
        }
        addGrid(tx, 0, 0, 20);
        addGrid(textFieldPlayTime, 0, 100, 15);       
    }
    
    public void createHeart() {
        Random rand = new Random();
        int mine = 45;
        for(int i=0;i<20;i++){
            for(int j=0;j<20;j++)
                arr[i][j]="0";
        }
        while (mine-- > 0) {
            int row = rand.nextInt(20);
            int col = rand.nextInt(20);
            if (arr[row][col].equals("-1"))
                mine++;
            if (arr[row][col].equals("0"))
                arr[row][col] = "-1";
        }
        for(int i=0;i<20;i++){
            for(int j=0;j<20;j++){
                int a = getMine(i, j);
                if(arr[i][j].equals("0")&&a!=0)
                    arr[i][j] = String.valueOf(a);
            }
        }
    }
    
    public boolean isExist(int row, int col){
        if(row<0||row>=20||col<0||col>=20)
            return false;
        return arr[row][col].equals("-1");
    }
    
    public int getMine(int row, int col){
        int cnt = 0;
        if(isExist(row-1, col-1)) cnt++;
        if(isExist(row-1, col)) cnt++;
        if(isExist(row-1, col+1)) cnt++;
        if(isExist(row, col-1)) cnt++;
        if(isExist(row, col+1)) cnt++;
        if(isExist(row+1, col-1)) cnt++;
        if(isExist(row+1, col)) cnt++;
        if(isExist(row+1, col+1)) cnt++;

        return cnt;
    }
    
    public void addGrid(Component c, int x, int y, int w){
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = 1;
        grid.setConstraints(c, gbc);
        add(c);
    }

    @Override
	public void onTimeTick(int timeSecond) {
		if (textFieldPlayTime != null) {
			textFieldPlayTime.setText("Play Time : " + String.valueOf(timeSecond));
		}				
	}
}

// custom size 로 지뢰찾기 그리드를 만드는 패널 클래스
class PanelCustom extends JPanel implements minesweeper, GameTimeListener{
    JPanelChange pc;
    GridBagLayout grid;
    GridBagConstraints gbc = new GridBagConstraints();
    JButton[][] btn;
    String[][] arr;
    int customRow = 0; // 가로 규격 Row
    int customCol = 0; // 세로 규격 Column
    TimeSingleTone timeSingleTone;
    JTextField textFieldPlayTime;
    
    public PanelCustom(JPanelChange pc, int row, int col){
        grid = new GridBagLayout();
        setLayout(grid);
        this.pc = pc;           
        customRow = row;
        customCol = col;
        btn = new JButton[customRow][customCol];
        arr = new String[customRow][customCol];
        timeSingleTone = TimeSingleTone.getInstance();        
        setScreen();
    }
    
    public void setListener() {
    	timeSingleTone.setListener(this);
    }
    
    @Override
    public void setScreen(){
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx=1.0;
        gbc.weighty=1.0;

        JTextField tx = new JTextField(20);
        tx.setText(String.valueOf((customRow * customCol) / 9));
        tx.setHorizontalAlignment(JLabel.CENTER);
        tx.setEditable(false);
        createHeart();
               
        timeSingleTone.setUpdateTime(0);
        
        textFieldPlayTime = new JTextField(20);
        textFieldPlayTime.setText("Play Time : 0");
        textFieldPlayTime.setHorizontalAlignment(JLabel.CENTER);
        textFieldPlayTime.setEditable(false);                             
        
        for(int i=0;i<customRow;i++){
            for(int j=0;j<customCol;j++){
                btn[i][j] = new JButton(String.valueOf(arr[i][j]));
                btn[i][j].setBackground(Color.PINK);
                btn[i][j].setForeground(Color.PINK);
                btn[i][j].setPreferredSize(new Dimension(10, 10));
                Panel10.bntActionListener bn = new Panel10.bntActionListener(btn, arr, i, j, customRow);
                btn[i][j].addActionListener(bn);
                btn[i][j].addMouseListener(new Panel10.bntMouseAdapter(tx, customRow, customCol, btn));
                addGrid(btn[i][j], j, i+1, 1);
            }
        }
        addGrid(tx, 0, 0, 20);
        addGrid(textFieldPlayTime, 0, 100, 15);       
    }
    public void createHeart() {
        Random rand = new Random();
        // custom 규격의 경우 mine의 적당한 수를 위해계산식을 세움.
        int mine = (customRow * customCol) / 9;
        for(int i=0;i<customRow;i++){
            for(int j=0;j<customCol;j++)
                arr[i][j]="0";
        }
        while (mine-- > 0) {
            int row = rand.nextInt(customRow);
            int col = rand.nextInt(customCol);
            if (arr[row][col].equals("-1"))
                mine++;
            if (arr[row][col].equals("0"))
                arr[row][col] = "-1";
        }
        for(int i=0;i<customRow;i++){
            for(int j=0;j<customCol;j++){
                int a = getMine(i, j);
                if(arr[i][j].equals("0")&&a!=0)
                    arr[i][j] = String.valueOf(a);
            }
        }
    }
    
    public boolean isExist(int row, int col){
        if(row<0||row>=customRow||col<0||col>=customCol)
            return false;
        return arr[row][col].equals("-1");
    }
    
    public int getMine(int row, int col){
        int cnt = 0;
        if(isExist(row-1, col-1)) cnt++;
        if(isExist(row-1, col)) cnt++;
        if(isExist(row-1, col+1)) cnt++;
        if(isExist(row, col-1)) cnt++;
        if(isExist(row, col+1)) cnt++;
        if(isExist(row+1, col-1)) cnt++;
        if(isExist(row+1, col)) cnt++;
        if(isExist(row+1, col+1)) cnt++;

        return cnt;
    }
    
    public void addGrid(Component c, int x, int y, int w){
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = 1;
        grid.setConstraints(c, gbc);
        add(c);
    }

    @Override
	public void onTimeTick(int timeSecond) {
		if (textFieldPlayTime != null) {
			textFieldPlayTime.setText("Play Time : " + String.valueOf(timeSecond));
		}				
	}
}

class JPanelChange extends JFrame{
    TitlePanel titlePanel = null;
    Panel10 panel10 = null;
    Panel15 panel15 = null;
    Panel20 panel20 = null;
    PanelCustom panelCustom = null;  

    public void change(String panelName){
        getContentPane().removeAll();
        switch (panelName) {
            case "titlePanel":
            	TimeSingleTone.getInstance().setControlThread(false);            	
                getContentPane().add(titlePanel);               
                break;
            case "panel10":            
                getContentPane().add(panel10);              
                break;
            case "panel15":            	
                getContentPane().add(panel15);                
                break;
            case "panel20":            
            	getContentPane().add(panel20);            
            	break;
            default: 
            	getContentPane().add(panelCustom);            	
            	break;
        }
        revalidate();
        repaint();
    }
}

public class heartsweeper extends JFrame {
    JPanelChange panelSet = new JPanelChange();
    public heartsweeper(){  // 생성자 (화면 구성)
        panelSet.setTitle("지뢰 찾기");

        panelSet.titlePanel = new TitlePanel(panelSet);
        panelSet.panel10 = new Panel10(panelSet);
        panelSet.panel15 = new Panel15(panelSet);
        panelSet.panel20 = new Panel20(panelSet);

        createMenu();
        panelSet.add(panelSet.titlePanel);
        panelSet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelSet.setSize(900, 700);
        panelSet.setVisible(true);
    }

    public void createMenu(){   // 메뉴 바 만드는 함수
        JMenuBar mb = new JMenuBar();
        String[] barName = {"Game", "Help"};
        String[][] subItem = {{"Home", "10 x 10", "15 x 15", "20 x 20", "custom"}, {"도움말"}};
        JMenu[] menu = new JMenu[4];
        JMenuItem[][] menuItems = new JMenuItem[4][];
        for(int i=0;i<barName.length;i++){
            menu[i] = new JMenu(barName[i]);
            menu[i].setFont(new Font("맑은 고딕", Font.ITALIC, 13));
            menuItems[i] = new JMenuItem[subItem[i].length];
            for (int j=0;j<subItem[i].length;j++) {
                menuItems[i][j] = new JMenuItem(subItem[i][j]);
                menuItems[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                menuItems[i][j].addActionListener(new MenuActionListener());
                menuItems[i][j].setFont(new Font("맑은 고딕", Font.ITALIC, 13));
                menu[i].add(menuItems[i][j]);
                if(i==0 && j==0) menu[i].addSeparator();
            }
            mb.add(menu[i]);
        }
        panelSet.setJMenuBar(mb);
    }

    class MenuActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String cmd = e.getActionCommand();                                   
            
            switch (cmd) {
                case "Home":                 	
                	panelSet.change("titlePanel");                	
                	break;
                case "10 x 10":
                	System.out.println("Selected Menu : 10 x 10");
                	panelSet.panel10 = new Panel10(panelSet);
                	panelSet.change("panel10");
                	panelSet.panel10.setListener();
                	TimeSingleTone.getInstance().setControlThread(true);
                	break;
                case "15 x 15":
                	System.out.println("Selected Menu : 15 x 15");
                	panelSet.panel15 = new Panel15(panelSet);
                	panelSet.change("panel15");
                	panelSet.panel15.setListener();
                	TimeSingleTone.getInstance().setControlThread(true);
                	break;
                case "20 x 20":
                	System.out.println("Selected Menu : 20 x 20");
                	panelSet.panel20 = new Panel20(panelSet);
                	panelSet.change("panel20");
                	panelSet.panel20.setListener();
                	TimeSingleTone.getInstance().setControlThread(true);
                	break;
                case "custom size":
                	System.out.println("Selected Menu : custom size");
                	JTextField xField = new JTextField(5);
                    JTextField yField = new JTextField(5);

                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("가로 :"));
                    myPanel.add(xField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("세로 :"));
                    myPanel.add(yField);

                    // 좌상단 메뉴에서 커스텀 규격을 선택 했을 때 규격을 묻는 다이얼로그 표시 
                    int result = JOptionPane.showConfirmDialog(null, myPanel, 
                             "가로 세로 값을 모두 입력해주세요", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                    	panelSet.panelCustom = new PanelCustom(panelSet, Integer.parseInt(yField.getText()), Integer.parseInt(xField.getText()));
                    	panelSet.change("custom size");
                    	panelSet.panelCustom.setListener();
                    	TimeSingleTone.getInstance().setControlThread(true);
                       System.out.println("가로 (Row) value: " + yField.getText());
                       System.out.println("세로 (Col) value: " + xField.getText());
                    }
                	break;
                default:
                	// 도움말
                    JTextArea textArea = new JTextArea(6, 25);
                    String path = TitlePanel.class.getResource("").getPath();
                    StringBuilder line = new StringBuilder();
                    try {
                        FileReader r = new FileReader(path + "message.txt");
                        int k;
                        for (; ; ) {
                            k = r.read();
                            if (k == -1) break;
                            line.append((char) k);
                        }
                        r.close();
                    } catch (IOException t) {
                        t.printStackTrace();
                    }
                    textArea.setText(line.toString());
                    JOptionPane.showMessageDialog(panelSet, textArea);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        new heartsweeper();
    }
}