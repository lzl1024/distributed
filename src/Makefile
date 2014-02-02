Configfilename = /afs/andrew.cmu.edu/usr5/zhuolinl/18842/Lab0.yaml
Name = alice

all:	
	javac -cp .:lib/snakeyaml-1.13.jar clock/*.java logging/*.java \
	message/*.java record/*.java thread/*.java util/*.java  

run:	all
	java -cp .:lib/snakeyaml-1.13.jar message.MessagePasser ${Configfilename} ${Name}

log:    all	
	java -cp .:lib/snakeyaml-1.13.jar logging.Logger ${Configfilename} Logger

clean:
	find . -name *.class | xargs rm -rf >> a.out
	find -name *.class | xargs rm -rf >> a.out
	rm a.out
