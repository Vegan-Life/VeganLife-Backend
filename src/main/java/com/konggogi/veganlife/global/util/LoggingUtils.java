package com.konggogi.veganlife.global.util;


import com.konggogi.veganlife.global.aop.domain.MethodSignature;
import com.konggogi.veganlife.global.exception.ApiException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class LoggingUtils {

    // 출처: https://code-boki.tistory.com/41 [개발꾼:티스토리]
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String RED_UNDERLINED = "\033[4;31m";
    public static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 메서드 인자 출력 */
    public static void printArgumentLog(Object arg) {
        log.debug("type: {} | value: {}", arg.getClass().getSimpleName(), arg);
    }

    /** 커스텀 예외 */
    public static void exceptionLog(
            MethodSignature signature, HttpStatus httpStatus, ApiException e) {
        String errorDate = setTextColor(ANSI_YELLOW, LocalDateTime.now().format(format));
        String exceptionName = setTextColor(ANSI_PURPLE, e.getErrorCode().getCode());
        String status = setTextColor(ANSI_RED, httpStatus.toString());
        String controllerName = setTextColor(ANSI_GREEN, signature.className());
        String methodName = setTextColor(ANSI_BLUE, signature.methodName());
        String message = setTextColor(ANSI_RED, e.getErrorCode().getDescription());
        String lineNumber =
                setTextColor(RED_UNDERLINED, String.valueOf(e.getStackTrace()[0].getLineNumber()));
        printExceptionLog(
                errorDate, controllerName, methodName, lineNumber, exceptionName, status, message);
    }

    /** Java Common 예외 */
    public static void exceptionLog(MethodSignature signature, HttpStatus httpStatus, Exception e) {
        String errorDate = setTextColor(ANSI_YELLOW, LocalDateTime.now().format(format));
        String exceptionName = setTextColor(ANSI_PURPLE, e.getClass().getSimpleName());
        String status = setTextColor(ANSI_RED, httpStatus.toString());
        String controllerName = setTextColor(ANSI_GREEN, signature.className());
        String methodName = setTextColor(ANSI_BLUE, signature.methodName());
        String message = setTextColor(ANSI_RED, e.getMessage());
        String lineNumber =
                setTextColor(RED_UNDERLINED, String.valueOf(e.getStackTrace()[0].getLineNumber()));
        printExceptionLog(
                errorDate, controllerName, methodName, lineNumber, exceptionName, status, message);
    }

    /** Java Common 예외일 경우 메세지 커스텀 가능 */
    public static void exceptionLog(
            MethodSignature signature, HttpStatus httpStatus, Exception e, String detail) {
        String errorDate = setTextColor(ANSI_YELLOW, LocalDateTime.now().format(format));
        String exceptionName = setTextColor(ANSI_PURPLE, e.getClass().getSimpleName());
        String status = setTextColor(ANSI_RED, httpStatus.toString());
        String controllerName = setTextColor(ANSI_GREEN, signature.className());
        String methodName = setTextColor(ANSI_BLUE, signature.methodName());
        String message = setTextColor(ANSI_RED, detail);
        String lineNumber =
                setTextColor(RED_UNDERLINED, String.valueOf(e.getStackTrace()[0].getLineNumber()));
        printExceptionLog(
                errorDate, controllerName, methodName, lineNumber, exceptionName, status, message);
    }

    private static String setTextColor(String color, String message) {
        return color + message + ANSI_RESET;
    }

    private static void printExceptionLog(
            String errorDate,
            String controllerName,
            String methodName,
            String lineNumber,
            String exceptionName,
            String status,
            String message) {
        log.error(
                "\n[Time: {} | Class: {} | Method: {} | LineNumber: {}]\n[Exception: {} | Status: {} | Message: {}]",
                errorDate,
                controllerName,
                methodName,
                lineNumber,
                exceptionName,
                status,
                message);
    }
}
