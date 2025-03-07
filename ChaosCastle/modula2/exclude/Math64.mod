IMPLEMENTATION MODULE Math64;

 FROM SYSTEM IMPORT LONGSET, CAST;
 FROM Memory IMPORT INT16, INT32;
 IMPORT mt: MathIEEEDoubTrans, mb: MathIEEEDoubBas;


 PROCEDURE Abs(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mb.Abs(x)
 END Abs;

 PROCEDURE Floor(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mb.Floor(x)
 END Floor;

 TYPE
  IEEE = RECORD CASE :INTEGER OF
           0: r: LONGREAL;
          |1: hi, lo: LONGSET;
         END; END;

 PROCEDURE SetExp(x: LONGREAL; exp: INT16): LONGREAL;
  VAR
   conv: IEEE;
   lexp: INT32;
 BEGIN
  lexp:= exp;
  WITH conv DO
   r:= x;
   hi:= hi * LONGSET{0..19, 31} + CAST(LONGSET, lexp * 1048576);
   RETURN r
  END
 END SetExp;

 PROCEDURE GetExp(x: LONGREAL): INT16;
  VAR
   conv: IEEE;
 BEGIN
  WITH conv DO
   r:= x;
   RETURN CAST(INT32, hi * LONGSET{20..30}) DIV 1048576
  END
 END GetExp;

 PROCEDURE Exp(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Exp(x)
 END Exp;

 PROCEDURE Ln(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Log(x)
 END Ln;

 PROCEDURE Pow(x, y: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Pow(x, y)
 END Pow;

 PROCEDURE Sqrt(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Sqrt(x)
 END Sqrt;

 PROCEDURE Sin(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Sin(x)
 END Sin;

 PROCEDURE Cos(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Cos(x);
 END Cos;

 PROCEDURE SinCos(x: LONGREAL; VAR sin, cos: LONGREAL);
 BEGIN
  sin:= mt.Sincos(x, cos)
 END SinCos;

 PROCEDURE Tan(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Tan(x)
 END Tan;

 PROCEDURE Cot(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN 1.0 / mt.Tan(x)
 END Cot;


 PROCEDURE ASin(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Asin(x)
 END ASin;

 PROCEDURE ACos(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Acos(x)
 END ACos;

 PROCEDURE ATan(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Atan(x)
 END ATan;

 PROCEDURE ACot(x: LONGREAL): LONGREAL;
  CONST
   Pi2 = Pi / 2.0;
 BEGIN
  RETURN Pi2 - mt.Atan(x)
 END ACot;


 PROCEDURE SinH(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Sinh(x)
 END SinH;

 PROCEDURE CosH(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Cosh(x)
 END CosH;

 PROCEDURE TanH(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Tanh(x)
 END TanH;

 PROCEDURE CotH(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN 1.0 / mt.Tanh(x)
 END CotH;


 PROCEDURE ASinH(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Log(x + mt.Sqrt(x * x + 1.0))
 END ASinH;

 PROCEDURE ACosH(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN mt.Log(x + mt.Sqrt(x * x - 1.0))
 END ACosH;

 PROCEDURE ATanH(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN 0.5 * mt.Log((1.0 + x) / (1.0 - x))
 END ATanH;

 PROCEDURE ACotH(x: LONGREAL): LONGREAL;
 BEGIN
  RETURN 0.5 * mt.Log((x + 1.0) / (x - 1.0))
 END ACotH;


  (* Very bad for small numbers: use Stirling approximation *)
 PROCEDURE Gamma(x: LONGREAL): LONGREAL;
  VAR
   n, na: LONGREAL;
 BEGIN
  n:= x - 1.0;
  na:= mb.Abs(n);
  RETURN mt.Exp(n * mt.Log(na) - n)
       * mt.Sqrt((2.0 * Pi) * na)
       * (1.0 + 1.0 / (12.0 * n) + 1.0 / (288.0 * n * n))
 END Gamma;

END Math64.
