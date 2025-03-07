package ch.chaos.library.graphics;

import java.util.EnumSet;

import ch.chaos.library.Graphics.AreaPtr;
import ch.chaos.library.Graphics.GraphicsErr;
import ch.chaos.library.Graphics.Image;
import ch.chaos.library.Graphics.Modes;
import ch.chaos.library.Graphics.TextModes;
import ch.chaos.library.Memory;
import ch.pitchtech.modula.runtime.Runtime;

public interface IGraphics {

    public void GetGraphicsSysAttr(/* VAR */ Memory.TagItem what);

    public GraphicsErr GetGraphicsErr();

    public AreaPtr CreateArea(Memory.TagItem tags);

    public void DeleteArea(/* VAR */ Runtime.IRef<AreaPtr> a);

    public void AreaToFront();

    public void SwitchArea();

    public void UpdateArea();

    public void SetBuffer(boolean first, boolean off);

    public void GetBuffer(/* VAR */ Runtime.IRef<Boolean> first, /* VAR */ Runtime.IRef<Boolean> off);

    public void SetArea(AreaPtr a);

    public void SetPalette(short color, short red, short green, short blue);

    public void SetCopyMode(EnumSet<Modes> dm);

    public void SetPlanes(long planes, boolean clear);

    public void SetPen(long color);

    public void SetBPen(long color);

    public void SetPat(short v);

    public void SetPattern(/* VAR */ byte[] pattern);

    public void DrawPixel(short x, short y);

    public void DrawLine(short x1, short y1, short x2, short y2);

    public void OpenPoly(short x, short y);

    public void AddLine(short x, short y);

    public void FillPoly();

    public void FillRect(short x1, short y1, short x2, short y2);

    public void FillEllipse(short x1, short y1, short x2, short y2);

    public void FillFlood(short x, short y, long borderCol);

    public void SetTextMode(EnumSet<TextModes> tm);

    public void SetTextSize(short s);

    public void SetTextPos(short x, short y);

    public short TextWidth(Runtime.IRef<String> t);

    public void DrawText(Runtime.IRef<String> t);

    public void FillShadow(AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height);

    public void DrawShadow(AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height);

    public void DrawImage(Image image, short sx, short sy, short dx, short dy, short width, short height);

    public void CopyRect(AreaPtr sa, short sx, short sy, short dx, short dy, short width, short height);

    public void CopyShadow(AreaPtr sa, AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height);

    public void CopyMask(AreaPtr sa, AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height);

    public void ScrollRect(short x, short y, short width, short height, short dx, short dy);

    public void ScaleRect(AreaPtr sa, short sx1, short sy1, short sx2, short sy2, short dx1, short dy1, short dx2, short dy2);

    public void WaitTOF();

}
