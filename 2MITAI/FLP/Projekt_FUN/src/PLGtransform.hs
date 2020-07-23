--- Projekt FLP 2020
--- plg-2-nka
--- Anton Firc (xfirca00)

-- Module containing functions to transform PLG into suitable form for NKA creation
-- PLG = Prava linearni gramatika (Right linear grammar)
-- NKA = Nedeterministicky konecny automat (Nondeterministic finite automaton)
module PLGtransform (
  transformPLG
) where

  import PLGmodule
  import Data.List
  import Data.Set (Set)
  import qualified Data.Set as Set

  -- add all generated non-terminals from transformation to
  -- non-terminal grammar
  updateNTs :: NT_Alphabet -> [Rule] -> NT_Alphabet
  updateNTs nts rules = Set.filter (\n -> n /= "") (Set.fromList (map (\r -> (dstN r) ) rules))

  -- get last used index of nonterminal -> from [(A,a,A1),(A1,b,A2),(A2,b,C)] return 2
  lastIdx :: [Rule] -> Int
  lastIdx [] = 1
  lastIdx rule = read (drop 1 $ src $ last rule) :: Int

  -- get all rules A->a
  getSimpleNTRules :: PLG -> [Rule]
  getSimpleNTRules plg = filter (\r -> (length((dstT r)) == 1) && (length(dstN r)) == 1) (rules plg)

  -- get all rules A->#
  getEpsilonRules :: PLG -> [Rule]
  getEpsilonRules plg = filter (\r -> (dstT r) == "#" ) (rules plg)

  -- get all rules A->a...zB
  getNTRules :: PLG -> [Rule]
  getNTRules plg = filter (\r -> (length(dstT r) > 1) && (length(dstN r) == 1)) (rules plg)

  -- get all rules A->a...z
  getTRules :: PLG -> [Rule]
  getTRules plg = filter (\r -> (length(dstT r) >= 1) && (length(dstN r) == 0) && ((dstT r) /= "#" )) (rules plg)

  -- create new nonterminal using index
  createNT nt idx =
    if (length(nt) == 1)
      then
        nt++show(idx)
      else
        head(nt):(show idx)

  -- transform rules A->a...zB to list of rules
  transformNTRule :: (Show a, Num a) => a -> Rule -> [Rule]
  transformNTRule idx rule =
    if (length(dstT rule) == 1)
      then
        [rule]
      else
        (Rule { src = (src rule), dstT = (take 1 (dstT rule)), dstN = (createNT (src rule) idx)} )
        : transformNTRule (idx+1) (Rule {src = (createNT (src rule) idx), dstT = drop 1 (dstT rule), dstN = (dstN rule)})

  transformNTRules _ [] = []
  transformNTRules idx (x:xs) = newRules++transformNTRules newIdx xs
    where newRules = transformNTRule idx x
          newIdx = (lastIdx newRules)+1

  -- transform rules A->a...z to list of rules
  transformTRule :: (Show a, Num a) => a -> Rule -> [Rule]
  transformTRule idx rule =
    if (length(dstT rule) == 0)
      then
        [Rule {src = (src rule),dstT = "#", dstN = ""}]
      else
        (Rule {src = (src rule), dstT = (take 1 (dstT rule)), dstN = (createNT (src rule) idx)})
        : transformTRule (idx+1) (Rule {src = (createNT (src rule) idx), dstT = drop 1 (dstT rule), dstN = ""})

  transformTRules _ [] = []
  transformTRules idx (x:xs) = newRules++transformTRules newIdx xs
    where newRules = transformTRule idx x
          newIdx = (lastIdx newRules)+1


  -- transforms rules of grammar
  transformRules plg = sNT++eps++nT++t
    where newIdx = (lastIdx nT)+1
          sNT = (getSimpleNTRules plg)
          eps = (getEpsilonRules plg)
          nT = (transformNTRules 1 (getNTRules plg))
          t = (transformTRules newIdx (getTRules plg))


  -- transform PLG using veta 3.2
  transformPLG :: PLG -> PLG
  transformPLG plg = PLG {
    terminals = new_ts,
    starting_symbol = new_start,
    rules = new_rules,
    non_terminals = new_nts
    }
    where new_ts = (terminals plg)
          new_start = (starting_symbol plg)
          new_rules = transformRules plg
          new_nts = Set.union (non_terminals plg) (updateNTs (non_terminals plg) new_rules)
