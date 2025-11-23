-- ~/.config/nvim/init.lua

vim.lsp.enable("java_jice")

vim.diagnostic.config {
    underline = true,
    virtual_text = {
        prefix = "",
        severity = nil,
        source = "if_many",
        format = nil,
    },
    signs = true,
    severity_sort = true,
    update_in_insert = true,
}

vim.cmd[[set completeopt+=menuone,noselect,popup]]
vim.api.nvim_create_autocmd('LspAttach', {
    group = vim.api.nvim_create_augroup('java_jice', {}),
    callback = function(args)
        local client = assert(vim.lsp.get_client_by_id(args.data.client_id))
        if client:supports_method('textDocument/completion') then
            vim.lsp.completion.enable(true, client.id, args.buf, { autotrigger = true })
        end
        vim.keymap.set("n", "<space>e", vim.diagnostic.open_float, { noremap = true, silent = true, buffer = args.buf })
    end,
})
