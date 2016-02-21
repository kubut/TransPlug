import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kubut on 20.02.2016
 */
public class SettingsDialog{
    private Project project;
    private PropertiesComponent propertiesComponent;

    public SettingsDialog(Project project){
        this.project = project;
        this.propertiesComponent = PropertiesComponent.getInstance(project);
    }

    public void show(IDialogCallback callback){
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JTextField path = new JTextField();

        label.setText(Text.PATH);
        label.setLabelFor(path);

        path.setPreferredSize(new Dimension(200,30));
        path.setText(this.propertiesComponent.getValue("transPath",""));

        panel.setMinimumSize(new Dimension(300,30));
        panel.add(label);
        panel.add(path);

        if(this.prepareDialog(panel).show() == DialogWrapper.OK_EXIT_CODE){
            this.propertiesComponent.setValue("transPath",path.getText());
            callback.okCallback();
        }
    }

    private DialogBuilder prepareDialog(JComponent panel){
        DialogBuilder builder = new DialogBuilder(this.project);
        builder.setTitle(Text.SETTINGS);
        builder.removeAllActions();
        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(panel);

        return builder;
    }
}
