import java.lang.*;
import java.util.*;
import java.io.*;

public class arp
{
    public static void main(String[] args)
    {
        ArrayList<IpMac> consoleIM = getConsoleIpMacs();
        ArrayList<IpMac> fileIM    = getFileIpMacs();
        generateIpMacFile(consoleIM, fileIM);
        generateAvailableIpMacFile(consoleIM, fileIM);
    }
    
    public static ArrayList<IpMac> getConsoleIpMacs()
    {
        ArrayList<IpMac> lst = new ArrayList<>();
    try {    
        ProcessBuilder callArp = new ProcessBuilder("cmd.exe", "/c", "arp", "-a");
        callArp.redirectErrorStream(true);

        Process arp = callArp.start();
        Scanner arpIn  = new Scanner(new BufferedReader(new InputStreamReader(arp.getInputStream())));

        // Skip first lines
        for (int i = 0; arpIn.hasNext() && i < 3; ++i)
            arpIn.nextLine();

        for(IpMac temp = null; arpIn.hasNextLine(); lst.add(temp)) {
            String[] tokens = arpIn.nextLine().split("\\s+");
            temp = new IpMac(tokens[1], tokens[2], tokens[3]);
        }
        arpIn.close();

        int exitValue = arp.waitFor();
        if (exitValue != 0)
            throw new Exception("arp.exe terminated abnormally");

     } catch (Exception e) {
        e.printStackTrace();
     }
        return lst;
    }

    public static ArrayList<IpMac> getFileIpMacs()
    {
        ArrayList<IpMac> lst = new ArrayList<>();
     try{
         File arpTxt = new File("arp.txt");
         if(arpTxt.exists()) {
            Scanner fileIn = new Scanner(new BufferedReader(new FileReader("arp.txt")));

            // Skip header
            if (fileIn.hasNextLine())
                fileIn.nextLine();

            for(IpMac temp = null; fileIn.hasNextLine(); lst.add(temp)) {
                String[] tokens = fileIn.nextLine().split("\\s+");
                temp = new IpMac(tokens[1], tokens[2], tokens[3]);
            }
            fileIn.close();
        }

     } catch (Exception e) {
         e.printStackTrace();
     }
        return lst;
    }
    
    public static void generateIpMacFile(ArrayList<IpMac> consoleList, ArrayList<IpMac> fileList)
    {
     try{
        ArrayList<IpMac> newList = new ArrayList<>(consoleList.size() + fileList.size());
        newList.addAll(consoleList);
        newList.addAll(fileList);

        Set<IpMac> set = new TreeSet<>(newList);

        PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter("arp.txt")));
        String header = String.format("%18s%20s%12s", "Internet Address", "Physical Address", "Type");
        fileOut.println(header);

        for (IpMac elem : set)
            fileOut.println(elem);
        fileOut.close();

     } catch (Exception e) {
         e.printStackTrace();
     }
    }

    public static void generateAvailableIpMacFile(ArrayList<IpMac> consoleList, ArrayList<IpMac> fileList)
    {
     try {
        PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter("arpAvailable.txt")));
        String header = String.format("%18s%20s%12s", "Internet Address", "Physical Address", "Type");
        fileOut.println(header);

        for (IpMac elem : fileList)
            if(Collections.binarySearch(consoleList, elem) < 0)
                fileOut.println(elem);
        fileOut.close();
 
     } catch (Exception e) {
         e.printStackTrace();
     }
    }
}

class IpMac implements Comparable<IpMac>
{
    String ip;
    String mac;
    String type;
    
    IpMac(String ip, String mac, String type)
    {
        this.ip = ip;
        this.mac = mac;
        this.type = type;
    }

    public int compareTo(IpMac other)
    {
        if (!other.ip.equals(this.ip))
            return -other.ip.compareTo(this.ip);
        else if (!other.mac.equals(this.mac))
            return -other.mac.compareTo(this.mac);
        else
            return 0;
    }
    
    public String toString()
    {
        return String.format("%18s%20s%12s", ip, mac, type);
    }
}