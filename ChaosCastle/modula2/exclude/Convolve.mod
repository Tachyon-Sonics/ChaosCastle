IMPLEMENTATION MODULE Convolve;
(*$ OverflowChk:= FALSE *)

 FROM SYSTEM IMPORT ADR, ADDRESS, SHIFT;
 FROM Memory IMPORT INT16, CARD16, INT32, CARD32;

 CONST
  Modulo = 40961;
  MaxLog = 13;

 VAR
  powerPosRoot, powerNegRoot: ARRAY [1..MaxLog] OF CARD16;


 PROCEDURE SNTT(a, b: Card16ArrPtr; len, log: CARD16; forward: BOOLEAN);
  VAR
   sum: CARD32;
   i, j: CARD16;
   root, mult: CARD16;
 BEGIN
  IF forward THEN
   root:= powerNegRoot[log]
  ELSE
   root:= powerPosRoot[log]
  END;
  FOR i:= 0 TO len - 1 DO
   sum:= 0;
   FOR j:= 0 TO len - 1 DO
    mult:= Power(root, CARD32(i * j) MOD Modulo);
    INC(sum, CARD32(a^[j] * mult) MOD Modulo)
   END;
   b^[i]:= sum MOD Modulo
  END
 END SNTT;

 PROCEDURE BackFNTT(a: Card16ArrPtr; len, log: CARD16);
  VAR
   tmp: CARD32;
   i, k1, k2, lo: CARD16;
   order, root, mult, tmp1, tmp2: CARD16;
 BEGIN
  order:= 1; lo:= 0; (* lg(order) *)
  REPEAT
   INC(lo);
   root:= powerNegRoot[lo];

   k1:= 0;
   REPEAT
    k2:= k1 + order;
    tmp1:= a^[k1];
    tmp2:= a^[k2];
    tmp:= CARD32(tmp1) + CARD32(tmp2);
    IF tmp < Modulo THEN a^[k1]:= tmp ELSE a^[k1]:= tmp - Modulo END;
    IF tmp1 >= tmp2 THEN
     a^[k2]:= tmp1 - tmp2
    ELSE
     a^[k2]:= Modulo - tmp2 + tmp1
    END;
    INC(k1, order * 2)
   UNTIL k1 >= len;
   mult:= root;

   FOR i:= 1 TO order - 1 DO
    k1:= i;
    REPEAT
     k2:= k1 + order;

    (** Butterfly operation with indices k1 and k2 *)
     tmp1:= a^[k1];
     tmp2:= CARD32(mult * a^[k2]) MOD Modulo;
      (* (tmp1 + tmp2) MOD Modulo: *)
     tmp:= CARD32(tmp1) + CARD32(tmp2);
     IF tmp < Modulo THEN a^[k1]:= tmp ELSE a^[k1]:= tmp - Modulo END;
      (* (tmp1 - tmp2) MOD Modulo: *)
     IF tmp1 >= tmp2 THEN
      a^[k2]:= tmp1 - tmp2
     ELSE
      a^[k2]:= Modulo - tmp2 + tmp1
     END;

     INC(k1, order * 2)
    UNTIL k1 >= len;
    mult:= CARD32(mult * root) MOD Modulo
   END;
   order:= order * 2
  UNTIL order = len
 END BackFNTT;

 PROCEDURE ForwFNTT(a: Card16ArrPtr; len, log: CARD16);
  VAR
   tmp: CARD32;
   i, k1, k2: CARD16;
   order, root, mult, tmp1, tmp2: CARD16;
 BEGIN
  order:= len;
  REPEAT
   order:= order DIV 2;
   root:= powerPosRoot[log];
   mult:= 1;

   k1:= 0;
   REPEAT
    k2:= k1 + order;
    tmp1:= a^[k1];
    tmp2:= a^[k2];
    tmp:= CARD32(tmp1) + CARD32(tmp2);
    IF tmp < Modulo THEN a^[k1]:= tmp ELSE a^[k1]:= tmp - Modulo END;
    IF tmp1 >= tmp2 THEN
     a^[k2]:= tmp1 - tmp2
    ELSE
     a^[k2]:= Modulo - tmp2 + tmp1
    END;
    INC(k1, order * 2)
   UNTIL k1 >= len;
   mult:= root;

   FOR i:= 1 TO order - 1 DO
    k1:= i;
    REPEAT
     k2:= k1 + order;

    (** Invert Butterfly operation with indices k1 and k2 *)
     tmp1:= a^[k1];
     tmp2:= a^[k2];
      (* (tmp1 + tmp2) MOD Modulo: *)
     tmp:= CARD32(tmp1) + CARD32(tmp2);
     IF tmp < Modulo THEN a^[k1]:= tmp ELSE a^[k1]:= tmp - Modulo END;
      (* ((tmp1 - tmp2) * 1/mult) MOD Modulo: *)
     IF tmp1 >= tmp2 THEN
      a^[k2]:= CARD32((tmp1 - tmp2) * mult) MOD Modulo
     ELSE
      a^[k2]:= CARD32((Modulo - tmp2 + tmp1) * mult) MOD Modulo
     END;

     INC(k1, order * 2)
    UNTIL k1 >= len;
    mult:= CARD32(mult * root) MOD Modulo
   END;
   DEC(log)
  UNTIL order = 1
 END ForwFNTT;

 PROCEDURE Shift(a: ADDRESS; len: CARD16; shift: INT16);
  VAR
   ptr: POINTER TO CARD16;
   c: CARD16;
 BEGIN
  ptr:= a; c:= len;
  WHILE c > 0 DO
   ptr^:= SHIFT(ptr^, shift);
   INC(ptr, 2);
   DEC(c)
  END
 END Shift;

 PROCEDURE Power(base, exp: CARD16): CARD16;
  VAR
   result: CARD16;
 BEGIN
  IF exp = 0 THEN
   RETURN 1
  ELSIF ODD(exp) THEN
   result:= Power(base, exp DIV 2);
   result:= CARD32(result * result) MOD Modulo;
   RETURN CARD32(result * base) MOD Modulo
  ELSE
   result:= Power(base, exp DIV 2);
   RETURN CARD32(result * result) MOD Modulo
  END
 END Power;

 PROCEDURE Normalize(a: Card16ArrPtr; len: CARD16);
  VAR
   i, inverse: CARD16;
 BEGIN
   (* inverse:= 1 / len (MOD 40961) = len^(40961 - 2) *)
  inverse:= Power(len, Modulo - 2);
  FOR i:= 0 TO len - 1 DO
   a^[i]:= CARD32(a^[i] * inverse) MOD Modulo
  END
 END Normalize;

 PROCEDURE PTPMul(a, b, c: Card16ArrPtr; len: CARD16);
  VAR
   i: CARD16;
 BEGIN
  FOR i:= 0 TO len - 1 DO
   c^[i]:= (CARD32(a^[i] * b^[i])) MOD Modulo
  END
 END PTPMul;

 PROCEDURE FastConvolve(a, b, c: Card16ArrPtr; len, log: CARD16);
 BEGIN
  ForwFNTT(a, len, log);
  ForwFNTT(b, len, log);
  PTPMul(a, b, c, len);
  BackFNTT(c, len, log);
  Normalize(c, len)
 END FastConvolve;

 PROCEDURE FastReConvolve(a, b, c: Card16ArrPtr; len, log: CARD16);
 BEGIN
  ForwFNTT(a, len, log);
  PTPMul(a, b, c, len);
  BackFNTT(c, len, log)
 END FastReConvolve;

 PROCEDURE MediumConvolve(a, b, c: Card16ArrPtr; len, log: CARD16);
 BEGIN
  SNTT(a, c, len, log, TRUE);
  SNTT(b, a, len, log, TRUE);
  PTPMul(a, c, b, len);
  SNTT(b, c, len, log, FALSE);
  Normalize(c, len)
 END MediumConvolve;

 PROCEDURE SlowConvolve(a, b, c: Card16ArrPtr; len, log: CARD16);
  VAR
   sum: CARD32;
   i, j, k: CARD16;
 BEGIN
  FOR i:= 0 TO len - 1 DO
   sum:= 0;
   FOR j:= 0 TO len - 1 DO
    IF i >= j THEN k:= i - j ELSE k:= len - j + i END;
    INC(sum, CARD32(a^[k] * b^[j]))
   END;
   c^[i]:= sum
  END
 END SlowConvolve;

 PROCEDURE InitRoots;
  CONST
   Generator = 3;
  VAR
   c, posPower, negPower: CARD16;
 BEGIN
   (* Generator: root of order 40960 *)
   (* Generator^5: root of order 8192 *)
  posPower:= Power(Generator, 5);
  negPower:= Power(posPower, Modulo - 2);
  c:= MaxLog;
  WHILE c > 0 DO
   powerPosRoot[c]:= posPower;
   powerNegRoot[c]:= negPower;
   posPower:= CARD32(posPower * posPower) MOD Modulo;
   negPower:= CARD32(negPower * negPower) MOD Modulo;
   DEC(c)
  END
 END InitRoots;

BEGIN

 InitRoots;

END Convolve.
