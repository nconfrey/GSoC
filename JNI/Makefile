CC=gcc

JAVAHOME= "C:\Java"
FLAGS= -Wl,-add-stdcall-alias -D __int64="long long"
INCLUDE= -I"C:\Java\include" -I"C:\Java\include\win32"

INCLUDELIN= -I"/usr/lib/jvm/java-7-openjdk-amd64/include" -I"/usr/lib/jvm/java-7-openjdk-amd64/include/linux"
FLAGSLIN= -shared -fpic
LIN= $(FLAGSLIN) $(INCLUDELIN)

helloWin: HelloJNI.c
	gcc $(FLAGS) $(INCLUDE) -shared -o hello.dll HelloJNI.c

helloLin: HelloJNI.c
	gcc $(LIN) -o libhello.so HelloJNI.c

gstLin: gstJNI.c
	libtool --mode=link gcc `pkg-config --cflags --libs gstreamer-1.0` $(LIN) -z noexecstack -c -o libgstJNI.so gstJNI.c