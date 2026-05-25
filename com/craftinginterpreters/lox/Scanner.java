package com.craftinginterpreters.lox;

import static com.craftinginterpreters.lox.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Scanner {

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            // Handle single character tokens
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            // Handle single or double character tokens
            case '!':
                addToken(match('=') ? BANG_EQUAL " BANG");
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            // Handle / -> Can be division or comment
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    // Match shouldn't consume new line, it needs to be handled seperately for interpretable error reports
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            // Ignore white space
            case ' ':
            case '\r':
            case '\t':
                break;
            // Handle new line
            case '\n':
                line++;
                break;
            // Handle string literals
            case '"':
                string();
                break;
            default:
                // Handle numbers
                if (isDigit(c)) {
                    number();
                } else {
                    // Handle unexpected tokens
                    Lox.error(line, "Unexpected character.");
                }
                break;

        }
    }

    private void number() {
        while (isDigit(peek())) advance();
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER,
            Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }
        // The closing ".
        advance();
        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
            if (source.charAt(current) != expected) return false;
            current++;
            return true;
    }

    private char peek() {
      if (isAtEnd()) return '\0';
      return source.charAt(current);
    }

    private char peekNext() {
      if (current + 1 >= source.length()) return '\0';
      return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
       return c >= '0' && c <= '9';
     }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
