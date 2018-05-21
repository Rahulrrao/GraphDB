JDKPATH =/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/home
LIBPATH = /p/course/cs784-raghu/minibase_share/javaminibase/construction
CLASSPATH = $(JDKPATH)/lib/classes.zip:.:..:$(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac -classpath $(CLASSPATH)
JAVA  = $(JDKPATH)/bin/java  -classpath $(CLASSPATH)

PROGS = together

all: $(PROGS)

hf:Heapfile.java
	$(JAVAC) NodeHeapfile.java

hp:HFPage.java
	$(JAVAC) NHFPage.java

tuple:Tuple.java
	$(JAVAC) Node.java

scan:Scan.java
	$(JAVAC) NScan.java

together:*.java
	$(JAVAC) *.java

clean:
	\rm -f *.class *~ \#* core 
