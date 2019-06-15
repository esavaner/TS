package zad1;

import java.io.*;
import java.util.Scanner;
import java.util.zip.CRC32;

class Coder {
    String readFile(File source) {
        String stream = "";
        try {
            stream = new Scanner(source).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Input = " + stream);
        return stream;
    }

    String addTerminateSequence() {
        return "01111110";
    }

    StringBuilder addCRC(String stream) {
        StringBuilder input = new StringBuilder(stream);
        CRC32 crc = new CRC32();
        crc.update(stream.getBytes());
        StringBuilder calculatedCRC = new StringBuilder();
        calculatedCRC.append(Long.toBinaryString(crc.getValue()));
        while(calculatedCRC.length() < 32)
            calculatedCRC.insert(0,0);

        System.out.println("Crc = " + calculatedCRC);
        input.append(calculatedCRC);
        return input;
    }

    StringBuilder code(StringBuilder stream) {
        StringBuilder output = new StringBuilder();
        int i=0;
        int onesCounter = 0;
        int inputLength = stream.length();
        while(inputLength>0){
            if(onesCounter == 5) {
                output.append("0");
                onesCounter = 0;
            }

            if(stream.substring(i,i+1).equals("1")) {
                onesCounter++;
                output.append("1");
            } else {
                onesCounter = 0;
                output.append("0");
            }

            inputLength--;
            i++;
        }
        return output;
    }

    void writeToFile(StringBuilder output, File coded) {
        try {
            PrintWriter out = new PrintWriter(coded);
            out.print(output);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}