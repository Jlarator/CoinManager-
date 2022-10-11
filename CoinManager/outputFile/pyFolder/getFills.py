import coinbasepro
import sys
import tkinter as tk


def getFills(cproAuth, coinName):
    coinFills = cproAuth.get_fills(coinName)
    record = []
    output = []
    for item in coinFills:
        record.append(item)
    record.reverse()

    # base_volume = coinName.split("-")[1].lower() + "_volume"
    base_volume = "usd_volume"
    try:
        recordSize = len(record)
        index = 0

        lowestFound = False
        minSize = 0
        runningVal = 0
        while(index < recordSize):

            data = record[index]
            price = data["price"]
            date = f"{data['created_at'].year}/{data['created_at'].month}/{data['created_at'].day}" + \
                   f" {data['created_at'].hour}:{data['created_at'].minute}:{data['created_at'].second}"
            size = data["size"]
            fee = data["fee"]
            side = data["side"]
            volume = data[base_volume]
            postFee = volume

            if side == "sell":
                fee *= -1
            postFee += fee
            #print(f"\n Volume: {volume}\nfee: {fee}\nPost fee: {postFee}")
            revaluated = postFee / size
            currentSide = side
            nextData = index + 1
            currentIndex = index

            sizes = []
            amount = 0
            value = 0
            while not lowestFound and currentIndex < recordSize:
                sizes.append(record[currentIndex]['size'])
                minSize = min(sizes)

                if record[currentIndex-1]['side'] != record[currentIndex]['side'] and currentIndex > 0:

                    currentFee = record[currentIndex]['fee']
                    if record[currentIndex]['side'] =='sell': currentFee *= -1

                    if record[currentIndex]['size'] > amount:
                        thisUnitValue = (record[currentIndex][base_volume] + currentFee) / record[currentIndex]['size']
                        thisSide = amount * thisUnitValue
                        #print(f"amount: {amount}, fee: {currentFee} ")
                        if record[currentIndex]['side'] == 'sell':
                            value *= -1
                        else:
                            thisSide *= -1
                        #print(f"This: {thisSide} other Val: {value}")
                        gain = thisSide + value
                    else:
                        unitValue = value / amount
                        otherSide = record[currentIndex]['size'] * unitValue
                        thisSide = (record[currentIndex][base_volume] + currentFee)
                        if record[currentIndex]['side'] == 'sell':
                            otherSide *= -1
                        else:
                            thisSide *= -1
                            #print(f"This: {thisSide} other: {otherSide}")
                        gain = otherSide + thisSide

                    lowestFound = True
                    break

                amount += record[currentIndex]['size']
                currentFee = record[currentIndex]['fee']
                if record[currentIndex]['side'] == 'sell': currentFee *= -1
                value += record[currentIndex][base_volume] + currentFee
                currentIndex += 1

            balanceCost = (price * minSize) + fee

            if currentSide == "buy":
                runningVal -= balanceCost
            else:
                runningVal += balanceCost

            prevData = index - 1
            if record[prevData]['side'] != currentSide and index > 0:

                entry = f"Date={date}#Price={price}#Size={size}#Fee={abs(fee)}" \
                        f"#Side={side}#Volume={volume}" \
                        f"#PostFee={postFee}#Revaluated={revaluated}" \
                        f"#BalancedVolume={balanceCost}"\
                        f"#GainLoss={gain}"
                lowestFound = False
                if(index + 1) < recordSize:
                    record[index]['side'] = record[index + 1]['side']

                currentSide = record
                runningVal = 0
                output.append(entry)
                empty = "#=" * 10
                output.append(empty)
                # print(entry)
                # print("\n")
            else:
                entry = f"Date={date}#Price={price}#Size={size}#Fee={abs(fee)}" \
                        f"#Side={side}#Volume={volume}" \
                        f"#PostFee={postFee}#Revaluated={revaluated}" \
                        f"#BalancedVolume={balanceCost}#GainLoss="

                output.append(entry)
                #print(entry)
            index += 1

        return output
    except Exception as e:
        print(f"Error: {e}")

if __name__ == '__main__':

    args = sys.argv
    if(len(args) < 5):
        print("Error: Get Fills needs: public key, secret key, password, and coin to get fills from")
    else:
        public_key = args[1]
        secret_key = args[2]
        password = args[3]
        coin_name = args[4]
        try:
            cpro = coinbasepro.AuthenticatedClient( public_key,secret_key, password)
            record = getFills(cpro, coin_name)

            for i in record:
                print(i)
        except Exception as e:
            print(f"Error: {e}")