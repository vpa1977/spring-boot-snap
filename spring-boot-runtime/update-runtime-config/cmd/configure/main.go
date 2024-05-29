package main

import (
	"fmt"
	"os"

	"github.com/canonical/spring-boot-snap/spring-boot-runtime/internal/common"
	"github.com/canonical/spring-boot-snap/spring-boot-runtime/internal/gradle"
	"github.com/canonical/spring-boot-snap/spring-boot-runtime/internal/maven"
)

func main() {
	if err := run(); err != nil {
		fmt.Print("%v\n", err)
		os.Exit(1)
	}
}

func run() error {
	repositories := []common.Repository{
		{Id: "spring-boot", Name: "Spring Boot", Url: "file:///snap/spring-boot-runtime/current/spring-boot/"},
		{Id: "spring-framework", Name: "Spring Framework", Url: "file:///snap/spring-boot-runtime/current/spring-framework/"},
	}
	if err := gradle.UpdateInitScripts(repositories); err != nil {
		return err
	}

	return maven.UpdateSettings(repositories)
}
