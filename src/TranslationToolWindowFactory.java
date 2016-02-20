import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by kubut on 20.02.2016
 */
public class TranslationToolWindowFactory implements ToolWindowFactory {
    private JPanel panel1;
    private JLabel no_config_text;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.syncLayout();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(this.panel1, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public void syncLayout(){
        this.no_config_text.setText(Text.NO_FILES);
    }
}
