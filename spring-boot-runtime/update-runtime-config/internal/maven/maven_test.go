package maven_test

import (
	"testing"

	"github.com/canonical/spring-boot-snap/spring-boot-runtime/internal/maven"
)

func TestParsing(t *testing.T) {

	data := string(`
<settings>
  <localRepository> <foobar> test </foobar> </localRepository>
</settings>
`)
	ret := maven.ProcessSettings(data)
	if ret != data {
		t.Errorf("Expected %v but encoded %v", data, ret)
	}
}
