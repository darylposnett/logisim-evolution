/**
 * This file is part of Logisim-evolution.
 *
 * Logisim-evolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Logisim-evolution is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Logisim-evolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * Subsequent modifications by:
 *   + Haute École Spécialisée Bernoise
 *     http://www.bfh.ch
 *   + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *     http://hepia.hesge.ch/
 *   + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *     http://www.heig-vd.ch/
 *   + REDS Institute - HEIG-VD, Yverdon-les-Bains, Switzerland
 *     http://reds.heig-vd.ch
 * This version of the project is currently maintained by:
 *   + Kevin Walsh (kwalsh@holycross.edu, http://mathcs.holycross.edu/~kwalsh)
 */

package com.cburch.logisim.comp;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.WireSet;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Palette;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.util.GraphicsUtil;

public class ComponentDrawContext {
  private static final int PIN_OFFS = 2;
  private static final int PIN_RAD = 4;

  private java.awt.Component dest;
  private Circuit circuit;
  private CircuitState circuitState;
  private Graphics2D base;
  private Graphics2D g;
  // private boolean showState;
  // private boolean showColor;
  private boolean printView;
  private WireSet highlightedWires;
  private InstancePainter instancePainter;

  // PrintView (available when printing, when exporting as an image, and from
  // the app preferences frame) means the image will not depend on the current
  // simulation state or othe values, and it will typically use a dark-on-white
  // palette so that most things are plain black or gray on white. The grid is
  // hidden as well, but the canvas background (usually plain white) is shown.
  // Other palettes are possible, resulting in more colorful graphics.
  //  - simulator state is not shown (some components show their width instead)
  //  - constants are drawn using LINE_COLOR instead of value-dependent color
  //  - wires are drawn using LINE_COLOR instead of value-dependent color
  //  - resistors, etc., use LINE_COLOR instead of value-dependent color
  //  - grid is not shown, but canvas background color is used
  //  - some components don't show their pins, or hide unconnected pins
  //
  // LayoutThumbnailView (miniature view seen at lower right when designing a
  // custom appearance) means the same thing, but uses the standard palette.

  public ComponentDrawContext(java.awt.Component dest, Circuit circuit,
      CircuitState circuitState, Graphics base, Graphics g) {
    this(dest, circuit, circuitState, base, g, false);
  }

  public ComponentDrawContext(java.awt.Component dest, Circuit circuit,
      CircuitState circuitState, Graphics base, Graphics g,
      boolean printView) {
    this.dest = dest;
    this.circuit = circuit;
    this.circuitState = circuitState;
    this.base = (Graphics2D)base;
    this.g = (Graphics2D)g;
    this.showState = true;
    this.showColor = true;
    this.printView = printView;
    this.highlightedWires = WireSet.EMPTY;
    this.instancePainter = new InstancePainter(this, null);
  }

  //
  // helper methods
  //
  public void drawBounds(Component comp) {
    GraphicsUtil.switchToWidth(g, 2);
    Bounds bds = comp.getBounds();
    g.setColor(Palette.SOLID_COLOR);
    g.fillRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
    g.setColor(Palette.LINE_COLOR);
    g.drawRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
    GraphicsUtil.switchToWidth(g, 1);
  }

  public void drawClock(Component comp, int i, Direction dir) {
    Color curColor = g.getColor();
    g.setColor(Palette.LINE_COLOR);
    GraphicsUtil.switchToWidth(g, 2);

    EndData e = comp.getEnd(i);
    Location pt = e.getLocation();
    int x = pt.getX();
    int y = pt.getY();
    final int CLK_SZ = 4;
    final int CLK_SZD = CLK_SZ - 1;
    if (dir == Direction.NORTH) {
      g.drawLine(x - CLK_SZD, y - 1, x, y - CLK_SZ);
      g.drawLine(x + CLK_SZD, y - 1, x, y - CLK_SZ);
    } else if (dir == Direction.SOUTH) {
      g.drawLine(x - CLK_SZD, y + 1, x, y + CLK_SZ);
      g.drawLine(x + CLK_SZD, y + 1, x, y + CLK_SZ);
    } else if (dir == Direction.EAST) {
      g.drawLine(x + 1, y - CLK_SZD, x + CLK_SZ, y);
      g.drawLine(x + 1, y + CLK_SZD, x + CLK_SZ, y);
    } else if (dir == Direction.WEST) {
      g.drawLine(x - 1, y - CLK_SZD, x - CLK_SZ, y);
      g.drawLine(x - 1, y + CLK_SZD, x - CLK_SZ, y);
    }

    g.setColor(curColor);
    GraphicsUtil.switchToWidth(g, 1);
  }

  public void drawClockSymbol(Component comp, int xpos, int ypos) {
    GraphicsUtil.switchToWidth(g, 2);
    g.drawLine(xpos, ypos - 4, xpos + 8, ypos);
    g.drawLine(xpos, ypos + 4, xpos + 8, ypos);
    GraphicsUtil.switchToWidth(g, 1);
  }

  public void drawDongle(int x, int y) {
    GraphicsUtil.switchToWidth(g, 2);
    g.setColor(Palette.SOLID_COLOR);
    g.fillOval(x - 4, y - 4, 9, 9);
    g.setColor(Palette.LINE_COLOR);
    g.drawOval(x - 4, y - 4, 9, 9);
  }

  public void drawHandle(int x, int y) {
    g.setColor(Color.WHITE);
    g.fillRect(x - 3, y - 3, 7, 7);
    g.setColor(Color.BLACK);
    g.drawRect(x - 3, y - 3, 7, 7);
  }

  public void drawHandle(Location loc) {
    drawHandle(loc.getX(), loc.getY());
  }

