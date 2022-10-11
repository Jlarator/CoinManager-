import sys
from CoinbaseAuthentication import CoinbaseExchangeAuth
import requests


if __name__ == '__main__':
    args = sys.argv

    if len(args) < 4:
        print("Insufficient number of args")
    else:

        API_Public = args[1]
        API_secret = args[2]
        password = args[3]

        auth = CoinbaseExchangeAuth(API_Public, API_secret, password)

        api_url = "https://api.exchange.coinbase.com/products/stats"

        re = requests.get(api_url, auth =auth).json()

        for coin in re:
            if coin.endswith("-USD"):
                rate = re[coin]['stats_24hour']['last']
                coin = coin.split("-")[0]
                print(f"{coin}#{rate}")

