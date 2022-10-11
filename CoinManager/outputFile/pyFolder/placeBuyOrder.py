import sys
from CoinbaseAuthentication import CoinbaseExchangeAuth
import requests


if __name__ == '__main__':
    args = sys.argv

    if len(args) < 5:
        print("Buy order needs Authentication tokes plus order info")
    else:
        API_Public = args[1]
        API_Secret = args[2]
        password = args[3]

        orderInfo = args[4].split("|")
        product_id = orderInfo[0]
        stop = orderInfo[1]
        size = float(orderInfo[2])
        price = float(orderInfo[3])
        stop_price = float(orderInfo[4])

        auth = CoinbaseExchangeAuth(API_Public, API_Secret, password)

        url = "https://api.exchange.coinbase.com/orders"

        payload = {
            "profile_id": "default profile_id",
            "type": "limit",
            "side": "buy",
            "stp": "dc",
            "stop": stop,
            "time_in_force": "GTC",
            "cancel_after": "min",
            "post_only": "false",
            "size": size,
            "price": price,
            "stop_price": stop_price,
            "product_id": product_id
        }
        headers = {
            "Accept": "application/json",
            "Content-Type": "application/json"
        }

        response = requests.post(url, json=payload, headers=headers, auth=auth).json()

        if 'message' in response:
            print(response['message'])
        else:
            if 'created_at' in response:
                print("Order placed successfully")
