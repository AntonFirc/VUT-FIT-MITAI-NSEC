--- Projekt FLP 2020
--- plg-2-nka
--- Anton Firc (xfirca00)

import System.Environment
import System.Exit
import System.IO
import Control.Monad
import Data.List
import Data.Set (Set)
import qualified Data.Set as Set
import Data.List.Split
import Data.Char

import PLGmodule
import PLGtransform
import NKAmodule

progInfo = "FLP project 1, plg-2-nka\nUsage: ./plg-2-nka <-i|-1|-2> [filename]\nAnton Firc(xfirca00), 2020"

-- checks for valid run parameters
checkArgList :: [String] -> Bool
checkArgList args =
  case length(args) of 0 -> False
                       1 -> flagi args || flag1 args || flag2 args || isFile (head args)
                       2 -> (flagi args && flag1 args) ||
                            (flag1 args && flag2 args) ||
                            (flagi args && flag2 args) ||
                            (flagi args && isFile (args!!1)) ||
                            (flag1 args && isFile (args!!1)) ||
                            (flag2 args && isFile (args!!1))
                       3 -> (flagi args && flag1 args && flag2 args) ||
                            (flagi args && flag1 args && isFile (args!!2)) ||
                            (flagi args && flag2 args && isFile (args!!2)) ||
                            (flag1 args && flag2 args && isFile (args!!2))
                       4 -> flagi args && flag1 args && flag2 args && isFile (args!!3)

isFile :: String -> Bool
isFile (x:xs) = x /= '-'

flagi :: [String] -> Bool
flagi args = elem "-i" args

flag1 :: [String] -> Bool
flag1 args = elem "-1" args

flag2 :: [String] -> Bool
flag2 args = elem "-2" args

-- gets input file name given in arguments
getFileName :: [String] -> String
getFileName args =
  if isFile (last args)
    then (last args)
    else ""

-- returns either stdin or input file handler
getInput :: String -> IO String
getInput "" = getContents
getInput filename = readFile filename

-- returns number of terminal symbols on right side of the rule
takeLen :: String -> Int
takeLen (x:xs) =
  if isLower(x) || x == '#'
    then takeLen xs + 1
    else 0
takeLen "" = 0

-- stores input grammar into internal structure
grammarParser :: [String] -> PLG
grammarParser (non_terminals:terminals:starting_symbol:rules) = PLG {
  non_terminals = Set.fromList $ splitOn "," non_terminals,
  terminals = Set.fromList $ splitOn "," terminals,
  starting_symbol = starting_symbol,
  rules = parseRules rules
}
grammarParser others = error "Invalid input grammar format."

parseRules rules = map (\t -> parseRule t) rules
parseRule rule
  | length(ruleParts) /= 2 = error "Wrong rule format!"
  | otherwise = Rule {
        src = part1,
        dstT = part2,
        dstN = part3
      }
      where ruleParts = splitOn "->" rule
            part1 = head ruleParts
            part2 = take (takeLen (ruleParts!!1)) (ruleParts!!1)
            part3 = drop (takeLen (ruleParts!!1)) (ruleParts!!1)

main :: IO()
main = do
 argv <- getArgs
 let argList = filter (\a -> a /= " ") (intersperse " " argv)

 unless (checkArgList argList) (error progInfo)

 contents <- getInput $ getFileName argv

 let plg = grammarParser $ lines contents

 if validatePLG plg
   then do
     when (flagi argList) (prettyPrintPLG plg)
     let test = transformPLG plg
     when (flag1 argList)  (prettyPrintPLG test)
     let dict = gnrDict (non_terminals test) (Set.size(non_terminals test))
     let nka = constructNKA test dict
     when (flag2 argList) (prettyPrintNKA nka)
    else
      error "Wrong grammar format!"
