
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NBDrawApp1 extends JFrame {

    // GUI Component dimentsions.
    private final int CANVAS_INITIAL_WIDTH = 800;
    private final int CANVAS_INITIAL_HEIGHT = 640;
    private final int CONTROL_PANEL_WIDTH = 200;
    private final int MESSAGE_AREA_HEIGHT = 100;

    //Freehand pixels
    private final int MAX_FREEHAND_PIXELS = 1000;
    private Color[] freehandColour = new Color[MAX_FREEHAND_PIXELS];
    private int[][] fxy = new int[MAX_FREEHAND_PIXELS][3];
    private int freehandPixelsCount = 0;
    private int freehandThickness;

    private Color selectedColour = new Color(0.0F, 0.0F, 0.0F);

    private final int MAX_OBJECT_NUM = 10;
    
    //Rectangle
    private Color[] rectColour = new Color[MAX_OBJECT_NUM];
    private int[][] rxy = new int[MAX_OBJECT_NUM][4]; //because we need w, h, x and y coordinates
    private int RectangleCount = 0;

    //Line
    private Color[] lineColour = new Color[MAX_OBJECT_NUM];
    private int[][] lxy = new int[MAX_OBJECT_NUM][4];
    private int LineCount = 0;
    
    //Oval
    private Color[] ovalColour = new Color[MAX_OBJECT_NUM];
    private int[][] oxy = new int[MAX_OBJECT_NUM][4];
    private int OvalCount = 0;

    private Timer animatorTimer;

    private Canvas canvas;

    private JPanel controlPanel;
    private JLabel coordinatesLabel;
    private JRadioButton lineRadioButton, ovalRadioButton, rectangleRadioButton, freehandRadioButton;
    private JSlider freehandSizeSlider;
    private JCheckBox fineCheckBox, coarseCheckBox;
    private JButton colourButton, clearButton, animateButton;
    private JTextArea messageArea;
    private JMenuBar menuBar;
    private JOptionPane optionPanel;

    // Drawing area class (inner class).
    class Canvas extends JPanel {

        // Called every time there is a change in the canvas contents.
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);

        }

    }
    // end inner class Canvas

    private void draw(Graphics g) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        if (fineCheckBox.isSelected()) {
            g.setColor(new Color(0.8F, 0.8F, 0.8F));
            //replace oval drawing by code for 1st lines
            for (int x = 0; x < w; x = x + 10) {
                g.drawLine(x, 0, x, h);
            }
            for (int y = 0; y < h; y = y + 10) {
                g.drawLine(0, y, w, y);
            }

        }
        if (coarseCheckBox.isSelected()) {
            g.setColor(new Color(0.5F, 0.5F, 0.5F));
            //replace rect drawing by code for coarse lines
            for (int x = 0; x < w; x = x + 50) {
                g.drawLine(x, 0, x, h);
            }
            for (int y = 0; y < h; y = y + 50) {
                g.drawLine(0, y, w, y);
            }
        }
        //Freehand draw on Canvas
        for (int i = 0; i < freehandPixelsCount; i++) {
            if (freehandPixelsCount <= MAX_FREEHAND_PIXELS) {
                g.setColor(freehandColour[i]);
                g.fillRect(fxy[i][0], fxy[i][1], fxy[i][2], fxy[i][2]);
            }
        }
        for (int i = 0; i <= RectangleCount; i++) {
            if (RectangleCount <= MAX_OBJECT_NUM) {
                g.setColor(rectColour[i]);
                int width = rxy[i][2] - rxy[i][0];
                int height = rxy[i][3] - rxy[i][1];
                g.drawRect(rxy[i][0], rxy[i][1], width, height);
            }
        }

        for (int i = 0; i <= LineCount; i++) {
            if (LineCount <= MAX_OBJECT_NUM) {
                g.setColor(lineColour[i]);
                g.drawLine(lxy[i][0], lxy[i][1], lxy[i][2], lxy[i][3]);

            }
        }

        for (int i = 0; i <= OvalCount; i++) {
            if (OvalCount <= MAX_OBJECT_NUM) {
                g.setColor(ovalColour[i]);
                int width = oxy[i][2] - oxy[i][0];
                int height = oxy[i][3] - oxy[i][1];
                g.drawOval(oxy[i][0], oxy[i][1], width, height);

            }
        }
    }
   
    /**
     * ***************************************************************
     *
     * Constructor method starts here ... and goes on for quite a few lines of
     * code
     */
    
    public NBDrawApp1() {
        setTitle("Drawing Application (da1)");
        setLayout(new BorderLayout());  // Layout manager for the frame.
        // Canvas
        canvas = new Canvas();
        canvas.setBorder(new TitledBorder(new EtchedBorder(), "Canvas"));
        canvas.setPreferredSize(new Dimension(CANVAS_INITIAL_WIDTH, CANVAS_INITIAL_HEIGHT));
        // next line changes the cursor's rendering whenever the mouse drifts onto the canvas
        canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        add(canvas, BorderLayout.CENTER);

        // Menu bar
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileSaveMenuItem = new JMenuItem("Save");
        fileSaveMenuItem.addActionListener(new saveMenuListener());
        fileMenu.add(fileSaveMenuItem);
        JMenuItem fileLoadMenuItem = new JMenuItem("Load");
        fileLoadMenuItem.addActionListener(new loadMenuListener());
        fileMenu.add(fileLoadMenuItem);
        fileMenu.addSeparator();
        JMenuItem fileExitMenuItem = new JMenuItem("Exit");
        fileExitMenuItem.addActionListener(new exitMenuListener());
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpAboutMenuItem = new JMenuItem("About");
        helpAboutMenuItem.addActionListener(new aboutMenuListener());
        helpMenu.add(helpAboutMenuItem);
        menuBar.add(helpMenu);
        add(menuBar, BorderLayout.PAGE_START);

        // Control Panel
        controlPanel = new JPanel();
        controlPanel.setBorder(new TitledBorder(new EtchedBorder(), "Control Panel"));
        controlPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH, CANVAS_INITIAL_HEIGHT));
        // the following two lines put the control panel in a scroll pane (nicer?).      
        JScrollPane controlPanelScrollPane = new JScrollPane(controlPanel);
        controlPanelScrollPane.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH + 30, CANVAS_INITIAL_HEIGHT));
        add(controlPanelScrollPane, BorderLayout.LINE_START);

        // Control Panel contents are specified in the next section eg: 
        //    mouse coords panel; 
        //    shape tools panel; 
        //    trace-slider panel; 
        //    grid panel; 
        //    colour choice panel; 
        //    "clear" n "animate" buttons
        // Mouse Coordinates panel
        JPanel coordinatesPanel = new JPanel();
        coordinatesPanel.setBorder(new TitledBorder(new EtchedBorder(), "Drawing Position"));
        coordinatesPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 60));
        coordinatesLabel = new JLabel();
        coordinatesLabel.setText("some text");
        coordinatesPanel.add(coordinatesLabel);
        controlPanel.add(coordinatesPanel);

        // Drawing tools panel
        JPanel drawingToolsPanel = new JPanel();
        drawingToolsPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 140));
        drawingToolsPanel.setLayout(new GridLayout(0, 1));
        drawingToolsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Drawing Tools"));
        controlPanel.add(drawingToolsPanel);
        rectangleRadioButton = new JRadioButton("Rectangle");
        lineRadioButton = new JRadioButton("Line");
        ovalRadioButton = new JRadioButton("Oval");
        freehandRadioButton = new JRadioButton("Freehand");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rectangleRadioButton);
        bg.add(lineRadioButton);
        bg.add(ovalRadioButton);
        bg.add(freehandRadioButton);
        drawingToolsPanel.add(rectangleRadioButton);
        drawingToolsPanel.add(lineRadioButton);
        drawingToolsPanel.add(ovalRadioButton);
        drawingToolsPanel.add(freehandRadioButton);

        // Freehand trace size slider
        JPanel freehandSliderPanel = new JPanel();
        freehandSliderPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 90));
        drawingToolsPanel.setLayout(new GridLayout(0, 1));
        freehandSliderPanel.setBorder(new TitledBorder(new EtchedBorder(), "Freehand Size"));
        freehandSizeSlider = new JSlider(0, 20, 1);
        freehandThickness = freehandSizeSlider.getValue();
        freehandSliderPanel.add(freehandSizeSlider);
        controlPanel.add(freehandSliderPanel);
        freehandSizeSlider.addChangeListener(new FreehandSliderListener());

        // Grid Panel
        JPanel gridPanel = new JPanel();
        gridPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 80));
        gridPanel.setLayout(new GridLayout(0, 1));
        gridPanel.setBorder(new TitledBorder(new EtchedBorder(), "Grid"));
        fineCheckBox = new JCheckBox("Fine");
        fineCheckBox.addChangeListener(new MyCheckBoxesListener());
        gridPanel.add(fineCheckBox);
        coarseCheckBox = new JCheckBox("Coarse");
        coarseCheckBox.addChangeListener(new MyCheckBoxesListener());
        gridPanel.add(coarseCheckBox);
        controlPanel.add(gridPanel);

        // Colour Panel
        JPanel colourPanel = new JPanel();
        colourPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 90));
        colourPanel.setBorder(new TitledBorder(new EtchedBorder(), "Colour"));
        colourButton = new JButton();
        colourButton.addActionListener(new ColourActionListener());
        colourButton.setPreferredSize(new Dimension(50, 50));
        colourPanel.add(colourButton);
        controlPanel.add(colourPanel);

        // Clear button
        clearButton = new JButton("Clear Canvas");
        clearButton.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 50));
        controlPanel.add(clearButton);
        clearButton.addActionListener(new ClearCanvasListener());

        // Animate button 
        animateButton = new JButton("Animate");
        animateButton.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 50));
        controlPanel.add(animateButton);
        animateButton.addActionListener(new AnimateListener());
        MyAnimatorClass animator = new MyAnimatorClass();
        animatorTimer = new Timer(200, animator);
    // that completes the control panel section

        // Message area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(canvas.getBackground());
        JScrollPane textAreaScrollPane = new JScrollPane(messageArea);
        textAreaScrollPane.setBorder(new TitledBorder(new EtchedBorder(), "Message Area"));
        textAreaScrollPane.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH + CANVAS_INITIAL_WIDTH, MESSAGE_AREA_HEIGHT));
        add(textAreaScrollPane, BorderLayout.PAGE_END);

        //Mouse Listener
        canvas.addMouseMotionListener(new CanvasMouseMotionListener());
        canvas.addMouseListener(new CanvasMouseListener());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setVisible(true);

    }  // end of the NBDrawApp1 constructor method

    // Called by the canvas' paintComponent method
    class CanvasMouseMotionListener implements MouseMotionListener {

        public void mouseMoved(MouseEvent event){
            
            coordinatesLabel.setText(event.getX() + ", " + event.getY());
        }

        public void mouseDragged(MouseEvent event) {
            coordinatesLabel.setText(event.getX() + ", " + event.getY());

            if (freehandRadioButton.isSelected()) {
                if (freehandPixelsCount < MAX_FREEHAND_PIXELS) {
                    freehandColour[freehandPixelsCount] = selectedColour;
                    fxy[freehandPixelsCount][0] = event.getX();
                    fxy[freehandPixelsCount][1] = event.getY();
                    fxy[freehandPixelsCount][2] = freehandThickness; //dimensions
                    freehandPixelsCount++;
                    messageArea.append("Pixels: " + freehandPixelsCount + "\n");

                } else {
                    messageArea.setText("Array Full!");
                }
            } else if (rectangleRadioButton.isSelected()) {
                rxy[RectangleCount][2] = event.getX();
                rxy[RectangleCount][3] = event.getY();
            } else if (lineRadioButton.isSelected()) {
                lxy[LineCount][2] = event.getX();
                lxy[LineCount][3] = event.getY();

            } else if (ovalRadioButton.isSelected()) {
                oxy[OvalCount][2] = event.getX();
                oxy[OvalCount][3] = event.getY();
            }

            mouseMoved(event);
            canvas.repaint();
        }

    }

    class CanvasMouseListener implements MouseListener {

        public void mousePressed(MouseEvent event) {
            coordinatesLabel.setText(event.getX() + ", " + event.getY());

            if (rectangleRadioButton.isSelected()) {
                rxy[RectangleCount][0] = event.getX();
                rxy[RectangleCount][1] = event.getY();
                rxy[RectangleCount][2] = event.getX();
                rxy[RectangleCount][3] = event.getY();
                rectColour[RectangleCount] = selectedColour;
            } else if (lineRadioButton.isSelected()) {
                lxy[LineCount][0] = event.getX();
                lxy[LineCount][1] = event.getY();
                lxy[LineCount][2] = event.getX();
                lxy[LineCount][3] = event.getY();
                lineColour[LineCount] = selectedColour;
            } else if (ovalRadioButton.isSelected()) {
                oxy[OvalCount][0] = event.getX();
                oxy[OvalCount][1] = event.getY();
                oxy[OvalCount][2] = event.getX();
                oxy[OvalCount][3] = event.getY();
                ovalColour[OvalCount] = selectedColour;
            }
        }

        public void mouseReleased(MouseEvent event) {
            coordinatesLabel.setText(event.getX() + ", " + event.getY());

            if (rectangleRadioButton.isSelected()) {
                rxy[RectangleCount][2] = event.getX();
                rxy[RectangleCount][3] = event.getY();
                RectangleCount++;
            } else if (lineRadioButton.isSelected()) {
                lxy[LineCount][2] = event.getX();
                lxy[LineCount][3] = event.getY();
                LineCount++;
            } else if (ovalRadioButton.isSelected()) {
                oxy[OvalCount][2] = event.getX();
                oxy[OvalCount][3] = event.getY();
                OvalCount++;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseEntered(MouseEvent e) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent e) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    //Listen for grid-checkbox changes
    class MyCheckBoxesListener implements ChangeListener {

        public void stateChanged(ChangeEvent event) {
            canvas.repaint();
        }
    } //end class MyCheckBoxesListener

    class FreehandSliderListener implements ChangeListener {

        public void stateChanged(ChangeEvent event) {
            freehandThickness = freehandSizeSlider.getValue();
        }
    }

    class ColourActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JColorChooser colourChooser = new JColorChooser(selectedColour);
            selectedColour = colourChooser.showDialog(null, "Choose new drawing colour", selectedColour);
            //selectedColour = newColour;
            colourButton.setBackground(selectedColour);
       colourButton.setForeground(selectedColour);
        }

    }

    class ClearCanvasListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            freehandPixelsCount = 0;
            LineCount = 0;
            lxy[0][0] = 0;
            lxy[0][1] = 0;
            lxy[0][2] = 0;
            lxy[0][3] = 0;
            RectangleCount = 0;
            rxy[0][0] = 0;
            rxy[0][1] = 0;
            rxy[0][2] = 0;
            rxy[0][3] = 0;
            OvalCount = 0;
            oxy[0][0] = 0;
            oxy[0][1] = 0;
            oxy[0][2] = 0;
            oxy[0][3] = 0;
            canvas.repaint();
            animatorTimer.stop();
        }
    }

    class AnimateListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            animatorTimer.start();
        }
    }

    class MyAnimatorClass implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < OvalCount; i++) {
                if (oxy[i][3] < canvas.getHeight() - 5) {
                    oxy[i][1] = oxy[i][1] + 10;
                    oxy[i][3] = oxy[i][3] + 10;
                }
            }

            for (int i = 0; i < RectangleCount; i++) {
                if (rxy[i][3] < canvas.getHeight() - 5) {
                    rxy[i][1] = rxy[i][1] + 10;
                    rxy[i][3] = rxy[i][3] + 10;
                }
            }
            canvas.repaint();
        }
    }

    class exitMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            animatorTimer.stop();
            System.exit(0);
        }
    }

    class aboutMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            animatorTimer.stop();
            JOptionPane.showMessageDialog(canvas, "Simple Drawing Application");
        }
    }

    class saveMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            animatorTimer.stop();
            try {
                FileOutputStream fos = new FileOutputStream("drawing");
                ObjectOutputStream fh = new ObjectOutputStream(fos);
                fh.writeObject(lxy);
                fh.writeObject(lineColour);
                fh.writeObject(LineCount);
                fh.writeObject(rxy);
                fh.writeObject(rectColour);
                fh.writeObject(RectangleCount);
                fh.writeObject(oxy);
                fh.writeObject(ovalColour);
                fh.writeObject(OvalCount);
                fh.writeObject(fxy);
                fh.writeObject(freehandPixelsCount);
                fh.writeObject(freehandThickness);
                fh.writeObject(selectedColour);
                fh.close();
            } catch (FileNotFoundException Exception) {
                System.out.println("Error!");
            } catch (IOException ex) {
                Logger.getLogger(NBDrawApp1.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    class loadMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            animatorTimer.stop();
            try {
                FileInputStream fis = new FileInputStream("drawing");
                ObjectInputStream fh = new ObjectInputStream(fis);
                lxy = (int[][]) fh.readObject();
                lineColour = (Color[]) fh.readObject();
                LineCount = (int) fh.readObject();
                rxy = (int[][]) fh.readObject();
                rectColour = (Color[]) fh.readObject();
                RectangleCount = (int) fh.readObject();
                oxy = (int[][]) fh.readObject();
                ovalColour = (Color[]) fh.readObject();
                OvalCount = (int) fh.readObject();
                fxy = (int[][]) fh.readObject();
                freehandPixelsCount = (int) fh.readObject();
                freehandThickness = (int) fh.readObject();
                selectedColour = (Color) fh.readObject();
                fh.close();
            } catch (Exception ex) {
                System.out.println("Error!");
            }
            canvas.repaint();
        }
    }

    public static void main(String args[]) {
        NBDrawApp1 NBDrawApp1Instance = new NBDrawApp1();

    } // end main method

} // end of NBDrawApp1 class
