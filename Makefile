CC=gcc

#Non labeled are windows, appended "Lin" or "L" indicate linux builds

LIBS=-l "libgstreamer-0.10-0"
LIBPATH= -L "C:\gstreamer-sdk\0.10\x86_64\bin"
INCLUDE= -I "C:\gstreamer-sdk\0.10\x86_64\include\gstreamer-0.10" -I"C:\gstreamer-sdk\0.10\x86_64\include\glib-2.0" -I "C:\gstreamer-sdk\0.10\x86_64\lib\glib-2.0\include" -I"C:\gstreamer-sdk\0.10\x86_64\include\libxml2"

LIBS0= -l"libgstreamer-1.0-0"
LIBPATH0= -L "C:\gstreamer\1.0\x86_64\bin"
INCLUDE0= -I "C:\gstreamer\1.0\x86_64\include\gstreamer-1.0" -I"C:\gstreamer\1.0\x86_64\include\glib-2.0" -I "C:\gstreamer\1.0\x86_64\lib\glib-2.0\include" -I"C:\gstreamer\1.0\x86_64\include\libxml2" -I"C:\gstreamer\1.0\x86_64\lib\gstreamer-1.0\include"
INCLUDE1= -I "C:\gstreamer\1.0\x86_64\include\gstreamer-1.0" -I"C:\gstreamer\1.0\x86_64\include\glib-2.0" -I "C:\gstreamer\1.0\x86_64\lib\glib-2.0\include" -I"C:\gstreamer\1.0\x86_64\lib\gstreamer-1.0\include"

#For Linux
LIBSL=-l "libgstreamer-1.0.so.0"
LIBPATHL= -L "/usr/lib/x86_64-linux-gnu"
INCLUDEL= -I "/usr/lib/x86_64-linux-gnu/gstreamer-1.0"

server: basic-tutorial-1.c
	gcc -o server $(LIBPATH0) $(LIBS0) $(INCLUDE1) basic-tutorial-1.c

client: client.c
	gcc -o client $(LIBPATH0) $(LIBS0) $(INCLUDE0) client.c

#For Linux
serverLin: basic-tutorial-1.c
	libtool --mode=link gcc `pkg-config --cflags --libs gstreamer-1.0` -o server basic-tutorial-1.c

clientLin: client.c
	libtool --mode=link gcc `pkg-config --cflags --libs gstreamer-1.0` -o client client.c

clean:
	rm server client	