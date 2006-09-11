package com.muddyhorse.cynch.gui;

import java.awt.*;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import com.muddyhorse.cynch.Config;
import com.muddyhorse.cynch.Constants;
import com.muddyhorse.cynch.UpdateUtils;
import com.muddyhorse.cynch.manifest.DownloadType;
import com.muddyhorse.cynch.manifest.Operation;

public class UpdateTablePanel extends java.awt.Panel implements 
        com.muddyhorse.cynch.gui.SelectedOps.Listener, com.muddyhorse.cynch.Constants
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    //
    // Class variables:
    //
    static NumberFormat       snf              = new DecimalFormat("######K");
    //
    // Instance Variables:
    //
    private SelectedOps       selOps;
    private TextField         txtTtl;
    private Config            config;
    private ScrollPane        spn;
    private Panel             opnl;

    //
    // Constructors:
    //
    public UpdateTablePanel(Config cfg) {
        config = cfg;
        selOps = new SelectedOps();
        selOps.addListener(this);

        buildGUI(cfg);
        // DynamicUpdateUtils.writeHashtable(coreFile, remoteCore);
    }

    //
    // Data methods:
    //
    public SelectedOps getSelectedOperations() {
        return selOps;
    }

    //
    // View methods:
    //
    private void buildGUI(Config cfg) {
        setLayout(new GridBagLayout());
        setBackground(Constants.CYNCH_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;

        Label lbl = new Label(cfg.getAppDescription());
        add(lbl, gbc);

        lbl = new Label("Updatable Components:");
        lbl.setFont(new Font("Dialog", Font.BOLD, 16));
        gbc.gridy = 5;
        add(lbl, gbc);

        opnl = getOpsGUI(cfg, selOps);

        spn = new ScrollPane();
        spn.getHAdjustable().setUnitIncrement(10);
        spn.getVAdjustable().setUnitIncrement(21);
        spn.add(opnl);
        gbc.gridy = 10;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.insets.left = 32;
        gbc.insets.right = 10;
        add(spn, gbc);

        // add text description:
        gbc.insets.top = 0;
        gbc.insets.bottom = 0;
        gbc.insets.left = 0;
        gbc.insets.right = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;

        String allDesc = cfg.getUpdateDescription().replace('+', ',');
        int offs = -1;
        int last = 0;

        for (gbc.gridy = 40; gbc.gridy < 50; ++gbc.gridy) {
            offs = allDesc.indexOf("\\", last);
            if (offs == -1) {
                break;
            }
            Label desc = new Label(allDesc.substring(last, offs));
            add(desc, gbc);
            last = offs + 1;
        } // endfor

        // build button panel:
        gbc.gridy = 50;
        Panel pnl = new Panel(new GridBagLayout());
        gbc.insets.top = 10;
        gbc.insets.bottom = 6;
        add(pnl, gbc);

        gbc.insets.top = 5;
        gbc.insets.bottom = 5;
        gbc.insets.left = 5;
        gbc.insets.right = 5;
        gbc.gridy = 0;
        Label lblTtl = new Label("Total amount to download:", Label.RIGHT);
        gbc.gridx = 1;
        pnl.add(lblTtl, gbc);
        txtTtl = new TextField("0K", 8);
        txtTtl.setEditable(false);
        gbc.gridx = 2;
        pnl.add(txtTtl, gbc);
        refreshTotal();
    }

    private static Panel getOpsGUI(Config cfg, ItemListener l) {
        Panel pnlGUI = new Panel();
        String[] names = new String[] {
                "Operation", "Type", "File", "Description", "Size"
        };
        Font f = new Font("Dialog", Font.PLAIN, 10);

        pnlGUI.setBackground(Color.white);
        pnlGUI.setLayout(new GridBagLayout());
        pnlGUI.setFont(f);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);

        // place column headers:
        f = new Font("Dialog", Font.BOLD, 11);
        buildOpsRow(names, f, gbc, pnlGUI, null, DownloadType.optional);
        gbc.gridy++;

        // get table(s):
        buildOpsTable(cfg.getOperations(), gbc, pnlGUI, l);

        gbc.gridy++;
        gbc.weighty = 1.0;
        pnlGUI.add(new Canvas(), gbc);

        return pnlGUI;
    }

    private static void buildOpsTable(Map<String, Operation> opsTable, GridBagConstraints gbc, Panel pnl, ItemListener l) {
        /*
         Vector[] opSets  = DynamicUpdateUtils.sortOperationsByOp(opsTable, null);
         buildOpsTable(opSets[OP_UPDATE  ],gbc,pnl,l);
         buildOpsTable(opSets[OP_DOWNLOAD],gbc,pnl,l);
         buildOpsTable(opSets[OP_DELETE  ],gbc,pnl,l);
         buildOpsTable(opSets[OP_NOTHING ],gbc,pnl,l);
         //*/
        Map<DownloadType, List<Operation>> opSets = UpdateUtils.sortOperationsByType(opsTable, null);
        buildOpsTable(opSets.get(DownloadType.critical), gbc, pnl, l);
        buildOpsTable(opSets.get(DownloadType.required), gbc, pnl, l);
        buildOpsTable(opSets.get(DownloadType.optional), gbc, pnl, l);
    }

    private static void buildOpsTable(List<Operation> ops, GridBagConstraints gbc, Panel pnl, ItemListener l) {
        NumberFormat vnf = new DecimalFormat("#0.00");
        String[] names = new String[5];
        for (Operation op : ops) {
            boolean useCheck = true;

            String locVer = vnf.format(op.getLocal().getVersion());
            String rmtVer = vnf.format(op.getRemote().getVersion());

            switch (op.getOperation()) {
                case nothing:
                    if (op.getRemote().getDownloadType() != DownloadType.optional) {
                        // only display optional nothings
                        continue;
                    } // endif
                    useCheck = false;
                    names[0] = op.getOperation().getDescription() + " (v" + locVer + ")";
                break;
                case delete:
                    names[0] = op.getOperation().getDescription() + " v" + locVer;
                break;
                case update:
                    names[0] = op.getOperation().getDescription() + " from v" + locVer + " to v" + rmtVer;
                break;
                case download:
                    names[0] = op.getOperation().getDescription() + " v" + rmtVer;
                break;
                default:
                    // do what here?
            } // endswitch

            names[1] = op.getRemote().getDownloadType().getDescription();
            names[2] = op.getFileID();
            names[3] = op.getRemote().getDescription() == null ? op.getLocal().getDescription() : op.getRemote().getDescription();

            if (op.getRemote().getSize() == 0) { // TODO doublecheck that this change is valid
                names[4] = snf.format(op.getLocal().getSize() / 1024);
            } else {
                names[4] = snf.format(op.getRemote().getSize() / 1024);
            } // endif

            buildOpsRow(names, null, gbc, pnl, useCheck ? l : null, op.getRemote().getDownloadType());

            gbc.gridy++;
        } // endforeach

    }

    private static void buildOpsRow(String[] names, Font f, GridBagConstraints gbc, Panel pnl, ItemListener l, DownloadType type) {
        //! eventually: first col, always checkbox
        //  if nothing, skip chk
        //  if modifyable, span = 2 and add text
        //  if not modify, chk in col 1 (disabled) and text (lbl) in col 2
        // why? removes dependency on chk width, allows normal colors for text.
        Checkbox check = null;
        Label lblOp = null;

        if (l != null) { // indicates whether to use checkbox...
            gbc.gridx = 0;
            check = new Checkbox();
            check.setState(type != DownloadType.optional);
            check.setFont(f);
            if (type != DownloadType.optional) {
                lblOp = new Label(names[0]);
                lblOp.setFont(f);
                check.setEnabled(false);
                pnl.add(check, gbc);
                gbc.gridx++;
                pnl.add(lblOp, gbc);
            } else {
                // not core, need to add listener:
                check.setLabel(names[0]);
                check.addItemListener(l);
                check.setName(names[2]);
                gbc.gridwidth = 2;
                pnl.add(check, gbc);
                gbc.gridwidth = 1;
                gbc.gridx++;
            } // endif
        } else {
            // a nothing column
            lblOp = new Label(names[0]);
            lblOp.setFont(f);
            //            gbc.gridwidth = 2;
            gbc.gridx = 1;
            pnl.add(lblOp, gbc);
            //            gbc.gridwidth = 1;
            //            gbc.gridx++;
        } // endif

        Label lblType = new Label(names[1]);
        lblType.setFont(f);

        if (type != DownloadType.optional) {
            //            compOp.setBackground(Color.black);
            lblType.setBackground(Color.lightGray);
        } // endif

        gbc.gridx++;
        pnl.add(lblType, gbc);

        /*
         Label lblItem = new Label(names[2]);
         lblItem.setFont(f);
         gbc.gridx++;
         pnl.add(lblItem,gbc);
         //*/

        Label lblDesc = new Label(names[3]);
        lblDesc.setFont(f);
        gbc.gridx++;
        pnl.add(lblDesc, gbc);

        Label lblSize = new Label(names[4]);
        lblSize.setFont(f);
        gbc.gridx++;
        pnl.add(lblSize, gbc);

        gbc.gridx++;
        gbc.weightx = 1.0;
        pnl.add(new Canvas(), gbc);
        gbc.weightx = 0.0;
    }

    /* no easy way to use this right now...
     public void rebuildOpsTable(DUConfig cfg) {
     spn.remove(opnl);
     opnl = getOpsGUI(cfg, selOps);
     spn.add(opnl);
     }
     //*/

    //
    // Utility methods:
    //
    public void refreshTotal() {
        long ttl = UpdateUtils.countDownloadSize(config, DownloadType.critical, null)
                + UpdateUtils.countDownloadSize(config, DownloadType.required, null)
                + UpdateUtils.countDownloadSize(config, DownloadType.all, selOps.getSelectedIDs());
        txtTtl.setText(snf.format(ttl / 1024));
    }

    //
    // Implementation of the SelectedOps.Listener interface:
    //
    public void selectedIDsChanged(boolean anySelected) {
        refreshTotal();
    }
}
