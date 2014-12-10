/*******************************************************************************
 * This file is part of logisim-evolution.
 *
 *   logisim-evolution is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   logisim-evolution is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with logisim-evolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Original code by Carl Burch (http://www.cburch.com), 2011.
 *   Subsequent modifications by :
 *     + Haute École Spécialisée Bernoise
 *       http://www.bfh.ch
 *     + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *       http://hepia.hesge.ch/
 *     + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *       http://www.heig-vd.ch/
 *   The project is currently maintained by :
 *     + REDS Institute - HEIG-VD
 *       Yverdon-les-Bains, Switzerland
 *       http://reds.heig-vd.ch
 *******************************************************************************/
package com.cburch.logisim.std.tcl;

import java.util.ArrayList;
import java.util.List;

import com.cburch.logisim.instance.Port;

/**
 * This is a static TCL component. It onlyy defines the interface as all other
 * things are defined by the parent class.
 *
 * You can use this as an example to create other static TCL components.
 *
 * You may notice that this class is dynamically loaded. You have to define it
 * int Tcl.java library. If you change the name, older circuits will not be able
 * to load the rightful component, so please don't. But if you need to change
 * the display name, you can do this in the resource files (std.properties).
 */
public class TclConsoleReds extends TclComponent {

	public TclConsoleReds() {
		super("TclConsoleReds", Strings.getter("tclConsoleReds"));

		List<PortDescription> inputsDesc = new ArrayList<PortDescription>();
		List<PortDescription> outputsDesc = new ArrayList<PortDescription>();

		outputsDesc.add(new PortDescription("S0_obs", "output", 1));
		outputsDesc.add(new PortDescription("S1_obs", "output", 1));
		outputsDesc.add(new PortDescription("S2_obs", "output", 1));
		outputsDesc.add(new PortDescription("S3_obs", "output", 1));
		outputsDesc.add(new PortDescription("S4_obs", "output", 1));
		outputsDesc.add(new PortDescription("S5_obs", "output", 1));
		outputsDesc.add(new PortDescription("S6_obs", "output", 1));
		outputsDesc.add(new PortDescription("S7_obs", "output", 1));
		outputsDesc.add(new PortDescription("S8_obs", "output", 1));
		outputsDesc.add(new PortDescription("S9_obs", "output", 1));
		outputsDesc.add(new PortDescription("S10_obs", "output", 1));
		outputsDesc.add(new PortDescription("S11_obs", "output", 1));
		outputsDesc.add(new PortDescription("S12_obs", "output", 1));
		outputsDesc.add(new PortDescription("S13_obs", "output", 1));
		outputsDesc.add(new PortDescription("S14_obs", "output", 1));
		outputsDesc.add(new PortDescription("S15_obs", "output", 1));
		outputsDesc.add(new PortDescription("Val_A_obs", "output", 16));
		outputsDesc.add(new PortDescription("Val_B_obs", "output", 16));

		inputsDesc.add(new PortDescription("Hex0_sti", "input", 4));
		inputsDesc.add(new PortDescription("Hex1_sti", "input", 4));
		inputsDesc.add(new PortDescription("L0_sti", "input", 1));
		inputsDesc.add(new PortDescription("L1_sti", "input", 1));
		inputsDesc.add(new PortDescription("L2_sti", "input", 1));
		inputsDesc.add(new PortDescription("L3_sti", "input", 1));
		inputsDesc.add(new PortDescription("L4_sti", "input", 1));
		inputsDesc.add(new PortDescription("L5_sti", "input", 1));
		inputsDesc.add(new PortDescription("L6_sti", "input", 1));
		inputsDesc.add(new PortDescription("L7_sti", "input", 1));
		inputsDesc.add(new PortDescription("L8_sti", "input", 1));
		inputsDesc.add(new PortDescription("L9_sti", "input", 1));
		inputsDesc.add(new PortDescription("L10_sti", "input", 1));
		inputsDesc.add(new PortDescription("L11_sti", "input", 1));
		inputsDesc.add(new PortDescription("L12_sti", "input", 1));
		inputsDesc.add(new PortDescription("L13_sti", "input", 1));
		inputsDesc.add(new PortDescription("L14_sti", "input", 1));
		inputsDesc.add(new PortDescription("L15_sti", "input", 1));
		inputsDesc.add(new PortDescription("Result_A_sti", "input", 16));
		inputsDesc.add(new PortDescription("Result_B_sti", "input", 16));
		inputsDesc.add(new PortDescription("seg7_sti", "input", 8));
		inputsDesc.add(new PortDescription("sysclk_i", "input", 1));

		Port[] inputs = new Port[inputsDesc.size()];
		Port[] outputs = new Port[outputsDesc.size()];

		for (int i = 0; i < inputsDesc.size(); i++) {
			PortDescription desc = inputsDesc.get(i);
			inputs[i] = new Port(0, (i * PORT_GAP) + HEIGHT, desc.getType(),
					desc.getWidth());
			inputs[i].setToolTip(Strings.getter(desc.getName()));
		}

		for (int i = 0; i < outputsDesc.size(); i++) {
			PortDescription desc = outputsDesc.get(i);
			outputs[i] = new Port(WIDTH, (i * PORT_GAP) + HEIGHT,
					desc.getType(), desc.getWidth());
			outputs[i].setToolTip(Strings.getter(desc.getName()));
		}

		setPorts(inputs, outputs);
	}

	@Override
	public String getDisplayName() {
		return Strings.get("tclConsoleReds");
	}
}