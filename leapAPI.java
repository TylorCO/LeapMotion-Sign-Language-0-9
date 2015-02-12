import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import javax.swing.*;

import java.lang.Math;
import java.sql.*;

import javax.imageio.*;
import com.leapmotion.leap.*;
import java.awt.image.BufferedImage;

class leapAPI extends Listener
{
    int index = 0;
    int req = 0;
    int totalSigns = 0;
    int held = 0;
    double sucess = 1;
    double fail = 0;

    double averageX = 0;
    double averageY = 0;
    double averageSign = 0;

    double reqTime;

    double temp;
    double currentFrameNm = 0;
    double time = 0;
    double lastAssign = 0;
    double lastSecond = 0;

    double lastDrew = 0;
    String currentSign = "";
    boolean isHands = false;
    boolean newSign = true;
    boolean gotSign = false;

    JPanel testPanel;

    String name = "";
    String currentFrame = "";
    String status = "";    
    String toDraw = "img2";
    String colour;

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/leapmotion";

    String reqSign = "";

    ArrayList<Point> points = new ArrayList<>();
    ArrayList<Point> tips = new ArrayList<>();
    ArrayList<Point> baser = new ArrayList<>();
    //ArrayList<Point> points;
    ArrayList<Integer> z = new ArrayList<>();
    ArrayList<Double> DiffX = new ArrayList<>();
    ArrayList<Double> DiffY = new ArrayList<>();
    ArrayList<Integer> Count = new ArrayList<>();

    @Override
    public void onInit(Controller controller) 
    {
        System.out.println("Initialized");
        //JFrame nameFrame = new JFrame();
        name = JOptionPane.showInputDialog("User name" ,"Tylor");
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testPanel = new JPanel() 
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                DiffX =  new ArrayList<>();
                DiffY =  new ArrayList<>();
                double differenceY = 0;
                double differenceX = 0;
                int height = 50;

                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Color.black);
                //ChkSign();

