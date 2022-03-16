//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.daisydata.codescans.codeuploadsfx;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class GuiTools {
    private static final long serialVersionUID = -7485974900469002809L;

    public GuiTools() {
    }

    public String receiverPicker(ResultSet rs) {
        JFrame picker = this.setupFrame();
        String selectedReceiver = "";

        try {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            String[] headers = new String[colCount];

            for(int h = 1; h <= colCount; ++h) {
                headers[h - 1] = meta.getColumnName(h);
            }

            DefaultTableModel tableData = this.setupTable(headers);

            while(rs.next()) {
                String[] record = new String[colCount];

                for(int i = 0; i < colCount; ++i) {
                    record[i] = rs.getString(i + 1);
                }

                tableData.addRow(record);
            }
        } catch (SQLException var11) {
            var11.printStackTrace();
        }

        picker.setDefaultCloseOperation(3);
        picker.setPreferredSize(new Dimension(400, 200));
        picker.pack();
        picker.setVisible(true);

        while(picker.isShowing()) {
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException var10) {
                var10.printStackTrace();
            }
        }

        return selectedReceiver;
    }

    public JFrame setupFrame() {
        JFrame frame = new JFrame();
        frame.setTitle("Select an Option");
        frame.setAlwaysOnTop(true);
        JTable table = new JTable();
        frame.add(table);
        return frame;
    }

    public DefaultTableModel setupTable(String[] headers) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(headers);
        return model;
    }
}
