#!/bin/bash
self="$0"
dir=`dirname "$self"`
path=`pwd $0`
if [ "$dir" == "." ]  
then
  export dir=`pwd $0`
else
  export dir=$path"/"$dir	
fi
export PREFIX=`cd "$dir/.." && pwd`
cd "$PREFIX"/bin

if [[ "$#" -eq 2  && "$1" == "-f" ]]; then
	if [ -f $2 ] ;then
		data="{\"job\":\"$2\"}"
		echo "Running..."
		content=$(curl -s -X POST localhost:8089/runjob -H "Content-type: application/json" -d $data )
		echo "Finished !"
		if [ "$content" != "" ] ;then
			echo "------------------------"
			echo "Job result: "
			echo -e $content | sed 's/\\//g'
		else
			echo -e $content | sed 's/\\//g'
			echo "Error occurred ! Please check wiperdog log or console output !"
		fi
	else
		echo "Job file does not exists ! : $2 "
	fi
else
	 echo           Incorrect parameters!
	 echo			Example:
	 echo			jobrunner -f var/job/testjob.job
fi

