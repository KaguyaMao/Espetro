import com.google.gson.JsonParser;
import java.nio.file.*;
import java.util.stream.Stream;

public class JsonScan {
    public static void main(String[] args) throws Exception {
        int checked = 0, failed = 0;
        for (String root : args) {
            try (Stream<Path> s = Files.walk(Paths.get(root))) {
                for (Path p : (Iterable<Path>) s.filter(x -> x.toString().endsWith(".json")).sorted()::iterator) {
                    String text;
                    try { text = Files.readString(p); } catch (Exception e) { continue; }
                    if (!text.contains("\"OBB\"")) continue;
                    checked++;
                    try {
                        JsonParser.parseString(text);   // ← 与 SBW LoadingJsonEvent.getAsGsonObject() 相同（宽松）
                    } catch (Throwable t) {
                        failed++;
                        System.out.println("FAIL " + p + "  (" + text.length() + " B)");
                        System.out.println("     " + t.getClass().getName() + ": " + t.getMessage());
                        String[] lines = text.split("\n", -1);
                        int ln = 1;
                        try { ln = Integer.parseInt(t.getMessage().replaceAll(".*at line (\\d+).*", "$1")); } catch (Exception ignore) {}
                        for (int i = Math.max(0, ln - 4); i < Math.min(lines.length, ln + 2); i++) {
                            System.out.println("     L" + (i + 1) + ": " + lines[i]);
                        }
                    }
                }
            }
        }
        System.out.println("checked=" + checked + " failed=" + failed);
    }
}
