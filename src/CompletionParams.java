package dev.calion;
import dev.calion.Position;

public class CompletionParams {
    TextDocument textDocument;
    Position position;
    public class TextDocument {
        String uri;
    }
}