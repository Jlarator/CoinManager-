import coinbasepro
import CoinbaseAuthentication
import sys

class coinTrades:
    def __init__(self, publicKey, secretKey, passPhrase, coinName):
        self.coinName = coinName
        self.trades = None
        self.auth = CoinbaseAuthentication.CoinbaseExchangeAuth(publicKey, secretKey, passPhrase)
        self.cpro = coinbasepro.AuthenticatedClient(publicKey, secretKey, passKey)

    def displayTrades(self):
        self.trades = self.cpro.get_fills(self.coinName)
        for trade in self.trades:
            date = f"{trade['created_at'].year}/{trade['created_at'].month}/{trade['created_at'].day}" + \
                   f" {trade['created_at'].hour}:{trade['created_at'].minute}:{trade['created_at'].second}" \
                                  # + \ f".{str(trade['created_at'].microsecond)[:4:]}"
            trade['created_at'] = date
            print(trade)


if __name__ == '__main__':
    args = sys.argv

    if len(args) < 5 :
        print("Number of arguments is innadequate:\nNeed CoinName publicKey secretKey passKey")
    else:
        publicKey = args[1]
        secretKey = args[2]
        passKey = args[3]
        coinName = args[4]
        coinTrades(publicKey, secretKey, passKey, coinName)
        coinTrades.displayTrades()
