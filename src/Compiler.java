import javax.tools.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/12/2018.
 */
public class Compiler {

    private final String pkg, server, separator = System.getProperty("file.separator");
    private ArrayList<File> files = new ArrayList<>();

    public Compiler(String path, String server) {
        this.pkg = path;
        this.server = server;

        final File source = new File(path);

        if (source.exists() && source.isDirectory()) {
            final File[] sourceFiles = source.listFiles();
            if (sourceFiles == null || sourceFiles.length < 1) {
                throw new RuntimeException("- invalid path or empty directory");
            }
            for (File file : sourceFiles) {
                if (file.isDirectory()) {
                    System.out.println("+ Skipping directory " + file.getName());
                } else if (!file.getName().endsWith(".java")) {
                    System.out.println("+ Skipping non .java file " + file.getName());
                } else {
                    try {
                        final FileReader reader = new FileReader(file);
                        final BufferedReader fileBuff = new BufferedReader(reader);
                        String line;
                        while ((line = fileBuff.readLine()) != null) {
                            //if the file is the script entry point
                            if (line.toLowerCase().contains("extends activescript")) {
                                files.add(file);
                                break;
                            }
                        }
                        fileBuff.close();
                        reader.close();
                    } catch (Exception e) {
                        System.out.println("wrong boy");
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("+ Found " + sourceFiles.length + " files, " + files.size() + " of which were script entry points");
        } else {
            System.out.println("source doesn't exist or it isn't a directory");
        }
    }

    public void compile() {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.ENGLISH, Charset.defaultCharset());
        final Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjectsFromFiles(files);

        String output, xobot, classpath;
        output = (xobot = getXobotPath()) + separator + "Scripts" + separator;
        classpath = xobot + separator + "xobot.jar;" + xobot + separator + "APIs" + separator;

        switch (server) {
            case "alora":
                output += "Alora";
                classpath += "Alora.jar;";
                break;
            case "dawntained":
                output += "Dawntained";
                classpath += "Dawntained.jar;";
                break;
            case "soulplay":
                output += "SoulPlay";
                classpath += "SoulPlay.jar;";
                break;
            default:
                throw new RuntimeException("bad path " + server);
        }

        classpath += pkg;

        final String[] arguments = {"-classpath", classpath, "-d", output, ""};

        fileObjects.forEach(file -> {
            try {
                final String name = file.getName();
                arguments[4] = name;

                int result = compiler.run(null, null, null, arguments);
                if (result == 0) {
                    System.out.println("+ Successfully compiled " + name);
                } else {
                    System.out.println("- Something went wrong");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String getXobotPath() {
        final StringBuilder builder = new StringBuilder();
        builder.append(System.getProperty("user.home")).append(separator).append("Documents").append(separator).append("XoBot");
        return builder.toString();
    }
}
