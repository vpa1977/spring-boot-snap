package maven

import (
	"bytes"
	"encoding/xml"
	"fmt"
	"strings"

	"github.com/canonical/spring-boot-snap/spring-boot-runtime/internal/common"
)

const maven_profile = `
<profile>
<id>spring-boot-snap</id>
<activation>
  <activeByDefault>true</activeByDefault>
</activation>
<repositories>
  %v
</repositories>
<pluginRepositories>
   %v
</pluginRepositories>
</profile>
`

const maven_repository = `
<repository>
<id>%v</id>
<name>%v</name>
<snapshots>
  <enabled>true</enabled>
</snapshots>
<releases>
  <enabled>true</enabled>
  <updatePolicy>always</updatePolicy>
</releases>
<url>%v</url>
<layout>default</layout>
</repository>
`

const plugin_repository = `
<pluginRepository>
<id>%v</id>
<name>%v</name>
<releases>
  <enabled>true</enabled>
</releases>
<url>%v</url>
</pluginRepository>
`

func UpdateSettings(repositories []common.Repository) error {
	fmt.Print("This program will update ~/.m2/settings.xml to include a Spring Boot snap maven repositories.\n")
	return nil
}

type SettingsVisitor struct {
	inProfiles bool
	inProfile  bool
}

func (v SettingsVisitor) enter(token xml.StartElement) {
	if token.Name.Local == "profiles" {
		v.inProfiles = true
	} else if token.Name.Local == "profile" {
		v.inProfile = true
	} else if token.Name.Local == "id" {
		if v.inProfile
	}

}

func (v SettingsVisitor) exit(token xml.EndElement, encoder *xml.Encoder) {
	if token.Name.Local == "profiles" {
		v.inProfiles = false
	}
}

func ProcessSettings(input string) string {
	var err error
	output := new(bytes.Buffer)
	visitor := new(SettingsVisitor)
	decoder := xml.NewDecoder(strings.NewReader(input))
	encoder := xml.NewEncoder(output)
	for err == nil {
		token, err := decoder.RawToken()
		if err == nil {
			switch tt := token.(type) {
			case xml.StartElement:
				visitor.enter(tt)
			case xml.EndElement:
				visitor.exit(tt, encoder)
			}
			err = encoder.EncodeToken(token)
			if err != nil {
				panic(err)
			}
		} else {
			break
		}
	}
	encoder.Flush()
	return output.String()
}
