-- ~/.config/nvim/lsp/java_jice.lua

local path = vim.fn.fnamemodify(debug.getinfo(1, "S").source:sub(2), ":h")
return {
    cmd = { "java", "-jar", (path or "~/.config/nvim/lsp") .. "/java_jice.jar" },
    filetypes = { "java" },
    root_markers = { "jice.kdl", ".git" },
    capabilities = { textDocument = { completion = {} }}
}