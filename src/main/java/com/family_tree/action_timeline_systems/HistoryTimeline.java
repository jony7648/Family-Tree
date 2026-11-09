package com.family_tree.action_timeline_systems;

import com.family_tree.structures.classes.node_structures.PositionalList;
import javax.swing.DefaultListModel;

public class HistoryTimeline {
	private DefaultListModel<HistoryAction> _list_model = new DefaultListModel<>();
	private int _list_index = 0;

	public void add(HistoryAction action) {
		if (_list_index == _list_model.size()) {
			_list_model.addElement(action);
			_list_index++;
			return;
		}
		
		_list_model.setElementAt(action, _list_index);
		_list_model.removeRange(_list_index+1, _list_model.size());
	}

	public void redo() {
		if (_list_index < _list_model.size() - 1) {
			return;
		}

		_list_index++;
		HistoryAction action = _list_model.getElementAt(_list_index);
		action.event().perform_action(action.value());

	}

	public void undo() {
		if (_list_index < 1) {
			return;
		}
		
		HistoryAction action = _list_model.getElementAt(_list_index);
		
		_list_index--;
		action.event().undo_action(action.value());
	}

	public DefaultListModel<HistoryAction> get_list_model() {
		return _list_model;
	}
}
