import decimal

level_size = 11
unit_hp = 15

num_cells = level_size * level_size
acum = decimal.Decimal(1)

for n in range(num_cells):
    for i in range(1, n):
        acum = acum * decimal.Decimal((num_cells - i + 1) * unit_hp)

scientific_notation = "{:e}".format(acum)
print(scientific_notation)