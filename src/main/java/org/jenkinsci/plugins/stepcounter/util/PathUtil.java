package org.jenkinsci.plugins.stepcounter.util;

import java.io.File;

public class PathUtil {
    
    private PathUtil() {
    }

    public static String getParentDirRelativePath(File file, String rootPath) {
        String filePath = file.getParentFile().getAbsolutePath();
        filePath = filePath.replaceAll("\\\\", "/");
        rootPath = rootPath.replaceAll("\\\\", "/");
        String result = filePath.replaceFirst(rootPath, "");
        if(result.startsWith("/")){
            result = result.replaceFirst("/", "");
        }
        return result;
    }
    
}
