import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;



public class CTHead extends JFrame {

    private JButton mip_top_button, mip_front_button, mip_side_button, hisEq_top_button, hisEq_front_button, hisEq_side_button, thumbnail_button; //button to switch to MIP
    private JLabel image_icon1, image_icon2, image_icon3, image_icon4; // JLabel to display an image 
    private JSlider zslice_slider, yslice_slider, xslice_slider, size_slider; //sliders to step through the slices (z and y directions) 
    private BufferedImage image1, image2, image3, image4, image5, image6, image7, image8, image9, image10; //storing the image in memory
    private JFrame thumbnailFrame = new JFrame("Thumbnail");
    private short cthead[][][]; //store the 3D volume data set
    private short min, max; //min/max value in the 3D volume data set
    private boolean zslice_slider_state = false;
    private boolean yslice_slider_state = false;
    private boolean xslice_slider_state = false;
    private boolean mip_start = true;
    private boolean mip_top_on = false;
    private boolean mip_front_on = false;
    private boolean mip_side_on = false;
    private boolean hisEq_top_on = false;
    private boolean hisEq_front_on = false;
    private boolean hisEq_side_on = false;
    private boolean size_slider_state = false;
    private String lastOn = "";
    private String lastOff = "";
    private String lastSlider = "";
    private int RESIZE_MAX = 912;
    private BufferedImage topImages[], frontImages[], sideImages[];

    /*
        function sets up the GUI and reads the data set
    */
    private void Example() throws IOException {
        
        File file = new File("CThead");

        //Create a BufferedImage to store the image data
        image1=new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
        image2=new BufferedImage(256, 113, BufferedImage.TYPE_3BYTE_BGR);
        image3=new BufferedImage(256, 113, BufferedImage.TYPE_3BYTE_BGR);
        image4=new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
        image5=new BufferedImage(256, 113, BufferedImage.TYPE_3BYTE_BGR);
        image6=new BufferedImage(256, 113, BufferedImage.TYPE_3BYTE_BGR);
        image7=new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
        image8=new BufferedImage(256, 113, BufferedImage.TYPE_3BYTE_BGR);
        image9=new BufferedImage(256, 113, BufferedImage.TYPE_3BYTE_BGR);
        image10 = new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);


        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        int i, j, k; //loop through the 3D data set

        min=Short.MAX_VALUE; max=Short.MIN_VALUE; //set to extreme values
        short read; //value read in
        int b1, b2;

        cthead = new short[113][256][256];
        for (k=0; k<113; k++) {
            for (j=0; j<256; j++) {
                for (i=0; i<256; i++) {
                   
                    b1=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types 
                    b2=((int)in.readByte()) & 0xff; 
                    read=(short)((b2<<8) | b1); 
                    if (read<min) min=read; //update the minimum
                    if (read>max) max=read; //update the maximum
                    cthead[k][j][i]=read; //put the short into memory 
                }
            }
        }
        System.out.println(min+" "+max);

        Container container = getContentPane();
        container.setLayout(new FlowLayout());

        Box box = Box.createVerticalBox();
        Box hBox = Box.createHorizontalBox();

        hBox.add(new JLabel("Top View:"));
        hBox.add(Box.createRigidArea(new Dimension(200,0)));
        box.add(hBox);
        box.add(Box.createRigidArea(new Dimension(0,2)));

        hBox = Box.createHorizontalBox();
        //Top view MIP button
        mip_top_button = new JButton("MIP");
        hBox.add(mip_top_button);
        hBox.add(Box.createRigidArea(new Dimension(5,0)));
        //Top view Histogram Equalization button
        hisEq_top_button = new JButton("Histogram Equalization");
        hBox.add(hisEq_top_button);
        box.add(hBox);

        box.add(Box.createRigidArea(new Dimension(0,5)));
        //Icon 1
        image_icon1=new JLabel(new ImageIcon(image1));
        hBox = Box.createHorizontalBox();
        hBox.add(image_icon1);
        box.add(hBox);

        hBox = Box.createHorizontalBox();
        //Size slider
        size_slider = new JSlider(1,RESIZE_MAX);
        size_slider.setValue(256);
        size_slider.setMajorTickSpacing(100);
        size_slider.setMinorTickSpacing(10);
        size_slider.setPaintTicks(true);
        size_slider.setPaintLabels(true);
        hBox.add(size_slider);
        box.add(hBox);

