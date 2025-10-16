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

    public void SetPalette(int color, int red, int green, int blue);

    public void SetCopyMode(EnumSet<Modes> dm);

    public void SetPlanes(long planes, boolean clear);

    public void SetPen(long color);

    public void SetBPen(long color);

    public void SetPat(int v);

    public void SetPattern(/* VAR */ byte[] pattern);

    public void DrawPixel(int x, int y);

    public void DrawLine(int x1, int y1, int x2, int y2);

    public void OpenPoly(int x, int y);

    public void AddLine(int x, int y);

    public void FillPoly();

    public void FillRect(int x1, int y1, int x2, int y2);

    public void FillEllipse(int x1, int y1, int x2, int y2);

    public void FillFlood(int x, int y, long borderCol);

    public void SetTextMode(EnumSet<TextModes> tm);

    public void SetTextSize(int s);

    public void SetTextPos(int x, int y);

    public int TextWidth(Runtime.IRef<String> t);

    public void DrawText(Runtime.IRef<String> t);

    public void FillShadow(AreaPtr ma, int sx, int sy, int dx, int dy, int width, int height);

    public void DrawShadow(AreaPtr ma, int sx, int sy, int dx, int dy, int width, int height);

    public void DrawImage(Image image, int sx, int sy, int dx, int dy, int width, int height);

    public void CopyRect(AreaPtr sa, int sx, int sy, int dx, int dy, int width, int height);

    public void CopyShadow(AreaPtr sa, AreaPtr ma, int sx, int sy, int dx, int dy, int width, int height);

    public void CopyMask(AreaPtr sa, AreaPtr ma, int sx, int sy, int dx, int dy, int width, int height);

    public void ScrollRect(int x, int y, int width, int height, int dx, int dy);

    public void ScaleRect(AreaPtr sa, int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2);

    public void WaitTOF();

}
