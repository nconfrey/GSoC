CC=gcc

JAVAHOME= "C:\Java"
FLAGS= -Wl,-add-stdcall-alias -D __int64="long long" -mno-cygwin
INCLUDE= -I"C:\Java\include" -I"C:\Java\include\win32"

hello: HelloJNI.c
	gcc-3 $(FLAGS) $(INCLUDE) -shared -o hello.dll HelloJNI.c