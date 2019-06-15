package zad1;

public class l3z1 {
    private Variables v = new Variables();
    public static void main (String [] args) {
        l3z1 l = new l3z1();
        l.start();

    }

    private void start() {
        StringBuilder output = new StringBuilder();
        Coder c = new Coder();
        String source = c.readFile(v.source);

        output.append(c.addTerminateSequence());
        output.append(c.code(c.addCRC(source)));
        output.append(c.addTerminateSequence());

        System.out.println("Result = " + output);
        c.writeToFile(output, v.coded);



        StringBuilder input = new StringBuilder();
        Decoder d = new Decoder();
        String coded = d.readFile(v.coded);

        coded = d.removeTerminateSequence(coded);
        input.append(d.deleteCRC(d.decode(coded)));

        System.out.println("Result = " + input);
        d.writeToFile(input, v.decoded);



        d.calculateCRC(input);
        if(d.isEqual())
            System.out.println("CRC Equal");
        else
            System.out.println("CRC not equal");
    }
}