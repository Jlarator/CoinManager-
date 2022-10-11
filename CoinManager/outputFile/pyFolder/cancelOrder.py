import sys
import coinbasepro
import coinbasepro.exceptions

if __name__ == '__main__':


    args = sys.argv

    if(len(args) < 4):
        print("To cancel an order you need authentication info plus order id")
    else:

        API_public = args[1]
        API_secret = args[2]
        password = args[3]

        orderID = args[4]

        cpro = coinbasepro.AuthenticatedClient(API_public, API_secret, password)

        try:
            response = cpro.cancel_order(orderID)
            print(f"Order {orderID} cancelled succesfully")
        except coinbasepro.exceptions.CoinbaseAPIError as error:
            print(error)

