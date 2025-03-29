package src.CPU;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class RAMViewer extends Thread {
    JLabel[] tableHeader = new JLabel[17];

    Color headerColor = Color.decode("#20293d");
    Color titleBackground = Color.decode("#151c2e");
    
    public JFrame window;
    
    
    public void run() {
        window = new JFrame("RAM Viewer");
        window.setBounds(Main.screen.width / 2 - 1113 / 2, Main.screen.height / 2 - 550/2, 1113, 550);
        window.getContentPane().setBackground(headerColor);
        window.setLayout(null);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setResizable(false);
        window.setAlwaysOnTop(false);
        window.setIconImage(Main.imageLoader("./assets/CPU_ICON.png"));

        JLabel title = new JLabel("RAM Viewer");
        title.setBounds(0, 0, window.getWidth(), 60);
        title.setBackground(titleBackground);
        title.setFont(new Font("Arial", Font.PLAIN, 40));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setForeground(Main.labelForegroundColor);
        title.setOpaque(true);
        window.add(title);

        int headerLabelWidth = 61;
        for (int i = 0; i < tableHeader.length; i++) {
            if (i == 0) {
                tableHeader[i] = new JLabel("");
                tableHeader[i].setBounds(0, 60, 2 * headerLabelWidth, 50);
            }
            else {
                tableHeader[i] = new JLabel(Integer.toHexString(i-1).toUpperCase());
                tableHeader[i].setBounds(headerLabelWidth * (i + 1), 60, headerLabelWidth, 50);
            }
            tableHeader[i].setBorder(BorderFactory.createLineBorder(Main.borderColor, 1));
            tableHeader[i].setBackground(headerColor);
            tableHeader[i].setFont(new Font("Arial", Font.BOLD, 20));
            tableHeader[i].setHorizontalAlignment(SwingConstants.CENTER);
            tableHeader[i].setVerticalAlignment(SwingConstants.CENTER);
            tableHeader[i].setForeground(Main.labelForegroundColor);
            tableHeader[i].setOpaque(true);
            window.add(tableHeader[i]);
        }

        DefaultTableModel tableModel = new DefaultTableModel(RAM.getRAMSize()/16, 17);
        JTable table = new JTable(tableModel);

        table.setDefaultEditor(Object.class, null);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(50);
        table.setShowGrid(true);
        table.setGridColor(Main.borderColor);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
        table.setTableHeader(null);

        table.getColumnModel().getColumn(0).setPreferredWidth(2 * headerLabelWidth);

        for (int i = 1; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(headerLabelWidth);
        }

        table.setDefaultRenderer(Object.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value == null ? "" : value.toString());
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                if (column == 0)
                    label.setFont(new Font("Arial", Font.PLAIN, 20));
                else
                    label.setFont(new Font("Arial", Font.PLAIN, 12));
                if (column != 0)
                    label.setBorder(BorderFactory.createLineBorder(Main.borderColor, 1));

                if (isSelected) {
                    label.setBackground(headerColor.brighter());
                    label.setForeground(Main.labelForegroundColor);
                } else {
                    label.setBackground(headerColor);
                    label.setForeground(Main.labelForegroundColor);
                }

                label.setOpaque(true);
                return label;
            }
        });
      
        for (int i = 0; i < RAM.getRAMSize()/16; i++) {
            tableModel.setValueAt(String.format("0x%04X", i * 16), i, 0);
        }

        JScrollPane scrollPane = new JScrollPane(table);


        scrollPane.setBounds(-1,109,1100, 525 - 120 );
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getVerticalScrollBar().setPreferredSize(new java.awt.Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new java.awt.Dimension(0, 0));

        window.add(scrollPane);

        window.setVisible(true);
        
        
        new RAMViewerUpdater(tableModel).start();
    }
}
