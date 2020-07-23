/*
 * FLP 2020 Projekt - Rubikova kocka
 * Anton Firc (xfirca00)
 */

/*********************  taken from input2.pl  *********************/
 %Reads line from stdin, terminates on LF or EOF.
 read_line(L,C) :-
 	get_char(C),
 	(isEOFEOL(C), L = [], !;
 		read_line(LL,_),% atom_codes(C,[Cd]),
 		[C|LL] = L).

 %Tests if character is EOF or LF.
 isEOFEOL(C) :-
 	C == end_of_file;
 	(char_code(C,Code), Code==10).

 read_lines(Ls) :-
 	read_line(L,C),
 	( C == end_of_file, Ls = [] ;
 	  read_lines(LLs), Ls = [L|LLs]
 	).

 % rozdeli radek na podseznamy
 split_line([],[[]]) :- !.
 split_line([' '|T], [[]|S1]) :- !, split_line(T,S1).
 split_line([32|T], [[]|S1]) :- !, split_line(T,S1).    % aby to fungovalo i s retezcem na miste seznamu
 split_line([H|T], [[H|G]|S1]) :- split_line(T,[G|S1]). % G je prvni seznam ze seznamu seznamu G|S1

 % vstupem je seznam radku (kazdy radek je seznam znaku)
 split_lines([],[]).
 split_lines([L|Ls],[H|T]) :- split_lines(Ls,T), split_line(L,H).

/*********************  *******************  *********************/