        hBox = Box.createHorizontalBox();

        // top view MIP for resize
        MIP(image10);
        mip_start = false;
        //Icon 4
        image_icon4=new JLabel(new ImageIcon(image10));
        hBox.add(image_icon4);
        box.add(hBox);
        box.add(Box.createRigidArea(new Dimension(RESIZE_MAX,0)));
        container.add(box);

        box = Box.createVerticalBox();
        hBox = Box.createHorizontalBox();

        hBox.add(new JLabel("Front View:"));
        hBox.add(Box.createRigidArea(new Dimension(190,0)));
        box.add(hBox);
        box.add(Box.createRigidArea(new Dimension(0,2)));

        hBox = Box.createHorizontalBox();
        //Front view MIP button
        mip_front_button = new JButton("MIP");
        hBox.add(mip_front_button);
        hBox.add(Box.createRigidArea(new Dimension(5,0)));
        //Front view Histogram Equalization button
        hisEq_front_button = new JButton("Histogram Equalization");
        hBox.add(hisEq_front_button);
        box.add(hBox);

        box.add(Box.createRigidArea(new Dimension(0,5)));
        //Icon 2
        image_icon2=new JLabel(new ImageIcon(image2));
        hBox = Box.createHorizontalBox();
        hBox.add(image_icon2);
        box.add(hBox);
        box.add(Box.createRigidArea(new Dimension(0,143)));
        container.add(box);

        box = Box.createVerticalBox();
        hBox = Box.createHorizontalBox();

        hBox.add(new JLabel("Side View:"));
        hBox.add(Box.createRigidArea(new Dimension(197,0)));
        box.add(hBox);
        box.add(Box.createRigidArea(new Dimension(0,2)));

        hBox = Box.createHorizontalBox();
        //Side view MIP button
        mip_side_button = new JButton("MIP");
        hBox.add(mip_side_button);
        hBox.add(Box.createRigidArea(new Dimension(5,0)));
        //Side view Histogram Equalization button
        hisEq_side_button = new JButton("Histogram Equalization");
        hBox.add(hisEq_side_button);
        box.add(hBox);

        box.add(Box.createRigidArea(new Dimension(0,5)));
        //Icon 3
        image_icon3=new JLabel(new ImageIcon(image3));
        hBox = Box.createHorizontalBox();
        hBox.add(image_icon3);
        box.add(hBox);
        box.add(Box.createRigidArea(new Dimension(0,143)));
        container.add(box);

        box = Box.createVerticalBox();
        box.add(new JLabel("Top View:"));
        box.add(Box.createRigidArea(new Dimension(0,10)));
        //Zslice slider
        zslice_slider = new JSlider(0,112);
        box.add(zslice_slider);
        //Add labels 
        zslice_slider.setMajorTickSpacing(50);
        zslice_slider.setMinorTickSpacing(10);
        zslice_slider.setPaintTicks(true);
        zslice_slider.setPaintLabels(true);
        box.add(Box.createRigidArea(new Dimension(0,30)));

        box.add(new JLabel("Front View:"));
        box.add(Box.createRigidArea(new Dimension(0,10)));
        //Y slice slider
        yslice_slider = new JSlider(0,255);
        box.add(yslice_slider);
        //Add labels 
        yslice_slider.setMajorTickSpacing(50);
        yslice_slider.setMinorTickSpacing(10);
        yslice_slider.setPaintTicks(true);
        yslice_slider.setPaintLabels(true);
        box.add(Box.createRigidArea(new Dimension(0,30)));
        box.add(new JLabel("Side View:"));
        box.add(Box.createRigidArea(new Dimension(0,10)));
        //X slice slider
        xslice_slider = new JSlider(0,255);
        box.add(xslice_slider);
        //Add labels 
        xslice_slider.setMajorTickSpacing(50);
        xslice_slider.setMinorTickSpacing(10);
        xslice_slider.setPaintTicks(true);
        xslice_slider.setPaintLabels(true);

        //Thumbnail button
        thumbnail_button = new JButton("Thumbnail");
        box.add(thumbnail_button);
        container.add(box);

