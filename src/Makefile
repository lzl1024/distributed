Configfilename = ../Lab0.yaml
Local_name = alice

all:	
	javac -cp .:lib/snakeyaml-1.13.jar message/*.java record/*.java io/*.java thread/*.java \
	util/*.java

run:	all
	java -cp .:lib/snakeyaml-1.13.jar message.MessagePasser ${Configfilename} ${Local_name}	

clean:
	find . -name *.class | xargs rm -rf >> a.out
	find -name *.class | xargs rm -rf >> a.out
	rm a.out
