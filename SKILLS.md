# 🍿 Popcorn Gradle Plugin - Claude Skills Guide

Este documento descreve os skills disponíveis para trabalhar com o Popcorn Gradle Plugin usando Claude Code ou Cowork.

> **Skills** são workflows reutilizáveis que você dispara com `/nome` para executar tarefas comuns de desenvolvimento, review e validação.

---

## 📋 Quick Start

Para usar um skill, digite `/nome-do-skill` em uma conversa com Claude. Os skills incluem:

| Skill | Descrição | Quando usar |
|-------|-----------|------------|
| `/run-tests-popcorn` | Executa testes com coverage | Após mudanças, antes de commit |
| `/build-and-check` | Compila e valida build | Validação pré-push, releases |
| `/validate-architecture` | Analisa estrutura arquitetural | Code review, design de features |
| `/review-pull-request` | Revisão sistemática de PR | Antes de merge |

---

## 🧪 1. `/run-tests-popcorn`

**Executa unit tests e gera relatório Kover de code coverage.**

### Quando usar
- Após fazer mudanças no código
- Antes de fazer commit ou push
- Para validar que tests existentes ainda passam
- Para debugar falhas de testes
- Para verificar cobertura de código

### Comandos principais

**Rodar suite completa com coverage:**
```bash
./gradlew popcornguineapigplugin:koverHtmlReport
```

**Rodar um teste específico:**
```bash
./gradlew popcornguineapigplugin:test --tests "com.github.codandotv.popcorn.domain.rules.NoDependencyRuleTest"
```

**Rodar um método específico:**
```bash
./gradlew popcornguineapigplugin:test --tests "com.github.codandotv.popcorn.domain.rules.NoDependencyRuleTest.testValidDependencies"
```

### Resultados
- 📊 Relatório HTML: `popcornguineapigplugin/build/reports/kover/html/index.html`
- 📋 Resultados: `popcornguineapigplugin/build/test-results/test/`

### Estrutura de testes
```
Domain Layer Tests
├── rules/               # NoDependencyRule, JustWithRule, DoNotWithRule
├── usecases/           # CheckArchitectureUseCase, GenerateReportUseCase
└── input/              # ProjectType, modelos de domínio

Data Layer Tests
├── report/             # Formatação de relatórios (Markdown, tabelas)
└── dto/                # Transformação de dados

Presentation Layer Tests
└── Integração Gradle   # Tasks, plugin registration
```

### Exemplo: Debugar falha de teste

```bash
# 1. Você vê falha na regra "NoDependencyRule"
./gradlew popcornguineapigplugin:test --tests "NoDependencyRuleTest"

# 2. Use /validate-architecture para entender a estrutura
# 3. Verificar se a lógica da regra está correta
# 4. Rodar novamente após fix
```

---

## 🏗️ 2. `/build-and-check`

**Compila o plugin e valida a configuração de build.**

### Quando usar
- Após fazer mudanças que afetam build
- Validar que não há erros de compilação
- Verificar dependências estão corretas
- Preparar para release
- Validação pré-push
- Atualizações de versão

### Comandos principais

**Build completo:**
```bash
./gradlew popcornguineapigplugin:build
```

**Clean build (remove artifacts antigos):**
```bash
./gradlew clean
./gradlew popcornguineapigplugin:build
```

### Arquivos de configuração importantes

| Arquivo | Propósito |
|---------|-----------|
| `popcornguineapigplugin/build.gradle.kts` | Config específica do plugin |
| `gradle/libs.versions.toml` | Versões centralizadas de deps |
| `gradle.properties` | Argumentos JVM, settings Kotlin |
| `settings.gradle.kts` | Setup de repositórios e projetos |

### Problemas comuns e soluções

| Problema | Solução |
|----------|---------|
| "Plugin is already compiled" | Rodar `./gradlew clean` primeiro |
| "JVM memory error" | Aumentar `org.gradle.jvmargs` em `gradle.properties` |
| "Repository not accessible" | Verificar internet e URLs em `settings.gradle.kts` |
| Kotlin compiler daemon error | Esperado! Desabilitado intencionalmente (KT-65761) |

### Saída do build
- 📦 Plugin JAR: `popcornguineapigplugin/build/libs/popcornguineapigplugin-<version>.jar`
- 📋 Metadata: `popcornguineapigplugin/build/libs/popcornguineapigplugin-<version>-gradle-metadata.json`

---

## 🏛️ 3. `/validate-architecture`

**Analisa a estrutura do código e valida padrões arquiteturais.**

### Quando usar
- Revisar mudanças que afetam arquitetura
- Validar que novos arquivos estão na camada certa
- Analisar violações de importação entre camadas
- Revisar implementação de novas regras
- Garantir separação de responsabilidades
- Projetar novas features

### Arquitetura em 3 camadas

