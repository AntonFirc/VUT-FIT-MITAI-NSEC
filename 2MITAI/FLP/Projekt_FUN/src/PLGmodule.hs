--- Projekt FLP 2020
--- plg-2-nka
--- Anton Firc (xfirca00)

-- Module containing functions for creation and print of PLG
-- PLG = Prava linearni gramatika (Right linear grammar)
module PLGmodule (
  PLG(..),
  Rule(..),
  Symbol,
  NT_Alphabet,
  validatePLG,
  prettyPrintPLG
) where

  import Data.Set (Set)
  import qualified Data.Set as Set
  import Data.Char
  import Data.List
  import Debug.Trace

  -- define placeholder types used in PLG structure
  type Symbol = String
  type NT_Alphabet = Set.Set Symbol
  type T_Alphabet = Set.Set Symbol

  -- data structure representing rule of PLG
  data Rule = Rule {
    src :: Symbol,
    dstT :: Symbol,
    dstN :: Symbol
  }

  instance Show Rule where
    show (Rule src dstT dstN) =
      "("++show src++","++show dstT++","++show dstN++")"

  -- data structure representing PLG
  data PLG = PLG {
    non_terminals:: NT_Alphabet,
    terminals:: T_Alphabet,
    starting_symbol:: Symbol,
    rules:: [Rule]
  }

  instance Show PLG where
    show (PLG non_terminals terminals starting_symbol rules) =
      "N: "++show non_terminals++"\n"++
      "T: "++show terminals++"\n"++
      "S: "++show starting_symbol++"\n"++
      "P:\n"++show rules

  -- print PLG in format given in assignment
  prettyPrintPLG :: PLG -> IO()
  prettyPrintPLG grammar = do
    let nts = intercalate "," (Set.toList (non_terminals grammar))
    let ts = intercalate "," (Set.toList (terminals grammar))
    let s = starting_symbol grammar
    let r = intercalate "\n" (map (\rule->src rule++"->"++dstT rule++dstN rule) (rules grammar))
    putStrLn nts
    putStrLn ts
    putStrLn s
    putStrLn r

  -- check if non-terminal in non-terminal alphabet
  validateNT :: Symbol -> Bool
  validateNT symb = all isUpper symb

  -- check all non-terminals if defined
  validateNTs :: NT_Alphabet -> Bool
  validateNTs nts = all validateNT (Set.toList(nts))

  -- check if terminal in terminal alphabet
  validateT :: Symbol -> Bool
  validateT symb = all isLower symb

  -- check all terminals if defined
  validateTs :: NT_Alphabet -> Bool
  validateTs nts = all validateT (Set.toList(nts))

  -- check if starting symbol is valid
  validateS :: NT_Alphabet -> Symbol -> Bool
  validateS nts symb = elem symb (Set.toList(nts))

  -- validate that left side of rule is non-terminal symbol
  validateSrc :: Symbol -> NT_Alphabet -> Bool
  validateSrc symb nts = elem symb (Set.toList(nts))

  -- validate terminal symbol from rule
  validateDstTSymbol :: T_Alphabet -> Char -> Bool
  validateDstTSymbol ts symb = elem (symb:"") (Set.toList(ts))

  -- validate right side of rule that should contain terminals
  validateDstTstring :: String -> T_Alphabet -> Bool
  validateDstTstring symb ts = (symb == "#") || all (validateDstTSymbol ts) symb

  -- validate non-terminal symbol from rule
  validateDstN :: Symbol -> NT_Alphabet -> Bool
  validateDstN symb nts = elem symb (Set.toList(nts)) || symb == ""

  -- validate rule format
  validateR :: PLG -> Rule -> Bool
  validateR grammar rule = validateSrc (src rule) (non_terminals grammar)&&
                    validateDstTstring (dstT rule) (terminals grammar) &&
                    validateDstN (dstN rule) (non_terminals grammar)

  -- validate all rules in grammar
  validateRs :: [Rule] -> PLG -> Bool
  validateRs rules grammar = all (validateR grammar) rules

  -- validate PLG format
  validatePLG :: PLG -> Bool
  validatePLG grammar = validateNTs (non_terminals grammar) &&
                        validateTs (terminals grammar) &&
                        validateS (non_terminals grammar) (starting_symbol grammar) &&
                        validateRs (rules grammar) grammar
