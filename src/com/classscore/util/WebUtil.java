package com.classscore.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Web 层通用小工具。
 */
public final class WebUtil {

    private WebUtil() {
    }

    public static String trim(String value) {
        return value == null ? null : value.trim();
    }

    public static String blankToNull(String value) {
        String trimmed = trim(value);
        return trimmed == null || trimmed.isEmpty() ? null : trimmed;
    }

    public static Integer intOrNull(String value) {
        String trimmed = blankToNull(value);
        if (trimmed == null) {
            return null;
        }
        try {
            return Integer.valueOf(trimmed);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static int intOrDefault(String value, int defaultValue) {
        Integer parsed = intOrNull(value);
        return parsed == null ? defaultValue : parsed;
    }

    public static void flash(HttpSession session, String type, String message) {
        if (session != null) {
            session.setAttribute("flashType", type);
            session.setAttribute("flashMessage", message);
        }
    }

    public static String contextPath(HttpServletRequest request) {
        String context = request.getContextPath();
        return context == null ? "" : context;
    }
}
