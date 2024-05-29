package gradle

import (
	"fmt"

	"github.com/canonical/spring-boot-snap/spring-boot-runtime/internal/common"
)

const gradle_plugin = `

apply plugin: SpringBootSnapRepositoryPlugin

class SpringBootSnapRepositoryPlugin implements Plugin<Gradle> {

	void apply(Gradle gradle) {
		gradle.allprojects { project ->
			project.repositories {
				// add the enterprise repository
				%v
			}
		}
	}

}
`

//maven {
//	name "Spring_Boot_Snap_Repository"
//	url "file:///snap/spring-boot/current/maven-repo/"
//}

const repository_entry = `
%v {
	name "%v"
	url "%v"
}
`

func UpdateInitScripts(repositories []common.Repository) error {
	fmt.Print("This program will add 'springbootsnap.gradle' to ~/.gradle/init.d to configure Spring Boot maven repository.\n")
	return nil
}
