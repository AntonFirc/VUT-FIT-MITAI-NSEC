import copy
import logging

from .utils import probability_of_successful_attack, calc_board
from .utils import possible_attacks

from dicewars.client.ai_driver import BattleCommand, EndTurnCommand


class FinalAI:
    """Agent using ExpectiMiniMax algorithm and evaluation of the board to calculate
    best moves for winning the game

    Authors
    -------
    Tomas Lapsansky
    Anton Firc
    Jakub Filo

    """

    def __init__(self, player_name, board, players_order):
        """
        Parameters
        ----------
        player_name : int
        board : board
        players_order : list

        """
        self.player_name = player_name
        self.logger = logging.getLogger('AI')

        self.players_order = players_order
        while self.player_name != self.players_order[0]:
            self.players_order.append(self.players_order.pop(0))

        nb_players = board.nb_players_alive()
        self.logger.info('Setting up for {}-player game'.format(nb_players))

        self.largest_region = []

    def ai_turn(self, board, nb_moves_this_turn, nb_turns_this_game, time_left):
        """AI agent's turn

        AI gets list of preferred moves and plays the best, or if the list is empty,
        ends turn

        """
        self.board = board
        turns = self.possible_turns(self.board, self.player_name)

        if turns:
            turn = turns[0]
            self.logger.debug("Possible turn: {}".format(turn))
            self.logger.debug("{0}->{1} attack".format(turn[0], turn[1]))

            return BattleCommand(turn[0], turn[1])

        return EndTurnCommand()

    def possible_turns(self, board, player_name):
        """Gets list of possible turns with their evaluation

        Attributes
        ----------
        board : board
        player_name : int

        Returns
        -------
        list
            Sorted list of possible turns with their evaluation

        """
        turns = []
        for source, target in possible_attacks(board, player_name):
            preference = self.calculate_possible_turn(source, target)
            if preference is not None:
                turns.append([source.get_name(), target.get_name(), preference])

        return sorted(turns, key=lambda turn: turn[2], reverse=True)

    def simulate_turn(self, board, source, target):
        """Simulation of turn on given game board

        Attributes
        ----------
        board : board
        source : int
        target : int

        Returns
        -------
        board

        """
        src_name = source.get_name()
        src_tmp = board.get_area(src_name)
        tgt_name = target.get_name()
        tgt_tmp = board.get_area(tgt_name)
        tgt_tmp.set_owner(source.get_owner_name())
        tgt_tmp.set_dice(source.get_dice() - 1)
        src_tmp.set_dice(1)

        return board

    def calculate_possible_turn(self, source, target):
        """Calculate evaluation of possible move

        Simulate our move and find the best continuation for enemies to harass us,
        then gets evaluation of board after simulation fo moves

        Attributes
        ----------
        source : int
        target : int

        Returns
        -------
        float
            Evaluation of possible turn

        """
        if source.get_dice() == 1:
            return None
        if source.get_dice() <= target.get_dice() and source.get_dice() != 8:
            return None
        win_pst = probability_of_successful_attack(self.board, source.get_name(), target.get_name())
        if win_pst < 0.5 and source.get_dice() != 8:
            return None

        starting_val = calc_board(self.board, self.player_name)

        bot_board = copy.deepcopy(self.board)
        bot_board = self.simulate_turn(bot_board, source, target)

        # iterate over every player
        for player in self.players_order:
            if player != self.player_name:
                bot_turns = self.do_ai_turn(bot_board, player)
                if not bot_turns:
                    continue
                bot_src = bot_board.get_area(bot_turns[0][0])
                bot_tgt = bot_board.get_area(bot_turns[0][1])
                bot_board = self.simulate_turn(bot_board, bot_src, bot_tgt)

        finishing_val = calc_board(bot_board, self.player_name)
        return (finishing_val - starting_val) * win_pst

    def do_ai_turn(self, board, player_name):
        """Construct sorted list of enemy AI best possible turns with their evaluation

        Attributes
        ----------
        board : board
        player_name : int

        Returns
        -------
        list
            Sorted list of AI possible turns

        """
        turns = []

        our_value = calc_board(board, self.player_name)

        for source, target in possible_attacks(board, player_name):
            win_pst = probability_of_successful_attack(board, source.get_name(), target.get_name())
            if win_pst < 0.5 and source.get_dice() != 8:
                continue

            tmp_board = copy.deepcopy(board)
            tmp_board = self.simulate_turn(tmp_board, source, target)
            new_value = calc_board(tmp_board, self.player_name)

            turn_efficiency = our_value - new_value
            if turn_efficiency > 0:
                turns.append([source.get_name(), target.get_name(), turn_efficiency])

        return sorted(turns, key=lambda turn: turn[2], reverse=True)

    def get_largest_region(self):
        """Get size of the largest region, including the areas within

        Attributes
        ----------
        largest_region : list of int
            Names of areas in the largest region

        Returns
        -------
        int
            Number of areas in the largest region
        """
        self.largest_region = []

        players_regions = self.board.get_players_regions(self.player_name)
        max_region_size = max(len(region) for region in players_regions)
        max_sized_regions = [region for region in players_regions if len(region) == max_region_size]

        self.largest_region = max_sized_regions[0]
        return max_region_size
