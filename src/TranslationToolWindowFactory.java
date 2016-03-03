import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by kubut on 20.02.2016
 */
public class TranslationToolWindowFactory implements IDialogCallback, ToolWindowFactory {
    private JPanel noTranslationPanel;
    private JPanel translationPanel;
    private JPanel panel;
    private JLabel no_config_text;
    private JButton settings;
    private JTable translationsTable;
    private JButton reload;
    private JButton add;

    private Project project;
    private TransTableModel transTableModel;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Notifications.Bus.register(Text.TOAST_RELOAD_TITLE, NotificationDisplayType.BALLOON);

        this.project = project;

        this.removeActionListeners(this.settings);
        this.settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SettingsDialog(TranslationToolWindowFactory.this.project).show(TranslationToolWindowFactory.this);
            }
        });
        this.settings.setText(Text.SETTINGS);

        this.removeActionListeners(this.reload);
        this.reload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApplicationManager.getApplication().saveAll();
                TranslationToolWindowFactory.this.syncLayout();
            }
        });
        this.reload.setText(Text.RELOAD);

        this.syncLayout();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(this.panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void okCallback(){
        ApplicationManager.getApplication().saveAll();
        this.syncLayout();
    }

    public void syncLayout(){
        FilesService filesService = FilesService.getInstance(this.project);

        if(filesService.loadFiles()){
            this.transTableModel = new TransTableModel(filesService, this);

            this.translationsTable.setModel(this.transTableModel);
            this.translationsTable.addMouseListener(
                    new RightClickMenu(this.translationsTable, this.transTableModel, this.project)
            );
            this.translationPanel.setVisible(true);
            this.noTranslationPanel.setVisible(false);
            this.translationsTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
            this.translationsTable.setRowHeight(25);
            this.removeActionListeners(this.add);
            this.add.setVisible(true);
            this.add.setText(Text.ADD_KEY);
            this.add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(TranslationToolWindowFactory.this.transTableModel.synchronizeStatus()){
                        new AddDialog(
                                TranslationToolWindowFactory.this.project,
                                TranslationToolWindowFactory.this.transTableModel
                        ).show();
                    }
                }
            });
        } else {
            this.add.setVisible(false);
            this.no_config_text.setText(Text.NO_FILES);
            this.noTranslationPanel.setVisible(true);
            this.translationPanel.setVisible(false);
        }
    }

    private void removeActionListeners(JButton button){
        for( ActionListener al : button.getActionListeners() ) {
            button.removeActionListener( al );
        }
    }
}
