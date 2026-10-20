# CleanEditor v1.0.2 (prévia)

Desenvolvimento da v1.0.2 em `release/v1.0.2` sem alterar a versão estável `main` (v1.0.1).

## Alterações

- Salvamento automático aproximadamente 1,8 segundo após parar de digitar, nos documentos modificados.
- Cópia de segurança de rascunho no armazenamento privado antes de cada tentativa de salvar.
- Preservados: editor, busca/substituição, desfazer/refazer, abas, gerenciador de arquivos, lembretes e realce de sintaxe da v1.0.1.
- Numeração de versão Android atualizada para 1.0.2 (versionCode 3).

## Compilar

Workflow `.github/workflows/android-build.yml`. Extrai o ZIP-base v1.0.1 com `unzip -n` para **não sobrescrever** os arquivos v1.0.2 presentes no repositório. A compilação gera o artefato `CleanEditor-v1.0.2-debug-apk`.

> A recuperação manual dos backups e funcionalidades adicionais de interface permanecem para uma atualização posterior. Compilação e testes em dispositivo dependem do resultado do GitHub Actions.
