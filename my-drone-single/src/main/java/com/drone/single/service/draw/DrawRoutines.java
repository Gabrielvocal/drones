package com.drone.single.service.draw;

import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

@Service
public class DrawRoutines  {

    static class PaintPanel extends JPanel implements ActionListener, MouseListener {


        public Color backgroundColor = Color.lightGray;
        public int radius =4;
        private Graphics g;
        private List<double[][]>list;
        public PaintPanel (List<double[][]> list) {

            this.list=new ArrayList<>(list);
        }







        @Override
        public void mouseEntered(MouseEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }


        public void mouseClicked (MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {

        }

        public void mouseReleased (MouseEvent e) {}




        public void paintLine (List<double[][]> list) {
           for (double[][] doubles : list) {
                for(int i=0;i<doubles.length-1;i++){//doubles.length-1
                    //g.drawLine((int)(doubles[i][0]*1000-114200)*3,(int)(doubles[i][1]*1000-30300)*3,(int)(doubles[i+1][0]*1000-114200)*3,(int)(doubles[i+1][1]*1000-30300)*3);
                    g.drawLine((int)(doubles[i][0]*100+200),(int)(doubles[i][1]*100+200),(int)(doubles[i+1][0]*100+200),(int)(doubles[i+1][1]*100+200));
                    //g.drawLine((int)(doubles[i][0]-100+200),(int)(doubles[i][1]*100+200),(int)(doubles[i+1][0]*100+200),(int)(doubles[i+1][1]*100+200));
                }

            }
            /*for(int i=0;i<2;i++){//doubles.length-1,list.get(0).length-1
                int x1=(int)list.get(0)[i][0]*100+200;
                double y=list.get(0)[i][1]*100+200;
                int y1=(int)(list.get(0)[i][1]*100+200);
                int x2=(int)list.get(0)[i+1][0]*100+200;
                int y2=(int)list.get(0)[i+1][1]*100+200;
                g.drawLine((int)(list.get(0)[i][0]*100+200),(int)(list.get(0)[i][1]*100+200),(int)(list.get(0)[i+1][0]*100+200),(int)(list.get(0)[i+1][1]*100+200));
            }*/
        }


        public void paintComponent (Graphics g) {
            super.paintComponent(g);
            this.g = g;

            Color temp = g.getColor();
            g.setColor(backgroundColor);
           // g.fillRect(0, 0, this.getWidth(), this.getHeight());
            g.setColor(temp);

            paintLine(list);

            temp = g.getColor();
            g.setColor(backgroundColor);

        }







        @Override
        public void actionPerformed(ActionEvent e) {
            String actioncom = e.getActionCommand();

            if (actioncom.equals("clear"))
                //delaunay = new Delaunay(initialSuperTriangle);

                repaint();

        }
    }
    public void getData(List<double[][]> list){
        JButton clearButton = new JButton("Clear");
        clearButton.setActionCommand("clear");
        ButtonGroup group = new ButtonGroup();
        JPanel buttonPanel = new JPanel();
        //buttonPanel.add(voronoiButton);
        buttonPanel.add(clearButton);
       /* List<double[][]> list1=new ArrayList<>();
        list1.add(new double[][]{{1.0,2.1},{2.1,3.5}});
        list1.add(new double[][]{{3.0,4.1},{4.3,5.5}});*/
        PaintPanel graphicsPanel = new PaintPanel(list) ;
        graphicsPanel.setBackground(Color.green);
        //voronoiButton.addActionListener(graphicsPanel);
        clearButton.addActionListener(graphicsPanel);
        graphicsPanel.addMouseListener(graphicsPanel);
        JFrame dWindow = new JFrame();
        dWindow.setSize(1200, 1200);
        dWindow.setTitle("Voronoi/Delaunay Window");
        dWindow.setLayout(new BorderLayout());
        dWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dWindow.setVisible(true);
        dWindow.add(buttonPanel,"North");
        dWindow.add(graphicsPanel,"Center");

    }
}
