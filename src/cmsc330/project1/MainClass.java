/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmsc330.project1;

/**
 * File: MainClass.java
 * Date: 11/18/2018
 * Author: Dillan Cobb
 * Purpose: Loads in a text file from a path, and handles creation of GUI from
 * that file.
**/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;



public class MainClass {

    // Various objects for the program to use
    // Tokens for the application
    Token token;        // Handles the current token
    Token appLvl;       // Token that toggles between window and panel
    Token itemLvl;      // Token that toggles each widget item
    Lexer lexer;        // Lexer handles the file
    JFileChooser fc = new JFileChooser(".");    // Filechooser opened at launch
    boolean inParen = false;    // Boolean check if you are inside of parenthesis
    
    JOptionPane msgPane = new JOptionPane(); // For displaying errors
    
    // MainClass constructor, used for starting the application
    public MainClass() {
        // Open the file and declare the file to te filereader
        try {
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                lexer = new Lexer (fc.getSelectedFile().toString());
            }
            token = lexer.getNextToken();
            generateGui();
        } catch (SyntaxError | IOException e) {
            msgPane.showMessageDialog(null, "Error: " + e.getMessage() + "\nChe"
                    + "ck file contents and try again.");
        }
    }
    
    // Main creates the application and runs it at launch
    public static void main(String[] args){
        MainClass application = new MainClass();
    }
    
    // generateGui method takes from the Lexer to handle which items generates
    // which Gui items, then displays it
    public void generateGui() throws SyntaxError, IOException{
        // Default variables
        int width = 0, height = 0;                          // For frame
        int row = 0, column = 0, bufferX = 0, bufferY = 0;  // Layouts
        int tempInt = 0;                    // Temp int stored for when a number token appears
        String tempStr = null;              // Temp string stored for when a stirng token appears
        String appTitle = null;             // String for the application title
        JFrame applicationFrame = null;     // Frame object for application
        boolean fileFinished = false;       // boolean for handling the file loop
        JPanel applicationPanel = null;     // Panel object for the application
        GridLayout gridLayout = null;       // Gridlayout for the application
        FlowLayout flowLayout = null;       // Flowlayout for the application
        ArrayList<JPanel> panels = new ArrayList<JPanel>(); // Used to hold all the panels for nested panels
        JPanel addedPanels = new JPanel();                  // For added the nested panels
        addedPanels.setLayout(new BoxLayout(addedPanels, BoxLayout.Y_AXIS)); // Layout for nested panels
        
        // Runs a loop until fileFinished is true
        while(fileFinished == false) {
            
            // Runs a switch on the token to determine what to do
            switch(token) {
                
                // Triggers the setup for generating a button
                case BUTTON:
                    itemLvl = Token.BUTTON;
                    token = lexer.getNextToken();
                    break;

                // Triggers the end of the file
                case END:
                    appLvl = Token.END;
                    itemLvl = Token.END;
                    token = lexer.getNextToken();
                    break;

                // Triggers the setup for each flow layout
                case FLOW:
                    if (itemLvl == Token.LAYOUT) {
                        itemLvl = Token.FLOW;
                        token = lexer.getNextToken();
                        flowLayout = new FlowLayout();
                    }
                    break;

                // Triggers the setup for each grid layout
                case GRID:
                    if (itemLvl == Token.LAYOUT) {
                        itemLvl = Token.GRID;
                        token = lexer.getNextToken();
                        gridLayout = new GridLayout();
                    }
                    break;

                case GROUP:
                    break;

                // Triggers the setup for the labels
                case LABEL:
                    itemLvl = Token.LABEL;
                    token = lexer.getNextToken();
                    break;

                // Triggers the setup for layouts of windows/panels
                case LAYOUT:
                    itemLvl = Token.LAYOUT;
                    token = lexer.getNextToken();
                    break;

                // Sets the appLvl to panel, all following items will appear on
                // the panel.
                case PANEL:
                    if (applicationPanel == null) {
                        applicationPanel = createPanel();
                    } else if (applicationPanel != null) {  // Means there is already a panel in use
                        panels.add(applicationPanel);       // Sends the current panel to the arraylist panels
                        applicationPanel = createPanel();   // Creates a new panel to be used
                    }
                    
                    //applicationPanel = new JPanel();
                    appLvl = Token.PANEL;
                    token = lexer.getNextToken();
                    break;

                // Triggers the setup for radiobuttons
                case RADIO:
                    itemLvl = Token.RADIO;
                    token = lexer.getNextToken();
                    break;

                // Triggers the setup for textfields
                case TEXTFIELD:
                    itemLvl = Token.TEXTFIELD;
                    token = lexer.getNextToken();
                    break;

                // Sets applvl to window, all following items will appear on the
                // window frame
                case WINDOW:
                    appLvl = Token.WINDOW;
                    applicationFrame = new JFrame();
                    token = lexer.getNextToken();
                    break;

                // Handles what to do when a comma appears
                case COMMA:
                    token = lexer.getNextToken();
                    break;

                // Handles what to do when a colon appears
                case COLON:
                    // If a colon appears while in the FLOW for layouts, handle accordingly
                    if (itemLvl == Token.FLOW) {
                        // Setup flow layout for window
                        if (appLvl == Token.WINDOW) {
                            applicationFrame.setLayout(flowLayout);
                        }
                        
                        // Setup flow layout for panel
                        if (appLvl == Token.PANEL) {
                            applicationPanel.setLayout(flowLayout);
                        }
                        
                        token = lexer.getNextToken();
                        break;
                    }
                    
                    // If a colon appears while in the GRID for layouts, handle accordingly
                    if (itemLvl == Token.GRID) {
                        // Checks if all the integer inputs are above 0
                        // if bufferX & bufferY are not above 0, only handles the 
                        // row and columns for grid layout
                        if ((row > 0) && (column > 0) && (bufferX > 0) && (bufferY > 0)) {
                            gridLayout = new GridLayout(row, column, bufferX, bufferY);
                            if (appLvl == Token.WINDOW) {
                                applicationFrame.setLayout(gridLayout);
                            }

                            if (appLvl == Token.PANEL) {
                                applicationPanel.setLayout(gridLayout);
                            }
                        } else if ((row > 0 && (column > 0))) {
                            gridLayout = new GridLayout(row, column);
                            if (appLvl == Token.WINDOW) {
                                applicationFrame.setLayout(gridLayout);
                            }

                            if (appLvl == Token.PANEL) {
                                applicationPanel.setLayout(gridLayout);
                            }
                        }
                        
                        // Reset values for next grid layout
                        row = 0;
                        column = 0;
                        bufferX = 0;
                        bufferY = 0;
                        token = lexer.getNextToken();
                        break;
                    }
                    break;

                // Handles what to happen when a semi colon appears
                case SEMICOLON:
                    // If a semi colon and the item level is a textfield, then
                    // handle creation of a textfield
                    if (itemLvl == Token.TEXTFIELD) {
                        if (appLvl == Token.WINDOW) {
                            if (tempInt > 0) {
                                applicationFrame.add(createTextField(tempInt));
                            } else {
                                applicationFrame.add(createTextField());
                            }
                        } else if (appLvl == Token.PANEL) {
                            if (tempInt > 0) {
                                applicationPanel.add(createTextField(tempInt));
                            } else {
                                applicationFrame.add(createTextField());
                            }
                        }
                        
                        // Reset tempint for next item that needs a int
                        tempInt = 0;
                        token = lexer.getNextToken();
                        break;
                    }
                    
                    // If semi colon and button, then handle creating a button
                    if (itemLvl == Token.BUTTON) {
                        if (appLvl == Token.WINDOW) {
                            if (tempStr == null) {
                                applicationFrame.add(createButton());
                            } else {
                                applicationFrame.add(createButton(tempStr));
                            }
                        }
                        
                        if (appLvl == Token.PANEL) {
                            System.out.println("String" + tempStr);
                            if (tempStr == null) {
                                applicationPanel.add(createButton());
                            } else {
                                applicationPanel.add(createButton(tempStr));
                            }
                        }
                        // Reset the tempstr
                        tempStr = null;
                        token = lexer.getNextToken();
                        break;
                    }
                    
                    // Handle creating a label if label and semicolon appear
                    if (itemLvl == Token.LABEL) {
                        if (appLvl == Token.WINDOW) {
                            if (tempStr == null) {
                                applicationFrame.add(createLabel());
                            } else {
                                applicationFrame.add(createLabel(tempStr));
                            }
                        }
                        
                        if (appLvl == Token.PANEL) {
                            if (tempStr == null) {
                                applicationPanel.add(createLabel());
                            } else {
                                applicationPanel.add(createLabel(tempStr));
                            }
                        }
                        // Reset the tempstr
                        tempStr = null;
                        token = lexer.getNextToken();
                        break;
                    }
                    
                    // Handle creating a radio button if radio and semicolon appear
                    if (itemLvl == Token.RADIO) {
                        if (appLvl == Token.WINDOW) {
                            if (tempStr == null) {
                                applicationFrame.add(createRadioButton());
                            } else {
                                applicationFrame.add(createRadioButton(tempStr));
                            }
                        }
                        
                        if (appLvl == Token.PANEL) {
                            if (tempStr == null) {
                                applicationPanel.add(createRadioButton());
                            } else {
                                applicationPanel.add(createRadioButton(tempStr));
                            }
                        }
                        // Reset the tempstr
                        tempStr = null;
                        token = lexer.getNextToken();
                        break;
                    }
                    
                    // Incase a semicolon appears and not doing anything, then goto next
                    // -this happens at the end
                    token = lexer.getNextToken();
                    break;

                // Period only happens at the end of the file, signifies end of file
                case PERIOD:
                    // Double check for the ends to active, then set fileFinished to true
                    if ((appLvl == Token.END) || (itemLvl == Token.END)) {
                        fileFinished = true;
                    }
                    break;

                // Handles left parenthesis, sets the inParen to true
                case LEFT_PAREN:
                    token = lexer.getNextToken();
                    inParen = true;
                    break;

                // Handles right parenthesis, sets the inParen to False
                case RIGHT_PAREN:
                    // If we are in window, then we are setting the window size
                    if (appLvl == Token.WINDOW) {
                        if (applicationFrame != null) {
                            if ((width != 0) && (height != 0)) {
                                applicationFrame.setSize(width, height);
                            }
                        }
                    }
                    
                    // Resets width, height and inParen for any other uses
                    width = 0;
                    height = 0;
                    inParen = false;
                    
                    token = lexer.getNextToken();
                    break;

                // Handles what to do when a string appears
                case STRING:
                    // If in a window and string, set the title
                    if (appLvl == Token.WINDOW) {
                        if (applicationFrame != null) {
                            if (appTitle == null) {
                                appTitle = lexer.getLexeme();
                                applicationFrame.setTitle(appTitle);
                            }
                        }
                    }
                    
                    // If in a string and button, set the tempstr
                    if (itemLvl == Token.BUTTON) {
                        tempStr = lexer.getLexeme();
                    }
                    
                    // If in a string and label, set the tempstr
                    if (itemLvl == Token.LABEL) {
                        tempStr = lexer.getLexeme();
                    }
                    
                    // If in a string and radio, set the tempstr
                    if (itemLvl == Token.RADIO) {
                        tempStr = lexer.getLexeme();
                    }
                    
                    token = lexer.getNextToken();
                    break;

                // Handle what to happen when in a number
                case NUMBER:
                    // If we are in a window and a number, then we are setting
                    // the variables for window size
                    if (appLvl == Token.WINDOW) {
                        if (applicationFrame != null) {
                            if (inParen) {
                                if (width == 0) {
                                    width = (int) lexer.getValue();
                                    token = lexer.getNextToken();
                                    break;
                                }
                                
                                if (width != 0) {
                                    height = (int) lexer.getValue();
                                    token = lexer.getNextToken();
                                    break;
                                }
                            }
                        }
                    }
                    
                    // if we are in a textfield, we are setting the columns in the
                    // textfield
                    if (itemLvl == Token.TEXTFIELD) {
                        tempInt = (int) lexer.getValue();
                        token = lexer.getNextToken();
                        break;
                    }
                    
                    // if we are in a grid, we are gathering the grid items
                    if (itemLvl == Token.GRID) {
                        if (inParen == true) {
                            // Get  the row
                            if (row == 0) {
                                row = (int) lexer.getValue();
                                System.out.println("Set row to: " + row);
                                token = lexer.getNextToken();
                                break;
                            }
                            
                            // Get the column
                            if ((row != 0) && (column == 0)) {
                                column = (int) lexer.getValue();
                                System.out.println("Set column to: " + column);
                                token = lexer.getNextToken();
                                gridLayout = new GridLayout(row, column);
                                break;
                            }
                            
                            // Get the bufferX
                            if ((row != 0) && (column != 0) && (bufferX == 0)) {
                                bufferX = (int) lexer.getValue();
                                System.out.println("Set bufferx to: " + bufferX);
                                token = lexer.getNextToken();
                                break;
                            }
                            
                            // Get the bufferY
                            if ((row != 0) && (column != 0) && (bufferX != 0) &&
                                    (bufferY == 0)) {
                                bufferY = (int) lexer.getValue();
                                System.out.println("Set buffery to: " + bufferY);
                                token = lexer.getNextToken();
                                gridLayout = new GridLayout(row, column, bufferX,
                                        bufferY);
                                break;
                            }
                        }
                    }
                    
                    break;

                // Incase of an EOF, close the loop
                case EOF:
                    fileFinished = true;
                    break;
                    
                default:
                    System.out.println("ISSUE WITH FILE, CAN NOT GENERATE GUI");
                    fileFinished = true;
                    break;
                }
            
            
        }
        
       
        
        // Grabs all the panels from the panels arraylist and adds them to the 
        // frame
        for (int i = 0; i < panels.size(); i++) {
            addedPanels.add(panels.get(i));
        }
        addedPanels.add(applicationPanel);
         
        // Outside of the loop we add the panel to the frame, set the location
        // to center screen, closer operation and make it visible for the user
        applicationFrame.add(addedPanels);
        applicationFrame.setLocationRelativeTo(null);
        applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applicationFrame.setVisible(true);
    }
    
    // createTextField method creates a blank textfield
    public JTextField createTextField() {
        JTextField newText = new JTextField();
        
        return newText;
    }
    
    // createTextField(x) method creates a textfield with x columns
    public JTextField createTextField(int columns) {
        JTextField newText = new JTextField(columns);
        
        return newText;
    }
    
    // createButton creates a button
    public JButton createButton() {
        JButton newButton = new JButton();
        
        return newButton;
    }
    
    // createButton(string) creates a button with the text of string
    public JButton createButton(String btn) {
        JButton newButton = new JButton(btn);
        
        return newButton;
    }
    
    // createLabel creates a label
    public JLabel createLabel() {
        JLabel newLabel = new JLabel();
        
        return newLabel;
    }
    
    // createLabel(string) creates a label of string
    public JLabel createLabel(String lbl) {
        JLabel newLabel = new JLabel(lbl);
        
        return newLabel;
    }
    
    // createRadioButton creates a blank radio button
    public JRadioButton createRadioButton() {
        JRadioButton newRadioButton = new JRadioButton();
        
        return newRadioButton;
    }
    
    // createRadioButton(string) creates a radio button with the text of string
    public JRadioButton createRadioButton(String str) {
        JRadioButton newRadioButton = new JRadioButton(str);
        
        return newRadioButton;
    }
    
    // createPanel creates the panels for multiple nested panels
    public JPanel createPanel() {
        JPanel newPanel = new JPanel();
        
        return newPanel;
    }
}
