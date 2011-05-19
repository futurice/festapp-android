package fi.ruisrock.android.domain.to;

import java.util.ArrayList;
import java.util.List;

public class MapLayerOptions {
	private List<SelectableOption> options;
	
	public MapLayerOptions(List<SelectableOption> options) {
		this.options = options;
	}
	
	public List<SelectableOption> getSelectableOptions() {
		return options;
	}
	
	public boolean isOptionSelected(String optionName) {
		for (SelectableOption o : options) {
			if (o.getName().equals(optionName)) {
				return o.isSelected();
			}
		}
		return false;
	}
	
	public CharSequence[] getOptions() {
		List<CharSequence> optionChars = new ArrayList<CharSequence>();
		for (SelectableOption option : options) {
			optionChars.add(option.getName());
		}
		return optionChars.toArray(new CharSequence[optionChars.size()]);
	}
	
	public boolean[] getOptionBooleans() {
		final boolean[] primitives = new boolean[options.size()];
		
		for (int i = 0; i < options.size(); i++) {
			primitives[i] = options.get(i).isSelected();
		}
	    
	    return primitives;
	}
	
	public void setOptionValue(int i, boolean selected) {
		options.get(i).setSelected(selected);
	}
	
	public String getSelectedValuesAsConcatenatedString() {
		StringBuilder sb = new StringBuilder();
		for (SelectableOption option : options) {
			if (option.isSelected()) {
				sb.append(option.getName());
			}
		}
		return sb.toString();
	}
	
	/*
	public void setOptionValue(String option, boolean value) {
		for (SelectableOption o : options) {
			if (o.getName().equals(option)) {
				o.setSelected(value);
			}
		}
	}
	*/

}
