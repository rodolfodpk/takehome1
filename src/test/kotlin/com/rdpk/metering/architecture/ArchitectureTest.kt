package com.rdpk.metering.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

/**
 * Architecture tests using ArchUnit to enforce package-per-layer architecture rules.
 * 
 * These tests ensure:
 * - Layer dependencies are respected (no circular dependencies)
 * - Naming conventions are followed
 * - Proper annotations are used
 * - Domain models remain pure (no framework dependencies)
 */
class ArchitectureTest : DescribeSpec({

    val classes: JavaClasses = ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("com.rdpk.metering")

    describe("Layer Dependencies") {

        it("controllers should only depend on service, dto, exception, and domain layers") {
            noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .because("Controllers should not access repositories directly - use service layer")
                .check(classes)

            noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..scheduler..")
                .because("Controllers should not access schedulers directly")
                .check(classes)
        }

        it("services should not depend on controllers") {
            noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .because("Services should not depend on controllers (circular dependency)")
                .check(classes)
        }

        it("repositories should only depend on domain layer") {
            noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..service..")
                .because("Repositories should not depend on services")
                .check(classes)

            noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .because("Repositories should not depend on controllers")
                .check(classes)

            noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..scheduler..")
                .because("Repositories should not depend on schedulers")
                .check(classes)
        }

        it("schedulers should not depend on controllers") {
            noClasses()
                .that().resideInAPackage("..scheduler..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .because("Schedulers should not depend on controllers")
                .check(classes)
        }

        it("domain models should not depend on any other layer") {
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..service..")
                .because("Domain models should be pure - no service dependencies")
                .check(classes)

            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .because("Domain models should be pure - no repository dependencies")
                .check(classes)

            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .because("Domain models should be pure - no controller dependencies")
                .check(classes)

            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..scheduler..")
                .because("Domain models should be pure - no scheduler dependencies")
                .check(classes)
        }
    }

    describe("Naming Conventions") {

        it("controllers should be named with Controller suffix") {
            classes()
                .that().resideInAPackage("..controller..")
                .and().areNotNestedClasses()
                .should().haveSimpleNameEndingWith("Controller")
                .because("Controllers must follow naming convention: *Controller")
                .check(classes)
        }

        it("services should be named with Service suffix") {
            classes()
                .that().resideInAPackage("..service..")
                .and().areNotNestedClasses()
                .should().haveSimpleNameEndingWith("Service")
                .because("Services must follow naming convention: *Service")
                .check(classes)
        }

        it("repositories should be named with Repository suffix") {
            classes()
                .that().resideInAPackage("..repository..")
                .and().areNotNestedClasses()
                .should().haveSimpleNameEndingWith("Repository")
                .orShould().haveSimpleNameEndingWith("RepositoryExtensions")
                .orShould().haveSimpleNameEndingWith("RepositoryImpl")
                .because("Repositories must follow naming convention: *Repository, *RepositoryExtensions, or *RepositoryImpl")
                .check(classes)
        }

        it("schedulers should be named with Scheduler suffix") {
            classes()
                .that().resideInAPackage("..scheduler..")
                .and().areNotNestedClasses()
                .should().haveSimpleNameEndingWith("Scheduler")
                .orShould().haveSimpleNameEndingWith("Processor")
                .because("Schedulers must follow naming convention: *Scheduler or *Processor")
                .check(classes)
        }
    }

    describe("Annotations") {

        it("controllers should be annotated with @RestController") {
            classes()
                .that().resideInAPackage("..controller..")
                .and().areNotNestedClasses()
                .should().beAnnotatedWith(RestController::class.java)
                .because("Controllers must be annotated with @RestController")
                .check(classes)
        }

        it("services should be annotated with @Service") {
            classes()
                .that().resideInAPackage("..service..")
                .and().areNotNestedClasses()
                .should().beAnnotatedWith(Service::class.java)
                .because("Services must be annotated with @Service")
                .check(classes)
        }

        it("repositories should be annotated with @Repository") {
            classes()
                .that().resideInAPackage("..repository..")
                .and().areNotInterfaces()
                .and().areNotNestedClasses()
                .should().beAnnotatedWith(Repository::class.java)
                .because("Repository implementations must be annotated with @Repository")
                .allowEmptyShould(true)
                .check(classes)
        }

        it("schedulers should be annotated with @Component") {
            classes()
                .that().resideInAPackage("..scheduler..")
                .and().areNotNestedClasses()
                .should().beAnnotatedWith(Component::class.java)
                .because("Schedulers must be annotated with @Component")
                .check(classes)
        }
    }

    describe("Domain Model Purity") {

        it("domain models should not have Spring service annotations") {
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith(Service::class.java)
                .orShould().beAnnotatedWith(Repository::class.java)
                .orShould().beAnnotatedWith(RestController::class.java)
                .orShould().beAnnotatedWith(Component::class.java)
                .because("Domain models should not have Spring service annotations (Spring Data annotations are allowed)")
                .check(classes)
        }

        it("domain models should not depend on Spring service packages") {
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("org.springframework.stereotype..")
                .orShould().dependOnClassesThat().resideInAPackage("org.springframework.web..")
                .because("Domain models should not depend on Spring service/web packages (Spring Data annotations are allowed)")
                .check(classes)
        }
    }

    describe("Package Structure") {

        it("controller package should only contain controllers") {
            classes()
                .that().resideInAPackage("..controller..")
                .and().areNotNestedClasses()
                .should().beAnnotatedWith(RestController::class.java)
                .because("Controller package should only contain @RestController classes")
                .check(classes)
        }

        it("service package should only contain services") {
            classes()
                .that().resideInAPackage("..service..")
                .and().areNotNestedClasses()
                .should().beAnnotatedWith(Service::class.java)
                .because("Service package should only contain @Service classes")
                .check(classes)
        }

        it("repository package should only contain repositories") {
            classes()
                .that().resideInAPackage("..repository..")
                .and().areNotNestedClasses()
                .should().beAnnotatedWith(Repository::class.java)
                .orShould().beInterfaces()
                .because("Repository package should only contain @Repository classes or interfaces")
                .check(classes)
        }
    }
})

