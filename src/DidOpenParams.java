package dev.calion;

public class DidOpenParams {
    Item textDocument;
    public class Item {
        String uri;
        String languageId;
        int version;
        String text;
    }
}