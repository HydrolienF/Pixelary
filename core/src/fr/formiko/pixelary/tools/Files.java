package fr.formiko.pixelary.tools;

import java.util.HashSet;
import java.util.Set;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Files {
    /**
     * @param path the path of the directory to search
     * @return all subfiles of a directory. (&#38; sub sub files, &38; sub sub sub files, etc.)
     */
    public static Set<String> listSubFilesPathsRecusvively(String path) {
        Set<String> set = new HashSet<String>();
        String[] AssetsNames = Gdx.files.internal("assets.txt").readString().split("\n");
        for (String name : AssetsNames) {
            if (name.startsWith(path)) {
                set.add(name);
            }
        }
        return set;
    }
    /**
     * @param path the path of the directory to search
     * @return all subfiles of a directory. (&#38; sub sub files, &38; sub sub sub files, etc.)
     */
    public static Set<FileHandle> listSubFilesRecusvively(String path) {
        Set<FileHandle> set = new HashSet<FileHandle>();
        for (String name : listSubFilesPathsRecusvively(path)) {
            set.add(Gdx.files.internal(name));
        }
        return set;
    }
    /**
     * @param path the path of the directory to search
     * @return direct directory of a directory
     */
    public static Set<String> listSubDirectory(String path) {
        Set<String> set = new HashSet<String>();
        for (String name : listSubFilesPathsRecusvively(path)) {
            if (name.contains("/")) {
                String dirName = name.substring(0, name.lastIndexOf("/"));
                if (dirName.length() > path.length()) { // avoid path or path+"/"
                    set.add(dirName.substring(path.length()));
                }
            }
        }
        return set;
    }
}