        // handlers class
        GUIEventHandler handler = new GUIEventHandler();

     
        mip_top_button.addActionListener(handler);
        mip_front_button.addActionListener(handler);
        mip_side_button.addActionListener(handler);
        yslice_slider.addChangeListener(handler);
        zslice_slider.addChangeListener(handler);
        xslice_slider.addChangeListener(handler);
        hisEq_top_button.addActionListener(handler);
        hisEq_front_button.addActionListener(handler);
        hisEq_side_button.addActionListener(handler);
        size_slider.addChangeListener(handler);
        thumbnail_button.addActionListener(handler);

       
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private class GUIEventHandler implements ActionListener, ChangeListener {

        //Change handler 
        public void stateChanged(ChangeEvent e) {
            //Slider for changing slices of top view
            if (e.getSource()==zslice_slider){
                zslice_slider_state = true;
                lastOff = "mip_top";
                mip_top_on = false;
                mip_top_button.setBackground(null);
                //Histogram Equalization mode on
                if (hisEq_top_on){
                    lastSlider = "hisEq_top";
                    image7=histogramEq(image7);
                    lastSlider="";
                    image_icon1.setIcon(new ImageIcon(image7));
          
                } else {
                    image1=view(image1);
                    image_icon1.setIcon(new ImageIcon(image1));
                }
                zslice_slider_state=false;
            //Slider for changing slices of front view
            } else if (e.getSource()==yslice_slider){
                yslice_slider_state = true;
                lastOff = "mip_front";
                mip_front_on = false;
                mip_front_button.setBackground(null);
                //Histogram Equalization mode on
                if (hisEq_front_on) {
                    lastSlider = "hisEq_front";
                    image8 = histogramEq(image8);
                    lastSlider = "";
                    image_icon2.setIcon(new ImageIcon(image8));
                //normal view mode
                } else {
                    image2=view(image2);
                    image_icon2.setIcon(new ImageIcon(image2));
                }
                yslice_slider_state=false;
            //Slider for changing slices of side view
            } else if (e.getSource()==xslice_slider){
                xslice_slider_state = true;
                lastOff = "mip_side";
                mip_side_on = false;
                mip_side_button.setBackground(null);
                //Histogram Equalization mode on
                if (hisEq_side_on) {
                    lastSlider = "hisEq_side";
                    image9 = histogramEq(image9);
                    lastSlider = "";
                    image_icon3.setIcon(new ImageIcon(image9));
                //normal view mode
                } else {
                    image3=view(image3);
                    image_icon3.setIcon(new ImageIcon(image3));
                }
                xslice_slider_state=false;
            //Slider for changing image size
            } else if (e.getSource() == size_slider) {
                size_slider_state = true;
                BufferedImage image11 = image10;
                image11=resize(image11, size_slider.getValue());
                image_icon4.setIcon(new ImageIcon(image11));
                size_slider_state = false;
            }
        }

        //action handlers 
        public void actionPerformed(ActionEvent event) {
            //MIP button for top view
            if (event.getSource()==mip_top_button) {
                mip_top_on = !mip_top_on;
                //Clicked for on
                if (mip_top_on) {
                    lastOn = "mip_top";
                    hisEq_top_on = false;
                    hisEq_top_button.setBackground(null);
                    mip_top_button.setBackground(Color.GREEN);
                    image4=MIP(image4);
                    lastOn = "";
                    image_icon1.setIcon(new ImageIcon(image4));
                //Clicked for off
                } else {
                    lastOff = "mip_top";
                    mip_top_button.setBackground(null);
                    image1 = view(image1);
                    image_icon1.setIcon(new ImageIcon(image1));
                }
            //MIP button for front view
            } else if (event.getSource()==mip_front_button){
                mip_front_on = !mip_front_on;
                //Clicked for on
                if (mip_front_on){
                    lastOn = "mip_front";
                    hisEq_front_on = false;
                    hisEq_front_button.setBackground(null);
                    mip_front_button.setBackground(Color.GREEN);
                    image5=MIP(image5);
                    lastOn = "";
                    image_icon2.setIcon(new ImageIcon(image5));
                //Clicked for off
                } else {
                    lastOff = "mip_front";
                    mip_front_button.setBackground(null);
                    image2 = view(image2);
                    image_icon2.setIcon(new ImageIcon(image2));
                }
            //MIP button for side view
            } else if (event.getSource()==mip_side_button){
                mip_side_on = !mip_side_on;
                //Clicked for on
                if (mip_side_on){
                    lastOn = "mip_side";
                    hisEq_side_on = false;
                    hisEq_side_button.setBackground(null);
                    mip_side_button.setBackground(Color.GREEN);
                    image6=MIP(image6);
                    lastOn = "";
                    image_icon3.setIcon(new ImageIcon(image6));
                //Clicked for off
                } else {
                    lastOff = "mip_side";
                    mip_side_button.setBackground(null);
                    image3 = view(image3);
                    image_icon3.setIcon(new ImageIcon(image3));
                }
            //Histogram Equalization button for top view
            } else if (event.getSource()==hisEq_top_button){
                hisEq_top_on = !hisEq_top_on;
                //Clicked for on
                if (hisEq_top_on){
                    mip_top_on = false;
                    mip_top_button.setBackground(null);
                    hisEq_top_button.setBackground(Color.GREEN);
                    lastOn = "hisEq_top";
                    image7=histogramEq(image7);
                    lastOn = "";
                    image_icon1.setIcon(new ImageIcon(image7));
                //Clicked for off
                } else {
                    lastOff = "hisEq_top";
                    hisEq_top_button.setBackground(null);
                    image1 = view(image1);
                    image_icon1.setIcon(new ImageIcon(image1));
                }

            } else if (event.getSource()==hisEq_front_button){
                hisEq_front_on = !hisEq_front_on;
                //Clicked for on
                if (hisEq_front_on){
                    mip_front_on = false;
                    mip_front_button.setBackground(null);
                    hisEq_front_button.setBackground(Color.GREEN);
                    lastOn = "hisEq_front";
                    image8=histogramEq(image8);
                    lastOn = "";
                    image_icon2.setIcon(new ImageIcon(image8));
                //Clicked for off
                } else {
                    lastOff = "hisEq_front";
                    hisEq_front_button.setBackground(null);
                    image2 = view(image2);
                    image_icon2.setIcon(new ImageIcon(image2));
                }
            //Histogram Equalization button for side view
            } else if (event.getSource()==hisEq_side_button) {
                hisEq_side_on = !hisEq_side_on;
                //Clicked for on
                if (hisEq_side_on) {
                    mip_side_on = false;
                    mip_side_button.setBackground(null);
                    hisEq_side_button.setBackground(Color.GREEN);
                    lastOn = "hisEq_side";
                    image9=histogramEq(image9);
                    lastOn = "";
                    image_icon3.setIcon(new ImageIcon(image9));
                //Clicked for off
                } else {
                    lastOff = "hisEq_side";
                    hisEq_side_button.setBackground(null);
                    image3=view(image3);
                    image_icon3.setIcon(new ImageIcon(image3));
                }
            //Thumbnail button
            } else if (event.getSource()==thumbnail_button){

                if (topImages == null){
                    thumbnail();
                    System.out.println("thumbnails filled in arrays");
                }
                thumbnailGUI();
            }
        }
    }

