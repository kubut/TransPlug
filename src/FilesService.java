import com.google.common.io.Files;
import com.google.gson.JsonParser;
import com.intellij.ide.util.PropertiesComponent;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import com.sun.istack.internal.Nullable;
import groovy.json.internal.Charsets;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by kubut on 20.02.2016
 */
public class FilesService {
    private Project project;
    private PropertiesComponent propertiesComponent;
    private HashMap<String, File> files;
    private static FilesService instance;

    public static FilesService getInstance(Project project){
        if(instance == null){
            instance = new FilesService(project);
        }

        return instance;
    }

    private FilesService(Project project){
        this.project = project;
        this.propertiesComponent = PropertiesComponent.getInstance(project);
        this.files = new HashMap<>();
    }

    public boolean isCorrectPath(){
        String path = this.project.getBasePath() + "/" + this.propertiesComponent.getValue("transPath", "");
        this.files.clear();

        try {
            this.loadFiles(path);
        } catch (Exception e){
            return false;
        }

        return !this.files.isEmpty();
    }

    public Set<String> getLanguagesList(){
        return this.files.keySet();
    }

    @Nullable
    public JsonObject getJsonByLanguage(String language){
        try{
            JsonParser parser = new JsonParser();
            File file = this.files.get(language);
            String fileContent = Files.toString(file, Charsets.UTF_8);
            JsonObject jsonObject = parser.parse(fileContent).getAsJsonObject();
            return jsonObject;
        } catch (Exception e){
            return null;
        }

    }

    private void loadFiles(String path){
        File dir = new File(path);
        if(dir.isDirectory()){
            for(File file : dir.listFiles()){
                if(!file.isDirectory() && this.isValidFile(file)){
                    this.files.put(this.getFileLanguage(file.getName()), file);
                }
            }
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    private String getFileLanguage(String filename){
        try {
            String language = filename.substring(filename.lastIndexOf("locale-") + 7);
            language = language.substring(0, language.lastIndexOf("."));

            if(this.files.get(language) != null){
                throw new Exception();
            }

            return language;
        } catch (Exception e) {
            return "";
        }
    }

    private boolean isValidFile(File file){
        return this.getFileExtension(file).equals("json") && !this.getFileLanguage(file.getName()).isEmpty();
    }


}
