import CoinbaseAuthentication
import CoinbasePriceIndex
import getFills
from CoinbaseAuthentication import CoinbaseExchangeAuth as CBAuth
import coinbasepro
import coinbase
import requests
from getFills import getFills


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    api_url = 'https://api.pro.coinbase.com/'


    coinAuth = CBAuth(APIkey, APIsecret, psswd)
    cpro = coinbasepro.AuthenticatedClient(APIkey, APIsecret, psswd)
    getFills(cpro, "MATIC-USD")


