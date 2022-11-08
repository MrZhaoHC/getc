package version4.frame;

import version4.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.awt.event.ActionListener;

/**
 * @author a_apple
 */
public class MainFrame {

    FunUtils funUtils = new FunUtils();
    private Fun fun = new Fun();
    private JFrame frame;
    private JPanel bigPanel;
    private JPanel colorPanel;
    /**
     * 自定义的MenuBar
     */
    private UpMenuBar menuBar;

    private JLabel labelRgb;
    private JLabel labelHex;
    private JLabel labelC;
    private JLabel labelFun;

    private JButton clearBtn;
    private JButton copyBtn;

    private JButton updateBtn;

    /**
     * 取色列表框
     */
    private JScrollPane listScroll;
    private JList<String> list;
    private DefaultListModel<String> dlm;

    /**
     * 鼠标第一次按下相对frame的位置,然后产生拖动事件
     */
    private int firstClickx;
    private int firstClicky;

    /**
     * 鼠标拖动窗体后相对（0,0）的位置,
     */
    private int zeroX;
    private int zeroY;

    /**
     * 是否能改变JLabel内容标记
     */
    private boolean isOk = true;
    /**
     * 窗口的打开位置
     */
    private int frameXIndex;
    private int frameYIndex;
    private int hotKey;

    /**
     * 来自JMenuBar的组件
     */
    private JMenuItem savaItem;
    private JMenuItem openItem;
    private JMenuItem exitItem;
    //private JMenuItem sysColorItem;
    private JRadioButtonMenuItem alwaysOnTopItem;
    private boolean isOnTop = false;

    private static PropertiesUtils prop = new PropertiesUtils();

    /**
     * 无参构造--初始化变量
     */
    public MainFrame() throws IOException {

        //初始胡fun
        fun.setF(funUtils.readProper());

        initGlobalFont(new Font("宋体", Font.BOLD, 16));

        frame = new JFrame();
        bigPanel = new JPanel();
        colorPanel = new JPanel();

        menuBar = new UpMenuBar();
        savaItem = menuBar.getSaveItem();
        openItem = menuBar.getOpenItem();
        exitItem = menuBar.getExitItem();
        //sysColorItem = menuBar.getSysColorItem();
        alwaysOnTopItem = menuBar.getAlwaysOnTopItem();

        clearBtn = new JButton("清空");
        copyBtn = new JButton("复制");
        updateBtn = new JButton("更改方程");

        labelRgb = new JLabel("rgb");
        labelC = new JLabel("c:");
        labelHex = new JLabel("hex");
        labelFun=new JLabel("方程");

        //管理JList数据的模型
        dlm = new DefaultListModel<>();
        list = new JList<>(dlm);
        listScroll = new JScrollPane(list);

        frameXIndex = Integer.parseInt(prop.getValue("frameXIndex", "400"));
        frameYIndex = Integer.parseInt(prop.getValue("frameYIndex", "400"));

        hotKey = Integer.parseInt(prop.getValue("hotKey", "67"));

        init();
        //获取颜色线程
        ColorGet colorGet = new ColorGet();
        colorGet.start();
    }

