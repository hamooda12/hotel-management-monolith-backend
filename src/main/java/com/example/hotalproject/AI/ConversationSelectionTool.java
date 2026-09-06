package com.example.hotalproject.AI;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Deterministic conversation selection tools. These are intentionally read/
 * remember-only operations: they never modify hotel catalog data.
 */
@Component
public class ConversationSelectionTool {

    private final ConversationSelectionState state;

    public ConversationSelectionTool(ConversationSelectionState state) {
        this.state = state;
    }

    @Tool(description = """
            Resolve a hotel selected by position from the MOST RECENT hotel
            search result in this conversation. Use this when the user says
            'the first one', 'the second hotel', 'option 2', 'number 3', etc.
            Never invent an ID. The hotel list must come from a previous
            searchHotels result supplied to rememberHotelSearchResults.
            """)
    public Map<String, Object> resolveHotelSelection(
            @ToolParam(description = "Client conversation ID used for this chat.") String conversationId,
            @ToolParam(description = "One-based position in the most recent hotel list, such as 2 for 'the second one'.") Integer position) {

        Object raw = state.hotelList(conversationId);
        if (!(raw instanceof List<?> hotels)) {
            return Map.of("resolved", false, "reason", "No hotel search result is stored for this conversation.");
        }

        if (position == null || position < 1 || position > hotels.size()) {
            return Map.of("resolved", false, "reason", "Hotel position is outside the most recent hotel list.");
        }

        Object value = hotels.get(position - 1);
        if (!(value instanceof Map<?, ?> hotel)) {
            return Map.of("resolved", false, "reason", "Stored hotel result has an unexpected format.");
        }

        Long hotelId = toLong(first(hotel, "id", "hotelId"));
        String hotelName = toString(first(hotel, "name", "hotelName"));
        if (hotelId == null || hotelName == null) {
            return Map.of("resolved", false, "reason", "Selected hotel result does not contain a usable ID and name.");
        }

        state.rememberSelectedHotel(conversationId, hotelName, hotelId);
        return Map.of(
                "resolved", true,
                "hotelId", hotelId,
                "hotelName", hotelName,
                "position", position
        );
    }

    @Tool(description = """
            Resolve a room type selected by position from the MOST RECENT room
            list stored for the current conversation. Use this after the user
            selects a hotel and the assistant retrieved that hotel's rooms.
            Never invent a roomTypeId.
            """)
    public Map<String, Object> resolveRoomSelection(
            @ToolParam(description = "Client conversation ID used for this chat.") String conversationId,
            @ToolParam(description = "One-based position in the most recent room list.") Integer position) {

        Object raw = state.roomList(conversationId);
        if (!(raw instanceof List<?> rooms)) {
            return Map.of("resolved", false, "reason", "No room search result is stored for this conversation.");
        }

        if (position == null || position < 1 || position > rooms.size()) {
            return Map.of("resolved", false, "reason", "Room position is outside the most recent room list.");
        }

        Object value = rooms.get(position - 1);
        if (!(value instanceof Map<?, ?> room)) {
            return Map.of("resolved", false, "reason", "Stored room result has an unexpected format.");
        }

        Long roomTypeId = toLong(first(room, "id", "roomTypeId"));
        String roomTypeName = toString(first(room, "name", "roomTypeName"));
        if (roomTypeId == null || roomTypeName == null) {
            return Map.of("resolved", false, "reason", "Selected room result does not contain a usable ID and name.");
        }

        state.rememberSelectedRoom(conversationId, roomTypeName, roomTypeId);
        return Map.of(
                "resolved", true,
                "roomTypeId", roomTypeId,
                "roomTypeName", roomTypeName,
                "position", position
        );
    }

    @Tool(description = """
            Get the currently selected hotel and room from deterministic
            conversation state. Use this before booking when the user refers
            to 'it', 'that hotel', 'that room', or another implicit reference.
            """)
    public Map<String, Object> getCurrentSelection(
            @ToolParam(description = "Client conversation ID used for this chat.") String conversationId) {

        List<Object> result = new ArrayList<>();
        result.add(state.selectedHotelName(conversationId));
        result.add(state.selectedHotelId(conversationId));
        result.add(state.selectedRoomTypeName(conversationId));
        result.add(state.selectedRoomTypeId(conversationId));

        return Map.of(
                "selectedHotelName", result.get(0) == null ? "" : result.get(0),
                "selectedHotelId", result.get(1) == null ? "" : result.get(1),
                "selectedRoomTypeName", result.get(2) == null ? "" : result.get(2),
                "selectedRoomTypeId", result.get(3) == null ? "" : result.get(3)
        );
    }

    private Object first(Map<?, ?> map, String... keys) {
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return null;
    }

    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String toString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
