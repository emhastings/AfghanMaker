/**
 * @author Emily Hastings
 * @version 10/23/2016
 * 
 * Weighted random afghan layout generator.
 * 
 * TODO:  Read in colors, quantities, dimensions from user.
 * TODO:  Save previous settings-- maybe in an XML document?
 * TODO:  Refactor
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class AfghanMaker implements ActionListener {

    private static int numColors;

    private String mesg;
    private Map<Color, Integer> squareQuantities;
    private int numSquares;
    private int length;
    private int width;


    public AfghanMaker()  {
        mesg = "";
        numSquares = 0;
        length = 0;
        width = 0;
        squareQuantities = new HashMap<Color, Integer>();
    }

    public void setColorsAndQuantities()  {
        //TODO:  read colors and quantities in from chooser 
        squareQuantities.put(new Color(183,26,26), 50);  //red
        squareQuantities.put(new Color(218,165,32), 10);  //yellow
        squareQuantities.put(new Color(204,92,17), 20);  //orange
        squareQuantities.put(new Color(51,51,0), 10);  //brown
        squareQuantities.put(new Color(4,72,45), 30);  //dark green
        squareQuantities.put(new Color(192,241,34), 30);  //light green
    }

    public Color[][] getGrid(int l, int w) {

        Color[][] retVal = new Color[w][l];       
        setColorsAndQuantities();        
        ArrayList<Color> squares = new ArrayList<Color>();

        Set<Color> colors = squareQuantities.keySet();
        for (Color color : colors)  {
            int count = squareQuantities.get(color);
            numSquares += count;
            for (int i=0; i<count; i++)  {
                squares.add(color);
            }
        }

        //populate grid
        for (int r=0; r<l; r++)  {  
            for (int c=0; c<w; c++)  {
                retVal[c][r] = Color.GRAY;
            }
        }

        Random rand = new Random();
        int n = squares.size();

        for (int r=0; r<l; r++)  { 
            int c=0;
            while (n>0 && c<w)  {
                //get the next color
                int item = rand.nextInt(n); 
                Color color = squares.get(item);
                retVal[c][r] = color;
                //remove it
                squares.remove(item);
                n = squares.size();
                c++;
            }
        }

        if (numSquares < l*w)
            mesg = "You need to make " + (l*w - numSquares) + " more squares for this size of afghan.";
        else
            mesg = "You will have " + (numSquares - l*w) + " squares left over."; 

        return retVal;
    }

    public void createAndShowGUI()  {
        //TODO: remove these once dialogues work
        numColors = 6;
        length = 25;
        width = 20;

        Color[][] squares = getGrid(length,width);

        //create gui
        JFrame window = new JFrame("AfghanMaker");

        //set up window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocation(0,0);
        //window.setSize(800,600);  //this doesn't work?
        window.setVisible(true);

        //color panel
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new GridBagLayout()); 

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new GridLayout(numColors+1, 2));

        TitledBorder title = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Current colors");
        title.setTitleJustification(TitledBorder.CENTER);
        innerPanel.setBorder(title);

        innerPanel.add(new JLabel("Color", SwingConstants.CENTER));
        innerPanel.add(new JLabel ("Quantity", SwingConstants.CENTER));

        Set<Color> colors = squareQuantities.keySet();
        for (Color color : colors)  {
            JLabel colorLab = new JLabel("\t");
            colorLab.setBackground(color);
            colorLab.setOpaque(true);
            colorLab.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            innerPanel.add(colorLab);
            innerPanel.add(new JLabel ("" + squareQuantities.get(color), SwingConstants.CENTER));
        }

        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridx = 0;
        c1.gridy = 0;
        c1.ipady=5;

        colorPanel.add(innerPanel, c1); 

        JButton chooser = new JButton("Add a color");
        chooser.addActionListener(this);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridx = 0;
        c2.gridy = 1;

        colorPanel.add(chooser, c2);        

        //dimension panel
        JPanel dimPanel = new JPanel();
        dimPanel.setLayout(new GridBagLayout());

        JPanel innerPanel2 = new JPanel();
        innerPanel2.setLayout(new GridLayout(2, 2));

        TitledBorder title2 = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Afghan dimensions");
        title2.setTitleJustification(TitledBorder.CENTER);
        innerPanel2.setBorder(title2);

        innerPanel2.add(new JLabel("Width"));
        JLabel wLab = new JLabel(width + " squares");
        innerPanel2.add(wLab);
        innerPanel2.add(new JLabel("Length"));
        JLabel lLab = new JLabel(length + " squares");
        innerPanel2.add(lLab);

        GridBagConstraints c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.gridx = 0;
        c3.gridy = 0;
        c3.ipady=5;

        dimPanel.add(innerPanel2,  c3);

        JButton chooser2 = new JButton("Choose dimensions");
        chooser2.addActionListener(this);

        GridBagConstraints c4 = new GridBagConstraints();
        c4.fill = GridBagConstraints.HORIZONTAL;
        c4.gridx = 0;
        c4.gridy = 1;

        dimPanel.add(chooser2, c4);

        //assemble top Panel
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        topPanel.add(colorPanel, BorderLayout.WEST);
        topPanel.add(dimPanel, BorderLayout.EAST);
        window.add(topPanel, BorderLayout.NORTH);

        //panel to display generated afghan
        JPanel ghanPanel = new AfghanPanel(length, width, squares);
        window.add(ghanPanel, BorderLayout.CENTER);        

        //bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2,1));
        JLabel message = new JLabel();
        message.setText(mesg);
        bottomPanel.add(message);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        JButton generate = new JButton("Generate!");
        generate.addActionListener(this);
        buttonPanel.add(generate);
        JButton restart = new JButton("Start over");
        restart.addActionListener(this);
        buttonPanel.add(restart);
        bottomPanel.add(buttonPanel);
        window.add(bottomPanel, BorderLayout.SOUTH);


        window.pack();
    }

    public static void main(String[] args)  {

        AfghanMaker ghanMaker = new AfghanMaker();
        ghanMaker.createAndShowGUI();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        // TODO Auto-generated method stub

        Object source = evt.getSource();  // Object that generated 
        //   the action event.

        System.out.println("Button pressed!");

    }
}