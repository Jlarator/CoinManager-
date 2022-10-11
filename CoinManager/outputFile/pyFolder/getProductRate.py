import sys
import CoinbaseAuthentication
import requests


if __name__ == '__main__':

    args = sys.argv


    if(len(args) < 5):
        print("Inadequate number of arguments")
    else:

        publicKey = args[1]
        secretKey = args[2]
        passKey = args[3]
        coinId = args[4]
        auth = CoinbaseAuthentication.CoinbaseExchangeAuth(publicKey, secretKey, passKey)
        api_url = "https://api.exchange.coinbase.com/accounts/"

        response = requests.get(api_url + coinId, auth=auth)

        re = response.json()

        # Example response
        # {'id': 'e1de3daf-131e-44b5-8664-13583d2c2395',
        #  'currency': '1INCH',
        #  'balance': '0.0000000000000000',
        #  'hold': '0.0000000000000000',
        #  'available': '0',
        #  'profile_id': '746880f8-66fc-43e9-9a75-51fbc78bf757',
        #  'trading_enabled': True}

        output = f"{re['balance']}#{re['hold']}#{re['available']}#{re['trading_enabled']}"

        print(output)