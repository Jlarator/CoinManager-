import requests.exceptions
from CoinbaseAuthentication import CoinbaseExchangeAuth
import coinbasepro
import coinbase
from coinbase.wallet.client import Client
from ErrorMessage import ErrorMessage
import sys


class marketMenu:
    def __init__(self, publicKey, secretKey, passphrase):
        self.cproClient = coinbasepro.AuthenticatedClient(secretKey, publicKey, passphrase)
        self.baseClient = Client(secretKey, publicKey)

        currencies = self.cproClient.get_currencies()
        exchageRates = self.baseClient.get_exchange_rates(currency="USD")['rates']

        self.market = {}

        for currency in currencies:

            id = currency['id']
            name = currency['name']

            try:

                coinValue = 1 / float(exchageRates[id])
                print(f"{id}#{name}#{coinValue}")
                entry = {'#': id, '#': name, '#': coinValue}
                self.market.update(entry)
            except KeyError as keyError:
                pass

if __name__ == '__main__':

    args = sys.argv

    if len(args) < 4:
        print("Coinbase price index needs:\n\tAPI key\n\tAPI secret key")
    else:

        API_Public = args[1]
        API_Secret = args[2]
        password = args[3]
        try:
            marketMenu(API_Public, API_Secret, password)
        except requests.exceptions.ConnectionError as error:
            error = str(error).split()

            error_string = ''
            for section in error:
                if section[-1] == ':':
                    error_string += section + '\n'
                else:
                    error_string += section

            ErrorMessage(error_string)
            sys.exit()

        # coinAuth = CoinbaseExchangeAuth(API_Public, API_Secret, password)

        # api_url = "https://api.exchange.coinbase.com/products/stats"