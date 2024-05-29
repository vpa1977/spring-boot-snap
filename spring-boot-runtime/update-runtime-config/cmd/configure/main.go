package main

import (
	"fmt"
	"os"

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
	if err := gradle.UpdateInitScripts(); err != nil {
		return err
	}

	return maven.UpdateSettings()
}
