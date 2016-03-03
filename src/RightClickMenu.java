import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by kubut on 03.03.2016
 */
public class RightClickMenu implements MouseListener {
    private JTable table;
    private TransTableModel model;

    public RightClickMenu(JTable table, TransTableModel model){
        this.table = table;
        this.model = model;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        final int row = this.table.rowAtPoint(e.getPoint());
        final int col = this.table.columnAtPoint(e.getPoint());
        if((col == 0) && e.isPopupTrigger() && (e.getComponent() instanceof JTable)){
            JPopupMenu menu = new JPopupMenu();
            JMenuItem copyItem = new JMenuItem(Text.COPY);
            copyItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String path = RightClickMenu.this.model.getPathByRow(row);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(path), null);
                }
            });
            menu.add(copyItem);
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
