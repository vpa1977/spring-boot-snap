# Prototype Devpack for Spring® Projects

## Introduction

This prototype snap packages Spring development tools.
The snap uses classic confinement and requires Java to be present in the PATH.

## Installation

`snap install devpack-for-spring --classic`

## Applications

### spring

`devpack-for-spring.spring` invokes [Spring Boot CLI](https://docs.spring.io/spring-boot/docs/current/reference/html/cli.html).

It can be used to initialize a new Spring Boot project, e.g.

`devpack-for-spring.spring init --dependencies=web,data-jpa my-project`

### spring-cli

`devpack-for-spring.spring-cli` invokes [Spring CLI](https://docs.spring.io/spring-cli/reference/index.html)

This tool provides a set of commands to create, manage and run day-to-day tasks on the Java project.

### install

`devpack-for-spring.install` install a content snap for a Spring project.

### list

`devpack-for-spring.list` lists available and installed content snaps.

### local-maven

`devpack-for-spring.local-maven` install content snaps into a local Maven repository.

## Sample

1. Create a project with `devpack-for-spring.spring`

`` $ devpack-for-spring.spring init --build=maven --java-version=21 --dependencies=ws --packaging=jar sample ``

2. Add JPA sample code from the standard catalog using `devpack-for-spring.spring-cli`

`` devpack-for-spring.spring-cli boot add jpa``


_Spring is a trademark of Broadcom Inc. and/or its subsidiaries._
