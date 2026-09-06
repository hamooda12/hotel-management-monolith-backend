package com.example.hotalproject.AI;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Deterministic conversation selection helpers used by the AI orchestration.
 * These tools only manage conversational selection metadata; they never modify
 * the hotel catalog or booking data.
 */
@Component
public class ConversationSelectionTool {

    private final ConversationSelectionState state;

    public ConversationSelectionTool(ConversationSelectionState state) {
        this.state = state;
    }

    @Tool(description = """
            Store the exact hotel search result for this conversation so later
            references such as 'the second one' can be resolved deterministically.
            Pass the hotel list (or a paginated result containing a content list)
            returned by searchHotels.
            """)
    public Map<String, Object> rememberHotelSearchResults(
            @ToolParam(description = "Client conversation ID for the current chat.") String conversationId,
            @ToolParam(description = "The exact hotel search result returned by searchHotels.") Object hotels) {
        state.rememberHotelList(conversationId, normalizeList(hotels));
        return Map.of("stored", true);
    }

    @Tool(description = """
            Resolve a hotel selected by one-based position from the most recent
            stored hotel search result. Use for phrases such as 'the second one',
            'second hotel', 'option 2', or 'number 3'. Never invent an ID.
            """)
    public Map<String, Object> resolveHotelSelection(
            @ToolParam(description = "Client conversation ID for the current chat.") String conversationId,
            @ToolParam(description = "One-based position in the most recent hotel list.") Integer position) {

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
        String hotelName = toText(first(hotel, "name", "hotelName"));
        if (hotelId == null || hotelName == null || hotelName.isBlank()) {
            return Map.of("resolved", false, "reason", "Selected hotel result does not contain a usable ID and name.");
        }

        state.rememberSelectedHotel(conversationId, hotelName, hotelId);
        return Map.of("resolved", true, "hotelId", hotelId, "hotelName", hotelName, "position", position);
    }

    @Tool(description = """
            Store the exact room-type list returned for the currently selected
            hotel. Use it before resolving 'the second room', 'option 2', etc.
            """)
    public Map<String, Object> rememberRoomSearchResults(
            @ToolParam(description = "Client conversation ID for the current chat.") String conversationId,
            @ToolParam(description = "The exact room-type list returned by getRoomTypesByHotel.") Object rooms) {
        state.rememberRoomList(conversationId, normalizeList(rooms));
        return Map.of("stored", true);
    }

    @Tool(description = """
            Resolve a room selected by one-based position from the most recent
            stored room list. Never invent a roomTypeId.
            """)
    public Map<String, Object> resolveRoomSelection(
            @ToolParam(description = "Client conversation ID for the current chat.") String conversationId,
            @ToolParam(description = "One-based position in the most recent room list.") Integer position) {

        Object raw = state.roomList(conversationId);
        if (!(raw instanceof List<?> rooms)) {
            return Map.of("resolved", false, "reason", "No room list is stored for this conversation.");
        }
        if (position == null || position < 1 || position > rooms.size()) {
            return Map.of("resolved", false, "reason", "Room position is outside the most recent room list.");
        }
        Object value = rooms.get(position - 1);
        if (!(value instanceof Map<?, ?> room)) {
            return Map.of("resolved", false, "reason", "Stored room result has an unexpected format.");
        }

        Long roomTypeId = toLong(first(room, "id", "roomTypeId"));
        String roomTypeName = toText(first(room, "name", "roomTypeName"));
        if (roomTypeId == null || roomTypeName == null || roomTypeName.isBlank()) {
            return Map.of("resolved", false, "reason", "Selected room result does not contain a usable ID and name.");
        }

        state.rememberSelectedRoom(conversationId, roomTypeName, roomTypeId);
        return Map.of("resolved", true, "roomTypeId", roomTypeId, "roomTypeName", roomTypeName, "position", position);
    }

    @Tool(description = """
            Return the currently selected hotel and room from deterministic
            conversation state. Use this before booking when the user refers to
            'it', 'that hotel', 'that room', or similar references.
            """)
    public Map<String, Object> getCurrentSelection(
            @ToolParam(description = "Client conversation ID for the current chat.") String conversationId) {
        return Map.of(
                "selectedHotelName", valueOrEmpty(state.selectedHotelName(conversationId)),
                "selectedHotelId", valueOrEmpty(state.selectedHotelId(conversationId)),
                "selectedRoomTypeName", valueOrEmpty(state.selectedRoomTypeName(conversationId)),
                "selectedRoomTypeId", valueOrEmpty(state.selectedRoomTypeId(conversationId))
        );
    }

    private List<?> normalizeList(Object value) {
        if (value instanceof Map<?, ?> map) {
            Object content = map.get("content");
            if (content instanceof List<?> list) {
                return list;
            }
        }
        if (value instanceof List<?> list) {
            return list;
        }
        throw new IllegalArgumentException("Expected a list or a paginated result containing content");
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

    private String toText(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Object valueOrEmpty(Object value) {
        return value == null ? "" : value;
    }
}
