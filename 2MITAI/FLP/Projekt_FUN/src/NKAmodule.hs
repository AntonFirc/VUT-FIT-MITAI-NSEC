--- Projekt FLP 2020
--- plg-2-nka
--- Anton Firc (xfirca00)

-- module containing functions for construction and print of NKA
-- NKA = Nedeterministicky konecny automat (Nondeterministic finite automaton)
module NKAmodule (
  NKA(..),
  Transition(..),
  gnrDict,
  constructNKA,
  prettyPrintNKA
) where

  import Data.Set (Set)
  import qualified Data.Set as Set
  import Data.Map.Strict as Map
  import Data.List
  import Data.Maybe
  import Debug.Trace

  import PLGmodule

  -- define placeholder types used in NKA structure
  type State = String
  type Dictionary = Map.Map Symbol State

  -- data structure representing transition of NKA
  data Transition = Transition {
    srcSymb :: State,
    input :: Symbol,
    dstSymb :: State
  }

  instance Show Transition where
    show (Transition srcSymb input dstSymb) =
      "("++show srcSymb++","++show input++","++show dstSymb++")"

  -- data structure representing NKA
  data NKA = NKA {
    states :: Set.Set State,
    starting_state :: State,
    final_states :: Set.Set State,
    transitions :: [Transition]
  }

  instance Show NKA where
    show (NKA states starting_state final_states transitions) =
      "Q: "++show states++"\n"++
      "S: "++show starting_state++"\n"++
      "F: "++show final_states++"\n"++
      "d:\n"++show transitions

  -- determine if PLG rule represents final state of NKA
  isFinal :: Rule -> Symbol
  isFinal rule =
    if (dstT rule) == "#"
      then
        src rule
      else
        ""

  -- get all final states of NKA
  finalStates :: [Rule] -> [Symbol]
  finalStates rules = Data.List.filter (\s -> s /= "") (Data.List.map (\rule -> isFinal rule) rules)

  -- translate symbol to state of NKA
  -- (char to int translation using previously created dictionary)
  getState :: Symbol -> Dictionary -> State
  getState "" dict = ""
  getState symb dict = fromJust (Map.lookup symb dict)

  -- create dictionary for non-teminal symbols
  -- each symbol is represented by integer
  gnrDict :: NT_Alphabet -> Int -> Dictionary
  gnrDict nts len = Map.fromList (Data.List.map makePair [1..len])
    where makePair x = ((Set.toList nts)!!(x-1), show x)

  -- creates NKA from given PLG
  constructNKA :: PLG -> Dictionary -> NKA
  constructNKA plg dict = NKA {
    states = nts_to_states,
    starting_state = s_to_s,
    final_states = fstates,
    transitions = transitions
  } where nts_to_states = Set.fromList (Data.List.map (\symb -> getState symb dict) (Set.toList (non_terminals plg)))
          s_to_s = getState (starting_symbol plg) dict
          fstates = Set.fromList (Data.List.map (\symb -> getState symb dict) (finalStates (rules plg)))
          transitions = Data.List.filter (\t -> (input t) /= "#") (Data.List.map (\rule -> Transition {srcSymb = getState (src rule) dict,
                                                            input = (dstT rule),
                                                            dstSymb = getState (dstN rule) dict}) (rules plg))


  -- print NKA in format given in assignment
  prettyPrintNKA :: NKA -> IO()
  prettyPrintNKA nka = do
    let sts = intercalate "," (Set.toList (states nka))
    let sState = starting_state nka
    let fStates = intercalate "," (Set.toList (final_states nka))
    let t = intercalate "\n" (Data.List.map (\trans->srcSymb trans++","++input trans++","++dstSymb trans) (transitions nka))
    putStrLn sts
    putStrLn sState
    putStrLn fStates
    putStrLn t
