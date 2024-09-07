set cp=./buildconstrains/target/classes
D:
cd \Develop0\TestTeX\TransportNet
java -classpath %cp% buildconstrains.BuildConstrains graph0.csv 0 0
java -classpath %cp% buildconstrains.BuildConstrains graph1.csv 0 0
java -classpath %cp% buildconstrains.BuildConstrains graph11.csv 0 0
java -classpath %cp% buildconstrains.BuildConstrains graph2.csv 0 0
java -classpath %cp% buildconstrains.BuildConstrains graph21.csv 0 0
java -classpath %cp% buildconstrains.BuildConstrains graph3.csv 0 0
java -classpath %cp% buildconstrains.BuildConstrains graph4.csv 0 0
java -classpath %cp% buildconstrains.BuildConstrains graph41.csv 0 0

