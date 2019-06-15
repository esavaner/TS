package zad1;

import java.io.*;
import java.util.Scanner;
import java.util.zip.CRC32;

class Decoder {
    private CRC32 crc = new CRC32();
    private String old;

    String readFile(File coded) {
        String stream = "";
        try {
            stream = new Scanner(coded).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Input  = " + stream);
        return stream;
    }

    String removeTerminateSequence(String stream) {
        stream = stream.substring(8, stream.length() - 8);
        return stream;
    }

    StringBuilder decode(String stream) {
        StringBuilder output = new StringBuilder();
        int i = 0;
        int onesCounter = 0;
        int streamLength = stream.length();
        while (streamLength > 0) {
            if (stream.substring(i, i + 1).equals("1")) {
                output.append("1");
                onesCounter++;
            } else {
                if(onesCounter == 5){
                    onesCounter = 0;
                } else {
                    onesCounter = 0;
                    output.append("0");
                }
            }
            streamLength--;
            i++;
        }
        return output;
    }

    StringBuilder deleteCRC(StringBuilder output) {
        old = output.substring(output.length()-32,output.length());
        output.delete(output.length()-32,output.length());
        return output;
    }

    void writeToFile(StringBuilder output, File decoded) {
        try {
            PrintWriter out = new PrintWriter(decoded);
            out.print(output);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void calculateCRC(StringBuilder input) {
        crc.update(input.toString().getBytes());
    }

    boolean isEqual() {
        StringBuilder tempCRC =  new StringBuilder(Long.toBinaryString(crc.getValue()));
        while(tempCRC.length()<32){
            tempCRC.insert(0,0);
        }
        return old.equals(tempCRC.toString());
    }
}