/* transform cube into internal representation */
loadCube(
  [
  S51, S52, S53,
  S54, S55, S56,
  S57, S58, S59,
  S11, S12, S13, S21, S22, S23, S31, S32, S33, S41, S42, S43,
  S14, S15, S16, S24, S25, S26, S34, S35, S36, S44, S45, S46,
  S17, S18, S19, S27, S28, S29, S37, S38, S39, S47, S48, S49,
  S61, S62, S63,
  S64, S65, S66,
  S67, S68, S69
  ],
  [
    [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
    [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
    [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
    [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
    [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
    [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
  ]
).

/* prints formatted cube */
printCube(
  [
    [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
    [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
    [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
    [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
    [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
    [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
  ]
) :-
  writef("%w%w%w\n%w%w%w\n%w%w%w\n", [S51, S52, S53, S54, S55, S56, S57, S58, S59]),
  writef("%w%w%w %w%w%w %w%w%w %w%w%w\n", [S11, S12, S13, S21, S22, S23, S31, S32, S33, S41, S42, S43]),
  writef("%w%w%w %w%w%w %w%w%w %w%w%w\n", [S14, S15, S16, S24, S25, S26, S34, S35, S36, S44, S45, S46]),
  writef("%w%w%w %w%w%w %w%w%w %w%w%w\n", [S17, S18, S19, S27, S28, S29, S37, S38, S39, S47, S48, S49]),
  writef("%w%w%w\n%w%w%w\n%w%w%w\n", [S61, S62, S63, S64, S65, S66, S67, S68, S69]).

/* checks whether cube is solved */
isSolved(
  [
    [[S1, S1, S1], [S1, S1, S1], [S1, S1, S1]],
    [[S2, S2, S2], [S2, S2, S2], [S2, S2, S2]],
    [[S3, S3, S3], [S3, S3, S3], [S3, S3, S3]],
    [[S4, S4, S4], [S4, S4, S4], [S4, S4, S4]],
    [[S5, S5, S5], [S5, S5, S5], [S5, S5, S5]],
    [[S6, S6, S6], [S6, S6, S6], [S6, S6, S6]]
  ]
).

/* find path leading to solved cube */
findPath(Cube, Path) :- tryMoves(Cube, Path, RotatedCube), isSolved(RotatedCube).

/* applies gradually all moves on cube */
tryMoves(Cube, [], Cube).
tryMoves(Cube, [Move|RemMoves], NewCube) :- tryMoves(RotatedCube, RemMoves, NewCube), move(Move, Cube, RotatedCube).

/* rotate top side clockwise */
move(
  top_cw,
    [
      [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
      [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
      [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
      [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
      [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
      [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
    ],
    [
      [[S21, S22, S23], [S14, S15, S16], [S17, S18, S19]],
      [[S31, S32, S33], [S24, S25, S26], [S27, S28, S29]],
      [[S41, S42, S43], [S34, S35, S36], [S37, S38, S39]],
      [[S11, S12, S13], [S44, S45, S46], [S47, S48, S49]],
      [[S57, S54, S51], [S58, S55, S52], [S59, S56, S53]],
      [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
    ]
  ).

move(top_ccw, C1, C2) :- move(top_cw, C2, C1).

/* rotate top side counter-clockwise */
move(
  top_ccw,
    [
      [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
      [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
      [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
      [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
      [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
      [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
    ],
    [
      [[S41, S42, S43], [S14, S15, S16], [S17, S18, S19]],
      [[S11, S12, S13], [S24, S25, S26], [S27, S28, S29]],
      [[S21, S22, S23], [S34, S35, S36], [S37, S38, S39]],
      [[S31, S32, S33], [S44, S45, S46], [S47, S48, S49]],
      [[S53, S56, S59], [S52, S55, S58], [S51, S54, S57]],
      [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
    ]
  ).

/* rotate top side clockwise */
move(
  bot_cw,
  [
    [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
    [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
    [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
    [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
    [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
    [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
  ],
  [
    [[S11, S12, S13], [S14, S15, S16], [S27, S28, S29]],
    [[S21, S22, S23], [S24, S25, S26], [S37, S38, S39]],
    [[S31, S32, S33], [S34, S35, S36], [S47, S48, S49]],
    [[S41, S42, S43], [S44, S45, S46], [S17, S18, S19]],
    [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
    [[S63, S66, S69], [S62, S65, S68], [S61, S64, S67]]
  ]
).

/* rotate bottom side counter-clockwise */
move(
  bot_ccw,
  [
    [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
    [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
    [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
    [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
    [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
    [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
  ],
  [
    [[S11, S12, S13], [S14, S15, S16], [S47, S48, S49]],
    [[S21, S22, S23], [S24, S25, S26], [S17, S18, S19]],
    [[S31, S32, S33], [S34, S35, S36], [S27, S28, S29]],
    [[S41, S42, S43], [S44, S45, S46], [S37, S38, S39]],
    [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
    [[S67, S64, S61], [S68, S65, S62], [S69, S66, S63]]
  ]
).

/* rotate front side clockwise */
move(
  front_cw,
  [
    [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
    [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
    [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
    [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
    [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
    [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
  ],
  [
    [[S17, S14, S11], [S18, S15, S12], [S19, S16, S13]],
    [[S57, S22, S23], [S58, S25, S26], [S59, S28, S29]],
    [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
    [[S41, S42, S61], [S44, S45, S62], [S47, S48, S63]],
    [[S51, S52, S53], [S54, S55, S56], [S49, S46, S43]],
    [[S27, S24, S21], [S64, S65, S66], [S67, S68, S69]]
  ]
).

/* rotate front side counter-clockwise */
move(
  front_ccw,
  [
    [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
    [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
    [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
    [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
    [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
    [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
  ],
  [
    [[S13, S16, S19], [S12, S15, S18], [S11, S14, S17]],
    [[S63, S22, S23], [S62, S25, S26], [S61, S28, S29]],
    [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
    [[S41, S42, S59], [S44, S45, S58], [S47, S48, S57]],
    [[S51, S52, S53], [S54, S55, S56], [S21, S24, S27]],
    [[S43, S46, S49], [S64, S65, S66], [S67, S68, S69]]
  ]
).

/* rotate back side clockwise */
move(
  back_cw,
  [
    [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
    [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
    [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
    [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
    [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
    [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
  ],
  [
    [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
    [[S21, S22, S51], [S24, S25, S52], [S27, S28, S53]],
    [[S33, S36, S39], [S32, S35, S38], [S31, S34, S37]],
    [[S67, S42, S43], [S68, S45, S46], [S69, S48, S49]],
    [[S47, S44, S41], [S54, S55, S56], [S57, S58, S59]],
    [[S61, S62, S63], [S64, S65, S66], [S29, S26, S23]]
  ]
).

/* rotate back side counter-clockwise */
move(
  back_ccw,
  [
    [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
    [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
    [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
    [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
    [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
    [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
  ],
  [
    [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
    [[S21, S22, S69], [S24, S25, S68], [S27, S28, S67]],
    [[S37, S34, S31], [S38, S35, S32], [S39, S36, S33]],
    [[S53, S42, S43], [S52, S45, S46], [S51, S48, S49]],
    [[S23, S26, S29], [S54, S55, S56], [S57, S58, S59]],
    [[S61, S62, S63], [S64, S65, S66], [S41, S44, S47]]
  ]
).

/* rotate right side clockwise */
move(
  right_cw,
    [
      [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
      [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
      [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
      [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
      [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
      [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
    ],
    [
      [[S11, S12, S63], [S14, S15, S66], [S17, S18, S69]],
      [[S27, S24, S21], [S28, S25, S22], [S29, S26, S23]],
      [[S59, S32, S33], [S56, S35, S36], [S53, S38, S39]],
      [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
      [[S51, S52, S13], [S54, S55, S16], [S57, S58, S19]],
      [[S61, S62, S37], [S64, S65, S34], [S67, S68, S31]]
    ]
).

/* rotate right side counter-clockwise */
move(
  right_ccw,
    [
      [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
      [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
      [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
      [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
      [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
      [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
    ],
    [
      [[S11, S12, S53], [S14, S15, S56], [S17, S18, S59]],
      [[S23, S26, S29], [S22, S25, S28], [S21, S24, S27]],
      [[S69, S32, S33], [S66, S35, S36], [S63, S38, S39]],
      [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
      [[S51, S52, S37], [S54, S55, S34], [S57, S58, S31]],
      [[S61, S62, S13], [S64, S65, S16], [S67, S68, S19]]
    ]
).

/* rotate left side clockwise */
move(
  left_cw,
    [
      [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
      [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
      [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
      [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
      [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
      [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
    ],
    [
      [[S51, S12, S13], [S54, S15, S16], [S57, S18, S19]],
      [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
      [[S31, S32, S67], [S34, S35, S64], [S37, S38, S61]],
      [[S47, S44, S41], [S48, S45, S42], [S49, S46, S43]],
      [[S39, S52, S53], [S36, S55, S56], [S33, S58, S59]],
      [[S11, S62, S63], [S14, S65, S66], [S17, S68, S69]]
    ]
  ).

/* rotate left side counter-clockwise */
move(
  left_ccw,
    [
      [[S11, S12, S13], [S14, S15, S16], [S17, S18, S19]],
      [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
      [[S31, S32, S33], [S34, S35, S36], [S37, S38, S39]],
      [[S41, S42, S43], [S44, S45, S46], [S47, S48, S49]],
      [[S51, S52, S53], [S54, S55, S56], [S57, S58, S59]],
      [[S61, S62, S63], [S64, S65, S66], [S67, S68, S69]]
    ],
    [
      [[S61, S12, S13], [S64, S15, S16], [S67, S18, S19]],
      [[S21, S22, S23], [S24, S25, S26], [S27, S28, S29]],
      [[S31, S32, S57], [S34, S35, S54], [S37, S38, S51]],
      [[S43, S46, S49], [S42, S45, S48], [S41, S44, S47]],
      [[S11, S52, S53], [S14, S55, S56], [S17, S58, S59]],
      [[S39, S62, S63], [S36, S65, S66], [S33, S68, S69]]
    ]
  ).

/* print path how cube should be solved
 * in each step does move cube and recursively call itself with rest of moves
 */
printPath(_, []).
printPath(Cube, [Move|RemMoves]) :- move(Move, Cube, RotatedCube), nl, printCube(RotatedCube), printPath(RotatedCube, RemMoves).

 main :-
 		prompt(_, ''),
 		read_lines(LL),
 		split_lines(LL,S),
    flatten(S, M), %remove unneeded arrays
    loadCube(M, Cube),
    printCube(Cube),
 		findPath(Cube, Path),
    printPath(Cube, Path),
 		halt.
