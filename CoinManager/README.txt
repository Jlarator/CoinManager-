This is my first Project uploaded to Github, It is an interface for viewing and placing trades
using Coinbase pro's API keys.

      The main reason why I made this was to analyze how my trading
strategy was working; it takes into account trading fees, these always weigh negatively in each
trade.

	 It is based on what i call "buy/sell" clusters; If you have multiple buys (or sales), the
next sale that you make will be added to this cluster and mark the end of the current cluster. The cluster will be judged on how that sale
performs against the previous set of buys. The calculation is compared using the lowest ammount
of coins of either sales or buys.

	Let's say that you have a cluster of 4 individual buys, and in each sale you sold 3 coins. The
total amount of coins bought in this cluster will be 4. Now, if your next trade is a sale of 10
coins, the cluster will use the lowest amount of coin traded per side to judge the cluster
(12 bought - 10 sold : 10 is the amount to be used).In this case it will calculate the how much
money you spent buying 10 coins, then it will compare that to the money you made by selling the 10
coins, the difference will be your gain/loss.

       I like this method because allows me to compare coin-per-coin trades, I think this is a more
accurate metric for how my trades are.


	 If you don't like this method, you can also refer to the average-buy and average-sell
metrics to the left of the screen. I find these to be very helpfull as well because.it allows me to
judge trades based on my trading history, not the market or someone else's. 

   	 


to compile:
   javac *.java -d [destination folder for .class files]


example:
	javac *.java -d outputFile

If you choose a different destination for you .class files, make sure that the pyFolder contaning the .py scripts is also moved or copied into that directory. Don't forget to add
execution permissions accordingly to the .py files. Otherwise the project won't work.
this is what the file structure should look like if you wish to compile with the above commmand. 

.
├── checkedBox.piko
├── CoinMngrFrontEnd.java
├── CoinTrades.java
├── element-list
├── EntryMenu.java
├── Market.java
├── outputFile
│   └── pyFolder
├── panelMaker
├── Portfolio.java
├── PyCaller.java
├── #README.txt#
├── resources
│   ├── glass.png
│   └── x.png
├── searchTree.java
├── uncheckedBox.piko
└── Window.java

To run the project. From the outputFile directory, use:

   java com.packages.CoinMngrFrontEnd

