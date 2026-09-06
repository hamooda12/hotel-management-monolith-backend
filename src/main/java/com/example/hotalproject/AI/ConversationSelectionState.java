package com.example.hotalproject.AI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Deterministic per-conversation selection state used to resolve references
 * such as "the second one" without relying on the model to reconstruct IDs.
 *
 * This state is intentionally small and ephemeral. The persisted transcript
 * remains the source for conversational history; this cache only stores the
 * latest structured catalog selections needed by the booking flow.
 */
public final class ConversationSelectionState {

    private final Map<String, State> states = new ConcurrentHashMap<>();

    public void rememberHotelList(String conversationId, Object hotels) {
        states.compute(conversationId, (key, current) -> {
            State state = current == null ? new State() : current;
            state.hotels = hotels;
            return state;
        });
    }

    public Object hotelList(String conversationId) {
        State state = states.get(conversationId);
        return state == null ? null : state.hotels;
    }

    public void rememberSelectedHotel(String conversationId, String hotelName, Long hotelId) {
        states.compute(conversationId, (key, current) -> {
            State state = current == null ? new State() : current;
            state.selectedHotelName = hotelName;
            state.selectedHotelId = hotelId;
            state.selectedRoomTypeName = null;
            state.selectedRoomTypeId = null;
            return state;
        });
    }

    public Long selectedHotelId(String conversationId) {
        State state = states.get(conversationId);
        return state == null ? null : state.selectedHotelId;
    }

    public String selectedHotelName(String conversationId) {
        State state = states.get(conversationId);
        return state == null ? null : state.selectedHotelName;
    }

    public void rememberRoomList(String conversationId, Object rooms) {
        states.compute(conversationId, (key, current) -> {
            State state = current == null ? new State() : current;
            state.rooms = rooms;
            return state;
        });
    }

    public Object roomList(String conversationId) {
        State state = states.get(conversationId);
        return state == null ? null : state.rooms;
    }

    public void rememberSelectedRoom(String conversationId, String roomTypeName, Long roomTypeId) {
        states.compute(conversationId, (key, current) -> {
            State state = current == null ? new State() : current;
            state.selectedRoomTypeName = roomTypeName;
            state.selectedRoomTypeId = roomTypeId;
            return state;
        });
    }

    public Long selectedRoomTypeId(String conversationId) {
        State state = states.get(conversationId);
        return state == null ? null : state.selectedRoomTypeId;
    }

    public String selectedRoomTypeName(String conversationId) {
        State state = states.get(conversationId);
        return state == null ? null : state.selectedRoomTypeName;
    }

    public void clear(String conversationId) {
        states.remove(conversationId);
    }

    private static final class State {
        private Object hotels;
        private Object rooms;
        private String selectedHotelName;
        private Long selectedHotelId;
        private String selectedRoomTypeName;
        private Long selectedRoomTypeId;
    }
}
