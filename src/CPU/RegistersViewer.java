package src.CPU;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

public class RegistersViewer extends Thread {
    JFrame window;

    public void run() {
        SwingUtilities.invokeLater(() -> {
            window = new JFrame("Registers");
            window.setBounds(Main.screen.width / 2 - 150, Main.screen.height / 2 - 150, 400, 300);
            window.setLayout(new BorderLayout());
            window.setResizable(false);
            window.getContentPane().setBackground(EditorASM.editorBackgroundColor);
            window.setAlwaysOnTop(false);
            window.setIconImage(Main.imageLoader("./assets/CPU_ICON.png"));

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(EditorASM.editorBackgroundColor);

            String[] columns = { "OPCODE", "REGISTER" };

            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            JTable table = new JTable(model);
            table.setFont(new Font("Monospaced", Font.PLAIN, 20));
            table.setForeground(EditorASM.standardColor);
            table.setBackground(EditorASM.editorBackgroundColor);
            table.setGridColor(Color.DARK_GRAY);
            table.setPreferredScrollableViewportSize(new Dimension(350, 200));
            table.setFillsViewportHeight(true);
            table.getTableHeader().setBackground(EditorASM.editorBackgroundColor);
            table.getTableHeader().setForeground(EditorASM.standardColor);

            table.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = new JLabel(value.toString(), SwingConstants.CENTER);
                    label.setFont(new Font("Monospaced", Font.PLAIN, 17));
                    label.setBackground(isSelected ? table.getSelectionBackground() : EditorASM.editorBackgroundColor);
                    label.setForeground(isSelected ? table.getSelectionForeground() : EditorASM.numberColor);
                    label.setOpaque(true);
                    return label;
                }
            });

            table.getColumnModel().getColumn(1).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = new JLabel(value.toString(), SwingConstants.CENTER);
                    label.setFont(new Font("Monospaced", Font.PLAIN, 17));
                    label.setBackground(isSelected ? table.getSelectionBackground() : EditorASM.editorBackgroundColor);
                    label.setForeground(isSelected ? table.getSelectionForeground() : EditorASM.instructionColor);
                    label.setOpaque(true);
                    return label;
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            
            scrollPane.getVerticalScrollBar().setBackground(new Color(0, 0, 0, 0)); 
            scrollPane.getHorizontalScrollBar().setBackground(new Color(0, 0, 0, 0));
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setPreferredSize(new java.awt.Dimension(0, 0));
            scrollPane.getHorizontalScrollBar().setPreferredSize(new java.awt.Dimension(0, 0));
            panel.add(scrollPane, BorderLayout.CENTER);

            window.add(panel, BorderLayout.CENTER);

            JMenuBar upperMenuBar = new JMenuBar();
            upperMenuBar.setFont(new Font("Monospaced", Font.PLAIN, 17));
            upperMenuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, EditorASM.instructionColor));
            upperMenuBar.setBackground(EditorASM.editorBackgroundColor.brighter());
            upperMenuBar.setForeground(EditorASM.standardColor);
            upperMenuBar.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 0));

            JButton closeButton = new JButton("CLOSE");
            closeButton.setRolloverEnabled(true);
            closeButton.setFocusPainted(false);
            closeButton.setBorder(null);
            closeButton.setBackground(EditorASM.editorBackgroundColor);
            closeButton.setFont(new Font("Monospaced", Font.PLAIN, 17));
            closeButton.setHorizontalAlignment(SwingConstants.CENTER);
            closeButton.setVerticalAlignment(SwingConstants.CENTER);
            closeButton.setForeground(EditorASM.standardColor);
            closeButton.setOpaque(true);
            closeButton.addActionListener(e -> window.dispose());

            upperMenuBar.add(closeButton);
            window.setJMenuBar(upperMenuBar);

            window.setVisible(true);

            loadInstructionSetIntoTable(model);
        });
    }

    public void loadInstructionSetIntoTable(DefaultTableModel model) {
        LinkedList<String[]> tablelist = new LinkedList<>();
        for (Map.Entry<String, String> entry : CPU.registers.entrySet()) {
            tablelist.add(new String[]{entry.getKey(), entry.getValue()});
        }
        tablelist.sort(Comparator.comparing(o -> o[0]));
        for(String[] tableElement : tablelist){
            model.addRow(tableElement);
        }
    }
}
