# Assinatura permanente e atualizações Android

## Regra para atualizar sem desinstalar
A atualização deve ter o mesmo `applicationId` (`com.cleaneditor.app`), o **mesmo certificado de assinatura** e `versionCode` superior ao instalado. O APK debug não é indicado para distribuição porque GitHub Actions recria a chave debug em runners diferentes.

## Como habilitar
1. Guarde a chave privada `cleaneditor-release.jks` e as senhas fornecidas no pacote privado, fora do repositório.
2. Acesse **Settings → Secrets and variables → Actions → New repository secret** no repositório.
3. Cadastre os quatro nomes, exatamente:
   - `ANDROID_KEYSTORE_BASE64`: conteúdo inteiro do arquivo `.jks` em base64
   - `ANDROID_KEYSTORE_PASSWORD`: senha da keystore
   - `ANDROID_KEY_ALIAS`: `cleaneditor`
   - `ANDROID_KEY_PASSWORD`: senha da chave
4. Rode o workflow **Compilar APK Android** nesta branch. Ele também produzirá o artefato `CleanEditor-release-assinado-atualizavel`, cujo APK pode atualizar as instalações feitas com a **mesma chave**.
5. Para cada próxima atualização aumente `versionCode` (atualmente 4; versão `1.0.3`) e preserve `applicationId` e `jks`.

**Migração inicial:** os APKs debug anteriores foram assinados por chaves debug temporárias. Não é possível prometer atualização sem desinstalar para quem já os instalou; faça backup dos dados antes de migrar para o APK release. Após instalar a primeira versão release assinada permanentemente, as próximas versões com a mesma assinatura poderão substituir a anterior sem apagar dados.

**Segurança:** nunca commite o arquivo `.jks`, as senhas ou seus valores base64 ao GitHub. O build release é omitido quando os secrets ainda não estão configurados.
