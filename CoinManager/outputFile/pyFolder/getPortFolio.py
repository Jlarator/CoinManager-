import os
import sys
import CoinbaseAuthentication
import coinbasepro
import requests

class Portfolio:
    def __init__(self, publicKey, secretKey, passKey):
        self.auth = CoinbaseAuthentication.CoinbaseExchangeAuth(publicKey, secretKey, passKey);
        self.entries = []
        self.apiUrl = 'https://api.pro.coinbase.com/'

        self.rq = requests.get(self.apiUrl + "accounts", auth = self.auth)

        self.response = self.rq.json()
        for coin in self.response:
            # coin will have the following fields:
            # id
            # currency
            # balance
            # hold
            # available
            # profile id
            # trading enabled

            entry = f"{coin['currency']}#{coin['balance']}#{coin['hold']}#" \
                    f"{coin['available']}#{coin['trading_enabled']}#{coin['id']}"
            print(entry)
            # self.entries.append(entry)

    def displayEntries(self):
        for entry in self.entries:
            print(entry)

if __name__ == '__main__':

    args = sys.argv

    if len(args) < 4:
        print("Inadequate Authentication arguments")
    else:
        publicKey = args[1]
        secretKey = args[2]
        passKey = args[3]

        portfolio = Portfolio(publicKey, secretKey, passKey)

