<<<<<<< HEAD
CC=gcc

JAVAHOME= "C:\Java"
FLAGS= -Wl,-add-stdcall-alias -D __int64="long long"
INCLUDE= -I"C:\Java\include" -I"C:\Java\include\win32"

INCLUDELIN= -I"/usr/lib/jvm/java-7-openjdk-amd64/include" -I"/usr/lib/jvm/java-7-openjdk-amd64/include/linux"
FLAGSLIN= -Wl,--add-stdcall-alias

helloWin: HelloJNI.c
	gcc $(FLAGS) $(INCLUDE) -shared -o hello.dll HelloJNI.c

helloLin: HelloJNI.c
	gcc $(FLAGSLIN) $(INCLUDELIN) -shared -o libhello.so HelloJNI.c
=======
INCLUDELIN= -I"/usr/lib/jvm/java-7-openjdk-amd64/include" -I"/usr/lib/jvm/java-7-openjdk-amd64/include/linux"
CFLAGSLIN= -std=c99 -fPIC -g
LDFLAGS= -shared

#FLAGSLIN= -shared -fPIC
LIN= $(INCLUDELIN) $(CFLAGSLIN)

gstLin: gstJNI.c
	libtool --mode=link gcc `pkg-config --cflags --libs gstreamer-1.0` $(LIN) -o libgstJNI.so gstJNI.c

compile:
	javac gstJNI.java
	javah gstJNI
	make gstLin

clean:
	rm gstJNI.h libgstJNI.so gstJNI.class
>>>>>>> b121986fc37f10eeb9eadbc77dde23132ef44548
