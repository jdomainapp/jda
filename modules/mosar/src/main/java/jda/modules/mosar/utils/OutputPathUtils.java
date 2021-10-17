package jda.modules.mosar.utils;

import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class OutputPathUtils {
    private static void recursiveCreatePath(Path path, boolean isFile) throws IOException {

        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            recursiveCreatePath(parent, false);
        }
        if (isFile) {
            Files.createFile(path);
        } else {
            Files.createDirectory(path);
        }
//        System.out.println("CREATED: " + path);
    }

    public static void writeToSource(CompilationUnit serviceCompilationUnit, Path outputPath) {
        try {
            if (!Files.exists(outputPath)) {
                recursiveCreatePath(outputPath, true);
            }
            Files.writeString(outputPath, serviceCompilationUnit.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
