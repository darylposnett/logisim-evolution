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

package com.cburch.logisim.gui.opts;
import static com.cburch.logisim.gui.opts.Strings.S;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.cburch.logisim.file.MouseMappings;
import com.cburch.logisim.gui.generic.AttrTable;
import com.cburch.logisim.gui.generic.AttrTableModel;
import com.cburch.logisim.gui.generic.ProjectExplorer;
import com.cburch.logisim.gui.generic.ProjectExplorerToolNode;
import com.cburch.logisim.gui.main.AttrTableToolModel;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.InputEventUtil;

class MouseOptions extends OptionsPanel {
  private class AddArea extends JPanel {
    private static final long serialVersionUID = 1L;

    public AddArea() {
      setPreferredSize(new Dimension(75, 60));
      setMinimumSize(new Dimension(75, 60));
      setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createEtchedBorder()));
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Dimension sz = getSize();
      g.setFont(remove.getFont());
      String label1;
      String label2;
      if (curTool == null) {
        g.setColor(Color.GRAY);
        label1 = S.get("mouseMapNone");
        label2 = null;
      } else {
        g.setColor(Color.BLACK);
        label1 = S.get("mouseMapText");
        label2 = S.fmt("mouseMapText2", curTool.getDisplayName());
      }
      FontMetrics fm = g.getFontMetrics();
      int x1 = (sz.width - fm.stringWidth(label1)) / 2;
      if (label2 == null) {
        int y = Math.max(0,
            (sz.height - fm.getHeight()) / 2 + fm.getAscent() - 2);
        g.drawString(label1, x1, y);
      } else {
        int x2 = (sz.width - fm.stringWidth(label2)) / 2;
        int y = Math.max(0,
            (sz.height - 2 * fm.getHeight()) / 2
            + fm.getAscent() - 2);
        g.drawString(label1, x1, y);
        y += fm.getHeight();
        g.drawString(label2, x2, y);
      }
    }
  }

  private class MappingsModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    ArrayList<Integer> cur_keys;

    MappingsModel() {
      fireTableStructureChanged();
    }

    // AbstractTableModel methods
    @Override
    public void fireTableStructureChanged() {
      cur_keys = new ArrayList<Integer>(getOptions().getMouseMappings()
          .getMappedModifiers());
      Collections.sort(cur_keys);
      super.fireTableStructureChanged();
    }

    public int getColumnCount() {
      return 2;
    }

    // other methods
    Integer getKey(int row) {
      return cur_keys.get(row);
    }

    int getRow(Integer mods) {
      int row = Collections.binarySearch(cur_keys, mods);
      if (row < 0)
        row = -(row + 1);
      return row;
    }

    public int getRowCount() {
      return cur_keys.size();
    }

    Tool getTool(int row) {
      if (row < 0 || row >= cur_keys.size())
        return null;
      Integer key = cur_keys.get(row);
      return getOptions().getMouseMappings().getToolFor(key.intValue());
    }

    public Object getValueAt(int row, int column) {
      Integer key = cur_keys.get(row);
      if (column == 0) {
        return InputEventUtil.toDisplayString(key.intValue());
      } else {
        Tool tool = getOptions().getMouseMappings().getToolFor(key);
        return tool.getDisplayName();
      }
    }
  }

  private class MyListener implements ActionListener, MouseListener,
          ListSelectionListener, MouseMappings.MouseMappingsListener,
          ProjectExplorer.Listener {
    //
    // ActionListener method
    //
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == remove) {
        int row = mappings.getSelectedRow();
        getProject().doAction(
            OptionsActions.removeMapping(getOptions()
              .getMouseMappings(), model.getKey(row)));
        row = Math.min(row, model.getRowCount() - 1);
        if (row >= 0)
          setSelectedRow(row);
      }
    }

    public void doubleClicked(ProjectExplorer.Event event) {
    }

    public JPopupMenu menuRequested(ProjectExplorer.Event event) {
      return null;
    }

    //
    // MouseListener methods
    //
    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    //
    // MouseMappingsListener method
    //
    public void mouseMappingsChanged() {
      model.fireTableStructureChanged();
    }

    public void mousePressed(MouseEvent e) {
      if (e.getSource() == addArea && curTool != null) {
        Tool t = curTool.cloneTool();
        Integer mods = Integer.valueOf(e.getModifiersEx());
        getProject().doAction(
            OptionsActions.setMapping(getOptions()
              .getMouseMappings(), mods, t));
        setSelectedRow(model.getRow(mods));
      }
    }

    public void mouseReleased(MouseEvent e) {
    }

    //
    // Explorer.Listener methods
    //
    public void selectionChanged(ProjectExplorer.Event event) {
      Object target = event.getTarget();
      if (target instanceof ProjectExplorerToolNode) {
        Tool tool = ((ProjectExplorerToolNode) target).getValue();
        setCurrentTool(tool);
      } else {
        setCurrentTool(null);
      }
    }

    //
    // ListSelectionListener method
    //
    public void valueChanged(ListSelectionEvent e) {
      int row = mappings.getSelectedRow();
      if (row < 0) {
        remove.setEnabled(false);
        attrTable.setAttrTableModel(null);
      } else {
        remove.setEnabled(true);
        Tool tool = model.getTool(row);
        Project proj = getProject();
        AttrTableModel model;
        if (tool.getAttributeSet() == null) {
          model = null;
        } else {
          model = new AttrTableToolModel(proj, tool);
        }
        attrTable.setAttrTableModel(model);
      }
    }
  }

  private static final long serialVersionUID = 1L;

  private MyListener listener = new MyListener();
  private Tool curTool = null;
  private MappingsModel model;

  private ProjectExplorer explorer;
  private JPanel addArea = new AddArea();
  private JTable mappings = new JTable();
  private AttrTable attrTable;
  private JButton remove = new JButton();

  public MouseOptions(OptionsFrame window) {
    super(window, new GridLayout(1, 3));

    explorer = new ProjectExplorer(getProject(), true /* show all */);
    explorer.setListener(listener);

    // Area for adding mappings
    addArea.addMouseListener(listener);

    // Area for viewing current mappings
    model = new MappingsModel();
    mappings.setTableHeader(null);
    mappings.setModel(model);
    mappings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    mappings.getSelectionModel().addListSelectionListener(listener);
    mappings.clearSelection();
    JScrollPane mapPane = new JScrollPane(mappings);

    // Button for removing current mapping
    JPanel removeArea = new JPanel();
    remove.addActionListener(listener);
    remove.setEnabled(false);
    removeArea.add(remove);

    // Area for viewing/changing attributes
    attrTable = new AttrTable(getOptionsFrame());

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gridbag);
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.gridheight = 4;
    gbc.fill = GridBagConstraints.BOTH;
    JScrollPane explorerPane = new JScrollPane(explorer,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    gridbag.setConstraints(explorerPane, gbc);
    add(explorerPane);
    gbc.weightx = 0.0;
    JPanel gap = new JPanel();
    gap.setPreferredSize(new Dimension(10, 10));
    gridbag.setConstraints(gap, gbc);
    add(gap);
    gbc.weightx = 1.0;
    gbc.gridheight = 1;
    gbc.gridx = 2;
    gbc.gridy = GridBagConstraints.RELATIVE;
    gbc.weighty = 0.0;
    gridbag.setConstraints(addArea, gbc);
    add(addArea);
    gbc.weighty = 1.0;
    gridbag.setConstraints(mapPane, gbc);
    add(mapPane);
    gbc.weighty = 0.0;
    gridbag.setConstraints(removeArea, gbc);
    add(removeArea);
    gbc.weighty = 1.0;
    gridbag.setConstraints(attrTable, gbc);
    add(attrTable);

    getOptions().getMouseMappings().addMouseMappingsListener(listener);
    setCurrentTool(null);
  }

  @Override
  public String getHelpText() {
    return S.get("mouseHelp");
  }

  @Override
  public String getTitle() {
    return S.get("mouseTitle");
  }

  @Override
  public void localeChanged() {
    remove.setText(S.get("mouseRemoveButton"));
    addArea.repaint();
  }

  private void setCurrentTool(Tool t) {
    curTool = t;
    localeChanged();
  }

  private void setSelectedRow(int row) {
    if (row < 0)
      row = 0;
    if (row >= model.getRowCount())
      row = model.getRowCount() - 1;
    if (row >= 0) {
      mappings.getSelectionModel().setSelectionInterval(row, row);
    }
  }
}