    /**
     * 初始化主界面
     */
    private void init() {

        frame.setLayout(null);
        frame.setBounds(frameXIndex, frameYIndex, 320, 240);
        frame.setUndecorated(true);

        bigPanel.setLayout(null);
        bigPanel.setBounds(0, 0, 320, 240);

        colorPanel.setBounds(30, 15, 100, 100);
        colorPanel.setBackground(new Color(255, 0, 0));

        labelRgb.setBounds(30, 125, 100, 30);
        //labelHex.setBounds(30, 185, 100, 30);
        labelC.setBounds(30, 155, 115, 30);
        labelFun.setBounds(150,120,650,30);
        labelFun.setFont(new Font(null,Font.PLAIN,13));
        labelFun.setSize(200,30);

        labelRgb.setBackground(new Color(255, 255, 0));
        //labelHex.setBackground(new Color(255, 0, 255));

        clearBtn.setBounds(155, 167, 65, 30);
        copyBtn.setBounds(230, 167, 65, 30);
        updateBtn.setBounds(30, 185, 105, 20);
        clearBtn.setBackground(new Color(240, 240, 240));
        copyBtn.setBackground(new Color(240, 240, 240));
        updateBtn.setBackground(new Color(240, 240, 240));
        clearBtn.setFont(new Font("宋体", Font.BOLD, 14));
        copyBtn.setFont(new Font("宋体", Font.BOLD, 14));
        updateBtn.setFont(new Font("宋体", Font.BOLD, 14));

        listScroll.setBounds(155, 15, 140, 100);
        //设置列表宽高
        list.setFixedCellHeight(30);
        list.setFixedCellWidth(700);
        //设置选择模式
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        bigPanel.add(colorPanel);
        bigPanel.add(labelRgb);
        bigPanel.add(labelC);
        bigPanel.add(labelFun);
        //bigPanel.add(labelHex);
        bigPanel.add(listScroll);
        bigPanel.add(clearBtn);
        bigPanel.add(copyBtn);
        bigPanel.add(updateBtn);

        /*添加*/
        frame.add(bigPanel);
        frame.setJMenuBar(menuBar);

        frameEvent();
        listEvent();
        historyEvent();
        frame.setVisible(true);

        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //显示输入方程对话框
                String input = JOptionPane.showInputDialog(
                        frame,
                        "输入方程:",
                        ""
                );
                if (input == null||input.length()==0) {
                    return;
                } else {
                    fun.setF(input);
                }
            }
        });
    }

    /**
     * 统一设置字体，父界面设置之后，所有由父界面进入的子界面都不需要再次设置字体
     */
    private static void initGlobalFont(Font font) {

        FontUIResource fontRes = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
    }

    /**
     * 主窗体frame事件监听
     */
    private void frameEvent() {
        //全局键盘监听事件alt
        Toolkit tk = Toolkit.getDefaultToolkit();
        tk.addAWTEventListener(new AllAwtEventListener(), AWTEvent.KEY_EVENT_MASK);

        //窗体frame
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                isOk = false;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                isOk = true;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    firstClickx = e.getX();
                    firstClicky = e.getY();
                }
            }
        });

        //拖动frame
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Point p = frame.getLocation();
                //获取此时鼠标的位置，然后通过鼠标的位置-相对位置=frame此时的位置
                zeroX = p.x + e.getX();
                zeroY = p.y + e.getY();
                frame.setLocation(zeroX - firstClickx, zeroY - firstClicky);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitAction();
            }
        });

        //清空按钮
        clearBtn.addActionListener(e -> dlm.removeAllElements());
    }

    /**
     * 列表监听事件
     */
    @SuppressWarnings("all")
    private void listEvent() {
        //清空会触发该事件
        //点击列表项也会触发该事件
        list.addListSelectionListener(e -> {

            //跳过异常---解决方法1
           /*isClickFirst = list.getLeadSelectionIndex() == 0 ? true : false;
           if(isClickFirst){
               clearBtn.setEnabled(false);
           }else {
               clearBtn.setEnabled(true);
           }*/

            //将该动作添加到事件分配线程中
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (dlm.getSize() > 0) {
                        int index;
                        index = list.getLeadSelectionIndex();
                        //获取hex值
                        String strHex = dlm.getElementAt(index);
                        //复制到系统剪切板
                        ClipboardUtils.setClipboardString(strHex);
                        //获得方程
                        String FunHex=strHex.substring(strHex.indexOf("程:")+3,strHex.length());
                        //去掉浓度方程
                        String colorHex = strHex.substring(0,strHex.indexOf(" "));

                        String[] arr=colorHex.split(",");
                        int r= Integer.parseInt(arr[0]);
                        int g= Integer.parseInt(arr[1]);
                        int b= Integer.parseInt(arr[2]);
                        Color hexInt = new Color(r,g,b);
                        //String replace = colorHex.replace(',', ' ');
                        colorPanel.setBackground(hexInt);

                        //String rgbStr = RgbUtils.toInt(colorHex);
                        System.out.println(colorHex);

                        labelRgb.setText(colorHex);
                        Fun fun1 = new Fun(hexInt);
                        fun1.setF(FunHex);
                        labelHex.setText(colorHex);
                        labelC.setText("c: "+fun1.getAnswer());
                        labelFun.setText(fun1.getF());
                        //设置选中条颜色
                        list.setSelectionBackground(hexInt);
                        copyBtn.setBackground(Color.green);
                    }
                }
            });
        });

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                copyBtn.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isOk = false;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    System.out.println("单击一次：" + list.getSelectedValue());
                }
            }
        });
    }

    /**
     * 打开、保存颜色信息到文件
     */
    private void historyEvent() {
        String endStr = ".dat";
        //文件选择对话框
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new MyFileFilter());
        chooser.setFileFilter(new FileNameExtensionFilter("Color File(*.dat)", "dat"));
        //saveItem
        savaItem.addActionListener(e -> {

            //设置打开的目录
            chooser.setCurrentDirectory(new File("c://"));
            //设置默认的文件保存名字
            chooser.setSelectedFile(new File("color.dat"));
            //打开文件对话框
            int result = chooser.showSaveDialog(null);
            //如果点击了保存选项
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if ((file != null) && file.getName().endsWith(endStr)) {
                    saveToFile(file.getPath());
                } else {
                    saveToFile(file.getPath() + endStr);
                }
            }
        });
        //openItem 打开
        openItem.addActionListener(e -> {
            //设置默认打开目录
            chooser.setCurrentDirectory(new File("c://"));
            chooser.showOpenDialog(null);
            File file = chooser.getSelectedFile();
            //点击保存按钮会返回一个文件对象
            if ((file != null) && file.getName().endsWith(endStr)) {
                String path = file.getPath();
                //获取路径--读取文件--放在JList
                readFile(path);
            } else {
                //logger.debug("未选中文件");
            }
        });

        exitItem.addActionListener(e -> {
            funUtils.setF(fun.getF());
            System.out.println(funUtils.getF());
            funUtils.writeProper();

            exitAction();
        });

        /*sysColorItem.addActionListener(e -> {
            Color color = JColorChooser.showDialog(frame, "选取颜色", null);
            if (color == null) {
                return;
            }
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            String hex = String.format("#%02x%02x%02x", red, green, blue);
            dlm.add(0, hex);
        });*/

        alwaysOnTopItem.addActionListener(this::actionPerformed);
    }

    /**
     * 退出程序执行的动作
     */
    private void exitAction() {

        Point location = frame.getLocationOnScreen();
        int x = (int) location.getX();
        int y = (int) location.getY();


        prop.setValue("frameXIndex", x + "");
        prop.setValue("frameYIndex", y + "");

        System.exit(0);
    }

    /**
     * 读取文件,并显示到JList
     */
    private void readFile(String path) {

        try (FileReader fr = new FileReader(new File(path)); BufferedReader br = new BufferedReader(fr)) {
            String info;
            while ((info = br.readLine()) != null && !info.startsWith("Date")) {
                System.out.println(info);
                dlm.add(0, info);
            }
        } catch (IOException e) {
        }
    }

    /**
     * 保存文件
     */
    private void saveToFile(String path) {

        try (FileWriter fw = new FileWriter(new File(path)); BufferedWriter bw = new BufferedWriter(fw)) {
            Object[] objects = dlm.toArray();
            for (Object object : objects) {
                String info = (String) object;
                bw.write(info);
                bw.newLine();
                bw.flush();
            }
            //记录文件创建日期
            fw.write("Date:" + new Date(System.currentTimeMillis()));
            bw.flush();
        } catch (IOException e) {
        }
    }

    private void actionPerformed(ActionEvent e) {
        if (!isOnTop) {
            alwaysOnTopItem.setSelected(true);
            frame.setAlwaysOnTop(true);
            isOnTop = true;
        } else {
            alwaysOnTopItem.setSelected(false);
            frame.setAlwaysOnTop(false);
            isOnTop = false;
        }
    }

    /**
     * 全局监听类--快捷键alt+c
     */
    class AllAwtEventListener implements AWTEventListener {

        @Override
        public void eventDispatched(AWTEvent event) {
            //先判断事件类型--键盘按下事件
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                KeyEvent keyEvent = (KeyEvent) event;
                hotKey = Integer.parseInt(prop.getValue("hotKey", "67"));
                if (keyEvent.isAltDown() && keyEvent.getKeyCode() == hotKey) {

                    //11.6 17:35
                    //在事件分配线程之外操作Swing组件，需要使用EventQueue类的invokeLater()方法
                    EventQueue.invokeLater(() -> {
                        String hex = labelHex.getText().trim();
                        ClipboardUtils.setClipboardString(hex);
                        dlm.add(0, hex);

                        //当前鼠标位置
                        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
                    });
                }
            }
        }
    }

    /**
     * 获取颜色的线程--不断显示在面板上
     */
    class ColorGet extends Thread {
        private Robot robot;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                // 使用中断机制，来合理终止线程
                try {
                    robot = new Robot();
                } catch (AWTException e) {
                }
                Point mousePoint = MouseInfo.getPointerInfo().getLocation();
                Color color = robot.getPixelColor((int) (mousePoint.getX() * 1), (int) (mousePoint.getY() * 1));
                /*String hex = RgbUtils.toHex(color.getRed(), color.getGreen(), color.getBlue());*/
                fun.setColor(color);
                String hex = color.getRed() + "," + color.getGreen() + "," + color.getBlue()+" c:"+fun.getAnswer()+" 方程: "+fun.getF();
                String rgb = color.getRed() + "," + color.getGreen() + "," + color.getBlue();

                //isOK代表暂停窗体内容变化标记
                if (isOk) {
                    //给面板设置为鼠标位置的颜色
                    colorPanel.setBackground(color);
                    //改变字体
                    labelHex.setText(hex);

                    if (fun.getAnswer().equals("方程错误")||fun.getAnswer().equals("RGB做分母")){
                        labelC.setForeground(Color.red);
                    }else {
                        labelC.setForeground(Color.BLACK);
                    }

                    labelC.setText("c:" + fun.getAnswer());
                    labelRgb.setText(rgb);
                    labelFun.setText(fun.getF());
                }
            }
        }
    }

    /**
     * 主函数
     */
    public static void main(String[] args) throws IOException {
        new MainFrame();
    }
}
