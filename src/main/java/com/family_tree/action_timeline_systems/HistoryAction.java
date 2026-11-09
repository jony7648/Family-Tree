package com.family_tree.action_timeline_systems;

public record HistoryAction (
	String event_name,
	Object value,
	ActionEvent event
) {
	public static interface ActionEvent {
		void perform_action(Object value);
		void undo_action(Object value);
	}

	@Override
	public String toString() {
		return event_name;
	}
};


