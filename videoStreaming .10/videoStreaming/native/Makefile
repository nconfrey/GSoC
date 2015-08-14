TARGET := libgstJNI.so
OBJS := gstJNI.o
CC := gcc
#Only going to compile on Linux for now, but this is nice to have
PLATFORM := $(shell uname -s)

CFLAGS := -std=c99 -fPIC -g

#For Linux only
CFLAGS += -I"/usr/lib/jvm/java-7-openjdk-amd64/include" 
CFLAGS += -I"/usr/lib/jvm/java-7-openjdk-amd64/include/linux"

LDFLAGS := -shared
#For Linux only
LDFLAGS += $(shell pkg-config gstreamer-1.0 --libs --cflags)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@

gstJNI.h

clean:
	rm -f $(TARGET) $(OBJS)