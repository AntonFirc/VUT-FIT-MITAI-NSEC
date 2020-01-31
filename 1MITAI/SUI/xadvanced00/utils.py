import numpy
from dicewars.client.game.board import Board


def probability_of_successful_attack(board, atk_area, target_area):
    """Calculate probability of attack success

    Parameters
    ----------
    board : Board
    atk_area : int
    target_area : int

    Returns
    -------
    float
        Calculated probability

    """
    atk = board.get_area(atk_area)
    target = board.get_area(target_area)
    atk_power = atk.get_dice()
    def_power = target.get_dice()
    return attack_succcess_probability(atk_power, def_power)


def attack_succcess_probability(atk, df):
    """Dictionary with pre-calculated probabilities for each combination of dice

    Parameters
    ----------
    atk : int
        Number of dice the attacker has
    df : int
        Number of dice the defender has

    Returns
    -------
    float

    """
    return {
        2: {
            1: 0.83796296,
            2: 0.44367284,
            3: 0.15200617,
            4: 0.03587963,
            5: 0.00610497,
            6: 0.00076625,
            7: 0.00007095,
            8: 0.00000473,
        },
        3: {
            1: 0.97299383,
            2: 0.77854938,
            3: 0.45357510,
            4: 0.19170096,
            5: 0.06071269,
            6: 0.01487860,
            7: 0.00288998,
            8: 0.00045192,
        },
        4: {
            1: 0.99729938,
            2: 0.93923611,
            3: 0.74283050,
            4: 0.45952825,
            5: 0.22044235,
            6: 0.08342284,
            7: 0.02544975,
            8: 0.00637948,
        },
        5: {
            1: 0.99984997,
            2: 0.98794010,
            3: 0.90934714,
            4: 0.71807842,
            5: 0.46365360,
            6: 0.24244910,
            7: 0.10362599,
            8: 0.03674187,
        },
        6: {
            1: 0.99999643,
            2: 0.99821685,
            3: 0.97529981,
            4: 0.88395347,
            5: 0.69961639,
            6: 0.46673060,
            7: 0.25998382,
            8: 0.12150697,
        },
        7: {
            1: 1.00000000,
            2: 0.99980134,
            3: 0.99466336,
            4: 0.96153588,
            5: 0.86237652,
            6: 0.68516499,
            7: 0.46913917,
            8: 0.27437553,
        },
        8: {
            1: 1.00000000,
            2: 0.99998345,
            3: 0.99906917,
            4: 0.98953404,
            5: 0.94773146,
            6: 0.84387382,
            7: 0.67345564,
            8: 0.47109073,
        },
    }[atk][df]


def possible_attacks(board, player_name):
    """Returns list of possible attacks of player

    Attributes
    ----------
    board : board
    player_name : int

    Returns
    -------
    list

    """
    for area in board.get_player_border(player_name):
        if not area.can_attack():
            continue

        neighbours = area.get_adjacent_areas()

        for adj in neighbours:
            adjacent_area = board.get_area(adj)
            if adjacent_area.get_owner_name() != player_name:
                yield area, adjacent_area


def get_largest_region(board, player_name):
    """Get size of the largest region, including the areas within

    Attributes
    ----------
    board : board
    player_name : int

    Returns
    -------
    int
        Number of areas in the largest region

    """
    largest_region = []

    players_regions = board.get_players_regions(player_name)
    max_region_size = max(len(region) for region in players_regions)
    max_sized_regions = [region for region in players_regions if len(region) == max_region_size]

    for region in max_sized_regions:
        for area in region:
            largest_region.append(area)
    return max_region_size


def get_weak_lands(bot_board, player_name):
    """get number of weak areas to enemies

    Attributes
    ----------
    bot_board : board
    player_name : int

    Returns
    -------
    int
        Number of weak areas

    """
    weak_areas = []

    for area in bot_board.get_player_border(player_name):
        neighbours = area.get_adjacent_areas()

        for adj in neighbours:
            adjacent_area = bot_board.get_area(adj)
            if adjacent_area.get_owner_name() != player_name:
                if adjacent_area.get_dice() > area.get_dice() + 1:
                    weak_areas.append(area)
                    break

    return len(weak_areas)


def easy_targets(bot_board, player_name):
    """get number of weak areas to enemies

    Attributes
    ----------
    bot_board : board
    player_name : int

    Returns
    -------
    int
        Number of weak areas

    """
    targets = 0

    attacks = possible_attacks(bot_board, player_name)
    for source, target in attacks:
        if source.get_dice() > target.get_dice() + 2:
            targets += 1

    return targets


def calc_board(bot_board, player_name):
    """get evaluation of the board

    Attributes
    ----------
    bot_board : board
    player_name : int

    Returns
    -------
    float
        Evaluation of the board

    """
    owned_fields = len(bot_board.get_player_areas(player_name))
    largest_region = get_largest_region(bot_board, player_name)

    weak_lands = get_weak_lands(bot_board, player_name)
    easy_tgt = easy_targets(bot_board, player_name)

    return numpy.log(0.3 * owned_fields + 0.35 * largest_region - 0.15 * weak_lands -
                     0.2 * (owned_fields - largest_region) + 0.3 * easy_tgt)
