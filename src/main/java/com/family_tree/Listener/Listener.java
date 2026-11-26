package com.family_tree.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
		HashMap<Object, Receiver<HolderType>> receiver_map = new HashMap<>();
		private boolean locked = false;

		public void add(Object key, Receiver<HolderType> receiver) {
			if (locked) {
				return;
			}
			
			receiver_map.put(key, receiver);
		}

		public void remove(Object key) {
			if (locked) {
				return;
			}

			receiver_map.remove(key);
		}

		public void fire_signals(Signal<HolderType> signal) {
			for (Receiver<HolderType> receiver : receiver_map.values()) {
				receiver.emit_interface.fire(holder, signal);
			}
		}

		public void fire_signals() {
			fire_signals(new Signal<HolderType>());		
		}
	}

	public record Receiver<HolderType> (
		Object key,
		ISignalFire<HolderType> emit_interface
	){};

	
	public Listener(HolderType holder, Class<SignalEnum> SignalEnumClass) {
		this.signal_count = SignalEnumClass.getEnumConstants().length;
		this.holder = holder;

		receiver_row_list = new ArrayList<>(signal_count);

		for (int i=0; i<signal_count; i++) {
			receiver_row_list.add(new ReceiverRow());
		}
	}

	public void add_receiver(Object key, SignalEnum id, ISignalFire<HolderType> emit_interface) {
		ReceiverRow receiver_row = receiver_row_list.get(id.ordinal());
		Receiver<HolderType> receiver = new Receiver<>(key, emit_interface);
		
		receiver_row.add(key, receiver);	
	}

	public void remove_receiver(Object key, SignalEnum id) {
		receiver_row_list.get(id.ordinal()).remove(key);
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
