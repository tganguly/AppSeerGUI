TOOL=AppSeer
TOOL_BIN=$(TOOL)/bin
TOOL_SRC=$(TOOL)/src/appseer
CONFIG=config.cfg

install:
	mkdir $(TOOL_BIN)
	javac -d $(TOOL_BIN) $(TOOL_SRC)/*.java

config:
	@while true ; do \
		echo "Enter the location for OUTPUT_DIR: "; \
		read line; \
		if [ -d $$line ]; then \
			echo "OUTPUT_DIR=\"$$line\"" >> config.cfg; \
			break ; \
		else \
			echo "Invalid location."; \
		fi ; \
	done 
	@while true ; do \
		echo "Enter the location for JADX_DIR: "; \
		read line; \
		if [ -d $$line ]; then \
			echo "JADX_DIR=\"$$line\"" >> config.cfg; \
			break ; \
		else \
			echo "Invalid location."; \
		fi ; \
	done  
	@while true ; do \
		echo "Enter the location for JADX_OUT: "; \
		read line; \
		if [ -d $$line ]; then \
			echo "JADX_OUT=\"$$line\"" >> config.cfg; \
			break ; \
		else \
			echo "Invalid location."; \
		fi ; \
	done
	@while true ; do \
		echo "Enter the location for APKTOOL_DIR: "; \
		read line; \
		if [ -d $$line ]; then \
			echo "APKTOOL_DIR=\"$$line\"" >> config.cfg; \
			break ; \
		else \
			echo "Invalid location."; \
		fi ; \
	done
	@while true ; do \
		echo "Enter the location for ADB_DIR: "; \
		read line; \
		if [ -d $$line ]; then \
			echo "ADB_DIR=\"$$line/adb\"" >> config.cfg; \
			break ; \
		else \
			echo "Invalid location."; \
		fi ; \
	done 
	@while true ; do \
		echo "Enter the location for SDK_ROOT: "; \
		read line; \
		if [ -d $$line ]; then \
			echo "SDK_ROOT=\"$$line\"" >> config.cfg; \
			break ; \
		else \
			echo "Invalid location."; \
		fi ; \
	done 
	@while true ; do \
		echo "Enter the location for ANDROID_ROOT: "; \
		read line; \
		if [ -d $$line ]; then \
			echo "ANDROID_ROOT=\"$$line\"" >> config.cfg; \
			break ; \
		else \
			echo "Invalid location."; \
		fi ; \
	done \

clean:
	rm -rf $(TOOL_BIN)
	rm -rf $(CONFIG)
