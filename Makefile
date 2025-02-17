SNAP_DIRS := $(shell find . -type d -exec test -e {}/snap/snapcraft.yaml \; -print)

all: build

build: $(SNAP_DIRS)

$(SNAP_DIRS):
	@echo "Building snap in $@"
	cd $@ && snapcraft && snap install *.snap --classic --dangerous

clean:
	for dir in $(SNAP_DIRS); do \
		(cd $$dir && snapcraft clean); \
	done

.PHONY: all install clean $(SNAP_DIRS) *.snap
