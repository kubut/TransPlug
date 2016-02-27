import com.google.gson.JsonObject;

import javax.swing.event.TableModelListener;
import java.util.*;

/**
 * Created by kubut on 21.02.2016
 */
public class TransTableModel implements javax.swing.table.TableModel{
    private ArrayList<String> languages;
    private FilesService filesService;
    private HashMap<String, HashMap<String, String>> translations;
    private Tree mergedKeysTree;

    public TransTableModel(FilesService filesService){
        this.filesService = filesService;
        this.translations = new HashMap<>();

        this.languages = new ArrayList<>();
        this.languages.addAll(this.filesService.getLanguagesList());

        FilesParserModel filesParserModel = new FilesParserModel();
        for(String lang : this.languages){
            JsonObject json = this.filesService.getJsonByLanguage(lang);
            if(json != null){
                translations.put(lang, filesParserModel.parseJson(this.filesService.getJsonByLanguage(lang)));
            }
        }

        this.mergedKeysTree = filesParserModel.getKeysTree();
    }

    @Override
    public int getRowCount() {
        return this.mergedKeysTree.getSize();
    }

    @Override
    public int getColumnCount() {
        return this.languages.size() + 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return (columnIndex == 0) ? Text.KEY : this.languages.get(columnIndex-1);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String value;

        if(columnIndex == 0){
            Tree.Node node = this.mergedKeysTree.getKeyByIndex(rowIndex+1);
            value = new String(new char[node.getLevel()-1]).replace("\0", "--");
            value += node.getKey();
        } else {
            String lang = this.languages.get(columnIndex-1);
            HashMap<String,String> column = this.translations.get(lang);
            Tree.Node keyNode = this.mergedKeysTree.getKeyByIndex(rowIndex+1);
            if(!keyNode.isLeaf()){
                value = "";
            } else {
                value = column.get(keyNode.getPath());
            }
        }

        return value == null ? "" : value;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }
}
