CC=gcc

LIBS=-l "libgstreamer-0.10-0"
LIBPATH= -L "C:\gstreamer-sdk\0.10\x86_64\bin"
INCLUDE= -I "C:\gstreamer-sdk\0.10\x86_64\include\gstreamer-0.10" -I"C:\gstreamer-sdk\0.10\x86_64\include\glib-2.0" -I "C:\gstreamer-sdk\0.10\x86_64\lib\glib-2.0\include" -I"C:\gstreamer-sdk\0.10\x86_64\include\libxml2"

LIBS0= -l"libgstreamer-1.0-0"
LIBPATH0= -L "C:\gstreamer\1.0\x86_64\bin"
INCLUDE0= -I "C:\gstreamer\1.0\x86_64\include\gstreamer-1.0" -I"C:\gstreamer\1.0\x86_64\include\glib-2.0" -I "C:\gstreamer\1.0\x86_64\lib\glib-2.0\include" -I"C:\gstreamer\1.0\x86_64\include\libxml2" -I"C:\gstreamer\1.0\x86_64\lib\gstreamer-1.0\include"

first: basic-tutorial-1.c
	gcc -o tut1 $(INCLUDE) $(LIBPATH) $(LIBS) basic-tutorial-1.c

server: basic-tutorial-1.c
	gcc -o server $(LIBPATH0) $(LIBS0) $(INCLUDE0) basic-tutorial-1.c

client: client.c
	gcc -o client $(LIBPATH0) $(LIBS0) $(INCLUDE0) client.c