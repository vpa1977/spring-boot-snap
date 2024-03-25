# Spring Boot snap prototype

# Introduction

This prototype snap packages Spring development tools.
The snap uses classic confinement and requires Java to be present in the PATH.

## Applications

### spring-boot-cli

`spring-boot.spring-boot-cli` invokes [Spring Boot CLI](https://docs.spring.io/spring-boot/docs/current/reference/html/cli.html).

It can be used to initialize a new Spring Boot project, e.g.

`spring init --dependencies=web,data-jpa my-project`

### spring-cli

`spring-boot.spring-cli` invokes [Spring CLI](https://docs.spring.io/spring-cli/reference/index.html)

This tool provides a set of commands to create, manage and run day-to-day tasks on the Java project.

### cache

`spring-boot.cache` is used to cache artifacts of a given Spring boot version in the local Maven repository, e.g.

`spring-boot.cache v3.2.1` will build and publish Spring Boot 3.2.1 to the local Maven repository.
