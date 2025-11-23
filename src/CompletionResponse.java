package dev.calion;
import dev.calion.CompletionItem;

public class CompletionResponse {
    int id;
    Result result;
    public class Result {
        boolean isIncomplete;
        CompletionItem[] items;
    }
    public CompletionResponse(int id, boolean isIncomplete, CompletionItem[] items) {
        this.id = id;
        this.result = new Result();
        this.result.isIncomplete = isIncomplete;
        this.result.items = items;
    }
}