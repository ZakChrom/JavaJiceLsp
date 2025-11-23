package dev.calion;

public class InitializeResponse {
    int id;
    Result result;

    public class Result {
        Capabilities capabilities;
        ServerInfo serverInfo;
        public Result(Capabilities capabilities, ServerInfo serverInfo) {
            this.capabilities = capabilities;
            this.serverInfo = serverInfo;
        }
    }
    public class Capabilities {
        int textDocumentSync;
        CompletionProvider completionProvider;
        public Capabilities(int textDocumentSync, CompletionProvider completionProvider) {
            this.textDocumentSync = textDocumentSync;
            this.completionProvider = completionProvider;
        }
    }
    public class CompletionProvider {
        String[] triggerCharacters;
        public CompletionProvider(String[] triggerCharacters) {
            this.triggerCharacters = triggerCharacters;
        }
    }
    public class ServerInfo {
        String name;
        String version;
        public ServerInfo(String name, String version) {
            this.name = name;
            this.version = version;
        }
    }
    public InitializeResponse(int id) {
        this.id = id;
        this.result = new Result(
            new Capabilities(1, new CompletionProvider(new String[] {".", "@" })),
            new ServerInfo("JavaJiceLsp", "0.1.0")
        );
    }
}