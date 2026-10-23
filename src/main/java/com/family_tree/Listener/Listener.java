package com.family_tree.Listener;

import java.util.ArrayList;
import java.util.Collections;

public class Listener<HolderType, SignalEnum extends Enum<SignalEnum>> {
	int signal_count = 0;
	HolderType holder;
	ArrayList<ReceiverRow> receiver_row_list;
	
	public interface ISignalFire<HolderType> {
		public void fire(HolderType holder, Signal<HolderType> raw_emit_signal);
	}

	
	public static class EmptySignal {};

	public static class Signal<HolderType> {
		public HolderType emitter;
		public Object sig_data;

		public Signal(HolderType emitter, Object sig_data) {
			this.emitter = emitter;
			this.sig_data = sig_data;
		}

		public Signal() {}
	}

	private class ReceiverRow {
		ArrayList<ISignalFire<HolderType>> receiver_list = new ArrayList<>();
		private boolean locked = false;

		public ISignalFire<HolderType> get(int id) {
			return receiver_list.get(id);
		}

		public void add(ISignalFire<HolderType> emit_interface) {
			if (locked) {
				return;
			}
			
			receiver_list.add(emit_interface);
		}

		public void remove(int id) {
			if (locked) {
				return;
			}

			receiver_list.remove(id);
		}

		public void fire_signals(Signal<HolderType> signal) {
			for (ISignalFire<HolderType> signal_interface : receiver_list) {
				signal_interface.fire(holder, signal);
			}
		}

		public void fire_signals() {
			fire_signals(new Signal<HolderType>());		
		}
	}

	
	public Listener(HolderType holder, Class<SignalEnum> SignalEnumClass) {
		this.signal_count = SignalEnumClass.getEnumConstants().length;
		this.holder = holder;

		receiver_row_list = new ArrayList<>(signal_count);

		for (int i=0; i<signal_count; i++) {
			receiver_row_list.add(new ReceiverRow());
		}
	}

	public void add_receiver(SignalEnum id, ISignalFire<HolderType> emit_interface) {
		ReceiverRow receiver_row = receiver_row_list.get(id.ordinal());
		receiver_row.add(emit_interface);	
	}

	public void fire_signal(SignalEnum id, Signal<HolderType> signal) {
		ReceiverRow receiver_row = receiver_row_list.get(id.ordinal());
		receiver_row.fire_signals(signal);
	}

	public void fire_signal(SignalEnum id) {
		ReceiverRow receiver_row = receiver_row_list.get(id.ordinal());
		receiver_row.fire_signals();
	}
}
