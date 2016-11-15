import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

/**
 * Created by kubut on 03.03.2016
 */
public class RightClickMenu implements MouseListener {
    private JTable table;
    private TransTableModel model;
    private Project project;

    public RightClickMenu(JTable table, TransTableModel model, Project project) {
        this.table = table;
        this.model = model;
        this.project = project;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.showMenuIfAllowed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.showMenuIfAllowed(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void showMenuIfAllowed(MouseEvent e) {
        final int row = this.table.rowAtPoint(e.getPoint());
        final int col = this.table.columnAtPoint(e.getPoint());

        if ((col == 0) && e.isPopupTrigger() && (e.getComponent() instanceof JTable)) {
            this.prepareMenu(row).show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private JPopupMenu prepareMenu(final int rowIndex) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem copyItem = new JMenuItem(Text.COPY);
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = RightClickMenu.this.model.getPathByRow(rowIndex);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(path), null);
            }
        });

        JMenuItem addItem = new JMenuItem(Text.ADD_KEY);
        addItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = RightClickMenu.this.model.getNodePathByRow(rowIndex);
                new AddDialog(
                        RightClickMenu.this.project,
                        RightClickMenu.this.model
                ).show(path + ".newKey");
            }
        });

        menu.add(copyItem);
        menu.add(addItem);

        for (String langCode : this.model.getLanguages()) {
            JMenuItem openFile = new JMenuItem(Text.OPEN_FILE + "locale-" + langCode + ".json");
            openFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String path = project.getBasePath() + "/" + PropertiesComponent.getInstance(project).getValue("transPath", "");
                    File file = new File(path + "/locale-" + langCode + ".json");
                    try {
                        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
                        FileEditorManager.getInstance(project).openFile(virtualFile, true, false);
                    } catch (Exception exception) {
                        Notifications.Bus.notify(
                                new Notification(
                                        Text.TOAST_NO_SUCH_FILE_TITLE,
                                        Text.TOAST_NO_SUCH_FILE_TITLE,
                                        Text.TOAST_NO_SUCH_FILE_CONTENT,
                                        NotificationType.WARNING
                                )
                        );
                    }

                }
            });

            menu.add(openFile);
        }

        return menu;
    }
}
