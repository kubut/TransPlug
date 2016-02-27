import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by kubut on 27.02.2016
 */
public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel c = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        TransTableModel tableModel = (TransTableModel)table.getModel();
        TranslationState state = tableModel.getCellState(row, column);
        setBackground(state.getColor());

        if(state.getText() != null){
            c.setToolTipText(state.getText());
        } else {
            c.setToolTipText(c.getText());
        }

        return this;
    }
}
