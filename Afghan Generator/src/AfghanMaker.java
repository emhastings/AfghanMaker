/**
 * @author Emily Hastings
 * @version 10/23/2016
 * 
 * Weighted random afghan layout generator.
 * 
 * TODO:  Read in colors, quantities, dimensions from user.
 * TODO:  Save previous settings-- maybe in an XML document?
 * TODO:  Refactor
 * TODO:  remove preset colors
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
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


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
    private JPanel topPanel;
    private JPanel dimPanel;
    private JColorChooser jcc;
    private JTextField quantityField;   
    private JButton dimButton;
    private JLabel wLab;
    private JLabel lLab;
    private JLabel messageLabel;

    public AfghanMaker()  {
        resetData();
    }

    public void resetData()  {
        mesg = "";
        numSquares = 0;
        length = DEFAULT_LENGTH;
        width = DEFAULT_WIDTH;
        squareQuantities = new HashMap<Color, Integer>();
        numColors = 0;
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
        numSquares = 0;  //reset to 0

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
        if (numSquares < (length*width))
            mesg = "You need to make " + ((length*width) - numSquares) + " more squares for this size of afghan.";
        else
            mesg = "You will have " + (numSquares - (length*width)) + " squares left over."; 

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
        createColorPanel();

        //dimension panel
        createDimensionPanel();

        //assemble top Panel
        topPanel = new JPanel();
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
        messageLabel = new JLabel();
        messageLabel.setText(mesg);
        bottomPanel.add(messageLabel);

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

    public void createColorPanel()  {
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
    }

    public void redrawColorPanel()  {
        //update afghan panel
        topPanel.removeAll();        

        createColorPanel();
        topPanel.add(colorPanel, BorderLayout.WEST);
        topPanel.add(dimPanel, BorderLayout.EAST);

        topPanel.revalidate();
        topPanel.repaint();
    }

    public void createDimensionPanel()  {
        dimPanel = new JPanel();
        dimPanel.setLayout(new GridBagLayout());

        JPanel innerPanel2 = new JPanel();
        innerPanel2.setLayout(new GridLayout(2, 2));

        TitledBorder title2 = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Afghan dimensions");
        title2.setTitleJustification(TitledBorder.CENTER);
        innerPanel2.setBorder(title2);
        
        innerPanel2.add(new JLabel("Length"));
        lLab = new JLabel(length + " squares");
        innerPanel2.add(lLab);
        innerPanel2.add(new JLabel("Width"));
        wLab = new JLabel(width + " squares");
        innerPanel2.add(wLab);

        GridBagConstraints c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.gridx = 0;
        c3.gridy = 0;
        c3.ipady=5;

        dimPanel.add(innerPanel2,  c3);

        dimButton = new JButton("Choose dimensions");
        dimButton.addActionListener(this);

        GridBagConstraints c4 = new GridBagConstraints();
        c4.fill = GridBagConstraints.HORIZONTAL;
        c4.gridx = 0;
        c4.gridy = 1;

        dimPanel.add(dimButton, c4);
    }

    public static void main(String[] args)  {

        AfghanMaker ghanMaker = new AfghanMaker();
        ghanMaker.createAndShowGUI();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {

        Object source = evt.getSource();  // Object that generated the action event.

        if (source == colorButton) {

            //make color chooser dialogue

            //chooser
            JPanel colorPanel = new JPanel();            
            colorPanel.add(new JLabel("Select a color:"), BorderLayout.WEST); 

            jcc = new JColorChooser();
            PreviewPane preview = new PreviewPane();
            jcc.getSelectionModel().addChangeListener(preview);            
            jcc.setPreviewPanel(preview);
            colorPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
            colorPanel.add(jcc, BorderLayout.EAST);

            //input quantity
            JPanel quantityPicker = new JPanel();
            quantityField = new JTextField(20);
            JLabel quantLabel = new JLabel("How many squares of this color do you have?");
            quantityPicker.add(quantLabel, BorderLayout.WEST);
            quantityPicker.add(quantityField, BorderLayout.EAST);

            //assemble dialogue
            JPanel panel = new JPanel();
            panel.add(colorPanel, BorderLayout.NORTH);
            panel.add(quantityPicker, BorderLayout.SOUTH);            
            JOptionPane.showMessageDialog(colorButton, panel, "Add a square", JOptionPane.PLAIN_MESSAGE);

            //get input
            Color newColor = jcc.getColor();
            int quantity = Integer.parseInt(quantityField.getText());

            //update data
            squareQuantities.put(newColor, quantity);
            numSquares+= quantity;
            numColors++;

            //update color panel
            redrawColorPanel();

            //update message
            messageLabel.setText(mesg);     

            //update afghan
            generateButton.doClick();

        }  else if (source == generateButton)  {
            redrawAfghan();

        }  else if(source== restart) {
            resetData();
            redrawAfghan();
            redrawColorPanel();
            //update dimension panel
            wLab.setText(width + " squares");
            lLab.setText(length + " squares");
            //update message
            messageLabel.setText(mesg);     


        }  else  {  //dimension set
            //make dimension chooser dialogue                       

            //input length
            JPanel lengthPanel = new JPanel();
            JTextField lengthField = new JTextField(20);
            JLabel lengthLabel = new JLabel("Length");
            lengthPanel.add(lengthLabel, BorderLayout.WEST);
            lengthPanel.add(lengthField, BorderLayout.EAST);

            //input width
            JPanel widthPanel = new JPanel();
            JTextField widthField = new JTextField(20);
            JLabel widthLabel = new JLabel("Width");
            widthPanel.add(widthLabel, BorderLayout.WEST);
            widthPanel.add(widthField, BorderLayout.EAST);

            //assemble dialogue
            JPanel panel = new JPanel();
            panel.add(new JLabel("Enter your afghan's dimensions:"), BorderLayout.NORTH); 
            panel.add(lengthPanel, BorderLayout.CENTER);
            panel.add(widthPanel, BorderLayout.SOUTH);            
            JOptionPane.showMessageDialog(dimButton, panel, "Enter dimensions", JOptionPane.PLAIN_MESSAGE);

            //get input and update data            
            length = Integer.parseInt(lengthField.getText());
            width = Integer.parseInt(widthField.getText());

            //update dimension panel
            wLab.setText(width + " squares");
            lLab.setText(length + " squares");

            //update afghan
            generateButton.doClick();

            //update message
            messageLabel.setText(mesg);     
        }


    }

    private class PreviewPane extends JLabel implements ChangeListener  {

        public PreviewPane()  {
            setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            setBackground(jcc.getColor());
            setOpaque(true);
            setText("\t");
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(50, 50);

        }

        public void stateChanged(ChangeEvent e) {
            Color newColor = jcc.getColor();
            setBackground(newColor);
        }        
    }        
}