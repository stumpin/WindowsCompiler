/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/4/2018.
 */
public final class Linker {

    private String path, server;

    public static void main(String... args) {
        if (args.length != 4) {
            throw new RuntimeException("- Please only provide 4 arguments (path, value, server, value)");
        }
        final Linker linker = new Linker();
        linker.parse(args);
        if (linker.path != null && linker.server != null) {
            final Compiler compiler = new Compiler(linker.path, linker.server);
            compiler.compile();
        } else {
            System.out.println("- No path or server provided");
        }
    }

    private void parse(String... arguments) {
        for (int index = 0; index < arguments.length; index++) {
            final String key = arguments[index];
            if (!key.isEmpty() && key.charAt(0) == '-') {
                String value = key.substring(1);
                switch (value) {
                    case "path":
                        this.path = sanitize(arguments[index + 1]);
                        break;
                    case "server":
                        this.server = sanitize(arguments[index + 1]).toLowerCase();
                        break;
                    default:
                        throw new RuntimeException("Unrecognized argument " + value);
                }
            }
        }
    }

    private String sanitize(String string) {
        if (string != null && !string.isEmpty()) {
            return string.replaceAll("[\"']", "");
        } else {
            throw new RuntimeException("Empty or null argument value");
        }
    }
}
