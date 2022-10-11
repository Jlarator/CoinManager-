from CoinbaseAuthentication import CoinbaseExchangeAuth
import sys
import requests


if __name__ == '__main__':

    args = sys.argv

    if(len(args) < 5):
        print("Inadequate number of args")
    else:

        publicKey = args[1]
        secretKey = args[2]
        passKey = args[3]
        coinId = args[4]
        auth = CoinbaseExchangeAuth(publicKey, secretKey, passKey)

        api_url = "https://api.exchange.coinbase.com/products/" + coinId + "/stats"

        re = requests.get(api_url, auth=auth).json()

        # ex. ouput:
        # {'open': '1679.31',
        #  'high': '1705',
        #  'low': '1606.9',
        #  'last': '1635.75',
        #  'volume': '173133.48669108',
        #  'volume_30day': '8839217.12802718'}

        output = f"{re['open']}#{re['high']}#{re['low']}#{re['last']}"

        print(output)

