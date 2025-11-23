const { LanguageClient, TransportKind } = require("vscode-languageclient/node");

let client;

function activate(context) {
	let thing = {
		command: "java",
		args: ["-jar", "/home/calion/Programming/JavaLsp/main.jar"],
		transport: TransportKind.stdio,
	};
    client = new LanguageClient(
        "javajicelsp",
        {
            run: thing,
			debug: thing
        },
        {
            documentSelector: [{ scheme: "file", language: "java" }],
        }
    );
    client.start();
}

function deactivate() {
    if (!client) {
        return undefined;
    }
    return client.stop();
}

module.exports = {
    activate,
    deactivate
}
