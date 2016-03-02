import com.google.common.io.Files;
import com.google.gson.JsonParser;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.util.PropertiesComponent;
import com.google.gson.JsonObject;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
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
    private long timestamp;

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

    public boolean loadFiles(){
        String path = this.getDirPath();
        this.files.clear();

        try {
            this.loadFiles(path);
            this.timestamp = System.currentTimeMillis();
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
            String fileContent = Files.toString(file, Charset.forName("UTF-8"));
            JsonObject jsonObject = parser.parse(fileContent).getAsJsonObject();
            return jsonObject;
        } catch (Exception e){
            return null;
        }

    }

    public void saveFile(String lang, String content){
        String path = this.project.getBasePath() + "/" + this.propertiesComponent.getValue("transPath", "");
        path = path+"/locale-"+lang+".json";

        File file = new File(path);

        if(!file.exists()){
            try{
                file.createNewFile();
            } catch (Exception e){
                return;
            }
        }

        try{
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.write(content);
            writer.close();
        } catch (Exception e){
            return;
        }

        VirtualFileManager.getInstance().syncRefresh();
        final VirtualFile vFile = LocalFileSystem.getInstance().findFileByIoFile(new File(path));
        final PsiFile psiFile = PsiManager.getInstance(this.project).findFile(vFile);
        ReformatCodeProcessor processor = new ReformatCodeProcessor(psiFile, false);
        processor.preprocessFile(psiFile, false);
        processor.setPostRunnable(new Runnable() {
            @Override
            public void run() {
                FileDocumentManager.getInstance().saveDocumentAsIs(FileDocumentManager.getInstance().getDocument(vFile));
                FilesService.this.timestamp = System.currentTimeMillis();
            }
        });
        processor.run();
    }

    public boolean isActual(){
        String path = this.getDirPath();
        File dir = new File(path);
        if(dir.isDirectory()){
            for(File file : dir.listFiles()){
                if(!file.isDirectory() && this.getFileExtension(file).equals("json")){
                    if (file.lastModified() > this.timestamp){
                        return false;
                    }
                }
            }
            return true;
        }

        return false;
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

    private String getDirPath(){
        return this.project.getBasePath() + "/" + this.propertiesComponent.getValue("transPath", "");
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
            int localeIndex = filename.lastIndexOf("locale-");

            if(localeIndex < 0) throw new Exception();

            String language = filename.substring(localeIndex + 7);
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