    private void thumbnailGUI() {
        thumbnailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(1018,700));
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);

        thumbnailFrame.getContentPane().add(scrollPane);
        thumbnailFrame.pack();

        JLabel top = new JLabel("Top View:");
        top.setFont(top.getFont().deriveFont(22.0f));
        panel.add(top);
        Box hBox  =  Box.createHorizontalBox();
     
        for (int i=0; i<cthead.length; i++){
            BufferedImage image = topImages[i];
            JLabel image_icon = new JLabel();
            image_icon.setIcon(new ImageIcon(image));
            
            if ((i%10)==0){
                hBox =  Box.createHorizontalBox();
            }
            hBox.add(image_icon);
            panel.add(hBox);
        }

        JLabel front = new JLabel("Front View:");
        front.setFont(front.getFont().deriveFont(22.0f));
        panel.add(front);
        hBox  =  Box.createHorizontalBox();
        //showing front view
        for (int i=0; i<cthead[0].length; i++){
            BufferedImage image = frontImages[i];
            JLabel image_icon = new JLabel();
            image_icon.setIcon(new ImageIcon(image));
            
            if ((i%10)==0){
                hBox =  Box.createHorizontalBox();
            }
            hBox.add(image_icon);
            panel.add(hBox);
        }

        JLabel side = new JLabel("Side View:");
        side.setFont(side.getFont().deriveFont(22.0f));
        panel.add(side);
        hBox  =  Box.createHorizontalBox();
        //side view
        for (int i=0; i<cthead[0][0].length; i++){
            BufferedImage image = sideImages[i];
            JLabel image_icon = new JLabel();
            image_icon.setIcon(new ImageIcon(image));
   
            if ((i%10)==0){
                hBox =  Box.createHorizontalBox();
            }
            hBox.add(image_icon);
            panel.add(hBox);
        }
        thumbnailFrame.setVisible(true);
    }


    private static byte[] GetImageData(BufferedImage image) {
        WritableRaster WR=image.getRaster();
        DataBuffer DB=WR.getDataBuffer();
        if (DB.getDataType() != DataBuffer.TYPE_BYTE)
            throw new IllegalStateException("That's not of type byte");

        return ((DataBufferByte) DB).getData();
    }


    private BufferedImage MIP(BufferedImage image) {
        //Get image dimensions, and declare loop variables
        int w=image.getWidth(), h=image.getHeight(), i, j, c, k;
        //Obtain pointer to data for fast processing
        byte[] data = GetImageData(image);
        float col;
        short maximum;

        for (j=0; j<h; j++) {
            for (i=0; i<w; i++) {
                maximum = Short.MIN_VALUE;
                //top view
                if ((lastOn.equals("mip_top") && mip_top_on) || mip_start){
                    for (k=0; k<113; k++){
                        if (cthead[k][j][i]>maximum) {
                            maximum=cthead[k][j][i];
                        }
                    }
                //front view
                } else if (lastOn.equals("mip_front") && mip_front_on) {
                    for (k=0; k<256; k++){
                        if (cthead[j][k][i]>maximum) {
                            maximum=cthead[j][k][i];
                        }
                    }
                //side view
                } else if(lastOn.equals("mip_side") &&  mip_side_on) {
                    for (k=0; k<256; k++){
                        if (cthead[j][i][k]>maximum) {
                            maximum=cthead[j][i][k];
                        }
                    }
                // unexpected
                } else {
                    System.out.println("ERROR!!");
                    maximum = 0;
                }

                //calculate colour by performing a mapping from (min,max) -> (0,255)
                col=(255.0f*((float)maximum-(float)min)/((float)(max-min)));
                for (c=0; c<3; c++) {
                    data[c+3*i+3*j*w]=(byte) col;
                } // colour loop
            } // column loop
        } // row loop

        return image;
    }

    private BufferedImage view(BufferedImage image) {
        int w=image.getWidth(), h=image.getHeight(), i, j, c;
        byte[] data = GetImageData(image);
        float col;
        short datum;
        for (j=0; j<h; j++) {
            for (i=0; i<w; i++) {
                //top view
                if (zslice_slider_state || lastOff.equals("mip_top") || lastOff.equals("hisEq_top") || size_slider_state){
                    datum=cthead[zslice_slider.getValue()][j][i];
                //front view
                } else if (yslice_slider_state || lastOff.equals("mip_front") || lastOff.equals("hisEq_front")){
                    datum = cthead[j][yslice_slider.getValue()][i];
                //side view
                } else if (xslice_slider_state || lastOff.equals("mip_side") || lastOff.equals("hisEq_side")) {
                    datum = cthead[j][i][xslice_slider.getValue()];
                //unexpected
                } else {
                    datum = cthead[0][0][0];
                    System.out.println("ERROR!!");
                }
                col=(255.0f*((float)datum-(float)min)/((float)(max-min)));
                for (c=0; c<3; c++) {
                    data[c+3*i+3*j*w]=(byte) col;
                } // colour loop
            } // column loop
        } // row loop
        return image;
    }

    private BufferedImage histogramEq(BufferedImage image) {
        //Get image dimensions, and declare loop variables
        int w=image.getWidth(), h=image.getHeight(), i, j, k, l, c;
        float col;
        byte[] data = GetImageData(image);
        short datum;
        int index;
        int histogram[] = new int[max-min+1];
        int t[] = new int[max-min+1];
        float mapping[] = new float[max-min+1];
        float SIZE = 256*256*113;

        //initialised histogram [index] to 0 for all index
        for (l=0; l<max-min+1; l++){
            histogram[l]=0;
        }

        //create histogram
        for (j=0; j<256; j++) {
            for (i=0; i<256; i++) {
                for (k=0; k<113; k++){
                    index = cthead[k][j][i]-min;
                    histogram[index]++;
                }
            }
        }

        //Create the cumulative distribution function and mapping
        for (int n = 0; n<max-min+1;n++){
            if (n == 0){
                t[0] = histogram[0];
            } else{
                t[n] = t[n-1]+histogram[n];
            }
            mapping[n] = 255.0f*(t[n]/SIZE);
        }

        //Create the image
        for (j=0; j<h; j++) {
            for (i = 0; i < w; i++) {
                //top view
                if ((lastSlider.equals("hisEq_top") || lastOn.equals("hisEq_top")) && hisEq_top_on) {
                    datum = cthead[zslice_slider.getValue()][j][i];
                //front view
                } else if ((lastSlider.equals("hisEq_front") || lastOn.equals("hisEq_front")) && hisEq_front_on) {
                    datum = cthead[j][yslice_slider.getValue()][i];
                //side view
                } else if ((lastSlider.equals("hisEq_side") || lastOn.equals("hisEq_side")) && hisEq_side_on) {
                    datum = cthead[j][i][xslice_slider.getValue()];
                //any unexpected
                } else {
                    datum=cthead[0][0][0];
                    System.out.println("ERROR!!");
                }
                col = mapping[datum-min];
                for (c = 0; c < 3; c++) {
                    data[c+3*i+3*j*w]=(byte) col;
                } // colour loop
            }
        }
        return image;
    }

    private BufferedImage resize(BufferedImage image, int resizedSize) {
        //Get image dimensions
        int w = image.getWidth(), h = image.getHeight();
        BufferedImage newImage = new BufferedImage(resizedSize, (int) (((float)resizedSize/(float)w)*h), BufferedImage.TYPE_3BYTE_BGR);
        int newW = newImage.getWidth(), newH = newImage.getHeight();
        float x, y;
        byte[] oldData = GetImageData(image);
        byte[] newData = GetImageData(newImage);

        for (int j=0; j<newH; j++){
            for (int i = 0; i<newW; i++){
                for (int c = 0; c < 3; c++){
                    y = j * (float)h / (float)newH;
                    x = i * (float)w / (float)newW;
                    newData[c+3*i+3*j*newW] = oldData[c+3*(int)x+3*(int)y*w];
                }
            }
        }
        return newImage;
    }

    private BufferedImage iteratingImages(BufferedImage image, String type, int fixed) {
        //Get image dimensions and declare loop variables
        int w=image.getWidth(), h=image.getHeight(), c, i, j;
        short datum;
        float col;
        byte[] data = GetImageData(image);

        for (j=0; j<h; j++) {
            for (i=0; i<w; i++) {
                if (type.equals("top")){
                    datum=cthead[fixed][j][i];
                } else if (type.equals("front")){
                    datum = cthead[j][fixed][i];

                } else if (type.equals("side")) {
                    datum = cthead[j][i][fixed];
                } else {
                    datum = cthead[0][0][0];
                    System.out.println("ERROR!!");
                }
                col=(255.0f*((float)datum-(float)min)/((float)(max-min)));
                for (c=0; c<3; c++) {
                    data[c+3*i+3*j*w]=(byte) col;
                } // colour loop
            } // column loop
        } // row loop
        return resize(image, 100);
    }

    /**
     * Filling arrays with thumbnails
     */
    private void thumbnail() {
        topImages = new BufferedImage[cthead.length];
        frontImages = new BufferedImage[cthead[0].length];
        sideImages = new BufferedImage[cthead[0][0].length];
        int i, j, k;

        //top view
        for (k=0; k<cthead.length; k++){
            BufferedImage image = new BufferedImage(cthead[0][0].length, cthead[0].length, BufferedImage.TYPE_3BYTE_BGR);
            topImages[k] = iteratingImages(image, "top", k);
        }
        //front view
        for (j=0; j<cthead[0].length; j++){
            BufferedImage image = new BufferedImage(cthead[0][0].length, cthead.length, BufferedImage.TYPE_3BYTE_BGR);
            frontImages[j] = iteratingImages(image, "front", j);
        }
        //side view
        for (i=0; i<cthead[0][0].length; i++){
            BufferedImage image = new BufferedImage(cthead[0].length, cthead.length, BufferedImage.TYPE_3BYTE_BGR);
            sideImages[i] = iteratingImages(image, "side", i);
        }
    }

    public static void main(String[] args) throws IOException {

        CTHead e = new CTHead();
        e.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        e.Example();
    }
}