package com.lpb.mid.ekyc.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class CifUtils {

    public static String genUUID(String str){

        // Chuyển đổi chuỗi số thành byte array
        byte[] byteArray = new byte[255];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);

        // Chuyển đổi chuỗi số thành hai số long (mỗi số long là 8 byte)
        long mostSignificantBits = 0L;
        long leastSignificantBits = 0L;

        // Lấy 8 byte đầu tiên cho mostSignificantBits
        int length = Math.min(8, str.length());
        mostSignificantBits = Long.parseLong(str.substring(0, length));

        // Lấy 8 byte tiếp theo cho leastSignificantBits
        if (str.length() > 8) {
            length = Math.min(16, str.length());
            leastSignificantBits = Long.parseLong(str.substring(8, length));
        }

        buffer.putLong(mostSignificantBits);
        buffer.putLong(leastSignificantBits);

        // Tạo UUID từ byte array
        UUID uuid = UUID.nameUUIDFromBytes(byteArray);
        return uuid.toString();
    }
}
