package com.MySessionContext;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistry {
    private static Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    public static synchronized void addSession(HttpSession session) {
        sessions.put(session.getId(), session);
    }

    public static synchronized void removeSession(HttpSession session) {
        sessions.remove(session.getId());
    }

    public static synchronized List<HttpSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    public static synchronized HttpSession getSession(String sessionId){
        return sessions.get(sessionId);
    }


}