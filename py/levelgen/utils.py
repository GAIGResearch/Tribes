def circle(center, radius, map_size):
    _circle = []
    row = center // map_size
    column = center % map_size
    i = row - radius
    if 0 <= i < map_size:
        for j in range(column - radius, column + radius):
            if 0 <= j < map_size:
                _circle.append(i * map_size + j)
    i = row + radius
    if 0 <= i < map_size:
        for j in range(column + radius, column - radius, -1):
            if 0 <= j < map_size:
                _circle.append(i * map_size + j)
    j = column - radius
    if 0 <= j < map_size:
        for i in range(row + radius, row - radius, -1):
            if 0 <= i < map_size:
                _circle.append(i * map_size + j)
    j = column + radius
    if 0 <= j < map_size:
        for i in range(row - radius, row + radius):
            if 0 <= i < map_size:
                _circle.append(i * map_size + j)
    return _circle


def round_(center, radius, map_size):
    _round = []
    for r in range(1, radius + 1):
        _round = _round + circle(center, r, map_size)
    _round.append(center)
    return _round


def distance(a, b, size):
    ax = a % size
    ay = a // size
    bx = b % size
    by = b // size
    return max(abs(ax - bx), abs(ay - by))


def plus_sign(center, map_size):
    _plus_sign = []
    row = center // map_size
    column = center % map_size
    if column > 0:
        _plus_sign.append(center - 1)
    if column < map_size - 1:
        _plus_sign.append(center + 1)
    if row > 0:
        _plus_sign.append(center - map_size)
    if row < map_size - 1:
        _plus_sign.append(center + map_size)
    return _plus_sign
