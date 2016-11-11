import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by kubut on 01.03.2016
 */
public class AddDialog {
    private Project project;
    private TransTableModel tableModel;
    private HashMap<String, JTextField> values;

    public AddDialog(Project project, TransTableModel transTableModel){
        this.project = project;
        this.tableModel = transTableModel;
        this.values = new HashMap<>();
    }

    public void show(){
        this.show("");
    }

    public void show(String key){
        JPanel panel = new JPanel(new GridLayout(0,1,2,2));
        JLabel label = new JLabel();
        JTextField path = new JTextField();
        path.setText(key);

        label.setText(Text.KEY);
        label.setLabelFor(path);

        panel.add(label);
        panel.add(path);

        Iterator<String> iterator = this.tableModel.getLanguages().iterator();
        while (iterator.hasNext()){
            String lang = iterator.next();
            JTextField valueField = new JTextField();
            JLabel valueLabel = new JLabel();

            valueLabel.setText(lang);
            valueLabel.setLabelFor(valueField);

            panel.add(valueLabel);
            panel.add(valueField);

            valueField.setPreferredSize(new Dimension(200,30));

            this.values.put(lang, valueField);
        }

        path.setPreferredSize(new Dimension(200,30));
        panel.setMinimumSize(new Dimension(300,30));

        if(this.prepareDialog(panel, path).show() == DialogWrapper.OK_EXIT_CODE){
            this.okAction(path.getText());
        }
    }

    private DialogBuilder prepareDialog(JComponent panel, JComponent focused){
        DialogBuilder builder = new DialogBuilder(this.project);
        builder.setTitle(Text.ADD_KEY);
        builder.removeAllActions();
        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(panel);
        builder.resizable(false);
        builder.setPreferredFocusComponent(focused);

        return builder;
    }

    private void okAction(String key){
        if(key.isEmpty()) return;

        Iterator<String> iterator = this.tableModel.getLanguages().iterator();
        while (iterator.hasNext()){
            String lang = iterator.next();
            this.tableModel.addTranslation(lang, key, this.values.get(lang).getText());
        }
        this.tableModel.reloadData();
    }
}
