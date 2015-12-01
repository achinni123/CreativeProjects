## AI2 data
if [ "$#" -eq 0 ]
then echo "Usage : sh run.sh dataset(AI2/IL/CC) module(optional)(Rel/Pair/Order/Cons)"
    exit 2
fi

if [ "$1" = "AI2" ]
then
    echo "Running on AI2 datset"
    if [ "$2" = "" -o "$2" = "Rel" ]
    then
	echo "Running RelDriver"
	java -cp target/classes/:target/dependency/* relevance.RelDriver crossVal true AI2 1>log/AI2Rel.out 
    fi
    if [ "$2" = "" -o "$2" == "Pair" ] 
    then
	echo "Running PairDriver"
	java -cp target/classes/:target/dependency/* pair.PairDriver crossVal true AI2 1>log/AI2Pair.out 
    fi
#    if [ "$2" == "" -o "$2" == "Cons" ]
#    then
#	echo "Running ConsDriverCV"
#	java -cp target/classes/:target/dependency/* constraints.ConsDriver crossVal AI2 1>log/AI2Cons.out
#    fi    
    if [ "$2" == "" -o "$2" == "Tune" ]
    then
	echo "Running ConsDriverTune"
	java -cp target/classes/:target/dependency/* constraints.ConsDriver tunedCrossVal AI2 1>log/AI2Tune.out 
    fi
fi

## IL data
if [ "$1" == "IL" ]
then
    echo "Running on IL datset"
    if [ "$2" == "" -o "$2" == "Rel" ]
    then
	echo "Running RelDriver"
	java -cp target/classes/:target/dependency/* relevance.RelDriver crossVal true IL 1>log/ILRel.out 
    fi
    if [ "$2" == "" -o "$2" == "Pair" ] 
    then
	echo "Running PairDriver"
	java -cp target/classes/:target/dependency/* pair.PairDriver crossVal true IL 1>log/ILPair.out 
    fi
#    if [ "$2" == "" -o "$2" == "Cons" ]
#    then
#	echo "Running ConsDriverCV"
#	java -cp target/classes/:target/dependency/* constraints.ConsDriver crossVal IL 1>log/ILCons.out
#    fi
    if [ "$2" == "" -o "$2" == "Tune" ]
    then
	echo "Running ConsDriverTune"
	java -cp target/classes/:target/dependency/* constraints.ConsDriver tunedCrossVal IL 1>log/ILTune.out 
    fi
fi

## CC data
if [ "$1" = "CC" ]
then
    echo "Running on CC datset"
    if [ "$2" == "" -o "$2" == "Pair" ]
    then
	echo "Running PairDriver"
	java -cp target/classes/:target/dependency/* pair.PairDriver crossVal true CC 1>log/CCPair.out 
    fi
#    if [ "$2" == "" -o "$2" == "Cons" ]
#    then
#	echo "Running ConsDriverCV"
#	java -cp target/classes/:target/dependency/* constraints.CCConsDriver crossVal 1>log/CCCons.out
#    fi
    if [ "$2" == "" -o "$2" == "Tune" ]
    then
	echo "Running ConsDriverTune"
	java -cp target/classes/:target/dependency/* constraints.CCConsDriver tunedCrossVal 1>log/CCTune.out 
    fi
fi