```
┌─────────────────────────────────────────┐
│   PRESENTATION LAYER (Gradle API)       │
│   ├── PopcornGpParentPlugin            │
│   ├── Tasks                             │
│   └── Configuração DSL                  │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│   DOMAIN LAYER (Lógica pura)            │
│   ├── Rules (validação de regras)       │
│   ├── UseCases (orquestração)           │
│   ├── Models (ProjectType, etc)         │
│   ├── Repository (interface contrato)   │
│   └── Report (ReportData, etc)          │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│   DATA LAYER (I/O e transformação)      │
│   ├── RepositoryImpl (implementação)    │
│   └── ReportDataSource (formatação)     │
└─────────────────────────────────────────┘
```

### Checklist de validação

**Localização de arquivos** ✓
```
Novo: Rule de validação         → domain/rules/
Novo: Use case                  → domain/usecases/
Novo: Modelo de dados           → domain/report/
Nova formatação de saída        → data/report/
Nova integração Gradle          → presentation/
```

**Imports apropriados** ✓
```kotlin
// ✅ CORRETO
class MyRule : ArchitectureRule {
    fun validate(modules: List<Module>): List<Violation>
}

// ❌ ERRADO (Domain não pode importar Gradle!)
import org.gradle.api.Project
class MyRule { ... }
```

**Separação de responsabilidades** ✓
- Domain: Lógica pura, sem Gradle
- Data: Carregamento e formatação
- Presentation: Integração Gradle, DSL

### Padrões de implementação

**Adicionar uma nova regra:**
1. `domain/rules/MyNewRule.kt` - Implementar lógica
2. `src/test/kotlin/domain/rules/MyNewRuleTest.kt` - Testes
3. `domain/usecases/CheckArchitectureUseCase.kt` - Integração
4. `DependencyFactory.kt` - Se precisar injetar

**Adicionar um novo formato de relatório:**
1. `data/report/MyFormatter.kt` - Implementar formatação
2. `src/test/kotlin/data/report/MyFormatterTest.kt` - Testes
3. `presentation/` - Integrar com task

---

## 📝 4. `/review-pull-request`

**Realiza revisão sistemática de pull requests.**

### Quando usar
- Revisar PRs antes de fazer merge
- Validar qualidade geral do código
- Verificar que testes foram adicionados
- Analisar se arquitetura foi mantida
- Assessar impacto de mudanças

### Checklist de revisão

#### ✓ Arquitetura
- [ ] Novos arquivos estão na camada correta?
- [ ] Não há violações de importação?
- [ ] DependencyFactory foi atualizado (se necessário)?

#### ✓ Testes
- [ ] Novos testes foram adicionados?
- [ ] Testes cobrem casos de sucesso E falha?
- [ ] Estrutura espelha source?
- [ ] Cobertura foi mantida ou melhorada?

#### ✓ Qualidade do código
- [ ] Nomes são descritivos?
- [ ] Código segue convenções Kotlin?
- [ ] Lógica complexa está dividida em funções menores?
- [ ] Comentários explicam "por quê" não "quê"?

#### ✓ Build & CI/CD
- [ ] Testes passam (`./gradlew popcornguineapigplugin:koverHtmlReport`)?
- [ ] Compila sem erros (`./gradlew popcornguineapigplugin:build`)?
- [ ] GitHub Actions workflow passa?

#### ✓ Documentação
- [ ] Commit messages são claras?
- [ ] CLAUDE.md foi atualizado (se mudanças arquiteturais)?
- [ ] Código tem comentários adequados?

### Red flags 🚩

Peça mudanças se encontrar:
- ❌ Domain importando `org.gradle.api.*`
- ❌ Apresentação com lógica de negócio
- ❌ Tests flaky ou faltando
- ❌ Cobertura diminuiu
- ❌ Imports circulares
- ❌ Documentação desatualizada

### Template de feedback

```markdown
## ✅ O que está bom
- Arquitetura bem organizada
- Testes abrangentes
- Código legível

## 🔧 Solicitar mudanças
- [ ] Adicionar teste para edge case de módulos vazios
- [ ] Atualizar docs/2-existing-rules.md
- [ ] Melhorar mensagem de erro para usuários

## ✨ Observações
- Considere refatorar MyRule em funções menores
- Ótimo trabalho na separação de responsabilidades!
```

---

## 🚀 Workflow Comum

### Implementar uma nova feature

```bash
# 1. Entender arquitetura
/validate-architecture

# 2. Implementar (Domain → Data → Presentation)
# 3. Escrever testes
/run-tests-popcorn

# 4. Compilar localmente
/build-and-check

# 5. Abrir PR
git push origin seu-branch

# 6. Revisar PR antes de merge
/review-pull-request
```

### Debugar teste falhando

```bash
# 1. Identificar falha
./gradlew popcornguineapigplugin:test

# 2. Rodar teste específico isolado
/run-tests-popcorn
# E especificar qual teste rodar

# 3. Entender arquitetura
/validate-architecture

# 4. Fazer fix e testar novamente
```

### Preparar para release

