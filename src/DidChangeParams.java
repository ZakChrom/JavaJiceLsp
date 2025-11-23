package dev.calion;

public class DidChangeParams {
    ContentChange[] contentChanges;
    TextDocument textDocument;
    public class TextDocument {
        String uri;
    }
    public class ContentChange {
        String text;
    }
}