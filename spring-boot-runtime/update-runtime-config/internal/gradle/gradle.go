package gradle

import "fmt"

func UpdateInitScripts() error {
	fmt.Print("This program will add 'springbootsnap.gradle' to ~/.gradle/init.d to configure Spring Boot maven repository.\n")
	return nil
}