```bash
# 1. Validar arquitetura
/validate-architecture

# 2. Rodar testes completos
/run-tests-popcorn

# 3. Compilar
/build-and-check

# 4. Revisar PRs abertas
/review-pull-request

# 5. Atualizar version.properties
# 6. Merge e trigger publish workflow
```

---

## 📚 Estrutura do Projeto

```
popcornguineapigplugin/
├── src/main/kotlin/com/github/codandotv/popcorn/
│   ├── presentation/          # Integração Gradle, tasks
│   │   ├── PopcornGpParentPlugin.kt
│   │   └── ...tasks
│   ├── domain/               # Lógica de negócio pura
│   │   ├── rules/           # NoDependency, JustWith, DoNotWith
│   │   ├── usecases/        # CheckArchitecture, GenerateReport
│   │   ├── input/           # ProjectType, modelos de entrada
│   │   ├── metadata/        # TargetModule, InternalDependenciesMetadata
│   │   ├── output/          # CheckResult, ArchitectureViolationError
│   │   ├── report/          # ReportData, AnalysisTableItemData, ReportInfo
│   │   └── PopcornGuineapigRepository.kt  # Interface contrato
│   ├── data/                # I/O e transformação
│   │   ├── report/          # Formatação de saída (ReportDataSource, ReportDataExt)
│   │   └── PopcornGuineapigRepositoryImpl.kt  # Implementação do repositório
│   └── DependencyFactory.kt # Service locator
│
├── src/test/kotlin/         # Testes espelhando source
│   ├── domain/              # Testes de regras e use cases
│   ├── data/                # Testes de DTOs e formatação
│   ├── presentation/        # Testes de integração Gradle
│   └── fakes/               # FakeRepository para mocks
│
├── build.gradle.kts         # Config do plugin
├── gradle/libs.versions.toml # Versões de deps (centralizadas!)
├── gradle.properties        # JVM args, settings Kotlin
└── settings.gradle.kts      # Repositórios, projetos

docs/                        # Documentação de usuário
├── 1-getting-started.md
├── 2-existing-rules.md
├── 3-custom-rules.md
└── 4-error-report.md
```

---

## ❓ FAQ

**P: Onde adiciono uma nova regra?**
A: Em `domain/rules/MyNewRule.kt`. Implemente a interface `ArchitectureRule` com lógica pura (sem Gradle). Adicione testes em `src/test/kotlin/domain/rules/MyNewRuleTest.kt`.

**P: Como testo uma regra sem carregar um projeto Gradle real?**
A: Use `FakePopcornGuineapigRepository` que fornece dados de teste. Veja `NoDependencyRuleTest.kt` como exemplo.

**P: Posso importar Gradle em uma regra (domain)?**
A: **Não!** Domain é pura. Coloque código que usa Gradle API em `data/` ou `presentation/`. Modelos de dados puros (sem Gradle) ficam em `domain/report/`; formatação de saída fica em `data/report/`.

**P: Aonde adiciono uma nova dependência?**
A: Em `gradle/libs.versions.toml` (TOML catalog). Nunca adicione versões hardcoded em `build.gradle.kts`.

**P: Como atualizo a versão do plugin?**
A: Em `popcornguineapigplugin/version.properties`. Siga semantic versioning (MAJOR.MINOR.PATCH).

**P: O plugin publica automaticamente para Maven Central?**
A: Sim! GitHub Actions workflow `publish.yml` publica quando acionado. Requer credenciais configuradas.

---

## 🔗 Recursos Importantes

| Recurso | Descrição |
|---------|-----------|
| **CLAUDE.md** | Documentação técnica completa (arquitetura, build, comandos) |
| **CLAUDE.md** na pasta `.claude/skills/` | Documentação dos skills |
| **docs/** | Documentação de usuário (regras, guias, contribuição) |
| **README.md** | Overview do projeto e getting started |
| **CONTRIBUTING.md** | Guia de contribuição |

---

## ✅ Antes de Fazer Commit

- [ ] `./gradlew popcornguineapigplugin:koverHtmlReport` ✓ (testes passam, cobertura mantida)
- [ ] `./gradlew popcornguineapigplugin:build` ✓ (compila sem erros)
- [ ] `/validate-architecture` ✓ (estrutura correta)
- [ ] Novos arquivos estão no lugar correto?
- [ ] Testes foram adicionados/atualizados?
- [ ] Documentação foi atualizada?
- [ ] Commit messages claras?

---

## 🎯 Próximos Passos

1. **Ler CLAUDE.md** para entender a arquitetura em detalhe
2. **Explorar skills** usando `/nome` em uma conversa com Claude
3. **Usar skills regularmente** durante desenvolvimento para manter qualidade
4. **Revisar PRs com checklist** de `/review-pull-request`

---

**Boa sorte! 🍿🐹**

Para dúvidas sobre os skills, pergunte ao Claude em uma conversa: "Qual skill devo usar para ...?"
