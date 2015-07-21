CC=gcc

LIBS=-l "libgstreamer-0.10-0"
LIBPATH= -L "C:\gstreamer-sdk\0.10\x86_64\bin"
INCLUDE= -I "C:\gstreamer-sdk\0.10\x86_64\include\gstreamer-0.10" -I"C:\gstreamer-sdk\0.10\x86_64\include\glib-2.0" -I "C:\gstreamer-sdk\0.10\x86_64\lib\glib-2.0\include" -I"C:\gstreamer-sdk\0.10\x86_64\include\libxml2"

first: basic-tutorial-1.c
	gcc -o tut1 $(INCLUDE) $(LIBPATH) $(LIBS) basic-tutorial-1.c