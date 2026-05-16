package com.mykogroup.riskclone.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utility to convert between IP/Port and a 6-character alphanumeric lobby code.
 * Assumes players are on the same local network subnet (first 3 octets match).
 */
public class LobbyCodeConverter {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encode(String ip, int port) {
        try {
            byte[] octets = InetAddress.getByName(ip).getAddress();
            // Encode: Last octet (8 bits) + Port (16 bits) = 24 bits
            long value = ((long)(octets[3] & 0xFF) << 16) | (port & 0xFFFF);
            return toBase36(value, 6);
        } catch (UnknownHostException e) {
            return "000000";
        }
    }

    public static class Address {
        public final String ip;
        public final int port;
        public Address(String ip, int port) { this.ip = ip; this.port = port; }
    }

    public static Address decode(String code) throws Exception {
        String hostIp = InetAddress.getLocalHost().getHostAddress();
        return new Address(decodeIp(code, hostIp), decodePort(code));
    }

    public static String decodeIp(String code, String clientIp) throws Exception {
        long value = fromBase36(code);
        int lastOctet = (int) ((value >> 16) & 0xFF);
        
        byte[] clientOctets = InetAddress.getByName(clientIp).getAddress();
        // Prepend first 3 octets of client IP
        return (clientOctets[0] & 0xFF) + "." + (clientOctets[1] & 0xFF) + "." + (clientOctets[2] & 0xFF) + "." + lastOctet;
    }

    public static int decodePort(String code) throws Exception {
        long value = fromBase36(code);
        return (int) (value & 0xFFFF);
    }

    private static String toBase36(long value, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.insert(0, ALPHABET.charAt((int) (value % 36)));
            value /= 36;
        }
        return sb.toString();
    }

    private static long fromBase36(String code) {
        long value = 0;
        for (char c : code.toUpperCase().toCharArray()) {
            int digit = ALPHABET.indexOf(c);
            if (digit < 0) throw new IllegalArgumentException("Invalid character in code: " + c);
            value = value * 36 + digit;
        }
        return value;
    }
}
