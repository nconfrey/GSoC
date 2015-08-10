INCLUDELIN= -I"/usr/lib/jvm/java-7-openjdk-amd64/include" -I"/usr/lib/jvm/java-7-openjdk-amd64/include/linux"
CFLAGSLIN= -std=c99 -fPIC -g
LDFLAGS= -shared

#FLAGSLIN= -shared -fPIC
LIN= $(INCLUDELIN) $(CFLAGSLIN)

gstLin: gstJNI.c
	libtool --mode=link gcc `pkg-config --cflags --libs gstreamer-1.0` $(LIN) -o libgstJNI.so gstJNI.c

clean:
	rm gstJNI.h libgstJNI.so gstJNI.class