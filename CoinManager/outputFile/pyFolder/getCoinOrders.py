import sys
import coinbasepro

if __name__ == '__main__':

    args = sys.argv

    if len(args) < 5:
        print("Error: Not enough arguments")
    else:

        publicKey = args[1]
        secretKey = args[2]
        password = args[3]
        coinName = args[4]

        try:
            cpro = coinbasepro.AuthenticatedClient(publicKey, secretKey, password)
            orders = cpro.get_orders(coinName)

            for data in orders:
                side = data['side']
                price = data['price']
                size = data['size']
                orderID = data['id']

                entry = f"Side={side}#Price={price}#Size={size}#id={orderID}"
                print(entry)
                print("_=" * 10)
        except Exception as e:
            print(f"Error: {e}")