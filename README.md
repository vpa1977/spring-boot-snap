# Spring Boot snap prototype

## Introduction

This prototype snap packages Spring development tools.
The snap uses classic confinement and requires Java to be present in the PATH.

## Installation

`snap install spring-boot --classic`

## Applications

### spring-boot-cli

`spring-boot.spring-boot-cli` invokes [Spring Boot CLI](https://docs.spring.io/spring-boot/docs/current/reference/html/cli.html).

It can be used to initialize a new Spring Boot project, e.g.

`spring init --dependencies=web,data-jpa my-project`

### spring-cli

`spring-boot.spring-cli` invokes [Spring CLI](https://docs.spring.io/spring-cli/reference/index.html)

This tool provides a set of commands to create, manage and run day-to-day tasks on the Java project.

## Sample

1. Create a project with `spring-boot.spring-boot-cli`

`` $ spring-boot.spring-boot-cli init --build=maven --java-version=21 --dependencies=ws --packaging=jar sample ``

2. Add JPA sample code from the standard catalog using `spring-boot.spring-cli`

`` spring-boot.spring-cli boot add jpa``

3. AI-assisted code generation using OpenAI [ note: not tested ]

3.1 Generate a new API key at https://platform.openai.com/api-keys and store it to file

```
echo OPEN_AI_API_KEY=<you-key> > ~/.openai
chmod 0600 ~/.openai
```

3.2 Generate changes using

`` spring-boot.spring-cli ai add --description '<openai prompt>' --preview ``