                testPanel.repaint();
                try {

                    BufferedImage img = ImageIO.read(new File("empty.jpg")); 
                    BufferedImage img2 = ImageIO.read(new File("empty2.jpg")); 
                    BufferedImage signIMG = ImageIO.read(new File(reqSign + ".jpg")); 
                    BufferedImage signIMGALT = ImageIO.read(new File(reqSign + "n.jpg")); 
                    for(int i = 0; i < points.size(); i++)
                    {
                        //System.out.println("Point[" + i + "] : " +  points.get(i).x + "," + points.get(i).y);
                        if(colour.equals("red"))
                        {
                            g.setColor(Color.red);
                        }
                        if(colour.equals("green"))
                        {
                            g.setColor(Color.green);
                        }
                        g.fillRect(points.get(i).x+400, points.get(i).y+300, z.get(i), z.get(i));
                    }
                    for (Point baser1 : baser) {
                        //System.out.println("Point[" + i + "] : " +  points.get(i).x + "," + points.get(i).y);
                        //g.fillRect(baser.get(i).x+400, baser.get(i).y+300, z.get(i), z.get(i));
                    }
                    g.setColor(Color.blue);
                    for(int i = 0; i < tips.size(); i++)
                    {
                        differenceX = tips.get(i).x - baser.get(i).x;
                        differenceY = tips.get(i).y - baser.get(i).y;
                        DiffX.add(differenceX);
                        DiffY.add(differenceY);

                        //double testx = (int)Double.parseDouble(tips.get(0).x - baser.get(i).x);

                        g.setColor(Color.blue);
                        //
                    }
                    for(int i=0; i < DiffX.size(); i++)
                    {

                        //g.drawString("Syncs[x] : " + DiffX.get(i), 50, height);
                        //g.drawString("Syncs[y] : " + DiffY.get(i), 140, height);
                        height += 20;
                    }

                    if(isHands == false)
                    {
                        if(currentFrameNm % 2 == 0) 
                        {
                            toDraw = "img";

                        }
                        else
                        {
                            toDraw = "img2";
                        }

                        if (toDraw.equals("img"))
                        {
                            g.drawImage(img, 0, 0, null);
                        }
                        if (toDraw.equals("img2"))
                        {
                            g.drawImage(img2, 0, 0, null);
                        }

                    }
                    if(isHands == true)
                    {
                        if((averageSign > 80) && (totalSigns > 10))
                        {
                            g.drawImage(signIMGALT, 670, 490, null);
                            
                        }
                        else
                        {

                           g.drawImage(signIMG, 670, 490, null);
                        }

                    }
                    double temp2 = (int)currentFrameNm - (int)lastAssign;

                    temp2 = temp2 /10;
                    temp2 = 360 * temp2 * 1.73;
                    int temp = (int)temp2;

                    g.drawArc(650, 470, 120, 120, 270, temp);
                    /*
                    g.drawString("Current Sign: " + currentSign, 10, 300);
                    g.drawString("Requested Sign: " + reqSign, 10, 400);
                    g.drawString("Correct rate: " + averageSign, 10, 420);

                    g.drawString("Time: " + currentFrameNm, 10, 380);
                    g.drawString("Are hands?: " + isHands, 10, 320);
                    g.drawString("User: " + name, 10, 340);
                    g.drawString("Status: " + status, 10, 360);
                    */

                    if((DiffY.get(1) > 0) && (DiffY.get(2) > 0) && (DiffY.get(3) > 0) && (DiffY.get(4) > 0) && (DiffY.get(0) < 0)) // Sign 0
                    {
                        currentSign = "zero";
                    } 

                    if((DiffY.get(1) < 0) && (DiffY.get(2) > 0) && (DiffY.get(3) > 0) && (DiffY.get(4) > 0) && (DiffX.get(0) < 20)) // Sign 1
                    {
                        currentSign = "one";
                    }
                    if((DiffY.get(1) < 0) && (DiffY.get(2) < 0) && (DiffY.get(3) > 0) && (DiffY.get(4) > 0) && (DiffX.get(0) < 20)) // Sign 2
                    {
                        currentSign = "two";
                    }
                    if((DiffY.get(1) < 0) && (DiffY.get(2) < 0) && (DiffY.get(3) > 0) && (DiffY.get(4) > 0) && (DiffX.get(0) > 20)) // Sign 3
                    {
                        currentSign = "three";
                    }
                    if((DiffY.get(1) < 0) && (DiffY.get(2) < 0) && (DiffY.get(3) < 0) && (DiffX.get(0) < 0)) // Sign 6
                    {
                        currentSign = "six";
                    }                
                    if((DiffY.get(1) < 0) && (DiffY.get(2) < 0) && (DiffY.get(3) < -20) && (DiffY.get(4) < 0) && (DiffX.get(0) < 10)) // Sign 4
                    {
                        currentSign = "four";
                    }
                    if((DiffY.get(1) < 0) && (DiffY.get(2) < 0) && (DiffY.get(3) < 0) && (DiffY.get(4) < 0) && (DiffX.get(0) > 0)) // Sign 5
                    {
                        currentSign = "five";
                    }        
                    if((DiffY.get(1) < 0) && (DiffY.get(2) < 0) && (DiffY.get(3) > -10) && (DiffY.get(4) < 0)) // Sign 7
                    {
                        currentSign = "seven";
                    }      
                    if((DiffY.get(1) < 0) && (DiffY.get(2) > -10) && (DiffY.get(3) < 0) && (DiffY.get(4) < 0)) // Sign 8
                    {
                        currentSign = "eight";
                    } 
                    if((DiffY.get(1) > -10) && (DiffY.get(2) < 0) && (DiffY.get(3) < 0) && (DiffY.get(4) < 0)) // Sign 9
                    {
                        currentSign = "nine";
                    }
                    if((DiffY.get(1) > 0) && (DiffY.get(2) < 0) && (DiffY.get(3) > 0) && (DiffY.get(4) > 0) && (DiffX.get(0) < 20)) // Sign The Bird
                    {
                        currentSign = "The Bird";
                    }

                } catch (Exception e)
                {
                    //System.out.println("Array is out of bounds or something, sorry.");
                    //System.out.println(e);
                    //points =  new ArrayList();
                }
            }

            @Override
            public Dimension getPreferredSize() 
            {
                return new Dimension(800, 600);
            }

        };
        frame.getContentPane().

        add(testPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void onConnect(Controller controller) 
    {
        System.out.println("Connected");
        status = "Connected";
    }

    @Override
    public void onDisconnect(Controller controller) 
    {

        System.out.println("Disconnected");
        status = "Disconnected";
    }

    @Override
    public void onExit(Controller controller) 
    {
        //System.out.println("Exited");
    }

    public void chkSign()
    {
        averageSign = (sucess / (sucess + fail)) * 100;
        averageSign  =  (int)averageSign * 100;
        averageSign  =  (double)averageSign / 100;

        System.out.println(averageSign);
        if(currentFrameNm == lastSecond)
        {
            //nada
        }
        else
        {
            lastSecond = currentFrameNm;

            if(currentFrameNm - lastAssign > 1)
            {
                colour = "";
            }
            if(currentSign.equals(reqSign))
            {
                held++;

            }
        }
        if((currentFrameNm - lastAssign > 6) && (gotSign == false))
        {
            newSign = true; 
            fail++;
            colour = "red";
        }   
        else
        {

        }
        if(held > 1)
        {
            gotSign = true;
            sucess++;
            newSign = true;    
            colour = "green";
        }

        if (newSign == true)
        {
            totalSigns++;
            lastAssign = currentFrameNm;
            Random rn = new Random(System.currentTimeMillis());
            req = rn.nextInt(10);
            held = 0;
            newSign = false;   
            gotSign = false;
            //System.out.println("Random Number: " + req);
            //if(currentFrameNm % 200 == 0) 
            {
                switch (req) {
                    case 0: 
                    reqSign = "zero";
                    break;
                    case 1: 
                    reqSign = "one";
                    break;
                    case 2: 
                    reqSign = "two";
                    break;
                    case 3: 
                    reqSign = "three";
                    break;
                    case 4: 
                    reqSign = "four";
                    break;
                    case 5: 
                    reqSign = "five";
                    break;
                    case 6: 
                    reqSign = "six";
                    break;
                    case 7: 
                    reqSign = "seven";
                    break;
                    case 8: 
                    reqSign = "eight";
                    break;
                    case 9: 
                    reqSign = "nine";
                    break;

                }
            }
        }

    }

    @Override
    public void onFrame(Controller controller) 
    {

        com.leapmotion.leap.Frame frame = controller.frame();
        currentFrameNm = frame.timestamp() * .000001;
        currentFrameNm =(int)(currentFrameNm);
        currentFrameNm = Math.abs(currentFrameNm);
        //System.out.println("Frame id: " + frame.id() + ", timestamp: " + frame.timestamp()+ ", hands: " + frame.hands().count() + ", fingers: " + frame.fingers().count() + ", tools: " + frame.tools().count()   + ", gestures " + frame.gestures().count());

        for(Hand hand : frame.hands())
        {

            String handType = hand.isLeft() ? "Left" : "Right";
            hand = frame.hands().leftmost();
            if(handType.equals("Left"))
            {

                points =  new ArrayList<>();
                baser =  new ArrayList<>();
                tips =  new ArrayList<>();
                z = new ArrayList<>();

                for(Finger finger : hand.fingers())
                {
                    for(Bone.Type boneType : Bone.Type.values())
                    {

                        Bone bone = finger.bone(boneType);

                        String pos = bone.center().toString();
                        String con = pos.substring(1,pos.length() -1);
                        String[] part = con.split(",");

                        Point jeeebus = new Point();

                        jeeebus.x = (int)Double.parseDouble(part[0]);
                        jeeebus.y = (int)Double.parseDouble(part[2]);
                        int zV = (int)Double.parseDouble(part[1]) /12;

                        z.add(zV);

                        points.add(jeeebus);

                    }
                    String tipTS = finger.tipPosition().toString();
                    String tip = tipTS.substring(1,tipTS.length() -1);
                    String[] aTip = tip.split(",");
                    Point fucketty = new Point();
                    fucketty.x = (int)Double.parseDouble(aTip[0]);
                    fucketty.y = (int)Double.parseDouble(aTip[2]);
                    tips.add(fucketty);

                    Bone bone = finger.bone(Bone.Type.TYPE_PROXIMAL);
                    String BaseBN = bone.center().toString();
                    String base = BaseBN.substring(1,BaseBN.length() -1);
                    String[] aBone = base.split(",");
                    Point thingy = new Point();
                    thingy.x = (int)Double.parseDouble(aBone[0]);
                    thingy.y = (int)Double.parseDouble(aBone[2]);
                    baser.add(thingy);
                }

            }

        }

        isHands = !frame.hands().isEmpty();
        if (isHands == true)
        {
            chkSign();
        }
    }
}

