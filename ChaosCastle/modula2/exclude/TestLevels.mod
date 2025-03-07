MODULE TestLevels;

 FROM Memory IMPORT SET16;
 FROM ChaosBase IMPORT Zone, zone, level, difficulty;
 FROM ChaosInterface IMPORT WarmInit;
 FROM ChaosLevels IMPORT MakeCastle;
 FROM Input IMPORT GetStick, Joy1;
 FROM Terminal IMPORT WriteString, WriteInt, WriteLn;

BEGIN

 WarmInit;
 zone:= Chaos;
 level[Castle]:= 1;
 difficulty:= 10;
 WriteString("Testing..."); WriteLn;
 REPEAT
  WriteInt(ORD(zone), 1);
  WriteInt(level[zone], 4);
  WriteLn;
  MakeCastle;
  IF zone = Chaos THEN
   zone:= Castle; level[Castle]:= 1
  ELSIF zone = Castle THEN
   IF level[Castle] >= 20 THEN
    zone:= Family; level[Family]:= 1
   ELSE
    INC(level[Castle])
   END
  ELSIF zone = Family THEN
   IF level[Family] >= 10 THEN
    zone:= Special; level[Special]:= 1
   ELSE
    INC(level[Family])
   END
  ELSE
   IF level[Special] >= 24 THEN
    zone:= Chaos; level[Chaos]:= 100
   ELSIF level[Special] >= 16 THEN
    level[Special]:= 24
   ELSE
    level[Special]:= level[Special] * 2
   END
  END
 UNTIL GetStick() <> SET16{};
 WriteString("Aborted."); WriteLn;

END TestLevels.
