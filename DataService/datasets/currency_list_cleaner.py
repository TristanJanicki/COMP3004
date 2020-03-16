f = open("currencyList.txt") # about 169 currencies in this list, (169 ^ 2) - 169 combos, 28,392
d = f.read().split("\n")

currencies = []

for r in d:
    if r not in currencies:
        currencies.append(r)

f.close()

f = open("currencyList.txt", "w")
for c in currencies:
    f.write(c + "\n")
f.flush()
f.close()