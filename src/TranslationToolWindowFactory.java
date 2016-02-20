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
    private JLabel test;

    private Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        this.syncLayout();

        this.settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SettingsDialog(TranslationToolWindowFactory.this.project).show(TranslationToolWindowFactory.this);
            }
        });
        this.settings.setText(Text.SETTINGS);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(this.panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void okCallback(){
        this.syncLayout();
    }

    public void syncLayout(){
        FilesService filesService = FilesService.getInstance(this.project);

        if(filesService.isCorrectPath()){
            this.test.setText("Jest tłumaczenie! :D");
            this.translationPanel.setVisible(true);
            this.noTranslationPanel.setVisible(false);
        } else {
            this.no_config_text.setText(Text.NO_FILES);
            this.noTranslationPanel.setVisible(true);
            this.translationPanel.setVisible(false);
        }
    }
}
