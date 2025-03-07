IMPLEMENTATION MODULE Trigo;

 FROM SYSTEM IMPORT SHIFT;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32;
 FROM GraphicsL IMPORT VBeamPos;
 IMPORT R;

 TYPE
  SinArr = ARRAY[0..90] OF INT16;

 CONST
  SinTable = SinArr
   {0, 18, 36, 54, 71, 89, 107, 125, 143, 160,
    178, 195, 213, 230, 248, 265, 282, 299, 316, 333,
    350, 367, 384, 400, 416, 433, 449, 465, 481, 496,
    512, 527, 543, 558, 573, 587, 602, 616, 630, 644,
    658, 672, 685, 698, 711, 724, 737, 749, 761, 773,
    784, 796, 807, 818, 828, 839, 849, 859, 868, 878,
    887, 896, 904, 912, 920, 928, 935, 943, 949, 956,
    962, 968, 974, 979, 984, 989, 994, 998, 1002, 1005,
    1008, 1011, 1014, 1016, 1018, 1020, 1022, 1023, 1023, 1024,
    1024};

 VAR
  randomcount: CARD32;


 PROCEDURE SIN(angle{R.D1}: INT16): INT16;
  VAR a{R.D0}: INT16;
 BEGIN
  a:= angle MOD 360;
  IF a < 90 THEN
   RETURN SinTable[a]
  ELSIF a < 180 THEN
   RETURN SinTable[180 - a]
  ELSIF a < 270 THEN
   RETURN -SinTable[a - 180]
  ELSE
   RETURN -SinTable[360 - a]
  END
 END SIN;

 PROCEDURE COS(angle: INT16): INT16;
 BEGIN
  IF angle >= 360 THEN
   RETURN SIN(angle - 270)
  ELSE
   RETURN SIN(angle + 90)
  END
 END COS;

 PROCEDURE SQRT(val{R.A0}: CARD32): CARD16;
  VAR
   a{R.A1}, x{R.D6}, y{R.D5}: CARD32;
   a2{R.D4}, x2{R.D3}, y2{R.D2}: CARD16;
 BEGIN
  IF val > 65535 THEN
   a:= val;
   x:= 2048;
   REPEAT
    y:= x;
    x:= (a DIV x + x + 1) DIV 2
   UNTIL x = y;
   RETURN x
  ELSE
   a2:= val;
   IF a2 = 0 THEN RETURN 0 END;
   x2:= 32;
   REPEAT
    y2:= x2;
    x2:= (a2 DIV x2 + x2 + 1) DIV 2
   UNTIL x2 = y2;
   RETURN x2
  END
 END SQRT;

 PROCEDURE SGN(val{R.D0}: INT16): INT16;
 BEGIN
  IF val > 0 THEN RETURN 1 ELSIF val = 0 THEN RETURN val ELSE RETURN -1 END
 END SGN;

 PROCEDURE RND(): CARD16;
 BEGIN
(*$ OverflowChk:= FALSE  RangeChk:= FALSE *)
  INC(randomcount, VBeamPos() * 65536);
  randomcount:= randomcount * 3083109 + 248381;
  RETURN randomcount DIV 65536
 END RND;
(*$ POP OverflowChk  POP RangeChk *)

END Trigo.
