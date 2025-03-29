package src.CPU;

import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

public class RAMViewerUpdater extends Thread {
    TableModel memoryTableModel;

    public RAMViewerUpdater(TableModel memoryTableModel) {
        this.memoryTableModel = memoryTableModel;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < memoryTableModel.getRowCount(); i++) {
                        for (int j = 1; j < memoryTableModel.getColumnCount(); j++) {  
                            int index = i * (memoryTableModel.getColumnCount() - 1) + (j - 1);
                            if (index < RAM.memory.length) {
                                memoryTableModel.setValueAt(RAM.memory[index], i, j);
                            }
                        }
                    }

                    ((javax.swing.table.DefaultTableModel) memoryTableModel).fireTableDataChanged();
                });
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
