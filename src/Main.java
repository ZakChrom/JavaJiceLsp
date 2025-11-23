package dev.calion;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.tools.*;
import com.sun.source.util.*;
import com.sun.source.tree.*;
import com.google.gson.Gson;
import dev.calion.*;

public class Main {
    public static void notify(String content) throws Exception {
        Runtime.getRuntime().exec(new String[] {"notify-send", content});
    }
    public static void addToThing(List<CompletionItem> items, CompletionItem a) {
        for (CompletionItem i : items) {
            if (i.label.equals(a.label) && i.kind == a.kind) {
                return;
            }
        }
        items.add(a);
    }
    public static void doDiagnostics(JavaCompiler compiler, Map<URI, String> files, Gson gson, URI docUri) throws Exception {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavacTask task = (JavacTask)compiler.getTask(
            null, null, diagnostics,
            List.of("-proc:none"),
            null,
            List.of(new SimpleJavaFileObject(URI.create("string:///" + docUri.getPath()), JavaFileObject.Kind.SOURCE) {
                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                    return files.get(docUri);
                }
            })
        );
        Iterable<? extends CompilationUnitTree> asts = task.parse();
        task.analyze();

        List<Diagnostics.Diagnostic> diags = new ArrayList<>();

        for (Diagnostic<?> d : diagnostics.getDiagnostics()) {
            int type;
            if (d.getKind().equals(Diagnostic.Kind.ERROR)) {
                type = 1;
            } else {
                type = 3;
            }
            Position pos = new Position((int)d.getLineNumber() - 1, (int)d.getColumnNumber() - 1);
            Diagnostics.Diagnostic diag = new Diagnostics.Diagnostic(
                new Range(pos, pos),
                type,
                d.getMessage(null)
            );
            diags.add(diag);
        }
        Diagnostics.Diagnostic[] diagsReal = new Diagnostics.Diagnostic[diags.size()];
        diagsReal = diags.toArray(diagsReal);
        Diagnostics response = new Diagnostics(docUri.toString(), diagsReal);
        String thing = gson.toJson(response);
        System.out.print("Content-Length: " + thing.length() + "\r\n\r\n" + thing);
    }
    public static void main(String[] args) throws Exception {
        notify("starigng");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Gson gson = new Gson();
        Map<URI, String> files = new HashMap<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        while (true) {
            String header = reader.readLine();
            if (header == null) { break; }
            Integer number = Integer.valueOf(header.split(": ")[1].replace("\r", ""));
            if (number == null) { throw new Exception(""); }
            reader.readLine();

            char[] json = new char[number];
            int result = reader.read(json, 0, number);
            if (result != number) { throw new Exception(""); }
            Message msg = gson.fromJson(String.valueOf(json), Message.class);
            notify(msg.method);

            if (msg.method.equals("initialize")) {
                InitializeResponse response = new InitializeResponse(msg.id);
                String thing = gson.toJson(response);
                System.out.print("Content-Length: " + thing.length() + "\r\n\r\n" + thing);
            } else if (msg.method.equals("exit")) {
                break;
            } else if (msg.method.equals("textDocument/didOpen")) {
                // Awful
                DidOpenParams p = gson.fromJson(gson.toJson(msg.params), DidOpenParams.class);
                URI uri = URI.create(p.textDocument.uri);
                files.put(uri, p.textDocument.text);
                doDiagnostics(compiler, files, gson, uri);
            } else if (msg.method.equals("textDocument/didChange")) {
                DidChangeParams p = gson.fromJson(gson.toJson(msg.params), DidChangeParams.class);
                URI uri = URI.create(p.textDocument.uri);
                notify("Stuff: " + p.contentChanges.length);
                files.put(uri, p.contentChanges[p.contentChanges.length - 1].text);
                doDiagnostics(compiler, files, gson, uri);
            } else if (msg.method.equals("textDocument/completion")) {
                CompletionParams p = gson.fromJson(gson.toJson(msg.params), CompletionParams.class);
                URI docUri = URI.create(p.textDocument.uri);

                doDiagnostics(compiler, files, gson, docUri);

                JavacTask task = (JavacTask)compiler.getTask(
                    null, null, null,
                    List.of("-proc:none"),
                    null,
                    List.of(new SimpleJavaFileObject(URI.create("string:///" + docUri.getPath()), JavaFileObject.Kind.SOURCE) {
                        @Override
                        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                            return files.get(docUri);
                        }
                    })
                );
                Iterable<? extends CompilationUnitTree> asts = task.parse();
                task.analyze();

                Trees trees = Trees.instance(task);
                List<CompletionItem> items = new ArrayList<>();

                for (CompilationUnitTree cu : asts) {
                    var decls = cu.getTypeDecls();
                    notify("len: " + decls.size());
                    for (var decl : decls) {
                        if (decl instanceof MethodTree) {
                            MethodTree method = (MethodTree)decl;
                            long start = trees.getSourcePositions().getStartPosition(cu, method);
                            long end = trees.getSourcePositions().getEndPosition(cu, method);
                            notify("method: " + method.getName() + " @ " + start + " -> " + end);
                        }
                    }
                    cu.accept(new TreeScanner<Void, Void>() {
                        @Override
                        public Void visitMethod(MethodTree node, Void p) {
                            addToThing(items, new CompletionItem(node.getName().toString(), 2));
                            return super.visitMethod(node, p);
                        }
                        @Override
                        public Void visitIdentifier(IdentifierTree node, Void p) {
                            addToThing(items, new CompletionItem(node.getName().toString(), 6));
                            return super.visitIdentifier(node, p);
                        }
                        @Override
                        public Void visitClass(ClassTree node, Void p) {
                            addToThing(items, new CompletionItem(node.getSimpleName().toString(), 7));
                            return super.visitClass(node, p);
                        }
                    }, null);
                }

                CompletionItem[] itemsReal = new CompletionItem[items.size()];
                itemsReal = items.toArray(itemsReal);

                CompletionResponse response = new CompletionResponse(msg.id, true, itemsReal);
                String thing = gson.toJson(response);
                System.out.print("Content-Length: " + thing.length() + "\r\n\r\n" + thing);
            } else if (msg.method.equals("initialized")) {
            } else if (msg.method.equals("shutdown")) {
            
            }
        }
    }
}
