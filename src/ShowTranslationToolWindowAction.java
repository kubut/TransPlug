import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

/**
 * Created by kubut on 20.02.2016
 */
public class ShowTranslationToolWindowAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        if(e.getProject() == null){
            return;
        }

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(e.getProject());
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Translations");

        if(toolWindow.isVisible()){
            toolWindow.hide(null);
        } else {
            toolWindow.show(null);
        }
    }
}
