package dev.calion;

public class CompletionItem {
    String label;
    int kind;
    public CompletionItem(String label, int kind) {
        this.label = label;
        this.kind = kind;
    }
    static int Text = 1;
	static int Method = 2;
	static int Function = 3;
	static int Constructor = 4;
	static int Field = 5;
	static int Variable = 6;
	static int Class = 7;
	static int Interface = 8;
	static int Module = 9;
	static int Property = 10;
	static int Unit = 11;
	static int Value = 12;
	static int Enum = 13;
	static int Keyword = 14;
	static int Snippet = 15;
	static int Color = 16;
	static int File = 17;
	static int Reference = 18;
	static int Folder = 19;
	static int EnumMember = 20;
	static int Constant = 21;
	static int Struct = 22;
	static int Event = 23;
	static int Operator = 24;
	static int TypeParameter = 25;
}