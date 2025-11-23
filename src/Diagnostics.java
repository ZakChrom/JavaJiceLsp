package dev.calion;
import dev.calion.Range;

public class Diagnostics {
    String method = "textDocument/publishDiagnostics";
    Params params;
    public class Params {
        String uri;
        Diagnostic[] diagnostics;
    }
    public Diagnostics(String uri, Diagnostic[] diagnostics) {
        this.params = new Params();
        this.params.uri = uri;
        this.params.diagnostics = diagnostics;
    }
    public static class Diagnostic {
        Range range;
        int severity;
        String message;
        public Diagnostic(Range range, int severity, String message) {
            this.range = range;
            this.severity = severity;
            this.message = message;
        }
    }

    static int Error = 1;
    static int Warning = 2;
    static int Information = 3;
    static int Hint = 4;
}