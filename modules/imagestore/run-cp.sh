CLASSPATH=`pwd`/build/imagestore.jar
for jar in lib/*.jar; do
    CLASSPATH=${CLASSPATH}:`pwd`/$jar
done

export CLASSPATH
$@