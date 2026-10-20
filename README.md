# CleanEditor v1.0.1

Aplicativo Android em Kotlin + Jetpack Compose (Material 3).

## Código-fonte

O arquivo `CleanEditor-v1.0.1-source.zip` na raiz contém o projeto completo. Os arquivos de configuração básicos também foram publicados diretamente. O GitHub Actions **extrai o ZIP original antes de compilar**, garantindo a presença de todos os arquivos Kotlin, recursos e manifesto.

## Gerar APK

Em [Actions](https://github.com/edgarcdc5-ux/blocos/actions), abra **Compilar APK Android** e execute **Run workflow**, ou aguarde a execução automática do último push.

O workflow usa Java 17, Gradle 8.9 e Android SDK 34. Após **sucesso**, baixe o artefato `CleanEditor-debug-apk`, contendo o APK de depuração.

A compilação precisa ser verificada nos logs. O código-fonte v1.0.1 não tinha compilação confirmada quando foi fornecido.
