package maven

import "fmt"

func UpdateSettings() error {
	fmt.Print("This program will update ~/.m2/settings.xml to include a Spring Boot snap maven repositories.\n")
	return nil
}
