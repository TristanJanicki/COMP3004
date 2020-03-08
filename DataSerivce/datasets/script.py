f = open("currencyList.txt")
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