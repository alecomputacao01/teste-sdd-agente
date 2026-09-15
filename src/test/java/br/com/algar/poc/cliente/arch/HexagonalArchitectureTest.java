package br.com.algar.poc.cliente.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Suíte de conformidade arquitetural para o estilo Hexagonal (plan.md §4).
 * Cada regra abaixo corresponde a uma seta de dependência permitida/proibida do estilo escolhido.
 * O runner da extensão JUnit5 do ArchUnit executa todo campo {@code @ArchTest} automaticamente
 * contra as classes importadas por {@code @AnalyzeClasses} — nenhum método de teste manual é necessário.
 *
 * Escrita já na Fase 2 do tasks.md, antes de existirem adapters (Fase 4): passa trivialmente hoje
 * porque só há domínio; passa a fiscalizar de fato assim que application/adapters forem adicionados.
 */
@AnalyzeClasses(packages = "br.com.algar.poc.cliente", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    private static final String BASE_PACKAGE = "br.com.algar.poc.cliente";

    @ArchTest
    static final ArchRule domain_nao_depende_de_adapters =
            noClasses().that().resideInAPackage(BASE_PACKAGE + ".domain..")
                    .should().dependOnClassesThat().resideInAPackage(BASE_PACKAGE + ".adapters..");

    @ArchTest
    static final ArchRule domain_nao_depende_de_application =
            noClasses().that().resideInAPackage(BASE_PACKAGE + ".domain..")
                    .should().dependOnClassesThat().resideInAPackage(BASE_PACKAGE + ".application..");

    @ArchTest
    static final ArchRule domain_nao_depende_de_spring_ou_jpa =
            noClasses().that().resideInAPackage(BASE_PACKAGE + ".domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta.persistence..");

    // allowEmptyShould(true): nesta fase (tasks.md Fase 2) os pacotes application/adapters ainda não
    // existem — a regra passa a checar de fato assim que Fase 4 adicionar classes a eles.
    @ArchTest
    static final ArchRule application_nao_depende_de_adapters =
            noClasses().that().resideInAPackage(BASE_PACKAGE + ".application..")
                    .should().dependOnClassesThat().resideInAPackage(BASE_PACKAGE + ".adapters..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule adapters_in_nao_depende_de_adapters_out =
            noClasses().that().resideInAPackage(BASE_PACKAGE + ".adapters.in..")
                    .should().dependOnClassesThat().resideInAPackage(BASE_PACKAGE + ".adapters.out..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule portas_de_entrada_sao_interfaces =
            classes().that().resideInAPackage(BASE_PACKAGE + ".domain.ports.in..")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule portas_de_saida_sao_interfaces =
            classes().that().resideInAPackage(BASE_PACKAGE + ".domain.ports.out..")
                    .should().beInterfaces();
}
