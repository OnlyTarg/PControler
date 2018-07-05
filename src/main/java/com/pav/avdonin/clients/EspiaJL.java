package com.pav.avdonin.clients;

import com.google.gson.Gson;
import com.pav.avdonin.Main;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by CleBo on 28.02.2018.
 */
public class EspiaJL {
    transient int hash=0;
    Properties properties= new Properties();
    //String name;
    JFrame frame;
    InetAddress ipAddress;
    Socket socket;
    boolean isAllowed;
    DataInputStream datain;
    DataOutputStream dataout;
    JButton b1, b2, b3, b4, b5, b6, b7, b8;
    JButton b1info, b2info, b3info, b4info, b5info, b6info, b7info, b8info;
    JButton b1who, b2who, b3who, b4who, b5who, b6who, b7who, b8who;
    JLabel connectionStatus = new JLabel("Статус соединения");
    transient Clip clipClick, clipZvonok, clipDoor;
    String currentIP;
    {
        try {
            currentIP = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    int [] countMigalka = {0,0,0,0,0,0,0,0,0}; //Счетчик для выхода из потока в случае, если была
    // повторно нажата кнопка до переставания мигания после первого нажаия
    // порядковый номер элемента массива соответсвует номеру кнопки (начинае с первого елемента). Тоесть countMigalka[1] Соответствует первой кнопке
    transient Logger logger= Logger.getLogger(Main.class.getName());
    Point point = null;


    public EspiaJL(Point point) {
        this.point = point;
        createLogger();
        try {
            properties.load(getClass().getResourceAsStream("/settings.properties"));
        } catch (IOException e) {
            StackTraceElement[] stack = e.getStackTrace();
            logger.log(Level.INFO, e.toString() + "\r\n" + stack[0] + "\r\n it's some problem with loading properties file");
        }
        window();
        buttons();
        createClient();
        readData();
        close();
    }




    public EspiaJL()  {
        createLogger();
        try {
            properties.load(getClass().getResourceAsStream("/settings.properties"));
        } catch (IOException e) {
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n it's some problem with loading properties file");
        }
        window();
            buttons();
            createClient();
            readData();
            close();
    }

    private void createLogger() {
        SimpleFormatter txtFormatter = new SimpleFormatter ();
        FileHandler fh = null;
        try {
            fh = new FileHandler("logFile.txt",true);
            fh.setFormatter(txtFormatter);
            logger.addHandler(fh);
        } catch (IOException e) {
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
            /* JOptionPane.showMessageDialog(null,"Помилка при створенні логгера");*/

        }
    }
    public void window() {
        //Создаю основное окно
        frame = new JFrame();
        if(point!=null){
            frame.setLocation(point);
        }
        frame.setTitle("EspiaJL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    //com.pav.avdonin.sql.SQL.goodExitInformer(hash);

                    dataout.writeUTF("exiting");
                    dataout.flush();
                    frame.dispose();

                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Помилка під час відправлення інформації про вихід до сервуру");
                    frame.dispose();
                } catch (Exception e1) {
                    frame.dispose();
                }
            }
        });
        frame.setSize(227,560);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);

    }
    public void buttons() {
        //Создаю все кнопки
        b1 = new JButton("");
        b2 = new JButton("");
        b3 = new JButton("");
        b4 = new JButton("");
        b5 = new JButton("");
        b6 = new JButton("");
        b7 = new JButton("");
        b8 = new JButton("");
        b1info = new JButton("....");
        b2info = new JButton("....");
        b3info = new JButton("....");
        b4info = new JButton("....");
        b5info = new JButton("....");
        b6info = new JButton("....");
        b7info = new JButton("....");
        b8info = new JButton("....");

        b1who = new JButton("....");
        b2who = new JButton("....");
        b3who = new JButton("....");
        b4who = new JButton("....");
        b5who = new JButton("....");
        b6who = new JButton("....");
        b7who = new JButton("....");
        b8who = new JButton("....");

        b1info.setVisible(false);
        b2info.setVisible(false);
        b3info.setVisible(false);
        b4info.setVisible(false);
        b5info.setVisible(false);
        b6info.setVisible(false);
        b7info.setVisible(false);
        b8info.setVisible(false);


        b1who.setVisible(false);
        b2who.setVisible(false);
        b3who.setVisible(false);
        b4who.setVisible(false);
        b5who.setVisible(false);
        b6who.setVisible(false);
        b7who.setVisible(false);
        b8who.setVisible(false);


        frame.setLayout(null);
        frame.repaint();


        Font font = new Font("Times new Roman", Font.BOLD, 20);
        Font fontinfo = new Font("Times new Roman", Font.BOLD, 14);


        b1.setFont(font);
        b2.setFont(font);
        b3.setFont(font);
        b4.setFont(font);
        b5.setFont(font);
        b6.setFont(font);
        b7.setFont(font);
        b8.setFont(font);
        connectionStatus.setFont(font);
        b1info.setFont(fontinfo);
        b2info.setFont(fontinfo);
        b3info.setFont(fontinfo);
        b4info.setFont(fontinfo);
        b5info.setFont(fontinfo);
        b6info.setFont(fontinfo);
        b7info.setFont(fontinfo);
        b8info.setFont(fontinfo);
        b1who.setFont(fontinfo);
        b2who.setFont(fontinfo);
        b3who.setFont(fontinfo);
        b4who.setFont(fontinfo);
        b5who.setFont(fontinfo);
        b6who.setFont(fontinfo);
        b7who.setFont(fontinfo);
        b8who.setFont(fontinfo);


        b1.setBounds(10, 10, 200, 50);
        b2.setBounds(10, 70, 200, 50);
        b3.setBounds(10, 130, 200, 50);
        b4.setBounds(10, 190, 200, 50);
        b5.setBounds(10, 250, 200, 50);
        b6.setBounds(10, 310, 200, 50);
        b7.setBounds(10, 370, 200, 50);
        b8.setBounds(10, 430, 200, 50);

        b1info.setBounds(212, 10, 120, 25);
        b2info.setBounds(212, 70, 120, 25);
        b3info.setBounds(212, 130, 120, 25);
        b4info.setBounds(212, 190, 120, 25);
        b5info.setBounds(212, 250, 120, 25);
        b6info.setBounds(212, 310, 120, 25);
        b7info.setBounds(212, 370, 120, 25);
        b8info.setBounds(212, 430, 120, 25);

        b1who.setBounds(212, 35, 120, 25);
        b2who.setBounds(212, 95, 120, 25);
        b3who.setBounds(212, 155, 120, 25);
        b4who.setBounds(212, 215, 120, 25);
        b5who.setBounds(212, 275, 120, 25);
        b6who.setBounds(212, 335, 120, 25);
        b7who.setBounds(212, 395, 120, 25);
        b8who.setBounds(212, 455, 120, 25);


        connectionStatus.setBounds(45, 490, 200, 30);

        //connectionStatus.setHorizontalTextPosition(SwingConstants.CENTER);


        b1.setBackground(Color.RED);
        b2.setBackground(Color.RED);
        b3.setBackground(Color.RED);
        b4.setBackground(Color.RED);
        b5.setBackground(Color.RED);
        b6.setBackground(Color.RED);
        b7.setBackground(Color.RED);
        b8.setBackground(Color.RED);
        connectionStatus.setForeground(Color.BLACK);
        b1info.setBackground(Color.YELLOW);
        b2info.setBackground(Color.YELLOW);
        b3info.setBackground(Color.YELLOW);
        b4info.setBackground(Color.YELLOW);
        b5info.setBackground(Color.YELLOW);
        b6info.setBackground(Color.YELLOW);
        b7info.setBackground(Color.YELLOW);
        b8info.setBackground(Color.YELLOW);
        b1who.setBackground(Color.YELLOW);
        b2who.setBackground(Color.YELLOW);
        b3who.setBackground(Color.YELLOW);
        b4who.setBackground(Color.YELLOW);
        b5who.setBackground(Color.YELLOW);
        b6who.setBackground(Color.YELLOW);
        b7who.setBackground(Color.YELLOW);
        b8who.setBackground(Color.YELLOW);

        /*b1.addActionListener(OnlineListener(b1, b1info, b1who));
        b2.addActionListener(OnlineListener(b2, b2info, b2who));
        b3.addActionListener(OnlineListener(b3, b3info, b3who));
        b4.addActionListener(OnlineListener(b4, b4info, b4who));
        b5.addActionListener(OnlineListener(b5, b5info, b5who));
        b6.addActionListener(OnlineListener(b6, b6info, b6who));
        b7.addActionListener(OnlineListener(b7, b7info, b7who));
        b8.addActionListener(OnlineListener(b8, b8info, b8who));*/

        frame.add(b1);
        frame.add(b2);
        frame.add(b3);
        frame.add(b4);
        frame.add(b5);
        frame.add(b6);
        frame.add(b7);
        frame.add(b8);
        frame.add(connectionStatus);
        frame.add(b1info);
        frame.add(b2info);
        frame.add(b3info);
        frame.add(b4info);
        frame.add(b5info);
        frame.add(b6info);
        frame.add(b7info);
        frame.add(b8info);
        frame.add(b1who);
        frame.add(b2who);
        frame.add(b3who);
        frame.add(b4who);
        frame.add(b5who);
        frame.add(b6who);
        frame.add(b7who);
        frame.add(b8who);
        //repaint();

    }

    //методы для воспросизведения звуков
    public void soundDoor() {
        try {
            clipDoor = AudioSystem.getClip();
            InputStream input = new BufferedInputStream(getClass().getResourceAsStream("/door.wav"));
            AudioInputStream ais = AudioSystem.getAudioInputStream(input);
            clipDoor.open(ais);
            clipDoor.start();
            ais.close();
        } catch (LineUnavailableException e) {
            /*JOptionPane.showMessageDialog(null,e.getMessage());
            e.printStackTrace();*/
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
        } catch (UnsupportedAudioFileException e) {
            /*e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.getMessage());*/
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
        } catch (IOException e) {
            /*e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.getMessage());*/
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
        }

    }
    public void soundClick() {
        try {
            clipClick = AudioSystem.getClip();
            InputStream input = new BufferedInputStream(getClass().getResourceAsStream("/click1.wav"));
            AudioInputStream ais = AudioSystem.getAudioInputStream(input);
            clipClick.open(ais);
            clipClick.start();
            ais.close();
        } catch (LineUnavailableException e) {
/*            e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.getStackTrace());*/
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
        } catch (UnsupportedAudioFileException e) {
     /*       e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.getStackTrace());*/
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
        } catch (IOException e) {
            /*e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.getStackTrace());*/
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
        }

    }
    public void soundZvonok() {
        try {
            clipZvonok = AudioSystem.getClip();
            InputStream input = new BufferedInputStream(getClass().getResourceAsStream("/zv1.wav"));
            AudioInputStream ais = AudioSystem.getAudioInputStream(input);
            clipZvonok.open(ais);
            clipZvonok.start();
            ais.close();
        } catch (LineUnavailableException e) {
           /* e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.getStackTrace());*/
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
        } catch (UnsupportedAudioFileException e) {
            /*e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.getStackTrace());*/
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
        } catch (IOException e) {
            /*e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.getStackTrace());*/
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
        }

    }
    public class Migalkaa extends Thread{
        JButton b;
        Color color;

        public Migalkaa(JButton b,Color color){
            this.b = b;
            this.color = color;
        }
        public void action(int migalka){
            migalka++;
            Thread.currentThread().setName("Migalka" + b.getText());


            for (int i = 0; i < 15; i++) {

                try {
                    b.setForeground(color);
                    Thread.currentThread().sleep(200);
                    //repaint();
                    b.setForeground(Color.BLACK);
                    Thread.currentThread().sleep(200);

                    if(migalka >1){
                        migalka--;
                        return;
                    }

                } catch (InterruptedException e) {
                    /*e.printStackTrace();
                    JOptionPane.showMessageDialog(null,e.getStackTrace());*/
                    StackTraceElement [] stack = e.getStackTrace();
                    logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
                }
            }
            migalka--;
        }
        @Override
        public void run() {
            try {
                if (b.equals(b1)) action(countMigalka[1]);
                if (b.equals(b2)) action(countMigalka[2]);
                if (b.equals(b3)) action(countMigalka[3]);
                if (b.equals(b4)) action(countMigalka[3]);
                if (b.equals(b5)) action(countMigalka[3]);
                if (b.equals(b6)) action(countMigalka[3]);
                if (b.equals(b7)) action(countMigalka[3]);
                if (b.equals(b8)) action(countMigalka[3]);
            }catch (Exception e){
                /*JOptionPane.showMessageDialog(null,"Подвійне натискання");
                e.printStackTrace();*/
                StackTraceElement [] stack = e.getStackTrace();
                logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
            }


        }

    }


    private class StatusButtons implements Serializable {
        // private static final long serialVersionUID = 1L;
        //Класс фиксирующий состояние (цвет) кнопок. Отправляется в виде JSON на клиенты при подключении
        Color b1, b2, b3, b4, b5, b6, b7, b8;
        String b1info, b2info, b3info, b4info, b5info, b6info, b7info, b8info;
        String b1who, b2who, b3who, b4who, b5who, b6who, b7who, b8who;
        String b1name, b2name, b3name, b4name, b5name, b6name, b7name, b8name;


        public StatusButtons(Color b1, Color b2, Color b3, Color b4, Color b5, Color b6, Color b7, Color b8,
                             String b1info, String b2info, String b3info, String b4info, String b5info,
                             String b6info, String b7info, String b8info, String b1who, String b2who, String b3who, String b4who,
                             String b5who, String b6who, String b7who, String b8who, String b1name, String b2name, String b3name, String b4name,
                             String b5name, String b6name, String b7name, String b8name) {
            this.b1 = b1;
            this.b2 = b2;
            this.b3 = b3;
            this.b4 = b4;
            this.b5 = b5;
            this.b6 = b6;
            this.b7 = b7;
            this.b1info = b1info;
            this.b2info = b2info;
            this.b3info = b3info;
            this.b4info = b4info;
            this.b5info = b5info;
            this.b6info = b6info;
            this.b7info = b7info;
            this.b8info = b8info;
            this.b1who = b1who;
            this.b2who = b2who;
            this.b3who = b3who;
            this.b4who = b4who;
            this.b5who = b5who;
            this.b6who = b6who;
            this.b7who = b7who;
            this.b8who = b8who;
            this.b1name = b1name;
            this.b2name = b2name;
            this.b3name = b3name;
            this.b4name = b4name;
            this.b5name = b5name;
            this.b6name = b6name;
            this.b7name = b7name;
            this.b8name = b8name;
        }
    }


    private void createClient() {
        //Создаю клиент
        try {
            hash = Thread.currentThread().hashCode()*(int)(Math.random()*999)+0;
            boolean isConnected = false;
            isAllowed = false;
            int serverPort = Integer.valueOf(properties.getProperty("port"));
            String address = properties.getProperty("ipServer"); //10.244.1.121    localhost
            while(!isConnected) {
                connectionStatus.setForeground(Color.BLACK);
                connectionStatus.setText("З'єднання......");
                ipAddress = InetAddress.getByName(address);
                frame.repaint();
                try{
                    socket = new Socket(ipAddress, serverPort);
                    socket.setSoTimeout(30000);
                    isConnected=true;
                }catch (Exception e){
                    /*e.printStackTrace();*/
                    //connectionStatus.setText("");
                    StackTraceElement [] stack = e.getStackTrace();
                    logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
                    for (int i = 30; i >= 0; i--) {
                        connectionStatus.setText("З'єднання "+i);
                        connectionStatus.setForeground(Color.RED);
                        Thread.currentThread().sleep(1000);
                    }
                }

            }

            datain = new DataInputStream(socket.getInputStream());
            dataout = new DataOutputStream(socket.getOutputStream());
            dataout.writeUTF("candidate_"+currentIP+"_"+hash);
            dataout.flush();



            Thread.currentThread().sleep(500);
            connectionStatus.setForeground(Color.GREEN);
            connectionStatus.setBounds(52, 490, 200, 30);
            connectionStatus.setText("Підключено");
            frame.setVisible(false);
            //Прием состояния кнопок (цвет кнопок) сервера на момент подключения
            Gson gson = new Gson();

            String msg="";
            int checker = 0;
            while (checker!=1) {
                try {
                    msg = datain.readUTF();
                } catch (IOException e) {
                    StackTraceElement [] stack = e.getStackTrace();
                    logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
                    /*JOptionPane.showMessageDialog(null, e.getMessage());*/

                }
                if (msg.length() > 500) {
                    StatusButtons statusButtons = gson.fromJson(msg, StatusButtons.class);
                    b1.setBackground(statusButtons.b1);
                    b2.setBackground(statusButtons.b2);
                    b3.setBackground(statusButtons.b3);
                    b4.setBackground(statusButtons.b4);
                    b5.setBackground(statusButtons.b5);
                    b6.setBackground(statusButtons.b6);
                    b7.setBackground(statusButtons.b7);
                    b8.setBackground(statusButtons.b8);
                    b1info.setText(statusButtons.b1info);
                    b2info.setText(statusButtons.b2info);
                    b3info.setText(statusButtons.b3info);
                    b4info.setText(statusButtons.b4info);
                    b5info.setText(statusButtons.b5info);
                    b6info.setText(statusButtons.b6info);
                    b7info.setText(statusButtons.b7info);
                    b8info.setText(statusButtons.b8info);
                    b1who.setText(statusButtons.b1who);
                    b2who.setText(statusButtons.b2who);
                    b3who.setText(statusButtons.b3who);
                    b4who.setText(statusButtons.b4who);
                    b5who.setText(statusButtons.b5who);
                    b6who.setText(statusButtons.b6who);
                    b7who.setText(statusButtons.b7who);
                    b8who.setText(statusButtons.b8who);
                    b1.setText(statusButtons.b1name);
                    b2.setText(statusButtons.b2name);
                    b3.setText(statusButtons.b3name);
                    b4.setText(statusButtons.b4name);
                    b5.setText(statusButtons.b5name);
                    b6.setText(statusButtons.b6name);
                    b7.setText(statusButtons.b7name);
                    b8.setText(statusButtons.b8name);
                    checker = 1;
                }
            }
            //JOptionPane.showMessageDialog(null,"Данные приняты успешно");


        } catch (Exception e) {
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
            connectionStatus.setForeground(Color.RED);
            connectionStatus.setBounds(30, 490, 200, 30);
            connectionStatus.setText("Помилка (код 01)");
            /*JOptionPane.showMessageDialog(null,"Ошибка при подключении к серверу");
            JOptionPane.showMessageDialog(null,e.getStackTrace());*/
            e.printStackTrace();
            //JOptionPane.showMessageDialog(null,"Данные не приняты ");
            //JOptionPane.showMessageDialog(null,e.getMessage());

        }


    }
    private void restart() {
        try {
            Thread.currentThread().sleep(3000);
            point = frame.getLocation();
            frame.dispose();
            new EspiaJL (point);
        } catch (Exception e1) {
            StackTraceElement [] stack = e1.getStackTrace();
            logger.log(Level.INFO,e1.toString()+"\r\n"+stack[0]+"\r\n");
            /*JOptionPane.showMessageDialog(null,e1.getMessage());
            e1.printStackTrace();*/
        }
//           try {
//                Thread.currentThread().sleep(3000);
//                frame.dispose();
//                new com.pav.avdonin.clients.EspiaJL();
//            } catch (Exception e1) {
//                StackTraceElement [] stack = e1.getStackTrace();
//                logger.log(Level.INFO,e1.toString()+"\r\n"+stack[0]+"\r\n");
//                /*JOptionPane.showMessageDialog(null,e1.getStackTrace());
//                e1.printStackTrace();*/
//            }

    }
    public void readData() {


        //JOptionPane.showMessageDialog(null,"Вхождение в метод чтения данных");
        //Метод принимает данные от сервера

        String value = "";
        while (true) {
            try {
                value = datain.readUTF();
            } catch (SocketException e) {
                StackTraceElement [] stack = e.getStackTrace();
                logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
                connectionStatus.setForeground(Color.RED);
                connectionStatus.setBounds(30, 490, 200, 30);
                connectionStatus.setText("Помилка (код 02)");
                frame.dispose();
                close();
                if(isAllowed){
                    restart();
                }

                break;

            } catch (IOException e) {
                StackTraceElement [] stack = e.getStackTrace();
                logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
                connectionStatus.setForeground(Color.RED);
                connectionStatus.setBounds(30, 490, 200, 30);
                connectionStatus.setText("Помилка (код 03)");
                e.printStackTrace();


                restart();

                break;
            }

            try {
                String[] values = value.split("_");


                /*Массив с значениями с входящего потока:
                values[0] - положение кнопки по вертекали
                values[1] - цвет (green;red)
                values[2] - имя (КПП-1; КПП-2(КТП))
                values[3] - время
                */
                switch (values[0]) {
                    case "10":
                        switchchoice(values[1], values[2], values[3], b1, b1info, b1who);
                        break;
                    case "70":
                        switchchoice(values[1], values[2], values[3], b2, b2info, b2who);
                        break;
                    case "130":
                        switchchoice(values[1], values[2], values[3], b3, b3info, b3who);
                        break;
                    case "190":
                        switchchoice(values[1], values[2], values[3], b4, b4info, b4who);
                        break;
                    case "250":
                        switchchoice(values[1], values[2], values[3], b5, b5info, b5who);
                        break;
                    case "310":
                        switchchoice(values[1], values[2], values[3], b6, b6info, b6who);
                        break;
                    case "370":
                        switchchoice(values[1], values[2], values[3], b7, b7info, b7who);
                        break;
                    case "430":
                        switchchoice(values[1], values[2], values[3], b8, b8info, b8who);
                        break;
                    case "Connection test":
                        //DO NOTHING;
                        break;
                    case "isAllowed":
                        //JOptionPane.showMessageDialog(null,"проверка");
                        System.out.println(values[1]);
                            if(values[1].equals("YES")){
                                isAllowed=true;
                            frame.setVisible(true);


                        }else {
                            frame.setVisible(false);
                            close();
                            JOptionPane.showMessageDialog(null,"Вхід не дозволено. Зверніться до адміністратора.");
                            isAllowed=false;
                                frame.dispose();
                        }
                        break;

                }
            }
            catch (IllegalArgumentException e){
                StackTraceElement [] stack = e.getStackTrace();
                logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
               // DO NOTHING

            }

            catch (Exception e) {
                StackTraceElement [] stack = e.getStackTrace();
                logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
                //e.printStackTrace();
                connectionStatus.setForeground(Color.RED);
                connectionStatus.setBounds(30, 490, 200, 30);
                connectionStatus.setText("Помилка (код 04)");
                /*JOptionPane.showMessageDialog(null,"Помилка (код 04)");
                JOptionPane.showMessageDialog(null,e.getMessage());*/
                break;


            }
        }


    }
    public void switchchoice(String color, String name, String when, JButton b, JButton binfo, JButton bwho) {
        new Migalkaa(b,Color.YELLOW).start();
        if (color.equals("green")) {
            if (b.getBackground().equals(Color.GREEN)) {
                //DONOTHING, ONLY SET TIME
                //soundZvonok();

            }
            if (b.getBackground().equals(Color.RED)) {
                b.setBackground(Color.GREEN);
                binfo.setText(time());
                bwho.setText(name);
                soundZvonok();

            }
        }
        if (color.equals("red")) {
            if (b.getBackground().equals(Color.GREEN)) {
                b.setBackground(Color.RED);
                binfo.setText(time());
                bwho.setText(name);

                soundDoor();

            }
            if (b.getBackground().equals(Color.RED)) {
                //DONOTHING, ONLY SET TIME
                //soundDoor();


            }
        }
    }
    public void close() {
        try {
            dataout.close();
            datain.close();
            socket.close();

        } catch (IOException e) {
            StackTraceElement [] stack = e.getStackTrace();
            logger.log(Level.INFO,e.toString()+"\r\n"+stack[0]+"\r\n");
            /*JOptionPane.showMessageDialog(null, "Потоки не закриті");*/
        }


    }
    public String time() {
        DateFormat df = new SimpleDateFormat("dd.MM HH:mm");
        Date currenttime = Calendar.getInstance().getTime();
        String time = df.format(currenttime);
        return time;

    }

    public static void main(String[] args) throws IOException {
        EspiaJL c = new EspiaJL();


    }
}