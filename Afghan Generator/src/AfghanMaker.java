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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class AfghanMaker implements ActionListener {

    private final int DEFAULT_LENGTH = 20;
    private final int DEFAULT_WIDTH = 15;

    private int numColors;
    private String mesg;
    private Map<Color, Integer> squareQuantities;
    private int numSquares;
    private int length;
    private int width;

    private JFrame window;
    private JPanel colorPanel;
    private JPanel innerPanel;
    private JButton colorButton;
    private JButton generateButton;
    private JButton restart;
    private JPanel ghanPanel;
    
    private JLabel noColors;
    private JLabel noQuantity;


    public AfghanMaker()  {
        noColors = new JLabel("No colors");
        noQuantity = new JLabel("\t");
        resetData();
    }

    public void resetData()  {
        mesg = "";
        numSquares = 0;
        length = DEFAULT_LENGTH;
        width = DEFAULT_WIDTH;
        squareQuantities = new HashMap<Color, Integer>();
    }

    public void setFallColorsAndQuantities()  {
        //TODO:  read colors and quantities in from chooser 
        numColors = 6;
        length = 25;
        width = 20;

        squareQuantities.put(new Color(183,26,26), 50);  //red
        squareQuantities.put(new Color(218,165,32), 10);  //yellow
        squareQuantities.put(new Color(204,92,17), 20);  //orange
        squareQuantities.put(new Color(51,51,0), 10);  //brown
        squareQuantities.put(new Color(4,72,45), 30);  //dark green
        squareQuantities.put(new Color(192,241,34), 30);  //light green
    }

    public Color[][] getGrid() {

        Color[][] retVal = new Color[width][length]; 
        ArrayList<Color> squares = new ArrayList<Color>();

        //populate list of squares
        Set<Color> colors = squareQuantities.keySet();
        for (Color color : colors)  {
            int count = squareQuantities.get(color);
            numSquares += count;
            for (int i=0; i<count; i++)  {
                squares.add(color);
            }
        }

        //populate grid with default color
        for (int r=0; r<length; r++)  {  
            for (int c=0; c<width; c++)  {
                retVal[c][r] = Color.GRAY;
            }
        }

        //add squares to grid
        Random rand = new Random();
        int n = squares.size();

        for (int r=0; r<length; r++)  { 
            int c=0;
            while (n>0 && c<width)  {
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

        //set message
        if (numSquares < length*width)
            mesg = "You need to make " + (length*width - numSquares) + " more squares for this size of afghan.";
        else
            mesg = "You will have " + (numSquares - length*width) + " squares left over."; 

        return retVal;
    }

    public void createAndShowGUI()  {        

        setFallColorsAndQuantities();  
        Color[][] squares = getGrid();

        //create gui
        window = new JFrame("AfghanMaker");

        //set up window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocation(0,0);
        //window.setSize(800,600);  //this doesn't work?
        window.setVisible(true);

        //color panel
        colorPanel = new JPanel();
        colorPanel.setLayout(new GridBagLayout()); 

        innerPanel = new JPanel();
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
        //c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridx = 0;
        c1.gridy = 0;
        c1.ipady=5;

        colorPanel.add(innerPanel, c1); 

        colorButton = new JButton("Add a color");
        colorButton.addActionListener(this);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridx = 0;
        c2.gridy = 1;

        colorPanel.add(colorButton, c2);        

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
        ghanPanel = new AfghanPanel(length, width, squares);
        window.add(ghanPanel, BorderLayout.CENTER);        

        //bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2,1));
        JLabel message = new JLabel();
        message.setText(mesg);
        bottomPanel.add(message);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        generateButton = new JButton("Generate!");
        generateButton.addActionListener(this);
        buttonPanel.add(generateButton);
        restart = new JButton("Start over");
        restart.addActionListener(this);
        buttonPanel.add(restart);
        bottomPanel.add(buttonPanel);
        window.add(bottomPanel, BorderLayout.SOUTH);


        window.pack();
    }

    public void redrawAfghan()  {
        //update afghan panel
        
        window.remove(ghanPanel);        
        Color[][] newSquares = getGrid();
        AfghanPanel newPanel = new AfghanPanel(length, width, newSquares);
        ghanPanel = newPanel;            
        
        window.add(ghanPanel, BorderLayout.CENTER);
        window.revalidate();
        window.repaint();
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

        if (source == colorButton) {  // TODO: this opens the picker and updates data, but doesn't update the color panel
            Color newColor = JColorChooser.showDialog(colorButton, "Add a square", Color.WHITE);
            int quantity = 10;
            squareQuantities.put(newColor, quantity);

            //update color panel


            window.revalidate();
            window.repaint();

        }  else if (source == generateButton)  {  //this works
            redrawAfghan();

        }  else if(source== restart) {  //TODO: reset dimension.  fix color panel size.
            resetData();
            redrawAfghan();
            
            
            innerPanel.removeAll();
            innerPanel.setLayout(new GridLayout(numColors+1, 2));
            innerPanel.add(new JLabel("Color", SwingConstants.CENTER));
            innerPanel.add(new JLabel ("Quantity", SwingConstants.CENTER));
            
            JLabel colorLab = new JLabel("\t");
            colorLab.setBackground(Color.GRAY);
            colorLab.setOpaque(true);
            colorLab.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            innerPanel.add(colorLab);
            innerPanel.add(new JLabel ("" + 0, SwingConstants.CENTER));
            innerPanel.revalidate();
            //innerPanel.add(noColors);
            //innerPanel.add(noQuantity);

            window.revalidate();
            //window.repaint();

        }  else
            System.out.println("Button pressed!");

    }
}