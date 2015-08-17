TARGET := libstreamvideo.so
OBJS := impl.o
CC := gcc `pkg-config gstreamer-1.0 --cflags`
PLATFORM := $(shell uname -s)


CFLAGS := -std=c99 -fPIC -g
ifeq ($(PLATFORM),Linux)
	# XXX: derive from javac location
	CFLAGS += -I"/usr/lib/jvm/java-7-openjdk-amd64/include" 
	CFLAGS += -I"/usr/lib/jvm/java-7-openjdk-amd64/include/linux"
endif
ifeq ($(PLATFORM),Darwin)
#	CFLAGS += -arch i386 -arch x86_64
	CFLAGS += -arch x86_64
	CFLAGS += -I/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/include
	CFLAGS += -I/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/include/darwin
endif
# XXX: still needs gstreamer headers installed on OS X (e.g. with "brew install gstreamer")
#CFLAGS += $(shell pkg-config gstreamer-1.0 --cflags-only-I)
#CFLAGS += -I/Library/Frameworks/GStreamer.framework/Headers

#Uncomment defs here to check that everything is linked properly
LDFLAGS := -shared #-z defs
ifeq ($(PLATFORM),Linux)
	LDFLAGS += -lgstreamer-1.0 -lgobject-2.0 -lglib-2.0 -pthread -lgstapp-1.0
endif
ifeq ($(PLATFORM),Darwin)
#	LDFLAGS += -arch i386 -arch x86_64
	LDFLAGS += -arch x86_64
#	LDFLAGS += -L../../library/macosx
    LDFLAGS += -L/Library/Frameworks/GStreamer.framework/Libraries
    LDFLAGS += -L/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/server/ -framework JavaVM
    
	LDFLAGS += -lgstreamer-1.0.0 -lgstapp-1.0.0 -lglib-2.0.0 -lgobject-2.0.0
endif


$(TARGET): $(OBJS)
	$(CC) $^ -o $@ $(CFLAGS) $(LDFLAGS)

iface.h:
	javah -classpath .. -o iface.h processing.streaming.SimpleVideo

clean:
	rm -f $(TARGET) $(OBJS)