  public void drawHandles(Component comp) {
    Bounds b = comp.getBounds(g);
    int left = b.getX();
    int right = left + b.getWidth();
    int top = b.getY();
    int bot = top + b.getHeight();
    drawHandle(right, top);
    drawHandle(left, bot);
    drawHandle(right, bot);
    drawHandle(left, top);
  }

  public void drawPin(Component comp, int i) {
    EndData e = comp.getEnd(i);
    Location pt = e.getLocation();
    Color curColor = g.getColor();
    if (getShowState()) {
      CircuitState state = getCircuitState();
      g.setColor(state.getValue(pt).getColor());
    } else {
      g.setColor(Palette.LINE_COLOR);
    }
    g.fillOval(pt.getX() - PIN_OFFS, pt.getY() - PIN_OFFS, PIN_RAD, PIN_RAD);
    g.setColor(curColor);
  }

  public void drawPin(Component comp, int i, String label, Direction dir) {
    Color curColor = g.getColor();
    if (i < 0 || i >= comp.getEnds().size())
      return;
    EndData e = comp.getEnd(i);
    Location pt = e.getLocation();
    int x = pt.getX();
    int y = pt.getY();
    if (getShowState()) {
      CircuitState state = getCircuitState();
      g.setColor(state.getValue(pt).getColor());
    } else {
      g.setColor(Palette.LINE_COLOR);
    }
    g.fillOval(x - PIN_OFFS, y - PIN_OFFS, PIN_RAD, PIN_RAD);
    g.setColor(curColor);
    if (dir == Direction.EAST) {
      GraphicsUtil.drawText(g, label, x + 3, y, GraphicsUtil.H_LEFT,
          GraphicsUtil.V_CENTER);
    } else if (dir == Direction.WEST) {
      GraphicsUtil.drawText(g, label, x - 3, y, GraphicsUtil.H_RIGHT,
          GraphicsUtil.V_CENTER);
    } else if (dir == Direction.SOUTH) {
      GraphicsUtil.drawText(g, label, x, y - 3, GraphicsUtil.H_CENTER,
          GraphicsUtil.V_BASELINE);
    } else if (dir == Direction.NORTH) {
      GraphicsUtil.drawText(g, label, x, y + 3, GraphicsUtil.H_CENTER,
          GraphicsUtil.V_TOP);
    }
  }

  public void drawPins(Component comp) {
    Color curColor = g.getColor();
    for (EndData e : comp.getEnds()) {
      Location pt = e.getLocation();
      if (getShowState()) {
        CircuitState state = getCircuitState();
        g.setColor(state.getValue(pt).getColor());
      } else {
        g.setColor(Palette.LINE_COLOR);
      }
      g.fillOval(pt.getX() - PIN_OFFS, pt.getY() - PIN_OFFS, PIN_RAD,
          PIN_RAD);
    }
    g.setColor(curColor);
  }

  public void drawRectangle(Component comp) {
    drawRectangle(comp, "");
  }

  public void drawRectangle(Component comp, String label) {
    Bounds bds = comp.getBounds(g);
    drawRectangle(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight(),
        label);
  }

  public void drawRectangle(ComponentFactory source, int x, int y,
      AttributeSet attrs, String label) {
    Bounds bds = source.getOffsetBounds(attrs);
    drawRectangle(source, x + bds.getX(), y + bds.getY(), bds.getWidth(),
        bds.getHeight(), label);
  }

  public void drawRectangle(ComponentFactory source, int x, int y, int width,
      int height, String label) {
    GraphicsUtil.switchToWidth(g, 2);
    g.drawRect(x + 1, y + 1, width - 1, height - 1);
    if (label != null && !label.equals("")) {
      FontMetrics fm = base.getFontMetrics(g.getFont());
      int lwid = fm.stringWidth(label);
      if (height > 20) { // centered at top edge
        g.drawString(label, x + (width - lwid) / 2,
            y + 2 + fm.getAscent());
      } else { // centered overall
        g.drawString(label, x + (width - lwid) / 2,
            y + (height + fm.getAscent()) / 2 - 1);
      }
    }
  }

  public void drawRectangle(int x, int y, int width, int height, String label) {
    GraphicsUtil.switchToWidth(g, 2);
    g.drawRect(x, y, width, height);
    if (label != null && !label.equals("")) {
      FontMetrics fm = base.getFontMetrics(g.getFont());
      int lwid = fm.stringWidth(label);
      if (height > 20) { // centered at top edge
        g.drawString(label, x + (width - lwid) / 2,
            y + 2 + fm.getAscent());
      } else { // centered overall
        g.drawString(label, x + (width - lwid) / 2,
            y + (height + fm.getAscent()) / 2 - 1);
      }
    }
  }

  public Circuit getCircuit() {
    return circuit;
  }

  public CircuitState getCircuitState() {
    return circuitState;
  }

  public java.awt.Component getDestination() {
    return dest;
  }

  public Object getGateShape() {
    return AppPreferences.GATE_SHAPE.get();
  }

  public Graphics2D getGraphics() {
    return g;
  }

  public WireSet getHighlightedWires() {
    return highlightedWires;
  }

  public InstancePainter getInstancePainter() {
    return instancePainter;
  }

  public boolean getShowState() {
    return !printView && showState;
  }

  public boolean isPrintView() {
    return printView;
  }

  public void setGraphics(Graphics g) {
    this.g = (Graphics2D)g;
  }

  public void setHighlightedWires(WireSet value) {
    this.highlightedWires = value == null ? WireSet.EMPTY : value;
  }

  public void setShowColor(boolean value) {
    showColor = value;
  }

  public void setShowState(boolean value) {
    showState = value;
  }

  public boolean shouldDrawColor() {
    return !printView && showColor;
  }

}
