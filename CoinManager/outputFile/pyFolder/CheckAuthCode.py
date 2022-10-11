from coinbase.wallet.client import Client
from ErrorMessage import ErrorMessage
import requests
import sys
from CoinbaseAuthentication import  CoinbaseExchangeAuth as CBAuth


api_url = 'https://api.pro.coinbase.com/'

if __name__ == '__main__':
    args = sys.argv
    
    if len(args) < 4:
        print("you need to pass 3 arguments to this function")
    else:
        api_key = args[1]
        secret_key = args[2]
        passphrase = args[3]

        output = f"public: {api_key}, secret {secret_key}, pass: {passphrase}"
        coinAuth = CBAuth(api_key, secret_key,  passphrase)

        re = requests.get(api_url + 'accounts', auth=coinAuth)

        print(re.status_code)


