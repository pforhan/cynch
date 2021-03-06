package com.muddyhorse.cynch.gui;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;

import com.muddyhorse.cynch.Constants;
import com.muddyhorse.cynch.manifest.Operation;
import com.muddyhorse.cynch.manifest.RemoteFileInfo;

/**
 *
 */
public class ProgressDialog extends java.awt.Dialog implements com.muddyhorse.cynch.ProgressListener, java.awt.event.ActionListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    //
    // Instance Variables:
    //
    private long               rsize;
    private long               currentOp;
    private long               totalOps;

    private boolean           interrupted;          // = false (VM default)

    private TextField         opTxt;
    private TextField         whatTxt;
    private TextField         thisProgTxt;
    private TextField         ttlProgTxt;

    private ProgressBar       thisProg;
    private ProgressBar       ttlProg;

    //
    // Constructors:
    //
    public ProgressDialog(Frame parent, long totalOperations) {
        super(parent, true);
        totalOps = totalOperations;
        buildGUI();
    }

    //
    // View methods:
    //
    private void buildGUI() {
        setResizable(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = 5;
        gbc.insets.bottom = 5;
        gbc.insets.left = 5;
        gbc.insets.right = 5;
        //        gbc.weightx = 0.5;

        Label lbl = new Label("Operation:");
        gbc.gridy = 0;
        gbc.gridx = 0;
        add(lbl, gbc);

        opTxt = new TextField(30);
        opTxt.setEditable(false);
        gbc.gridx = 1;
        add(opTxt, gbc);

        lbl = new Label("File:");
        gbc.gridx = 0;
        gbc.gridy = 10;
        add(lbl, gbc);

        whatTxt = new TextField(35);
        whatTxt.setEditable(false);
        gbc.gridx = 1;
        add(whatTxt, gbc);

        lbl = new Label("Progress:");
        gbc.gridx = 0;
        gbc.gridy = 20;
        add(lbl, gbc);

        thisProg = new ProgressBar();
        thisProg.setForeground(Color.blue);
        gbc.gridx = 1;
        add(thisProg, gbc);

        thisProgTxt = new TextField(15);
        thisProgTxt.setEditable(false);
        gbc.gridx = 2;
        add(thisProgTxt, gbc);

        lbl = new Label("Total Progress:");
        gbc.gridx = 0;
        gbc.gridy = 30;
        add(lbl, gbc);

        ttlProg = new ProgressBar();
        ttlProg.setForeground(Color.green.darker());
        gbc.gridx = 1;
        add(ttlProg, gbc);

        ttlProgTxt = new TextField(20);
        ttlProgTxt.setEditable(false);
        gbc.gridx = 2;
        add(ttlProgTxt, gbc);

        Button c = new Button("Cancel");
        c.setActionCommand(Constants.CMD_CANCEL);
        c.addActionListener(this);
        gbc.gridy = 40;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(c, gbc);

        pack();
    }

    //
    // Data Methods:
    //

    //
    // Utility methods:
    //
    private void setProgress(ProgressBar b, TextField t, long current, long total) {
        b.setProgress(current, total);
        t.setText("" + current + " of " + total + " bytes");
    }

    // copied from javax.swing.JDialog:
    @Override
    public void setLocationRelativeTo(Component c) {
        Container root = null;

        if (c != null) {
            if (c instanceof Window || c instanceof Applet) {
                root = (Container) c;
            } else {
                Container parent;
                for (parent = c.getParent(); parent != null; parent = parent.getParent()) {
                    if (parent instanceof Window || parent instanceof Applet) {
                        root = parent;
                        break;
                    }
                }
            }
        }

        if (c != null && !c.isShowing() || root == null || !root.isShowing()) {
            Dimension paneSize = getSize();
            Dimension screenSize = getToolkit().getScreenSize();

            setLocation((screenSize.width - paneSize.width) / 2, (screenSize.height - paneSize.height) / 2);
        } else {
            if (c != null) {
                Dimension invokerSize = c.getSize();
                Point invokerScreenLocation;
                // If this method is called directly after a call to
                // setLocation() on the "root", getLocationOnScreen()
                // may return stale results (Bug#4181562), so we walk
                // up the tree to calculate the position instead
                // (unless "root" is an applet, where we cannot walk
                // all the way up to a toplevel window)
                //
                if (root instanceof Applet) {
                    invokerScreenLocation = c.getLocationOnScreen();
                } else {
                    invokerScreenLocation = new Point(0, 0);
                    Component tc = c;
                    while (tc != null) {
                        Point tcl = tc.getLocation();
                        invokerScreenLocation.x += tcl.x;
                        invokerScreenLocation.y += tcl.y;
                        if (tc == root) {
                            break;
                        }
                        tc = tc.getParent();
                    }
                }
                Rectangle dialogBounds = getBounds();
                int dx = invokerScreenLocation.x + (invokerSize.width - dialogBounds.width >> 1);
                int dy = invokerScreenLocation.y + (invokerSize.height - dialogBounds.height >> 1);
                Dimension ss = getToolkit().getScreenSize();
                if (dy + dialogBounds.height > ss.height) {
                    dy = ss.height - dialogBounds.height;
                    dx = invokerScreenLocation.x < ss.width >> 1 ? invokerScreenLocation.x + invokerSize.width
                            : invokerScreenLocation.x - dialogBounds.width;
                }
                if (dx + dialogBounds.width > ss.width) {
                    dx = ss.width - dialogBounds.width;
                }
                if (dx < 0) {
                    dx = 0;
                }
                if (dy < 0) {
                    dy = 0;
                }
                setLocation(dx, dy);
            } // endif            
        }
    }

    //
    // Overrides:
    //

    //
    // Implementation of the ActionListener interface:
    //
    public void actionPerformed(ActionEvent e) {
        if (Constants.CMD_CANCEL.equals(e.getActionCommand())) {
            //System.out.println("pd.aP: cancel pressed! @"+System.currentTimeMillis());
            interrupted = true;
        } // endif
    }

    //
    // Implementation of the DUProgressListener interface:
    //
    public void starting(Operation op) {
        RemoteFileInfo remote = op.getRemote();
        String rdesc;
        if (remote != null) {
            rdesc = remote.getDescription();
        } else {
            rdesc = op.getLocal().getDescription();
        } // endif

        //System.out.println("pd.s: starting op "+op);
        String desc = rdesc != null ? rdesc : op.getLocal().getDescription();
        if (remote != null) {
            rsize = remote.getSize();

        } else {
            rsize = op.getLocal().getSize();
        } // endif

        opTxt.setText(op.getOperation().getDescription());
        whatTxt.setText(op.getFileID() + " -- " + desc);
        setProgress(thisProg, thisProgTxt, 0, rsize);
        setProgress(ttlProg, ttlProgTxt, currentOp, totalOps);
    }

    public void progress(String name, String desc, long amount, long total) throws InterruptedException {
        if (!interrupted) {
            //System.out.println("pd.p: n:"+name+"; d:"+desc+"; a="+amount+"; t="+total);
            setProgress(thisProg, thisProgTxt, amount, total);
            setProgress(ttlProg, ttlProgTxt, currentOp + amount, totalOps);
            /*
             try {
             Thread.sleep(150);
             } catch (InterruptedException ex) {
             System.out.println("pd.r: caught InterruptedException!");
             } // endtry
             //*/
        } else {
            throw new InterruptedException("User requested termination...");
        } // endif
    }

    public void finished(Operation op, boolean success) {
        currentOp += rsize;
        setProgress(ttlProg, ttlProgTxt, currentOp, totalOps);
        /*
         if (success) {
         System.out.println("pd.f: successfully finished op "+op);
         } else {
         System.out.println("pd.f: UNsuccessfully finished op "+op);
         } // endif
         //*/
    }

    //
    // Inner classes:
    //
    static class ProgressBar extends java.awt.Canvas
    {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        public long                progress;
        public long                total;

        // data methods:
        public void setProgress(long p, long t) {
            progress = p;
            total = t;
            repaint();
        }

        // Overrides:
        @Override
        public final Dimension getPreferredSize() {
            return new Dimension(200, 20);
        }

        @Override
        public final void update(Graphics g) {
            paint(g);
        }

        @Override
        public final void paint(Graphics g) {
            //            super.paint(g);

            if (g == null) {
                return;
            } // endif

            // get the current size and set scale factors:
            Dimension size = getSize();
            float scaleX = (float) (size.width - 4) / (float) total;

            if (progress == 0) {
                super.paint(g);
            } else {
                g.setColor(getForeground());
                //                g.fillRect(2,2, (int)(progress*scaleX), size.height-4);
                //                g.fill3DRect(2,2, (int)(progress*scaleX), size.height-4,false);
                g.fillRoundRect(2, 2, (int) (progress * scaleX), size.height - 4, 5, 5);
                /* attempted highlight:
                 g.setColor(getForeground().brighter().brighter());
                 g.fillRoundRect(5,5, (int)((progress-3)*scaleX), (int)((size.height-4)*.10),5,5);
                 //*/
            } // endif

            // draw border:
            g.setColor(Color.black);
            //            g.drawRect(0,0, size.width-1, size.height-1);
            g.drawRoundRect(0, 0, size.width - 1, size.height - 1, 5, 5);
        }
    } // endclass ProgressBar

    //
    // Testing methods:
    //
    /*
     public static void main(String[] args) {
     Frame f = new Frame("Progress Test...");
     //        f.setLayout(new FlowLayout());
     WindowAdapter winA = new WindowAdapter() {
     public void windowClosing(java.awt.event.WindowEvent event) {
     System.exit(0);
     }
     };
     f.addWindowListener(winA);

     ProgressBar pb = new ProgressBar();
     pb.setForeground(Color.blue);
     pb.setProgress(Integer.parseInt(args[0]),Integer.parseInt(args[1]));

     f.add(pb, BorderLayout.CENTER);
     f.pack();
     f.setVisible(true);
     }
     //*/
}
