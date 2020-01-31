package main.java.upa.view.main;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import main.java.upa.model.photo.Photo;
import main.java.upa.model.photo.PhotoModel;
import main.java.upa.view.map.ObjectDetailView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

public class ImageViewer {

    private JFrame frame;
    private PhotoModel pModel = new PhotoModel();
    private Photo img = null;
    private int  imgID;
    private int propertyID;
    JLabel lbl = new JLabel();


    public ImageViewer(int imgID, int propertyID) throws IOException, SQLException {
        this.frame = new JFrame("Image viewer");
        this.img= pModel.getImage(imgID);
        this.imgID = imgID;
        this.propertyID = propertyID;
    }

    public void show() {

        JButton delete = new JButton("Delete image");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pModel.deleteImage(img.getId());
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        JButton rotateRight = new JButton("Rotate Right");
        rotateRight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    img.resetImage(pModel.transformImage(img.getId(), "rotate 90"));

                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                finally {
                    lbl.setIcon(null);
                    frame.getContentPane().removeAll();
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    show();
                }

            }
        });

        JButton rotateLeft = new JButton("Rotate Left");
        rotateLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    img.resetImage(pModel.transformImage(img.getId(), "rotate 270"));

                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                finally {
                    lbl.setIcon(null);
                    frame.getContentPane().removeAll();
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    show();
                }

            }
        });

        JButton findSimilar = new JButton("Find similar");
        findSimilar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int similarID = pModel.similarSearch(imgID, propertyID);
                    int similarProperty = pModel.getPropertyIdFromImgId(similarID);
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run() {
                            ObjectDetailView view = new ObjectDetailView();
                            try {
                                view.display(similarProperty,true);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        BufferedImage bImg = SwingFXUtils.fromFXImage(this.img.getImage(), null);

        ImageIcon icon =  new ImageIcon(bImg);

        lbl.setIcon(icon);

        frame.getContentPane().add(lbl, BorderLayout.CENTER);
        frame.getContentPane().add(delete, BorderLayout.BEFORE_FIRST_LINE);
        frame.getContentPane().add(rotateRight, BorderLayout.AFTER_LINE_ENDS);
        frame.getContentPane().add(rotateLeft, BorderLayout.BEFORE_LINE_BEGINS);
        frame.getContentPane().add(findSimilar, BorderLayout.AFTER_LAST_LINE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
